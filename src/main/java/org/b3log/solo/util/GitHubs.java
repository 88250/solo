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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.solo.model.Common;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * GitHub utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 14, 2019
 * @since 3.0.0
 */
public final class GitHubs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(GitHubs.class);

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
            if (200 != res.statusCode()) {
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
     * Private constructor.
     */
    private GitHubs() {
    }
}
