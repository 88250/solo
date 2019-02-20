/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.URLs;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.*;
import org.b3log.solo.util.GitHubs;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth GitHub processor.
 * <ul>
 * <li>Redirects to GitHub auth page (/oauth/github/redirect), GET</li>
 * <li>GitHub callback (/oauth/github), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Feb 15, 2019
 * @since 2.9.5
 */
@RequestProcessor
public class OAuthGitHubProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OAuthGitHubProcessor.class);

    /**
     * OAuth parameters - state.
     */
    private static final Map<String, String> STATES = new ConcurrentHashMap<>();

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
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * Initialization service.
     */
    @Inject
    private InitService initService;

    /**
     * GitHub split.
     */
    public static final String GITHUB_SPLIT = ":@:";

    /**
     * Redirects to GitHub auth page.
     *
     * @param context the specified context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/oauth/github/redirect", method = HttpMethod.GET)
    public void redirectGitHub(final RequestContext context) {
        final HttpResponse res = HttpRequest.get("https://hacpai.com/oauth/solo/client").trustAllCerts(true).
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
        final String clientId = result.optString(Common.DATA);

        String referer = context.param("referer");
        if (StringUtils.isBlank(referer)) {
            referer = Latkes.getServePath();
        }
        final String state = referer + ":::" + RandomStringUtils.randomAlphanumeric(16);
        STATES.put(state, URLs.encode(state));

        final String path = "https://github.com/login/oauth/authorize" + "?client_id=" + clientId + "&state=" + state
                + "&scope=public_repo,read:user,user:email,user:follow";

        context.sendRedirect(path);
    }

    /**
     * Shows GitHub callback page.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/oauth/github", method = HttpMethod.GET)
    public synchronized void showGitHubCallback(final RequestContext context) {
        final String state = context.param("state");
        String referer = STATES.get(state);
        if (StringUtils.isBlank(referer)) {
            context.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }
        STATES.remove(state);
        referer = URLs.decode(referer);
        referer = StringUtils.substringBeforeLast(referer, "__");
        final String accessToken = context.param("ak");
        final JSONObject userInfo = GitHubs.getGitHubUserInfo(accessToken);
        if (null == userInfo) {
            context.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final HttpServletResponse response = context.getResponse();
        final HttpServletRequest request = context.getRequest();
        final String openId = userInfo.optString("openId");
        final String userName = userInfo.optString(User.USER_NAME);
        final String userEmail = userInfo.optString(User.USER_EMAIL);
        final String userAvatar = userInfo.optString(UserExt.USER_AVATAR);

        JSONObject user = userQueryService.getUserByGitHubId(openId);
        if (null == user) {
            if (!initService.isInited()) {
                final JSONObject initReq = new JSONObject();
                initReq.put(User.USER_NAME, userName);
                initReq.put(User.USER_EMAIL, userEmail);
                initReq.put(UserExt.USER_AVATAR, userAvatar);
                initReq.put(UserExt.USER_B3_KEY, openId);
                initReq.put(UserExt.USER_GITHUB_ID, openId);
                try {
                    initService.init(initReq);
                } catch (final Exception e) {
                    // ignored
                }
            } else {
                user = userQueryService.getUserByEmailOrUserName(userName);
                if (null == user) {
                    final JSONObject preference = preferenceQueryService.getPreference();
                    if (!preference.optBoolean(Option.ID_C_ALLOW_REGISTER)) {
                        context.sendError(HttpServletResponse.SC_FORBIDDEN);

                        return;
                    }

                    final JSONObject addUserReq = new JSONObject();
                    addUserReq.put(User.USER_NAME, userName);
                    addUserReq.put(User.USER_EMAIL, userEmail);
                    addUserReq.put(UserExt.USER_AVATAR, userAvatar);
                    addUserReq.put(User.USER_ROLE, Role.VISITOR_ROLE);
                    addUserReq.put(UserExt.USER_GITHUB_ID, openId);
                    addUserReq.put(UserExt.USER_B3_KEY, openId);
                    try {
                        userMgmtService.addUser(addUserReq);
                    } catch (final Exception e) {
                        // ignored
                    }
                }
            }
        }

        user = userQueryService.getUserByEmailOrUserName(userName);
        if (null == user) {
            context.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        Solos.login(user, response);
        context.sendRedirect(referer);
        LOGGER.log(Level.INFO, "Logged in [name={0}, remoteAddr={1}] with GitHub oauth", userName, Requests.getRemoteAddr(request));
    }
}
