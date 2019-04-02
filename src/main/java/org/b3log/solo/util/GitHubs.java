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
package org.b3log.solo.util;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * GitHub utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Feb 8, 2019
 * @since 3.0.0
 */
public final class GitHubs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(GitHubs.class);

    /**
     * Gets GitHub repos.
     *
     * @param githubUserId the specified GitHub user id
     * @return GitHub repos, returns {@code null} if not found
     */
    public static JSONArray getGitHubRepos(final String githubUserId) {
        try {
            final HttpResponse res = HttpRequest.get("https://hacpai.com/github/repos?id=" + githubUserId).trustAllCerts(true).
                    connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
            if (HttpServletResponse.SC_OK != res.statusCode()) {
                return null;
            }
            res.charset("UTF-8");
            final JSONObject result = new JSONObject(res.bodyText());
            if (0 != result.optInt(Keys.STATUS_CODE)) {
                return null;
            }
            final JSONObject data = result.optJSONObject(Common.DATA);
            final JSONArray ret = data.optJSONArray("githubrepos");

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets GitHub repos failed", e);

            return null;
        }
    }

    /**
     * Gets GitHub user info.
     *
     * @param accessToken the specified access token
     * @return GitHub user info, for example, <pre>
     * {
     *   "openId": "",
     *   "userName": "D",
     *   "userAvatar": "https://avatars3.githubusercontent.com/u/873584?v=4"
     * }
     * </pre>, returns {@code null} if not found QQ user info
     */
    public static JSONObject getGitHubUserInfo(final String accessToken) {
        try {
            final HttpResponse res = HttpRequest.get("https://hacpai.com/github/user?ak=" + accessToken).trustAllCerts(true).
                    connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
            if (HttpServletResponse.SC_OK != res.statusCode()) {
                return null;
            }
            res.charset("UTF-8");
            final JSONObject result = new JSONObject(res.bodyText());
            if (0 != result.optInt(Keys.STATUS_CODE)) {
                return null;
            }
            final JSONObject data = result.optJSONObject(Common.DATA);
            final String userName = StringUtils.trim(data.optString("userName"));
            final String openId = data.optString("userId");
            final String avatarUrl = data.optString("userAvatarURL");

            final JSONObject ret = new JSONObject();
            ret.put("openId", openId);
            ret.put(User.USER_NAME, userName);
            ret.put(UserExt.USER_AVATAR, avatarUrl);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets GitHub user info failed", e);

            return null;
        }
    }

    /**
     * Private constructor.
     */
    private GitHubs() {
    }
}
