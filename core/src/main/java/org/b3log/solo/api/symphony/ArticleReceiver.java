/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.solo.api.symphony;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Requests;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.QueryResults;
import org.json.JSONObject;
import org.jsoup.Jsoup;


/**
 * Article receiver (from B3log Symphony).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Mar 18, 2013
 * @since 0.5.5
 */
@RequestProcessor
public final class ArticleReceiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleReceiver.class.getName());

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Article management service.
     */
    private ArticleMgmtService articleMgmtService = ArticleMgmtService.getInstance();

    /**
     * Article query service.
     */
    private ArticleQueryService articleQueryService = ArticleQueryService.getInstance();

    /**
     * Article abstract length.
     */
    private static final int ARTICLE_ABSTRACT_LENGTH = 500;

    /**
     * Adds an article with the specified request.
     *
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
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "article": {
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3",
     *         "userB3Key": "",
     *         "articleEditorType": ""
     *     }
     * }
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/apis/symphony/article", method = HTTPRequestMethod.POST)
    public void addArticle(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
        throws Exception {
        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);
            final JSONObject article = requestJSONObject.optJSONObject(Article.ARTICLE);
            final String userB3Key = article.optString("userB3Key");
            final JSONObject preference = preferenceQueryService.getPreference();

            if (!userB3Key.equals(preference.optString(Preference.KEY_OF_SOLO))) {
                LOGGER.log(Level.WARNING, "B3 key not match, ignored add article");

                return;
            }
            article.remove("userB3Key");

            final UserQueryService userQueryService = UserQueryService.getInstance();
            final JSONObject admin = userQueryService.getAdmin();

            article.put(Article.ARTICLE_AUTHOR_EMAIL, admin.getString(User.USER_EMAIL));
            final String plainTextContent = Jsoup.parse(article.optString(Article.ARTICLE_CONTENT)).text();

            if (plainTextContent.length() > ARTICLE_ABSTRACT_LENGTH) {
                article.put(Article.ARTICLE_ABSTRACT, plainTextContent.substring(0, ARTICLE_ABSTRACT_LENGTH) + "....");
            } else {
                article.put(Article.ARTICLE_ABSTRACT, plainTextContent);
            }
            article.put(Article.ARTICLE_IS_PUBLISHED, true);
            article.put(Common.POST_TO_COMMUNITY, false); // Do not send to rhythm
            article.put(Article.ARTICLE_COMMENTABLE, true);
            article.put(Article.ARTICLE_VIEW_PWD, "");
            String content = article.getString(Article.ARTICLE_CONTENT);
            final String articleId = article.getString(Keys.OBJECT_ID);

            content += "<br/><br/><p style='font-size: 12px;'><i>该文章同步自 <a href='http://symphony.b3log.org/article/" + articleId
                + "' target='_blank'>B3log 社区</a></i></p>";
            article.put(Article.ARTICLE_CONTENT, content);

            articleMgmtService.addArticle(requestJSONObject);

            ret.put(Keys.OBJECT_ID, articleId);
            ret.put(Keys.MSG, "add article succ");
            ret.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Updates an article with the specified request.
     *
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
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "article": {
     *         "oId": "", // Symphony Article#clientArticleId
     *         "articleTitle": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3",
     *         "userB3Key": "",
     *         "articleEditorType": ""
     *     }
     * }
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/apis/symphony/article", method = HTTPRequestMethod.PUT)
    public void updateArticle(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
        throws Exception {
        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        renderer.setJSONObject(ret);

        try {
            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);
            final JSONObject article = requestJSONObject.optJSONObject(Article.ARTICLE);
            final String userB3Key = article.optString("userB3Key");
            final JSONObject preference = preferenceQueryService.getPreference();

            if (!userB3Key.equals(preference.optString(Preference.KEY_OF_SOLO))) {
                LOGGER.log(Level.WARNING, "B3 key not match, ignored update article");

                return;
            }
            article.remove("userB3Key");

            final String articleId = article.getString(Keys.OBJECT_ID);

            if (null == articleQueryService.getArticleById(articleId)) {
                ret.put(Keys.MSG, "No found article[oId=" + articleId + "] to update");
                ret.put(Keys.STATUS_CODE, false);

                return;
            }

            final String plainTextContent = Jsoup.parse(article.optString(Article.ARTICLE_CONTENT)).text();

            if (plainTextContent.length() > ARTICLE_ABSTRACT_LENGTH) {
                article.put(Article.ARTICLE_ABSTRACT, plainTextContent.substring(0, ARTICLE_ABSTRACT_LENGTH) + "....");
            } else {
                article.put(Article.ARTICLE_ABSTRACT, plainTextContent);
            }
            article.put(Article.ARTICLE_IS_PUBLISHED, true);
            article.put(Common.POST_TO_COMMUNITY, false); // Do not send to rhythm
            article.put(Article.ARTICLE_COMMENTABLE, true);
            article.put(Article.ARTICLE_VIEW_PWD, "");
            String content = article.getString(Article.ARTICLE_CONTENT);

            content += "<br/><br/><p style='font-size: 12px;'><i>该文章同步自 <a href='http://symphony.b3log.org/article/" + articleId
                + "' target='_blank'>B3log 社区</a></i></p>";
            article.put(Article.ARTICLE_CONTENT, content);

            articleMgmtService.updateArticle(requestJSONObject);

            ret.put(Keys.MSG, "update article succ");
            ret.put(Keys.STATUS_CODE, true);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
