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
package org.b3log.solo.service;


import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.urlfetch.*;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Comments;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.TimeZones;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Comment management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jan 18, 2013
 * @since 0.3.5
 */
public final class CommentMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentMgmtService.class.getName());

    /**
     * Comment repository.
     */
    private CommentRepository commentRepository = CommentRepositoryImpl.getInstance();

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();

    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * Preference query service.
     */
    private static PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Event manager.
     */
    private static EventManager eventManager = EventManager.getInstance();

    /**
     * Default user thumbnail.
     */
    private static final String DEFAULT_USER_THUMBNAIL = "default-user-thumbnail.png";

    /**
     * URL fetch service.
     */
    private static URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();

    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();

    /**
     * Adds page comment with the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "", // page id
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "", // optional
     *     "commentContent": "",
     *     "commentOriginalCommentId": "" // optional
     * }
     * </pre>
     * @return add result, for example,
     * <pre>
     * {
     *     "oId": "", // generated comment id
     *     "commentDate": "", // format: yyyy-MM-dd hh:mm:ss
     *     "commentOriginalCommentName": "" // optional, corresponding to argument "commentOriginalCommentId"
     *     "commentThumbnailURL": "",
     *     "commentSharpURL": ""
     * }
     * </pre>
     * @throws ServiceException service exception
     */
    public JSONObject addPageComment(final JSONObject requestJSONObject) throws ServiceException {
        final JSONObject ret = new JSONObject();

        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final String pageId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject page = pageRepository.get(pageId);
            final String commentName = requestJSONObject.getString(Comment.COMMENT_NAME);
            final String commentEmail = requestJSONObject.getString(Comment.COMMENT_EMAIL).trim().toLowerCase();
            final String commentURL = requestJSONObject.optString(Comment.COMMENT_URL);
            String commentContent = requestJSONObject.getString(Comment.COMMENT_CONTENT).replaceAll("\\n", SoloServletListener.ENTER_ESC);

            commentContent = StringEscapeUtils.escapeHtml(commentContent);
            final String originalCommentId = requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();

            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");

            JSONObject originalComment = null;

            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final JSONObject preference = preferenceQueryService.getPreference();
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
                    LOGGER.log(Level.WARNING, "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                        new String[] {originalCommentId, commentName, commentContent});
                }
            }
            setCommentThumbnailURL(comment);
            ret.put(Comment.COMMENT_THUMBNAIL_URL, comment.getString(Comment.COMMENT_THUMBNAIL_URL));
            // Sets comment on page....
            comment.put(Comment.COMMENT_ON_ID, pageId);
            comment.put(Comment.COMMENT_ON_TYPE, Page.PAGE);
            final String commentId = Ids.genTimeMillisId();

            ret.put(Keys.OBJECT_ID, commentId);
            // Save comment sharp URL
            final String commentSharpURL = Comments.getCommentSharpURLForPage(page, commentId);

            ret.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Keys.OBJECT_ID, commentId);
            commentRepository.add(comment);
            // Step 2: Update page comment count
            incPageCommentCount(pageId);
            // Step 3: Update blog statistic comment count
            statistics.incBlogCommentCount();
            statistics.incPublishedBlogCommentCount();
            // Step 4: Send an email to admin
            try {
                Comments.sendNotificationMail(page, comment, originalComment, preference);
            } catch (final Exception e) {
                LOGGER.log(Level.WARNING, "Send mail failed", e);
            }
            // Step 5: Fire add comment event
            final JSONObject eventData = new JSONObject();

            eventData.put(Comment.COMMENT, comment);
            eventData.put(Page.PAGE, page);
            eventManager.fireEventSynchronously(new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_PAGE, eventData));

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }

        return ret;
    }

    /**
     * Adds an article comment with the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example, 
     * <pre>
     * {
     *     "oId": "", // article id
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "", // optional
     *     "commentContent": "",
     *     "commentOriginalCommentId": "" // optional
     * }
     * </pre>
     * @return add result, for example,
     * <pre>
     * {
     *     "oId": "", // generated comment id
     *     "commentDate": "", // format: yyyy-MM-dd hh:mm:ss
     *     "commentOriginalCommentName": "" // optional, corresponding to argument "commentOriginalCommentId"
     *     "commentThumbnailURL": "",
     *     "commentSharpURL": ""
     * }
     * </pre>
     * @throws ServiceException service exception
     */
    public JSONObject addArticleComment(final JSONObject requestJSONObject) throws ServiceException {
        final JSONObject ret = new JSONObject();

        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject article = articleRepository.get(articleId);
            final String commentName = requestJSONObject.getString(Comment.COMMENT_NAME);
            final String commentEmail = requestJSONObject.getString(Comment.COMMENT_EMAIL).trim().toLowerCase();
            final String commentURL = requestJSONObject.optString(Comment.COMMENT_URL);
            String commentContent = requestJSONObject.getString(Comment.COMMENT_CONTENT).replaceAll("\\n", SoloServletListener.ENTER_ESC);
            final String contentNoEsc = commentContent;

            commentContent = StringEscapeUtils.escapeHtml(commentContent);
            final String originalCommentId = requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();

            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");

            JSONObject originalComment = null;

            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID));
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_NAME));
            final JSONObject preference = preferenceQueryService.getPreference();
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
                    LOGGER.log(Level.WARNING, "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                        new String[] {originalCommentId, commentName, commentContent});
                }
            }
            CommentMgmtService.setCommentThumbnailURL(comment);
            ret.put(Comment.COMMENT_THUMBNAIL_URL, comment.getString(Comment.COMMENT_THUMBNAIL_URL));
            // Sets comment on article....
            comment.put(Comment.COMMENT_ON_ID, articleId);
            comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);
            final String commentId = Ids.genTimeMillisId();

            comment.put(Keys.OBJECT_ID, commentId);
            ret.put(Keys.OBJECT_ID, commentId);
            final String commentSharpURL = Comments.getCommentSharpURLForArticle(article, commentId);

            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            ret.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

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
            comment.put(Comment.COMMENT_CONTENT, contentNoEsc);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventSynchronously(new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_ARTICLE, eventData));

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }

        return ret;
    }

    /**
     * Removes a comment of a page with the specified comment id.
     * 
     * @param commentId the given comment id
     * @throws ServiceException service exception
     */
    public void removePageComment(final String commentId)
        throws ServiceException {
        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final JSONObject comment = commentRepository.get(commentId);
            final String pageId = comment.getString(Comment.COMMENT_ON_ID);

            // Step 1: Remove comment
            commentRepository.remove(commentId);
            // Step 2: Update page comment count
            decPageCommentCount(pageId);
            // Step 3: Update blog statistic comment count
            statistics.decBlogCommentCount();
            statistics.decPublishedBlogCommentCount();

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Removes a comment of a page failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Removes a comment of an article with the specified comment id.
     *
     * @param commentId the given comment id
     * @throws ServiceException service exception
     */
    public void removeArticleComment(final String commentId)
        throws ServiceException {
        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final JSONObject comment = commentRepository.get(commentId);
            final String articleId = comment.getString(Comment.COMMENT_ON_ID);

            // Step 1: Remove comment
            commentRepository.remove(commentId);
            // Step 2: Update article comment count
            decArticleCommentCount(articleId);
            // Step 3: Update blog statistic comment count
            statistics.decBlogCommentCount();
            statistics.decPublishedBlogCommentCount();

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Removes a comment of an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Page comment count +1 for an page specified by the given page 
     * id.
     *
     * @param pageId the given page id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incPageCommentCount(final String pageId)
        throws JSONException, RepositoryException {
        final JSONObject page = pageRepository.get(pageId);
        final JSONObject newPage = new JSONObject(page, JSONObject.getNames(page));
        final int commentCnt = page.getInt(Page.PAGE_COMMENT_COUNT);

        newPage.put(Page.PAGE_COMMENT_COUNT, commentCnt + 1);
        pageRepository.update(pageId, newPage);
    }

    /**
     * Article comment count -1 for an article specified by the given 
     * article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    private void decArticleCommentCount(final String articleId)
        throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle = new JSONObject(article, JSONObject.getNames(article));
        final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);

        newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt - 1);

        articleRepository.update(articleId, newArticle);
    }

    /**
     * Page comment count -1 for an page specified by the given page id.
     *
     * @param pageId the given page id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    private void decPageCommentCount(final String pageId)
        throws JSONException, RepositoryException {
        final JSONObject page = pageRepository.get(pageId);
        final JSONObject newPage = new JSONObject(page, JSONObject.getNames(page));
        final int commentCnt = page.getInt(Page.PAGE_COMMENT_COUNT);

        newPage.put(Page.PAGE_COMMENT_COUNT, commentCnt - 1);
        pageRepository.update(pageId, newPage);
    }

    /**
     * Sets commenter thumbnail URL for the specified comment.
     * 
     * <p>
     * Try to set thumbnail URL using Gravatar service.
     * </p>
     *
     * @param comment the specified comment
     * @throws Exception exception
     */
    public static void setCommentThumbnailURL(final JSONObject comment) throws Exception {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);

        final String hashedEmail = MD5.hash(commentEmail.toLowerCase());
        String thumbnailURL = "http://secure.gravatar.com/avatar/" + hashedEmail + "?s=60&d=" + Latkes.getStaticServePath()
            + "/images/default-user-thumbnail.png";

        final URL gravatarURL = new URL(thumbnailURL);

        int statusCode = HttpServletResponse.SC_OK;

        try {
            final HTTPRequest request = new HTTPRequest();

            request.setURL(gravatarURL);
            final HTTPResponse response = urlFetchService.fetch(request);

            statusCode = response.getResponseCode();
        } catch (final IOException e) {
            LOGGER.log(Level.WARNING, "Can not fetch thumbnail from Gravatar[commentEmail={0}]", commentEmail);
        } finally {
            if (HttpServletResponse.SC_OK != statusCode) {
                thumbnailURL = Latkes.getStaticServePath() + "/images/" + DEFAULT_USER_THUMBNAIL;
            }
        }

        comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
    }

    /**
     * Gets the {@link CommentMgmtService} singleton.
     *
     * @return the singleton
     */
    public static CommentMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private CommentMgmtService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 18, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final CommentMgmtService SINGLETON = new CommentMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
