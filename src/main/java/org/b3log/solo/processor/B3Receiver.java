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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.service.*;
import org.json.JSONObject;

import java.util.Date;

/**
 * Receiving articles and comments from B3log community. Visits <a href="https://hacpai.com/article/1546941897596">B3log 构思 - 分布式社区网络</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.0.0.2, Mar 25, 2020
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
     * Comment repository.
     */
    @Inject
    private static CommentRepository commentRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Comment management service.
     */
    @Inject
    private CommentMgmtService commentMgmtService;

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
     * @param context the specified request context
     */
    public void postArticle(final RequestContext context) {
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
                article.put(Article.ARTICLE_COMMENTABLE, true);
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

    /**
     * Adds a comment with the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "comment": {
     *         "articleId": "",
     *         "content": "",
     *         "contentHTML": "",
     *         "ua": "",
     *         "ip": "",
     *         "authorName": "",
     *         "authorURL": "",
     *         "authorAvatarURL": "",
     *         "isArticleAuthor": true,
     *         "time": 1457784330398
     *     },
     *     "client": {
     *         "userName": "88250",
     *         "userB3Key": ""
     *     }
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": true
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void addComment(final RequestContext context) {
        final JSONObject ret = new JSONObject().put(Keys.CODE, 0);
        context.renderJSON(ret);

        final JSONObject requestJSONObject = context.requestJSON();

        LOGGER.log(Level.DEBUG, "Adds a comment from Sym [" + requestJSONObject.toString() + "]");

        try {
            final JSONObject symCmt = requestJSONObject.optJSONObject(Comment.COMMENT);
            if (null == symCmt) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found comment";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);

                return;
            }

            final JSONObject symClient = requestJSONObject.optJSONObject("client");
            if (null == symClient) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found client";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);

                return;
            }

            final String articleAuthorName = symClient.optString(User.USER_NAME);
            final JSONObject articleAuthor = userRepository.getByUserName(articleAuthorName);
            if (null == articleAuthor) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found user [" + articleAuthorName + "]";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);

                return;
            }

            final String b3Key = symClient.optString(UserExt.USER_B3_KEY);
            final String key = articleAuthor.optString(UserExt.USER_B3_KEY);
            if (!StringUtils.equals(key, b3Key)) {
                ret.put(Keys.CODE, 1);
                final String msg = "Wrong key";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);

                return;
            }

            final String articleId = symCmt.getString("articleId");
            final JSONObject article = articleRepository.get(articleId);
            if (null == article) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not found the specified article [id=" + articleId + "]";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);

                return;
            }

            final String commentName = symCmt.getString("authorName");
            String commentURL = symCmt.optString("authorURL");
            if (!Strings.isURL(commentURL)) {
                commentURL = "";
            }
            final String commentThumbnailURL = symCmt.getString("authorAvatarURL");

            final JSONObject commenter = userRepository.getByUserName(commentName);
            if (null == commenter) {
                // 社区回帖同步博客评论 https://github.com/b3log/solo/issues/12691
                final JSONObject addUserReq = new JSONObject();
                addUserReq.put(User.USER_NAME, commentName);
                addUserReq.put(UserExt.USER_AVATAR, commentThumbnailURL);
                addUserReq.put(User.USER_ROLE, Role.VISITOR_ROLE);
                addUserReq.put(UserExt.USER_GITHUB_ID, "");
                addUserReq.put(UserExt.USER_B3_KEY, "");
                try {
                    userMgmtService.addUser(addUserReq);
                    LOGGER.log(Level.INFO, "Created a user [role=" + Role.VISITOR_ROLE + "] via Sym comment");
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Adds a user [" + commentName + "] failed", e);
                    ret.put(Keys.CODE, 1);
                    ret.put(Keys.MSG, "Adds a user [" + commentName + "] failed");

                    return;
                }
            }

            if (!optionQueryService.allowComment() || !article.optBoolean(Article.ARTICLE_COMMENTABLE)) {
                ret.put(Keys.CODE, 1);
                final String msg = "Not allow comment";
                ret.put(Keys.MSG, msg);
                LOGGER.log(Level.WARN, msg);

                return;
            }

            String commentContent = symCmt.getString("content"); // Markdown

            final Transaction transaction = commentRepository.beginTransaction();
            final JSONObject comment = new JSONObject();
            final String commentId = Ids.genTimeMillisId();
            comment.put(Keys.OBJECT_ID, commentId);
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_THUMBNAIL_URL, commentThumbnailURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final Date date = new Date();
            comment.put(Comment.COMMENT_CREATED, date.getTime());
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            comment.put(Comment.COMMENT_ON_ID, articleId);
            final String commentSharpURL = Comment.getCommentSharpURLForArticle(article, commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            commentRepository.add(comment);
            articleMgmtService.incArticleCommentCount(articleId);
            transaction.commit();

            LOGGER.log(Level.INFO, "Added a comment from Sym [" + requestJSONObject.toString() + "]");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            ret.put(Keys.CODE, 1).put(Keys.MSG, e.getMessage());
        }
    }
}
