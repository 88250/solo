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
package org.b3log.solo.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.util.Markdowns;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Comment management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.4.0.3, Jun 6, 2019
 * @since 0.3.5
 */
@Service
public class CommentMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CommentMgmtService.class);

    /**
     * Minimum length of comment name.
     */
    private static final int MIN_COMMENT_NAME_LENGTH = 2;

    /**
     * Maximum length of comment name.
     */
    private static final int MAX_COMMENT_NAME_LENGTH = 20;

    /**
     * Minimum length of comment content.
     */
    private static final int MIN_COMMENT_CONTENT_LENGTH = 2;

    /**
     * Maximum length of comment content.
     */
    private static final int MAX_COMMENT_CONTENT_LENGTH = 500;

    /**
     * Event manager.
     */
    @Inject
    private static EventManager eventManager;

    /**
     * Article management service.
     */
    @Inject
    private ArticleMgmtService articleMgmtService;

    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;

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
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Checks the specified comment adding request.
     * <p>
     * XSS process (name) in this method.
     * </p>
     *
     * @param requestJSONObject the specified comment adding request, for example,
     *                          {
     *                          "type": "", // "article"
     *                          "oId": "",
     *                          "commentName": "",
     *                          "commentURL": "",
     *                          "commentContent": "",
     *                          }
     * @return check result, for example, <pre>
     * {
     *     "sc": boolean,
     *     "msg": "" // Exists if "sc" equals to false
     * }
     * </pre>
     */
    public JSONObject checkAddCommentRequest(final JSONObject requestJSONObject) {
        final JSONObject ret = new JSONObject();

        try {
            ret.put(Keys.STATUS_CODE, false);
            final JSONObject preference = optionQueryService.getPreference();

            if (null == preference || !preference.optBoolean(Option.ID_C_COMMENTABLE)) {
                ret.put(Keys.MSG, langPropsService.get("notAllowCommentLabel"));

                return ret;
            }

            final String id = requestJSONObject.optString(Keys.OBJECT_ID);
            final String type = requestJSONObject.optString(Common.TYPE);

            if (Article.ARTICLE.equals(type)) {
                final JSONObject article = articleRepository.get(id);

                if (null == article || !article.optBoolean(Article.ARTICLE_COMMENTABLE)) {
                    ret.put(Keys.MSG, langPropsService.get("notAllowCommentLabel"));

                    return ret;
                }
            } else {
                ret.put(Keys.MSG, langPropsService.get("notAllowCommentLabel"));

                return ret;
            }

            String commentName = requestJSONObject.getString(Comment.COMMENT_NAME);
            if (MAX_COMMENT_NAME_LENGTH < commentName.length() || MIN_COMMENT_NAME_LENGTH > commentName.length()) {
                LOGGER.log(Level.WARN, "Comment name is too long [{}]", commentName);
                ret.put(Keys.MSG, langPropsService.get("nameTooLongLabel"));

                return ret;
            }

            final JSONObject commenter = userRepository.getByUserName(commentName);
            if (null == commenter) {
                LOGGER.log(Level.WARN, "Not found user [" + commentName + "]");
                ret.put(Keys.MSG, langPropsService.get("queryUserFailedLabel"));

                return ret;
            }

            final String commentURL = requestJSONObject.optString(Comment.COMMENT_URL);

            if (!Strings.isURL(commentURL) || StringUtils.contains(commentURL, "<")) {
                requestJSONObject.put(Comment.COMMENT_URL, "");
            }

            String commentContent = requestJSONObject.optString(Comment.COMMENT_CONTENT);

            if (MAX_COMMENT_CONTENT_LENGTH < commentContent.length() || MIN_COMMENT_CONTENT_LENGTH > commentContent.length()) {
                LOGGER.log(Level.WARN, "Comment conent length is invalid[{}]", commentContent.length());
                ret.put(Keys.MSG, langPropsService.get("commentContentCannotEmptyLabel"));

                return ret;
            }

            ret.put(Keys.STATUS_CODE, true);
            requestJSONObject.put(Comment.COMMENT_CONTENT, commentContent);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Checks add comment request[" + requestJSONObject.toString() + "] failed", e);

            ret.put(Keys.STATUS_CODE, false);
            ret.put(Keys.MSG, langPropsService.get("addFailLabel"));

            return ret;
        }
    }

    /**
     * Adds an article comment with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "oId": "", // article id
     *                          "commentName": "",
     *                          "commentURL": "", // optional
     *                          "commentContent": "",
     *                          "commentOriginalCommentId": "" // optional
     *                          }
     * @return add result, for example,      <pre>
     * {
     *     "oId": "", // generated comment id
     *     "commentDate": "", // format: yyyy-MM-dd HH:mm:ss
     *     "commentOriginalCommentName": "" // optional, corresponding to argument "commentOriginalCommentId"
     *     "commentThumbnailURL": "",
     *     "commentSharpURL": "",
     *     "commentContent": "",
     *     "commentName": "",
     *     "commentURL": "", // optional
     *     "isReply": boolean,
     *     "article": {},
     *     "commentOriginalCommentId": "", // optional
     *     "commentable": boolean,
     *     "permalink": "" // article.articlePermalink
     * }
     * </pre>
     * @throws ServiceException service exception
     */
    public JSONObject addArticleComment(final JSONObject requestJSONObject) throws ServiceException {
        final JSONObject ret = new JSONObject();
        ret.put(Common.IS_REPLY, false);

        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject article = articleRepository.get(articleId);
            ret.put(Article.ARTICLE, article);
            final String commentName = requestJSONObject.getString(Comment.COMMENT_NAME);
            final String commentURL = requestJSONObject.optString(Comment.COMMENT_URL);
            final String commentContent = requestJSONObject.getString(Comment.COMMENT_CONTENT);
            final String originalCommentId = requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
            ret.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, originalCommentId);
            final JSONObject comment = new JSONObject();
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID));
            comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, requestJSONObject.optString(Comment.COMMENT_ORIGINAL_COMMENT_NAME));
            final JSONObject preference = optionQueryService.getPreference();
            final Date date = new Date();
            comment.put(Comment.COMMENT_CREATED, date.getTime());
            ret.put(Comment.COMMENT_T_DATE, DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
            ret.put("commentDate2", date);
            ret.put(Common.COMMENTABLE, preference.getBoolean(Option.ID_C_COMMENTABLE) && article.getBoolean(Article.ARTICLE_COMMENTABLE));
            ret.put(Common.PERMALINK, article.getString(Article.ARTICLE_PERMALINK));
            ret.put(Comment.COMMENT_NAME, commentName);
            String cmtContent = Markdowns.toHTML(commentContent);
            cmtContent = Markdowns.clean(cmtContent);
            ret.put(Comment.COMMENT_CONTENT, cmtContent);
            ret.put(Comment.COMMENT_URL, commentURL);

            JSONObject originalComment;
            if (StringUtils.isNotBlank(originalCommentId)) {
                originalComment = commentRepository.get(originalCommentId);
                if (null != originalComment) {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, originalCommentId);
                    final String originalCommentName = originalComment.getString(Comment.COMMENT_NAME);

                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, originalCommentName);
                    ret.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, originalCommentName);

                    ret.put(Common.IS_REPLY, true);
                } else {
                    LOGGER.log(Level.WARN, "Not found orginal comment[id={}] of reply[name={}, content={}]",
                            originalCommentId, commentName, commentContent);
                }
            }
            setCommentThumbnailURL(comment);
            ret.put(Comment.COMMENT_THUMBNAIL_URL, comment.getString(Comment.COMMENT_THUMBNAIL_URL));
            comment.put(Comment.COMMENT_ON_ID, articleId);
            final String commentId = Ids.genTimeMillisId();
            comment.put(Keys.OBJECT_ID, commentId);
            ret.put(Keys.OBJECT_ID, commentId);
            final String commentSharpURL = Comment.getCommentSharpURLForArticle(article, commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            ret.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

            commentRepository.add(comment);
            articleMgmtService.incArticleCommentCount(articleId);

            final JSONObject eventData = new JSONObject();
            eventData.put(Comment.COMMENT, comment);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventAsynchronously(new Event<>(EventTypes.ADD_COMMENT_TO_ARTICLE, eventData));

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
     * Removes a comment of an article with the specified comment id.
     *
     * @param commentId the given comment id
     * @throws ServiceException service exception
     */
    public void removeArticleComment(final String commentId) throws ServiceException {
        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final JSONObject comment = commentRepository.get(commentId);
            final String articleId = comment.getString(Comment.COMMENT_ON_ID);
            commentRepository.remove(commentId);
            decArticleCommentCount(articleId);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Removes a comment of an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Article comment count -1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException       json exception
     * @throws RepositoryException repository exception
     */
    private void decArticleCommentCount(final String articleId) throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle = new JSONObject(article, JSONObject.getNames(article));
        final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt - 1);
        articleRepository.update(articleId, newArticle, Article.ARTICLE_COMMENT_COUNT);
    }

    /**
     * Sets commenter thumbnail URL for the specified comment.
     *
     * @param comment the specified comment
     * @throws Exception exception
     */
    public void setCommentThumbnailURL(final JSONObject comment) throws Exception {
        final String commenterName = comment.optString(Comment.COMMENT_NAME);
        final JSONObject commenter = userRepository.getByUserName(commenterName);
        final String avatarURL = commenter.optString(UserExt.USER_AVATAR);
        comment.put(Comment.COMMENT_THUMBNAIL_URL, avatarURL);
    }
}
