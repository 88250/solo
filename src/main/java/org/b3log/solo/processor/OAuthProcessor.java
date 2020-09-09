/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.processor;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Requests;
import org.b3log.solo.Server;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.Statics;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth processor.
 * <ul>
 * <li>Redirects to HacPai auth page (/login/redirect), GET</li>
 * <li>OAuth callback (/login/callback), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Jun 10, 2020
 * @since 2.9.5
 */
@Singleton
public class OAuthProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(OAuthProcessor.class);

    /**
     * OAuth parameters - state &lt;state, redirectURL&gt;.
     */
    private static final Map<String, String> STATES = new ConcurrentHashMap();

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
    public void redirectAuth(final RequestContext context) {
        String referer = context.param("referer");
        if (StringUtils.isBlank(referer)) {
            referer = Latkes.getServePath();
        }

        final String state = RandomStringUtils.randomAlphanumeric(16);
        STATES.put(state, referer);

        final String loginAuthURL = "https://ld246.com/login?goto=" + Latkes.getServePath() + "/login/callback";
        final String path = loginAuthURL + "&state=" + state + "&v=" + Server.VERSION;
        context.sendRedirect(path);
    }

    /**
     * OAuth callback.
     *
     * @param context the specified context
     */
    public synchronized void authCallback(final RequestContext context) {
        String state = context.param("state");
        final String referer = STATES.get(state);
        if (null == referer) {
            context.sendError(400);
            return;
        }
        STATES.remove(state);

        final Response response = context.getResponse();
        final Request request = context.getRequest();
        final String accessToken = context.param("access_token");
        final JSONObject userInfo = Solos.getUserInfo(accessToken);
        if (null == userInfo) {
            LOGGER.log(Level.WARN, "Can't get user info with token [" + accessToken + "]");
            context.sendError(401);
            return;
        }

        final String userId = userInfo.optString("userId");
        final String userName = userInfo.optString(User.USER_NAME);
        final String userAvatar = userInfo.optString("avatar");

        JSONObject user = userQueryService.getUserByGitHubId(userId);
        if (null == user) {
            if (!initService.isInited()) {
                final JSONObject initReq = new JSONObject();
                initReq.put(User.USER_NAME, userName);
                initReq.put(UserExt.USER_AVATAR, userAvatar);
                initReq.put(UserExt.USER_B3_KEY, userName);
                initReq.put(UserExt.USER_GITHUB_ID, userId);
                initService.init(initReq);
            } else {
                user = userQueryService.getUserByName(userName);
                if (null == user) {
                    final JSONObject addUserReq = new JSONObject();
                    addUserReq.put(User.USER_NAME, userName);
                    addUserReq.put(UserExt.USER_AVATAR, userAvatar);
                    addUserReq.put(User.USER_ROLE, Role.VISITOR_ROLE);
                    addUserReq.put(UserExt.USER_GITHUB_ID, userId);
                    addUserReq.put(UserExt.USER_B3_KEY, userName);
                    try {
                        userMgmtService.addUser(addUserReq);
                    } catch (final Exception e) {
                        LOGGER.log(Level.ERROR, "Registers via oauth failed", e);
                        context.sendError(500);
                        return;
                    }
                } else {
                    user.put(UserExt.USER_GITHUB_ID, userId);
                    user.put(UserExt.USER_AVATAR, userAvatar);
                    try {
                        userMgmtService.updateUser(user);
                    } catch (final Exception e) {
                        LOGGER.log(Level.ERROR, "Updates user id failed", e);
                        context.sendError(500);
                        return;
                    }
                }
            }
        } else {
            user.put(User.USER_NAME, userName);
            user.put(UserExt.USER_AVATAR, userAvatar);
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
        Statics.clear();
        context.sendRedirect(referer);
        LOGGER.log(Level.INFO, "Logged in [name={}, remoteAddr={}] with oauth", userName, Requests.getRemoteAddr(request));
    }
}
