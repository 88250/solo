/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Tag;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Blog processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.1.3, Aug 2, 2018
 * @since 0.4.6
 */
@RequestProcessor
public class BlogProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BlogProcessor.class);

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
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Gets blog information.
     * <ul>
     * <li>Time of the recent updated article</li>
     * <li>Article count</li>
     * <li>Comment count</li>
     * <li>Tag count</li>
     * <li>Serve path</li>
     * <li>Static serve path</li>
     * <li>Solo version</li>
     * <li>Runtime mode</li>
     * <li>Runtime database</li>
     * <li>Locale</li>
     * <li>Admin username</li>
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
        jsonObject.put("articleCount", statistic.getLong(Option.ID_C_STATISTIC_PUBLISHED_ARTICLE_COUNT));
        jsonObject.put("commentCount", statistic.getLong(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT));
        jsonObject.put("tagCount", tagQueryService.getTagCount());
        jsonObject.put("servePath", Latkes.getServePath());
        jsonObject.put("staticServePath", Latkes.getStaticServePath());
        jsonObject.put("version", SoloServletListener.VERSION);
        jsonObject.put("runtimeMode", Latkes.getRuntimeMode());
        jsonObject.put("runtimeDatabase", Latkes.getRuntimeDatabase());
        jsonObject.put("locale", Latkes.getLocale());
        jsonObject.put("userName", userQueryService.getAdmin().optString(User.USER_NAME));
        jsonObject.put("qiniuDomain", "");
        jsonObject.put("qiniuBucket", "");
        final JSONObject qiniu = optionQueryService.getOptions(Option.CATEGORY_C_QINIU);
        if (null != qiniu) {
            jsonObject.put("qiniuDomain", qiniu.optString(Option.ID_C_QINIU_DOMAIN));
            jsonObject.put("qiniuBucket", qiniu.optString(Option.ID_C_QINIU_BUCKET));
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

        if (Latkes.getServePath().contains("localhost") || Strings.isIPv4(Latkes.getServePath())) {
            return;
        }

        final JSONObject preference = preferenceQueryService.getPreference();
        if (null == preference) {
            return; // not init yet
        }

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject admin = userQueryService.getAdmin();
        requestJSONObject.put(User.USER_NAME, admin.getString(User.USER_NAME));
        requestJSONObject.put(User.USER_EMAIL, admin.getString(User.USER_EMAIL));
        requestJSONObject.put(User.USER_PASSWORD, admin.getString(User.USER_PASSWORD));
        requestJSONObject.put("userB3Key", preference.optString(Option.ID_C_KEY_OF_SOLO));
        requestJSONObject.put("clientHost", Latkes.getServePath());

        HttpRequest.post(Solos.B3LOG_SYMPHONY_SERVE_PATH + "/apis/user").bodyText(requestJSONObject.toString()).contentTypeJson().sendAsync();
    }

    /**
     * Gets tags of all articles.
     * <p>
     * <pre>
     * {
     *     "data": [
     *         ["tag1", "tag2", ....], // tags of one article
     *         ["tagX", "tagY", ....], // tags of another article
     *         ....
     *     ]
     * }
     * </pre>
     * </p>
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception io exception
     */
    @RequestProcessing(value = "/blog/articles-tags", method = HTTPRequestMethod.GET)
    public void getArticlesTags(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final String pwd = request.getParameter("pwd");
        if (StringUtils.isBlank(pwd)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONObject admin = userQueryService.getAdmin();
        if (!DigestUtils.md5Hex(pwd).equals(admin.getString(User.USER_PASSWORD))) {
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
                if (StringUtils.isNotBlank(trim)) {
                    tagArray.put(tag);
                }
            }
        }
    }

    /**
     * Gets interest tags (top 10 and bottom 10).
     * <p>
     * <pre>
     * {
     *     "data": ["tag1", "tag2", ....]
     * }
     * </pre>
     * </p>
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
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
