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
package org.b3log.solo.api;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Comment receiver from B3log Symphony.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.18, Sep 25, 2018
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
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": true
     * }
     * </pre>
     * </p>
     *
     * @param context           the specified http request context
     * @param requestJSONObject the specified http servlet request, for example,
     *                          {
     *                          "comment": {
     *                          "userB3Key": "",
     *                          "oId": "",
     *                          "commentSymphonyArticleId": "",
     *                          "commentOnArticleId": "",
     *                          "commentAuthorName": "",
     *                          "commentAuthorEmail": "",
     *                          "commentAuthorURL": "",
     *                          "commentAuthorThumbnailURL": "",
     *                          "commentContent": "",
     *                          "commentOriginalCommentId": "" // optional, if exists this key, the comment is an reply
     *                          }
     *                          }
     * @throws Exception exception
     */
    @RequestProcessing(value = "/apis/symphony/comment", method = HTTPRequestMethod.PUT)
    public void addComment(final HTTPRequestContext context, final JSONObject requestJSONObject)
            throws Exception {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        final Transaction transaction = commentRepository.beginTransaction();
        try {
            final JSONObject symphonyCmt = requestJSONObject.optJSONObject(Comment.COMMENT);
            final JSONObject preference = preferenceQueryService.getPreference();
            final String keyOfSolo = preference.optString(Option.ID_C_KEY_OF_SOLO);
            final String key = symphonyCmt.optString("userB3Key");

            if (StringUtils.isBlank(keyOfSolo) || !keyOfSolo.equals(key)) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
                ret.put(Keys.MSG, "Wrong key");

                return;
            }

            final String articleId = symphonyCmt.getString("commentOnArticleId");
            final JSONObject article = articleRepository.get(articleId);

            if (null == article) {
                ret.put(Keys.STATUS_CODE, HttpServletResponse.SC_NOT_FOUND);
                ret.put(Keys.MSG, "Not found the specified article[id=" + articleId + "]");

                return;
            }

            final String commentName = symphonyCmt.getString("commentAuthorName");
            final String commentEmail = symphonyCmt.getString("commentAuthorEmail").trim().toLowerCase();
            String commentURL = symphonyCmt.optString("commentAuthorURL");
            if (!commentURL.contains("://")) {
                commentURL = "http://" + commentURL;
            }
            try {
                new URL(commentURL);
            } catch (final MalformedURLException e) {
                LOGGER.log(Level.WARN, "The comment URL is invalid [{0}]", commentURL);
                commentURL = "";
            }
            final String commentThumbnailURL = symphonyCmt.getString("commentAuthorThumbnailURL");

            final String commentId = symphonyCmt.optString(Keys.OBJECT_ID);
            String commentContent = symphonyCmt.getString(Comment.COMMENT_CONTENT);

//            commentContent += "<p class='cmtFromSym'><i>该评论同步自 <a href='" + SoloServletListener.B3LOG_SYMPHONY_SERVE_PATH
//                    + "/article/" + symphonyCmt.optString("commentSymphonyArticleId") + "#" + commentId
//                    + "' target='_blank'>黑客派</a></i></p>";
            final String originalCommentId = symphonyCmt.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();
            JSONObject originalComment = null;

            comment.put(Keys.OBJECT_ID, commentId);
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_THUMBNAIL_URL, commentThumbnailURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final Date date = new Date();

            comment.put(Comment.COMMENT_CREATED, date.getTime());
            ret.put(Comment.COMMENT_T_DATE, DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
            if (StringUtils.isNotBlank(originalCommentId)) {
                originalComment = commentRepository.get(originalCommentId);
                if (null != originalComment) {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, originalCommentId);
                    final String originalCommentName = originalComment.getString(Comment.COMMENT_NAME);

                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, originalCommentName);
                    ret.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, originalCommentName);
                } else {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
                    LOGGER.log(Level.WARN, "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                            originalCommentId, commentName, commentContent);
                }
            } else {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            }

            ret.put(Comment.COMMENT_THUMBNAIL_URL, comment.getString(Comment.COMMENT_THUMBNAIL_URL));
            // Sets comment on article....
            comment.put(Comment.COMMENT_ON_ID, articleId);
            comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);
            final String commentSharpURL = Comment.getCommentSharpURLForArticle(article, commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

            commentRepository.add(comment);
            // Step 2: Update article comment count
            articleMgmtService.incArticleCommentCount(articleId);
            // Step 3: Update blog statistic comment count
            statisticMgmtService.incBlogCommentCount();
            statisticMgmtService.incPublishedBlogCommentCount();
            // Step 4: Send an email to admin
            try {
                commentMgmtService.sendNotificationMail(article, comment, originalComment, preference);
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Send mail failed", e);
            }
            // Step 5: Fire add comment event
            final JSONObject eventData = new JSONObject();

            eventData.put(Comment.COMMENT, comment);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventSynchronously(new Event<>(EventTypes.ADD_COMMENT_TO_ARTICLE_FROM_SYMPHONY, eventData));

            transaction.commit();
            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.OBJECT_ID, commentId);

            ret.put(Keys.OBJECT_ID, articleId);
            ret.put(Keys.MSG, "add a comment to an article from symphony succ");
            ret.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
