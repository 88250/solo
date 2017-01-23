/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.processor;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.model.Tag;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticQueryService;
import org.b3log.solo.service.TagQueryService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Blog processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.4, Dec 17, 2015
 * @since 0.4.6
 */
@RequestProcessor
public class BlogProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BlogProcessor.class.getName());

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * URL fetch service.
     */
    private final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();

    /**
     * Gets blog information.
     *
     * <ul>
     * <li>Time of the recent updated article</li>
     * <li>Article count</li>
     * <li>Comment count</li>
     * <li>Tag count</li>
     * <li>Serve path</li>
     * <li>Static serve path</li>
     * <li>Solo version</li>
     * <li>Runtime environment (LOCAL)</li>
     * <li>Locale</li>
     * </ul>
     *
     * @param context the specified context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/blog/info", method = HTTPRequestMethod.GET)
    public void getBlogInfo(final HTTPRequestContext context) throws Exception {
        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();

        renderer.setJSONObject(jsonObject);

        jsonObject.put("recentArticleTime", articleQueryService.getRecentArticleTime());
        final JSONObject statistic = statisticQueryService.getStatistic();

        jsonObject.put("articleCount", statistic.getLong(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT));
        jsonObject.put("commentCount", statistic.getLong(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT));
        jsonObject.put("tagCount", tagQueryService.getTagCount());
        jsonObject.put("servePath", Latkes.getServePath());
        jsonObject.put("staticServePath", Latkes.getStaticServePath());
        jsonObject.put("version", SoloServletListener.VERSION);
        jsonObject.put("locale", Latkes.getLocale());
        jsonObject.put("runtimeMode", Latkes.getRuntimeMode());
        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        jsonObject.put("runtimeEnv", runtimeEnv);
        if (RuntimeEnv.LOCAL == runtimeEnv) {
            jsonObject.put("runtimeDatabase", Latkes.getRuntimeDatabase());
        }
    }

    /**
     * Sync user to https://hacpai.com.
     *
     * @param context the specified context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/blog/symphony/user", method = HTTPRequestMethod.GET)
    public void syncUser(final HTTPRequestContext context) throws Exception {
        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();

        renderer.setJSONObject(jsonObject);

        if (Latkes.getServePath().contains("localhost")) {
            return;
        }

        final JSONObject preference = preferenceQueryService.getPreference();

        if (null == preference) {
            return; // not init yet
        }

        final HTTPRequest httpRequest = new HTTPRequest();

        httpRequest.setURL(new URL(SoloServletListener.B3LOG_SYMPHONY_SERVE_PATH + "/apis/user"));
        httpRequest.setRequestMethod(HTTPRequestMethod.POST);
        final JSONObject requestJSONObject = new JSONObject();

        final JSONObject admin = userQueryService.getAdmin();

        requestJSONObject.put(User.USER_NAME, admin.getString(User.USER_NAME));
        requestJSONObject.put(User.USER_EMAIL, admin.getString(User.USER_EMAIL));
        requestJSONObject.put(User.USER_PASSWORD, admin.getString(User.USER_PASSWORD));
        requestJSONObject.put("userB3Key", preference.optString(Option.ID_C_KEY_OF_SOLO));
        requestJSONObject.put("clientHost", Latkes.getServePath());

        httpRequest.setPayload(requestJSONObject.toString().getBytes("UTF-8"));

        urlFetchService.fetchAsync(httpRequest);
    }

    /**
     * Gets tags of all articles.
     *
     * <pre>
     * {
     *     "data": [
     *         ["tag1", "tag2", ....], // tags of one article
     *         ["tagX", "tagY", ....], // tags of another article
     *         ....
     *     ]
     * }
     * </pre>
     *
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception io exception
     */
    @RequestProcessing(value = "/blog/articles-tags", method = HTTPRequestMethod.GET)
    public void getArticlesTags(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final String pwd = request.getParameter("pwd");

        if (Strings.isEmptyOrNull(pwd)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONObject admin = userQueryService.getAdmin();

        if (!MD5.hash(pwd).equals(admin.getString(User.USER_PASSWORD))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONObject requestJSONObject = new JSONObject();

        requestJSONObject.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, 1);
        requestJSONObject.put(Pagination.PAGINATION_PAGE_SIZE, Integer.MAX_VALUE);
        requestJSONObject.put(Pagination.PAGINATION_WINDOW_SIZE, Integer.MAX_VALUE);
        requestJSONObject.put(Article.ARTICLE_IS_PUBLISHED, true);

        final JSONArray excludes = new JSONArray();

        excludes.put(Article.ARTICLE_CONTENT);
        excludes.put(Article.ARTICLE_UPDATE_DATE);
        excludes.put(Article.ARTICLE_CREATE_DATE);
        excludes.put(Article.ARTICLE_AUTHOR_EMAIL);
        excludes.put(Article.ARTICLE_HAD_BEEN_PUBLISHED);
        excludes.put(Article.ARTICLE_IS_PUBLISHED);
        excludes.put(Article.ARTICLE_RANDOM_DOUBLE);

        requestJSONObject.put(Keys.EXCLUDES, excludes);

        final JSONObject result = articleQueryService.getArticles(requestJSONObject);
        final JSONArray articles = result.optJSONArray(Article.ARTICLES);

        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        renderer.setJSONObject(ret);

        final JSONArray data = new JSONArray();

        ret.put("data", data);

        for (int i = 0; i < articles.length(); i++) {
            final JSONObject article = articles.optJSONObject(i);
            final String tagString = article.optString(Article.ARTICLE_TAGS_REF);

            final JSONArray tagArray = new JSONArray();

            data.put(tagArray);

            final String[] tags = tagString.split(",");

            for (final String tag : tags) {
                final String trim = tag.trim();

                if (!Strings.isEmptyOrNull(trim)) {
                    tagArray.put(tag);
                }
            }
        }
    }

    /**
     * Gets interest tags (top 10 and bottom 10).
     *
     * <pre>
     * {
     *     "data": ["tag1", "tag2", ....]
     * }
     * </pre>
     *
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception io exception
     */
    @RequestProcessing(value = "/blog/interest-tags", method = HTTPRequestMethod.GET)
    public void getInterestTags(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);
        final Set<String> tagTitles = new HashSet<>();

        final List<JSONObject> topTags = tagQueryService.getTopTags(10);
        for (final JSONObject topTag : topTags) {
            tagTitles.add(topTag.optString(Tag.TAG_TITLE));
        }

        final List<JSONObject> bottomTags = tagQueryService.getBottomTags(10);
        for (final JSONObject bottomTag : bottomTags) {
            tagTitles.add(bottomTag.optString(Tag.TAG_TITLE));
        }

        ret.put("data", tagTitles);
    }
}
