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
package org.b3log.solo.api.symphony;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
import org.b3log.solo.model.Option;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.QueryResults;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Article receiver (from B3log Symphony).
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.3.7, Jan 15, 2017
 * @since 0.5.5
 */
@RequestProcessor
public class ArticleReceiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleReceiver.class.getName());
    /**
     * Article abstract length.
     */
    private static final int ARTICLE_ABSTRACT_LENGTH = 500;
    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;
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
     * Adds an article with the specified request.
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
     * @param request  the specified http servlet request, for example,
     *                 "article": {
     *                 "oId": "",
     *                 "articleTitle": "",
     *                 "articleContent": "",
     *                 "articleTags": "tag1,tag2,tag3",
     *                 "userB3Key": "",
     *                 "articleEditorType": ""
     *                 }
     * @param response the specified http servlet response
     * @param context  the specified http request context
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

            if (!userB3Key.equals(preference.optString(Option.ID_C_KEY_OF_SOLO))) {
                LOGGER.log(Level.WARN, "B3 key not match, ignored add article");

                return;
            }
            article.remove("userB3Key");

            final JSONObject admin = userQueryService.getAdmin();

            article.put(Article.ARTICLE_AUTHOR_EMAIL, admin.getString(User.USER_EMAIL));
            final String articleContent = article.optString(Article.ARTICLE_CONTENT);
            final String plainTextContent = Jsoup.clean(Markdowns.toHTML(articleContent), Whitelist.none());
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

//            content += "\n\n<p style='font-size: 12px;'><i>该文章同步自 <a href='https://hacpai.com/article/" + articleId
//                + "' target='_blank'>黑客派</a></i></p>";
            article.put(Article.ARTICLE_CONTENT, content);

            articleMgmtService.addArticle(requestJSONObject);

            ret.put(Keys.OBJECT_ID, articleId);
            ret.put(Keys.MSG, "add article succ");
            ret.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Updates an article with the specified request.
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
     * @param request  the specified http servlet request, for example,
     *                 "article": {
     *                 "oId": "", // Symphony Article#clientArticleId
     *                 "articleTitle": "",
     *                 "articleContent": "",
     *                 "articleTags": "tag1,tag2,tag3",
     *                 "userB3Key": "",
     *                 "articleEditorType": ""
     *                 }
     * @param response the specified http servlet response
     * @param context  the specified http request context
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

            if (!userB3Key.equals(preference.optString(Option.ID_C_KEY_OF_SOLO))) {
                LOGGER.log(Level.WARN, "B3 key not match, ignored update article");

                return;
            }
            article.remove("userB3Key");

            final String articleId = article.getString(Keys.OBJECT_ID);

            if (null == articleQueryService.getArticleById(articleId)) {
                ret.put(Keys.MSG, "No found article[oId=" + articleId + "] to update");
                ret.put(Keys.STATUS_CODE, false);

                return;
            }

            final String articleContent = article.optString(Article.ARTICLE_CONTENT);
            final String plainTextContent = Jsoup.clean(Markdowns.toHTML(articleContent), Whitelist.none());
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

//            content += "\n\n<p style='font-size: 12px;'><i>该文章同步自 <a href='https://hacpai.com/article/" + articleId
//                + "' target='_blank'>黑客派</a></i></p>";
            article.put(Article.ARTICLE_CONTENT, content);

            articleMgmtService.updateArticle(requestJSONObject);

            ret.put(Keys.MSG, "update article succ");
            ret.put(Keys.STATUS_CODE, true);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
