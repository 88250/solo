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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Blog processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.1.8, Feb 7, 2019
 * @since 0.4.6
 */
@RequestProcessor
public class BlogProcessor {

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
     */
    @RequestProcessing(value = "/blog/info", method = HttpMethod.GET)
    public void getBlogInfo(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        jsonObject.put("recentArticleTime", articleQueryService.getRecentArticleTime());
        final JSONObject statistic = statisticQueryService.getStatistic();
        jsonObject.put("articleCount", statistic.getLong(Option.ID_T_STATISTIC_PUBLISHED_ARTICLE_COUNT));
        jsonObject.put("commentCount", statistic.getLong(Option.ID_T_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT));
        jsonObject.put("tagCount", tagQueryService.getTagCount());
        jsonObject.put("servePath", Latkes.getServePath());
        jsonObject.put("staticServePath", Latkes.getStaticServePath());
        jsonObject.put("version", SoloServletListener.VERSION);
        jsonObject.put("runtimeMode", Latkes.getRuntimeMode());
        jsonObject.put("runtimeDatabase", Latkes.getRuntimeDatabase());
        jsonObject.put("locale", Latkes.getLocale());
        String userName = "";
        try {
            userName = userQueryService.getAdmin().optString(User.USER_NAME);
        } catch (final Exception e) {
            // ignored
        }
        jsonObject.put("userName", userName);
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
     * @param context the specified context
     */
    @RequestProcessing(value = "/blog/articles-tags", method = HttpMethod.GET)
    public void getArticlesTags(final RequestContext context) {
        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, 1);
        requestJSONObject.put(Pagination.PAGINATION_PAGE_SIZE, Integer.MAX_VALUE);
        requestJSONObject.put(Pagination.PAGINATION_WINDOW_SIZE, Integer.MAX_VALUE);
        requestJSONObject.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);

        final JSONArray excludes = new JSONArray();

        excludes.put(Article.ARTICLE_CONTENT);
        excludes.put(Article.ARTICLE_UPDATED);
        excludes.put(Article.ARTICLE_CREATED);
        excludes.put(Article.ARTICLE_AUTHOR_ID);
        excludes.put(Article.ARTICLE_RANDOM_DOUBLE);

        requestJSONObject.put(Keys.EXCLUDES, excludes);

        final JSONObject result = articleQueryService.getArticles(requestJSONObject);
        final JSONArray articles = result.optJSONArray(Article.ARTICLES);

        final JsonRenderer renderer = new JsonRenderer();
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
}
