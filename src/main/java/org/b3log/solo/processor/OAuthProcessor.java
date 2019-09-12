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

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.URLs;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.*;
import org.b3log.solo.util.GitHubs;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth processor.
 * <ul>
 * <li>Redirects to auth page (/oauth/github/redirect), GET</li>
 * <li>OAuth callback (/oauth/github), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.1, Sep 12, 2019
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
     * Redirects to auth page.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/oauth/github/redirect", method = HttpMethod.GET)
    public void redirectAuth(final RequestContext context) {
        final HttpResponse res = HttpRequest.get("https://hacpai.com/oauth/solo/client2").trustAllCerts(true).
                connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
        if (HttpServletResponse.SC_OK != res.statusCode()) {
            LOGGER.log(Level.ERROR, "Gets oauth client id failed: " + res.toString());

            context.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        res.charset("UTF-8");
        final JSONObject result = new JSONObject(res.bodyText());
        if (0 != result.optInt(Keys.CODE)) {
            LOGGER.log(Level.ERROR, "Gets oauth client id failed: " + result.optString(Keys.MSG));

            return;
        }
        final JSONObject data = result.optJSONObject(Keys.DATA);
        final String clientId = data.optString("clientId");
        final String loginAuthURL = data.optString("loginAuthURL");

        String referer = context.param("referer");
        if (StringUtils.isBlank(referer)) {
            referer = Latkes.getServePath();
        }
        final String cb = Latkes.getServePath() + "/oauth/github";
        String state = referer + ":::" + RandomStringUtils.randomAlphanumeric(16) + ":::cb=" + cb + ":::";
        STATES.add(state);

        final String path = loginAuthURL + "?client_id=" + clientId + "&state=" + URLs.encode(state) + "&scope=public_repo,read:user,user:follow";

        context.sendRedirect(path);
    }

    /**
     * OAuth callback.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/oauth/github", method = HttpMethod.GET)
    public synchronized void authCallback(final RequestContext context) {
        String state = context.param("state");
        if (!STATES.contains(state)) {
            context.sendError(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }
        STATES.remove(state);
        final String referer = URLs.decode(state);
        final String accessToken = context.param("ak");
        final JSONObject userInfo = GitHubs.getGitHubUserInfo(accessToken);
        if (null == userInfo) {
            LOGGER.log(Level.WARN, "Can't get user info with token [" + accessToken + "]");
            context.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        final HttpServletResponse response = context.getResponse();
        final HttpServletRequest request = context.getRequest();
        final String openId = userInfo.optString("openId");
        final String userName = userInfo.optString(User.USER_NAME);
        final String userAvatar = userInfo.optString(UserExt.USER_AVATAR);

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
                        context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                        return;
                    }
                } else {
                    user.put(UserExt.USER_GITHUB_ID, openId);
                    try {
                        userMgmtService.updateUser(user);
                    } catch (final Exception e) {
                        LOGGER.log(Level.ERROR, "Updates user GitHub id failed", e);
                        context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                        return;
                    }
                }
            }
        } else {
            // 更改账号后无法登录 https://github.com/b3log/solo/issues/12879
            // 使用 GitHub 登录名覆盖本地用户名，解决 GitHub 改名后引起的登录问题
            user.put(User.USER_NAME, userName);
            try {
                userMgmtService.updateUser(user);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Updates user name failed", e);
                context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                return;
            }
        }

        user = userQueryService.getUserByName(userName);
        if (null == user) {
            LOGGER.log(Level.WARN, "Can't get user by name [" + userName + "]");
            context.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        final String redirect = StringUtils.substringBeforeLast(referer, "__");
        Solos.login(user, response);
        context.sendRedirect(redirect);
        LOGGER.log(Level.INFO, "Logged in [name={0}, remoteAddr={1}] with oauth", userName, Requests.getRemoteAddr(request));
    }
}
