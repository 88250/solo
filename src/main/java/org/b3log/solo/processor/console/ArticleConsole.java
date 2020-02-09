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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Images;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Article console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 0.4.0
 */
@Singleton
public class ArticleConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArticleConsole.class);

    /**
     * Article management service.
     */
    @Inject
    private ArticleMgmtService articleMgmtService;

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Pushes an article to community.
     *
     * @param context the specified request context
     */
    public void pushArticleToCommunity(final RequestContext context) {
        final JSONObject result = new JSONObject().put(Keys.CODE, 0);
        context.renderJSON(result);
        final String articleId = context.param("id");
        articleMgmtService.pushArticleToCommunity(articleId);
    }

    /**
     * Gets article thumbs.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": true,
     *     "data": [
     *         "https://b3logfile.com/bing/20171226.jpg?imageView2/1/w/960/h/540/interlace/1/q/100",
     *         "https://b3logfile.com/bing/20171105.jpg?imageView2/1/w/960/h/540/interlace/1/q/100",
     *         "https://b3logfile.com/bing/20180105.jpg?imageView2/1/w/960/h/540/interlace/1/q/100",
     *         "https://b3logfile.com/bing/20171114.jpg?imageView2/1/w/960/h/540/interlace/1/q/100"
     *     ]
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getArticleThumbs(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject result = new JSONObject();
        renderer.setJSONObject(result);
        result.put(Keys.STATUS_CODE, true);
        final Request request = context.getRequest();
        String strN = context.param("n");
        if (!Strings.isNumeric(strN)) {
            strN = "6";
        }

        final int n = Integer.valueOf(strN);
        final List<String> urls = Images.randomImages(n);

        // original: 1920*1080

        final String wStr = context.param("w");
        int w = Article.ARTICLE_THUMB_IMG_WIDTH;
        if (Strings.isNumeric(wStr)) {
            w = Integer.valueOf(wStr);
        }
        final int width = w;
        final String hStr = context.param("h");
        int h = Article.ARTICLE_THUMB_IMG_HEIGHT;
        if (Strings.isNumeric(hStr)) {
            h = Integer.valueOf(hStr);
        }
        final int height = h;

        result.put("data", urls.stream().map(url -> Images.imageSize(url, width, height)).collect(Collectors.toList()));
    }

    /**
     * Gets an article by the specified request json object.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": "",
     *     "articleTitle": "",
     *     "articleAbstract": "",
     *     "articleContent": "",
     *     "articlePermalink": "",
     *     "articleTags": [{
     *         "oId": "",
     *         "tagTitle": ""
     *     }, ....],
     *     "articleSignId": "",
     *     "signs": [{
     *         "oId": "",
     *         "signHTML": ""
     *     }, ....]
     *     "sc": "GET_ARTICLE_SUCC"
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        try {
            final String articleId = context.pathVar("id");
            final JSONObject currentUser = Solos.getCurrentUser(context);
            if (!articleQueryService.canAccessArticle(articleId, currentUser)) {
                final JSONObject ret = new JSONObject();
                renderer.setJSONObject(ret);
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));

                return;
            }

            final JSONObject result = articleQueryService.getArticle(articleId);
            result.put(Keys.STATUS_CODE, true);
            renderer.setJSONObject(result);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Gets articles(by crate date descending) by the specified request json object.
     * <p>
     * The request URI contains the pagination arguments. For example, the request URI is
     * /console/articles/status/published/1/10/20, means the current page is 1, the page size is 10, and the window size
     * is 20.
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "articles": [{
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleCommentCount": int,
     *         "articleCreateTime"; long,
     *         "articleViewCount": int,
     *         "articleTags": "tag1, tag2, ....",
     *         "articlePutTop": boolean,
     *         "articleStatus": int
     *      }, ....]
     * }
     * </pre>, order by article update date and sticky(put top).
     * </p>
     *
     * @param context the specified request context
     */
    public void getArticles(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        try {
            String path = context.requestURI().substring((Latkes.getContextPath() + "/console/articles/status/").length());
            final String status = StringUtils.substringBefore(path, "/");

            path = path.substring((status + "/").length());
            final JSONObject requestJSONObject = Solos.buildPaginationRequest(path);
            requestJSONObject.put(Article.ARTICLE_STATUS, "published".equals(status) ? Article.ARTICLE_STATUS_C_PUBLISHED : Article.ARTICLE_STATUS_C_DRAFT);

            final JSONArray excludes = new JSONArray();
            excludes.put(Article.ARTICLE_CONTENT);
            excludes.put(Article.ARTICLE_UPDATED);
            excludes.put(Article.ARTICLE_CREATED);
            excludes.put(Article.ARTICLE_AUTHOR_ID);
            excludes.put(Article.ARTICLE_RANDOM_DOUBLE);
            requestJSONObject.put(Keys.EXCLUDES, excludes);

            final String keyword = StringUtils.trim(context.param("k"));
            if (StringUtils.isNotBlank(keyword)) {
                requestJSONObject.put(Common.KEYWORD, keyword);
            }

            final JSONObject result = articleQueryService.getArticles(requestJSONObject);
            result.put(Keys.STATUS_CODE, true);
            renderer.setJSONObject(result);

            final JSONArray articles = result.optJSONArray(Article.ARTICLES);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.optJSONObject(i);
                String title = article.optString(Article.ARTICLE_TITLE);
                title = StringEscapeUtils.escapeXml(title);
                article.put(Article.ARTICLE_TITLE, title);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Removes an article by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void removeArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);
        final String articleId = context.pathVar("id");
        final JSONObject currentUser = Solos.getCurrentUser(context);

        try {
            if (!articleQueryService.canAccessArticle(articleId, currentUser)) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));

                return;
            }

            articleMgmtService.removeArticle(articleId);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
        }
    }

    /**
     * Cancels publish an article by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void cancelPublishArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        try {
            final String articleId = context.pathVar("id");
            final JSONObject currentUser = Solos.getCurrentUser(context);
            if (!articleQueryService.canAccessArticle(articleId, currentUser)) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));

                return;
            }

            articleMgmtService.cancelPublishArticle(articleId);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("unPulbishSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("unPulbishFailLabel"));
        }
    }

    /**
     * Cancels an top article by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void cancelTopArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);
        if (!Solos.isAdminLoggedIn(context)) {
            ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));
            ret.put(Keys.STATUS_CODE, false);

            return;
        }

        try {
            final String articleId = context.pathVar("id");
            articleMgmtService.topArticle(articleId, false);
            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("cancelTopSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("cancelTopFailLabel"));
        }
    }

    /**
     * Puts an article to top by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void putTopArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);
        if (!Solos.isAdminLoggedIn(context)) {
            ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));
            ret.put(Keys.STATUS_CODE, false);

            return;
        }

        try {
            final String articleId = context.pathVar("id");
            articleMgmtService.topArticle(articleId, true);
            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("putTopSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("putTopFailLabel"));
        }
    }

    /**
     * Updates an article by the specified request json object.
     * <p>
     * The specified request json object, for example,
     * <pre>
     * {
     *     "article": {
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3", // optional, default set "待分类"
     *         "articlePermalink": "", // optional
     *         "articleStatus": int, // 0: published, 1: draft
     *         "articleSignId": "" // optional
     *         "articleCommentable": boolean,
     *         "articleViewPwd": "",
     *         "postToCommunity": boolean
     *     }
     *  }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void updateArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final JSONObject article = requestJSONObject.getJSONObject(Article.ARTICLE);
            final String articleId = article.getString(Keys.OBJECT_ID);
            renderer.setJSONObject(ret);

            final JSONObject currentUser = Solos.getCurrentUser(context);
            if (!articleQueryService.canAccessArticle(articleId, currentUser)) {
                ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));
                ret.put(Keys.STATUS_CODE, false);

                return;
            }

            // 打印请求日志，如果发生特殊情况丢失数据，至少还可以根据日志寻回内容
            LOGGER.log(Level.INFO, "Updates an article request [" + requestJSONObject.toString() + "]");

            articleMgmtService.updateArticle(requestJSONObject);

            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            ret.put(Keys.STATUS_CODE, true);
        } catch (final ServiceException e) {
            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Adds an article with the specified request.
     * <p>
     * The specified request json object, for example,
     * <pre>
     * {
     *     "article": {
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3", // optional, default set "待分类"
     *         "articlePermalink": "", // optional
     *         "articleStatus": int, // 0: published, 1: draft
     *         "postToCommunity": boolean,
     *         "articleSignId": "" // optional
     *         "articleCommentable": boolean,
     *         "articleViewPwd": ""
     *     }
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "oId": "", // Generated article id
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void addArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final JSONObject currentUser = Solos.getCurrentUser(context);
            requestJSONObject.getJSONObject(Article.ARTICLE).put(Article.ARTICLE_AUTHOR_ID, currentUser.getString(Keys.OBJECT_ID));

            // 打印请求日志，如果发生特殊情况丢失数据，至少还可以根据日志寻回内容
            LOGGER.log(Level.INFO, "Adds an article request [" + requestJSONObject.toString() + "]");

            final String articleId = articleMgmtService.addArticle(requestJSONObject);
            ret.put(Keys.OBJECT_ID, articleId);
            ret.put(Keys.MSG, langPropsService.get("addSuccLabel"));
            ret.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
