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
package org.b3log.solo.util;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.solo.model.Common;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;

/**
 * GitHub utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Sep 3, 2020
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
            final HttpResponse res = HttpRequest.get("https://ld246.com/github/repos?id=" + githubUserId).
                    trustAllCerts(true).followRedirects(true).
                    connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
            if (200 != res.statusCode()) {
                return null;
            }
            res.charset("UTF-8");
            final JSONObject result = new JSONObject(res.bodyText());
            if (0 != result.optInt(Keys.CODE)) {
                return null;
            }
            final JSONObject data = result.optJSONObject(Common.DATA);
            return data.optJSONArray("githubrepos");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets GitHub repos failed", e);
            return null;
        }
    }

    /**
     * Updates a file by the specified personal access token, GitHub login name, repo name, file path and file content.
     *
     * @param pat       the specified personal access token
     * @param loginName the specified GitHub login name
     * @param repoName  the specified repo name
     * @param filePath  the specfiied file name
     * @param content   the specified file content
     * @return {@code true} if ok, returns {@code false} if failed
     */
    public static boolean updateFile(final String pat, final String loginName, final String repoName, final String filePath, final byte[] content) {
        final String fullRepoName = loginName + "/" + repoName;
        try {
            HttpResponse response = HttpRequest.get("https://api.github.com/repos/" + fullRepoName + "/git/trees/master").header("Authorization", "token " + pat).
                    connectionTimeout(7000).timeout(60000).header("User-Agent", Solos.USER_AGENT).send();
            int statusCode = response.statusCode();
            response.charset("UTF-8");
            String responseBody = response.bodyText();
            if (200 != statusCode && 409 != statusCode) {
                LOGGER.log(Level.ERROR, "Get git tree of file [" + filePath + "] failed: " + responseBody);
                return false;
            }

            final JSONObject body = new JSONObject().
                    put("message", ":memo: 更新博客").
                    put("content", Base64.getEncoder().encodeToString(content));
            if (200 == statusCode) {
                final JSONObject responseData = new JSONObject(responseBody);
                final JSONArray tree = responseData.optJSONArray("tree");
                for (int i = 0; i < tree.length(); i++) {
                    final JSONObject file = tree.optJSONObject(i);
                    if (StringUtils.equals(filePath, file.optString("path"))) {
                        body.put("sha", file.optString("sha"));
                        break;
                    }
                }
            }

            response = HttpRequest.put("https://api.github.com/repos/" + fullRepoName + "/contents/" + filePath).header("Authorization", "token " + pat).
                    connectionTimeout(7000).timeout(60000 * 2).header("User-Agent", Solos.USER_AGENT).bodyText(body.toString()).send();
            statusCode = response.statusCode();
            response.charset("UTF-8");
            responseBody = response.bodyText();
            if (200 != statusCode && 201 != statusCode) {
                LOGGER.log(Level.ERROR, "Updates repo [" + repoName + "] file [" + filePath + "] failed: " + responseBody);
                return false;
            }
            return true;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Updates repo [" + repoName + "] file [" + filePath + "] failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates or updates a GitHub repository by the specified personal access token, GitHub login name, repo name repo desc and repo homepage.
     *
     * @param pat          the specified personal access token
     * @param loginName    the specified GitHub login name
     * @param repoName     the specified repo name
     * @param repoDesc     the specified repo desc
     * @param repoHomepage the specified repo homepage
     * @return {@code true} if ok, returns {@code false} if failed
     */
    public static boolean createOrUpdateGitHubRepo(final String pat, final String loginName, final String repoName, final String repoDesc, final String repoHomepage) {
        try {
            final JSONObject body = new JSONObject().
                    put("name", repoName).
                    put("description", repoDesc).
                    put("homepage", repoHomepage).
                    put("has_wiki", false).
                    put("has_projects", false);
            HttpResponse response = HttpRequest.post("https://api.github.com/user/repos").header("Authorization", "token " + pat).
                    connectionTimeout(7000).timeout(30000).header("User-Agent", Solos.USER_AGENT).bodyText(body.toString()).send();
            int statusCode = response.statusCode();
            response.charset("UTF-8");
            String responseBody = response.bodyText();
            if (201 != statusCode && 422 != statusCode) {
                LOGGER.log(Level.ERROR, "Creates GitHub repo [" + repoName + "] failed: " + responseBody);
                return false;
            }
            if (201 == statusCode) {
                return true;
            }

            response = HttpRequest.patch("https://api.github.com/repos/" + loginName + "/" + repoName).header("Authorization", "token " + pat).
                    connectionTimeout(7000).timeout(30000).header("User-Agent", Solos.USER_AGENT).bodyText(body.toString()).send();
            statusCode = response.statusCode();
            responseBody = response.bodyText();
            if (200 != statusCode) {
                LOGGER.log(Level.ERROR, "Updates GitHub repo [" + repoName + "] failed: " + responseBody);
                return false;
            }
            return true;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Creates or updates GitHub repo failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets GitHub user by the specified personal access token.
     *
     * @param pat the specified personal access token
     * @return GitHub user, returns {@code null} if failed
     */
    public static JSONObject getGitHubUser(final String pat) {
        try {
            final HttpResponse response = HttpRequest.get("https://api.github.com/user").header("Authorization", "token " + pat).
                    connectionTimeout(7000).timeout(30000).header("User-Agent", Solos.USER_AGENT).send();
            if (200 != response.statusCode()) {
                return null;
            }
            response.charset("UTF-8");
            return new JSONObject(response.bodyText());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets GitHub user info failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Private constructor.
     */
    private GitHubs() {
    }
}
