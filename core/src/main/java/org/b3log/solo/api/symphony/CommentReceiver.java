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


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.urlfetch.HTTPHeader;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Comments;
import org.b3log.solo.util.QueryResults;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.TimeZones;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Comment receiver (from B3log Symphony).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Mar 18, 2013
 * @since 0.5.5
 */
@RequestProcessor
public final class CommentReceiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentReceiver.class.getName());

    /**
     * Comment repository.
     */
    private static CommentRepository commentRepository = CommentRepositoryImpl.getInstance();

    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Article repository.
     */
    private static ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Statistic utilities.
     */
    private static Statistics statistics = Statistics.getInstance();

    /**
     * Default user thumbnail.
     */
    private static final String DEFAULT_USER_THUMBNAIL = "default-user-thumbnail.png";

    /**
     * URL fetch service.
     */
    private static URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();

    /**
     * Event manager.
     */
    private static EventManager eventManager = EventManager.getInstance();

    /**
     * Adds an article with the specified request.
     *
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": true
     * }
     * </pre>
     * </p>
     *
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "comment": {
     *         "userB3Key": "",
     *         "oId": "",
     *         "commentSymphonyArticleId": "",
     *         "commentOnArticleId": "",
     *         "commentAuthorName": "",
     *         "commentAuthorEmail": "",
     *         "commentAuthorURL": "",
     *         "commentContent": "",
     *         "commentOriginalCommentId": "" // optional, if exists this key, the comment is an reply
     *     }
     * }
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/apis/symphony/comment", method = HTTPRequestMethod.PUT)
    public void addComment(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
        throws Exception {
        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();

        renderer.setJSONObject(ret);

        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);
            final JSONObject symphonyCmt = requestJSONObject.optJSONObject(Comment.COMMENT);
            final JSONObject preference = preferenceQueryService.getPreference();
            final String keyOfSolo = preference.optString(Preference.KEY_OF_SOLO);
            final String key = symphonyCmt.optString("userB3Key");

            if (Strings.isEmptyOrNull(keyOfSolo) || !keyOfSolo.equals(key)) {
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
                LOGGER.log(Level.WARNING, "The comment URL is invalid [{0}]", commentURL);
                commentURL = "";
            }

            final String commentId = symphonyCmt.optString(Keys.OBJECT_ID);
            String commentContent = symphonyCmt.getString(Comment.COMMENT_CONTENT);

            commentContent += "<br/><br/><p style='font-size: 12px;'><i>该评论同步自 <a href='http://symphony.b3log.org/article/"
                + symphonyCmt.optString("commentSymphonyArticleId") + "#" + commentId + "' target='_blank'>B3log 社区</a></i></p>";
            final String originalCommentId = symphonyCmt.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();
            JSONObject originalComment = null;

            comment.put(Keys.OBJECT_ID, commentId);
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final String timeZoneId = preference.getString(Preference.TIME_ZONE_ID);
            final Date date = TimeZones.getTime(timeZoneId);

            comment.put(Comment.COMMENT_DATE, date);
            ret.put(Comment.COMMENT_DATE, DateFormatUtils.format(date, "yyyy-MM-dd hh:mm:ss"));
            if (!Strings.isEmptyOrNull(originalCommentId)) {
                originalComment = commentRepository.get(originalCommentId);
                if (null != originalComment) {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, originalCommentId);
                    final String originalCommentName = originalComment.getString(Comment.COMMENT_NAME);

                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, originalCommentName);
                    ret.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, originalCommentName);
                } else {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
                    LOGGER.log(Level.WARNING, "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                        new String[] {originalCommentId, commentName, commentContent});
                }
            } else {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            }

            setCommentThumbnailURL(comment);
            ret.put(Comment.COMMENT_THUMBNAIL_URL, comment.getString(Comment.COMMENT_THUMBNAIL_URL));
            // Sets comment on article....
            comment.put(Comment.COMMENT_ON_ID, articleId);
            comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);

            final String commentSharpURL = getCommentSharpURLForArticle(article, commentId);

            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

            commentRepository.add(comment);
            // Step 2: Update article comment count
            articleUtils.incArticleCommentCount(articleId);
            // Step 3: Update blog statistic comment count
            statistics.incBlogCommentCount();
            statistics.incPublishedBlogCommentCount();
            // Step 4: Send an email to admin
            try {
                Comments.sendNotificationMail(article, comment, originalComment, preference);
            } catch (final Exception e) {
                LOGGER.log(Level.WARNING, "Send mail failed", e);
            }
            // Step 5: Fire add comment event
            final JSONObject eventData = new JSONObject();

            eventData.put(Comment.COMMENT, comment);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventSynchronously(new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_ARTICLE_FROM_SYMPHONY, eventData));

            transaction.commit();
            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.OBJECT_ID, commentId);

            ret.put(Keys.OBJECT_ID, articleId);
            ret.put(Keys.MSG, "add a comment to an article from symphony succ");
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
     * Gets comment sharp URL with the specified article and comment id.
     *
     * @param article the specified article
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    private static String getCommentSharpURLForArticle(final JSONObject article,
        final String commentId)
        throws JSONException {
        final String articleLink = article.getString(Article.ARTICLE_PERMALINK);

        return articleLink + "#" + commentId;
    }

    /**
     * Sets commenter thumbnail URL for the specified comment.
     *
     * @param comment the specified comment
     * @throws Exception exception
     */
    private static void setCommentThumbnailURL(final JSONObject comment)
        throws Exception {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String id = commentEmail.split("@")[0];
        final String domain = commentEmail.split("@")[1];
        String thumbnailURL = null;

        // Try to set thumbnail URL using Gravatar service
        final String hashedEmail = MD5.hash(commentEmail.toLowerCase());
        final int size = 60;
        final URL gravatarURL = new URL(
            "http://secure.gravatar.com/avatar/" + hashedEmail + "?s=" + size + "&d=" + Latkes.getServePath()
            + "/images/default-user-thumbnail.png");

        try {
            final HTTPRequest request = new HTTPRequest();

            request.setURL(gravatarURL);
            final HTTPResponse response = urlFetchService.fetch(request);
            final int statusCode = response.getResponseCode();

            if (HttpServletResponse.SC_OK == statusCode) {
                final List<HTTPHeader> headers = response.getHeaders();
                boolean defaultFileLengthMatched = false;

                for (final HTTPHeader httpHeader : headers) {
                    if ("Content-Length".equalsIgnoreCase(httpHeader.getName())) {
                        if (httpHeader.getValue().equals("2147")) {
                            defaultFileLengthMatched = true;
                        }
                    }
                }

                if (!defaultFileLengthMatched) {
                    thumbnailURL = "http://secure.gravatar.com/avatar/" + hashedEmail + "?s=" + size + "&d=" + Latkes.getServePath()
                        + "/images/default-user-thumbnail.png";
                    comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
                    LOGGER.log(Level.FINEST, "Comment thumbnail[URL={0}]", thumbnailURL);

                    return;
                }
            } else {
                LOGGER.log(Level.WARNING, "Can not fetch thumbnail from Gravatar[commentEmail={0}, statusCode={1}]",
                    new Object[] {commentEmail, statusCode});
            }
        } catch (final IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.log(Level.WARNING, "Can not fetch thumbnail from Gravatar[commentEmail={0}]", commentEmail);
        }

        if (null == thumbnailURL) {
            LOGGER.log(Level.WARNING, "Not supported yet for comment thumbnail for email[{0}]", commentEmail);
            thumbnailURL = "/images/" + DEFAULT_USER_THUMBNAIL;
            comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
        }
    }
}
