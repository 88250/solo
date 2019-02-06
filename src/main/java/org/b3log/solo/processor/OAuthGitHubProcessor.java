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
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.URLs;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth GitHub processor.
 * <ul>
 * <li>Redirects to GitHub auth page (/oauth/github/redirect), GET</li>
 * <li>GitHub callback (/oauth/github), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Jan 29, 2019
 * @since 2.9.5
 */
@RequestProcessor
public class OAuthGitHubProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OAuthGitHubProcessor.class);

    /**
     * Client id.
     */
    private static final String CLIENT_ID = "77f93670fee557f1a613";

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
        final String state = Latkes.getServePath() + ":::" + RandomStringUtils.randomAlphanumeric(16);
        STATES.put(state, URLs.encode(state));

        final String path = "https://github.com/login/oauth/authorize" + "?client_id=" + CLIENT_ID + "&state=" + state
                + "&scope=public_repo,user";

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

        final String accessToken = context.param("ak");
        final JSONObject userInfo = Solos.getGitHubUserInfo(accessToken);
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

        JSONObject oauthGitHubOpt = optionQueryService.getOptionById(Option.ID_C_OAUTH_GITHUB);
        if (null == oauthGitHubOpt) {
            oauthGitHubOpt = new JSONObject();
            oauthGitHubOpt.put(Keys.OBJECT_ID, Option.ID_C_OAUTH_GITHUB);
            oauthGitHubOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_OAUTH);
            oauthGitHubOpt.put(Option.OPTION_VALUE, "[]");
        }
        String value = oauthGitHubOpt.optString(Option.OPTION_VALUE);
        if (StringUtils.isBlank(value)) {
            value = "[]";
        }
        final JSONArray github = new JSONArray(value);
        final Set<String> githubAuths = CollectionUtils.jsonArrayToSet(github);
        final String oAuthPair = Option.getOAuthPair(githubAuths, openId); // openId:@:userId
        if (StringUtils.isBlank(oAuthPair)) {
            if (!initService.isInited()) {
                final JSONObject initReq = new JSONObject();
                initReq.put(User.USER_NAME, userName);
                initReq.put(User.USER_EMAIL, userEmail);
                initReq.put(UserExt.USER_AVATAR, userAvatar);
                initReq.put(UserExt.USER_T_B3_KEY, openId);
                try {
                    initService.init(initReq);
                } catch (final Exception e) {
                    // ignored
                }
            } else {
                JSONObject user = userQueryService.getUserByEmailOrUserName(userName);
                if (null == user) {
                    user = userQueryService.getUserByEmailOrUserName(userEmail);
                }
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
                    try {
                        userMgmtService.addUser(addUserReq);
                    } catch (final Exception e) {
                        // ignored
                    }
                }
            }

            JSONObject user = userQueryService.getUserByEmailOrUserName(userName);
            if (null == user) {
                user = userQueryService.getUserByEmailOrUserName(userEmail);
            }
            final String userId = user.optString(Keys.OBJECT_ID);
            githubAuths.add(openId + GITHUB_SPLIT + userId);
            value = new JSONArray(githubAuths).toString();
            oauthGitHubOpt.put(Option.OPTION_VALUE, value);
            try {
                optionMgmtService.addOrUpdateOption(oauthGitHubOpt);
            } catch (final Exception e) {
                // ignored
            }

            Solos.login(user, response);
            context.sendRedirect(Latkes.getServePath());
            LOGGER.log(Level.INFO, "Logged in [email={0}, remoteAddr={1}] with GitHub oauth", userEmail, Requests.getRemoteAddr(request));

            return;
        }

        final String[] openIdUserId = oAuthPair.split(GITHUB_SPLIT);
        final String userId = openIdUserId[1];
        final JSONObject userResult = userQueryService.getUser(userId);
        if (null == userResult) {
            context.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONObject user = userResult.optJSONObject(User.USER);
        Solos.login(user, response);
        context.sendRedirect(Latkes.getServePath());
        LOGGER.log(Level.INFO, "Logged in [email={0}, remoteAddr={1}] with GitHub oauth", userEmail, Requests.getRemoteAddr(request));
    }
}
