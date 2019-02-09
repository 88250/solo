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
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * Receiving articles from B3log community. Visits <a href="https://hacpai.com/b3log">B3log 构思</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.4.5, Feb 9, 2019
 * @since 0.5.5
 */
@RequestProcessor
public class B3ArticleReceiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(B3ArticleReceiver.class);

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

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
     * Adds an article with the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "article": {
     *         "id": "",
     *          "title": "",
     *          "content": "",
     *          "contentHTML": "",
     *          "tags": "tag1,tag2,tag3"
     *     },
     *     "client": {
     *         "userName": "",
     *         "userB3Key": ""
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
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/apis/symphony/article", method = HttpMethod.POST)
    public void addArticle(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        final JSONObject requestJSONObject = context.requestJSON();

        try {
            final JSONObject client = requestJSONObject.optJSONObject("client");
            final String articleAuthorName = client.optString(User.USER_NAME);
            final JSONObject articleAuthor = userRepository.getByUserName(articleAuthorName);
            if (null == articleAuthor) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
                ret.put(Keys.MSG, "No found user [" + articleAuthorName + "]");

                return;
            }

            final String b3Key = client.optString(UserExt.USER_B3_KEY);
            final String key = articleAuthor.optString(UserExt.USER_B3_KEY);
            if (!StringUtils.equals(key, b3Key)) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
                ret.put(Keys.MSG, "Wrong key");

                return;
            }

            ret.put(Keys.MSG, "add article succ");
            ret.put(Keys.STATUS_CODE, true);
            renderer.setJSONObject(ret);

            final JSONObject symArticle = requestJSONObject.optJSONObject(Article.ARTICLE);
            final String articleId = symArticle.getString(Keys.OBJECT_ID);
            final JSONObject oldArticle = articleQueryService.getArticleById(articleId);
            String localId = articleId;
            if (null == oldArticle) {
                final JSONObject article = new JSONObject().
                        put(Keys.OBJECT_ID, symArticle.optString("id")).
                        put(Article.ARTICLE_TITLE, symArticle.optString("title")).
                        put(Article.ARTICLE_CONTENT, symArticle.optString("content")).
                        put(Article.ARTICLE_TAGS_REF, symArticle.optString("tags"));
                article.put(Article.ARTICLE_AUTHOR_ID, articleAuthor.getString(Keys.OBJECT_ID));
                final String articleContent = article.optString(Article.ARTICLE_CONTENT);
                article.put(Article.ARTICLE_ABSTRACT, Article.getAbstract(articleContent));
                article.put(Article.ARTICLE_IS_PUBLISHED, true);
                article.put(Common.POST_TO_COMMUNITY, false); // Do not send to rhythm
                article.put(Article.ARTICLE_COMMENTABLE, true);
                article.put(Article.ARTICLE_VIEW_PWD, "");
                final String content = article.getString(Article.ARTICLE_CONTENT);
                article.put(Article.ARTICLE_CONTENT, content);
                localId = articleMgmtService.addArticle(requestJSONObject);
                ret.put(Keys.OBJECT_ID, localId);

                return;
            }

            final String articleContent = symArticle.optString("content");
            oldArticle.put(Article.ARTICLE_ABSTRACT, Article.getAbstract(articleContent));
            oldArticle.put(Article.ARTICLE_CONTENT, articleContent);
            oldArticle.put(Article.ARTICLE_TITLE, symArticle.optString("title"));
            oldArticle.put(Article.ARTICLE_TAGS_REF, symArticle.optString("tags"));
            oldArticle.put(Common.POST_TO_COMMUNITY, false); // Do not send to rhythm
            final JSONObject updateRequest = new JSONObject().put(Article.ARTICLE, oldArticle);
            articleMgmtService.updateArticle(updateRequest);
            localId = oldArticle.optString(Keys.OBJECT_ID);
            ret.put(Keys.OBJECT_ID, localId);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
