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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.UserMgmtService;
import org.json.JSONObject;

/**
 * Receiving articles from B3log community. Visits <a href="https://ld246.com/article/1546941897596">B3log 构思 - 分布式社区网络</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.0.0.5, Jul 8, 2020
 * @since 0.5.5
 */
@Singleton
public class B3Receiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(B3Receiver.class);

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

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
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * Adds or updates an article with the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "article": {
     *         "id": "",
     *         "title": "",
     *         "content": "",
     *         "contentHTML": "",
     *         "tags": "tag1,tag2,tag3"
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
     *     "code": int,
     *     "oId": "", // Generated article id
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void receiveArticle(final RequestContext context) {
        final JSONObject ret = new JSONObject().put(Keys.CODE, 0);
        context.renderJSON(ret);

        final JSONObject requestJSONObject = context.requestJSON();
        LOGGER.log(Level.DEBUG, "Adds an article from Sym [" + requestJSONObject.toString() + "]");

        try {
            final JSONObject client = requestJSONObject.optJSONObject("client");
            if (null == client) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found client";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);
                return;
            }


            final String articleAuthorName = client.optString(User.USER_NAME);
            final JSONObject articleAuthor = userRepository.getByUserName(articleAuthorName);
            if (null == articleAuthor) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found user [" + articleAuthorName + "]";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);
                return;
            }

            final String b3Key = client.optString(UserExt.USER_B3_KEY);
            final String key = articleAuthor.optString(UserExt.USER_B3_KEY);
            if (!StringUtils.equals(key, b3Key)) {
                ret.put(Keys.CODE, 1);
                final String msg = "Wrong key";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);
                return;
            }

            final JSONObject symArticle = requestJSONObject.optJSONObject(Article.ARTICLE);
            if (null == symArticle) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found article";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);
                return;
            }

            final String title = symArticle.optString("title");
            final String articleId = symArticle.optString("id");
            final JSONObject oldArticle = articleQueryService.getArticleById(articleId);
            if (null == oldArticle) {
                final JSONObject article = new JSONObject().
                        put(Keys.OBJECT_ID, symArticle.optString("id")).
                        put(Article.ARTICLE_TITLE, title).
                        put(Article.ARTICLE_CONTENT, symArticle.optString("content")).
                        put(Article.ARTICLE_TAGS_REF, symArticle.optString("tags"));
                article.put(Article.ARTICLE_AUTHOR_ID, articleAuthor.getString(Keys.OBJECT_ID));
                final String articleContent = article.optString(Article.ARTICLE_CONTENT);
                article.put(Article.ARTICLE_ABSTRACT, Article.getAbstractText(articleContent));
                article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
                article.put(Common.POST_TO_COMMUNITY, false); // Do not send to rhythm
                article.put(Article.ARTICLE_VIEW_PWD, "");
                final String content = article.getString(Article.ARTICLE_CONTENT);
                article.put(Article.ARTICLE_CONTENT, content);
                final JSONObject addRequest = new JSONObject().put(Article.ARTICLE, article);
                articleMgmtService.addArticle(addRequest);
                LOGGER.log(Level.INFO, "Added an article [" + title + "] via Sym");
                return;
            }

            final String articleContent = symArticle.optString("content");
            oldArticle.put(Article.ARTICLE_ABSTRACT, Article.getAbstractText(articleContent));
            oldArticle.put(Article.ARTICLE_CONTENT, articleContent);
            oldArticle.put(Article.ARTICLE_TITLE, symArticle.optString("title"));
            oldArticle.put(Article.ARTICLE_TAGS_REF, symArticle.optString("tags"));
            oldArticle.put(Common.POST_TO_COMMUNITY, false); // Do not send to rhythm
            final JSONObject updateRequest = new JSONObject().put(Article.ARTICLE, oldArticle);
            articleMgmtService.updateArticle(updateRequest);
            LOGGER.log(Level.INFO, "Updated an article [" + title + "] via Sym");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            ret.put(Keys.CODE, 1).put(Keys.MSG, e.getMessage());
        }
    }
}
