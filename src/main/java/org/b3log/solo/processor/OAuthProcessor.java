/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.solo.processor;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.HttpMethod;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.http.annotation.RequestProcessing;
import org.b3log.latke.http.annotation.RequestProcessor;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.URLs;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth processor.
 * <ul>
 * <li>Redirects to HacPai auth page (/login/redirect), GET</li>
 * <li>OAuth callback (/login/callback), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.2, Dec 14, 2019
 * @since 2.9.5
 */
@RequestProcessor
public class OAuthProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OAuthProcessor.class);

    /**
     * OAuth parameters - state.
     */
    private static final Set<String> STATES = ConcurrentHashMap.newKeySet();

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Option management service.
     */
    @Inject
    private OptionMgmtService optionMgmtService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * Initialization service.
     */
    @Inject
    private InitService initService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Redirects to HacPai auth page.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/login/redirect", method = HttpMethod.GET)
    public void redirectAuth(final RequestContext context) {
        String referer = context.param("referer");
        if (StringUtils.isBlank(referer)) {
            referer = Latkes.getServePath();
        }

        String state = RandomStringUtils.randomAlphanumeric(16) + referer;
        STATES.add(state);

        final String loginAuthURL = "https://hacpai.com/login?goto=" + Latkes.getServePath() + "/login/callback";
        final String path = loginAuthURL + "?state=" + URLs.encode(state);
        context.sendRedirect(path);
    }

    /**
     * OAuth callback.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/login/callback", method = HttpMethod.GET)
    public synchronized void authCallback(final RequestContext context) {
        String state = context.param("state");
        if (!STATES.contains(state)) {
            context.sendError(400);

            return;
        }
        STATES.remove(state);
        String referer = URLs.decode(state);
        referer = StringUtils.substring(referer, 16);

        final Response response = context.getResponse();
        final Request request = context.getRequest();
        final String openId = context.param("userId");
        final String userName = context.param(User.USER_NAME);
        final String userAvatar = context.param("avatar");

        JSONObject user = userQueryService.getUserByGitHubId(openId);
        if (null == user) {
            if (!initService.isInited()) {
                final JSONObject initReq = new JSONObject();
                initReq.put(User.USER_NAME, userName);
                initReq.put(UserExt.USER_AVATAR, userAvatar);
                initReq.put(UserExt.USER_B3_KEY, openId);
                initReq.put(UserExt.USER_GITHUB_ID, openId);
                initService.init(initReq);
            } else {
                user = userQueryService.getUserByName(userName);
                if (null == user) {
                    final JSONObject addUserReq = new JSONObject();
                    addUserReq.put(User.USER_NAME, userName);
                    addUserReq.put(UserExt.USER_AVATAR, userAvatar);
                    addUserReq.put(User.USER_ROLE, Role.VISITOR_ROLE);
                    addUserReq.put(UserExt.USER_GITHUB_ID, openId);
                    addUserReq.put(UserExt.USER_B3_KEY, openId);
                    try {
                        userMgmtService.addUser(addUserReq);
                    } catch (final Exception e) {
                        LOGGER.log(Level.ERROR, "Registers via oauth failed", e);
                        context.sendError(500);

                        return;
                    }
                } else {
                    user.put(UserExt.USER_GITHUB_ID, openId);
                    try {
                        userMgmtService.updateUser(user);
                    } catch (final Exception e) {
                        LOGGER.log(Level.ERROR, "Updates user GitHub id failed", e);
                        context.sendError(500);

                        return;
                    }
                }
            }
        } else {
            user.put(User.USER_NAME, userName);
            try {
                userMgmtService.updateUser(user);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Updates user name failed", e);
                context.sendError(500);

                return;
            }
        }

        user = userQueryService.getUserByName(userName);
        if (null == user) {
            LOGGER.log(Level.WARN, "Can't get user by name [" + userName + "]");
            context.sendError(404);

            return;
        }

        Solos.login(user, response);
        context.sendRedirect(referer);
        LOGGER.log(Level.INFO, "Logged in [name={0}, remoteAddr={1}] with oauth", userName, Requests.getRemoteAddr(request));
    }
}
