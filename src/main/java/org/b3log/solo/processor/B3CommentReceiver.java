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
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Receiving comments from B3log community. Visits <a href="https://hacpai.com/b3log">B3log 构思</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.21, Feb 8, 2019
 * @since 0.5.5
 */
@RequestProcessor
public class B3CommentReceiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(B3CommentReceiver.class);

    /**
     * Comment management service.
     */
    @Inject
    private CommentMgmtService commentMgmtService;

    /**
     * Comment repository.
     */
    @Inject
    private static CommentRepository commentRepository;

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
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Event manager.
     */
    @Inject
    private static EventManager eventManager;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

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
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/apis/symphony/comment", method = HttpMethod.PUT)
    public void addComment(final RequestContext context) {
        final JSONObject ret = new JSONObject();
        context.renderJSON(ret);

        final JSONObject requestJSONObject = context.requestJSON();
        final Transaction transaction = commentRepository.beginTransaction();
        try {
            final JSONObject symCmt = requestJSONObject.optJSONObject(Comment.COMMENT);
            final JSONObject symClient = requestJSONObject.optJSONObject("client");
            final String articleAuthorName = symClient.optString(User.USER_NAME);
            final JSONObject articleAuthor = userRepository.getByUserName(articleAuthorName);
            if (null == articleAuthor) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
                ret.put(Keys.MSG, "No found user [" + articleAuthorName + "]");

                return;
            }

            final String b3Key = symClient.optString(UserExt.USER_B3_KEY);
            final String key = articleAuthor.optString(UserExt.USER_B3_KEY);
            if (!StringUtils.equals(key, b3Key)) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
                ret.put(Keys.MSG, "Wrong key");

                return;
            }

            final String articleId = symCmt.getString("articleId");
            final JSONObject article = articleRepository.get(articleId);
            if (null == article) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_NOT_FOUND);
                ret.put(Keys.MSG, "Not found the specified article [id=" + articleId + "]");

                return;
            }

            final String commentName = symCmt.getString("authorName");
            String commentURL = symCmt.optString("authorURL");
            if (!Strings.isURL(commentURL)) {
                commentURL = "";
            }
            final String commentThumbnailURL = symCmt.getString("authorAvatarURL");
            String commentContent = symCmt.getString("content"); // Markdown

            // Step 1: Add comment
            final JSONObject comment = new JSONObject();
            final String commentId = Ids.genTimeMillisId();
            comment.put(Keys.OBJECT_ID, commentId);
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, "");
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_THUMBNAIL_URL, commentThumbnailURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final Date date = new Date();
            comment.put(Comment.COMMENT_CREATED, date.getTime());
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            comment.put(Comment.COMMENT_ON_ID, articleId);
            comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);
            final String commentSharpURL = Comment.getCommentSharpURLForArticle(article, commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            commentRepository.add(comment);
            articleMgmtService.incArticleCommentCount(articleId);
            try {
                final JSONObject preference = preferenceQueryService.getPreference();
                commentMgmtService.sendNotificationMail(article, comment, null, preference);
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Send mail failed", e);
            }
            transaction.commit();

            ret.put(Keys.STATUS_CODE, true);

            final JSONObject eventData = new JSONObject();
            eventData.put(Comment.COMMENT, comment);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventAsynchronously(new Event<>(EventTypes.ADD_COMMENT_TO_ARTICLE_FROM_SYMPHONY, eventData));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false).put(Keys.MSG, e.getMessage());
            context.renderJSON(jsonObject);
        }
    }
}
