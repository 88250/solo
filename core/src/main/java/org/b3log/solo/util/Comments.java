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
package org.b3log.solo.util;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Comment utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.11, May 17, 2013
 * @since 0.3.1
 */
public final class Comments {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Comments.class.getName());

    /**
     * Language service.
     */
    private static LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Preference query service.
     */
    private static PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Article repository.
     */
    private static ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Page repository.
     */
    private static PageRepository pageRepository = PageRepositoryImpl.getInstance();

    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

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
     * Comment mail HTML body.
     */
    public static final String COMMENT_MAIL_HTML_BODY = "<p>{articleOrPage} [<a href=\"" + "{articleOrPageURL}\">" + "{title}</a>]"
        + " received a new comment:</p>" + "{commenter}: <span><a href=\"http://{commentSharpURL}\">" + "{commentContent}</a></span>";

    /**
     * Gets comment sharp URL with the specified page and comment id.
     *
     * @param page the specified page
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    public static String getCommentSharpURLForPage(final JSONObject page, final String commentId) throws JSONException {
        return page.getString(Page.PAGE_PERMALINK) + "#" + commentId;
    }

    /**
     * Gets comment sharp URL with the specified article and comment id.
     *
     * @param article the specified article
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    public static String getCommentSharpURLForArticle(final JSONObject article, final String commentId) throws JSONException {
        final String articleLink = article.getString(Article.ARTICLE_PERMALINK);

        return articleLink + "#" + commentId;
    }

    /**
     * Checks the specified comment adding request.
     * 
     * @param requestJSONObject the specified comment adding request, for example, 
     * <pre>
     * {
     *     "type": "", // "article"/"page"
     *     "oId": "",
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "",
     * }
     * </pre>
     * @return check result, for example, 
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": "" // Exists if "sc" equals to false
     * }
     * </pre>
     */
    public static JSONObject checkAddCommentRequest(final JSONObject requestJSONObject) {
        final JSONObject ret = new JSONObject();

        try {
            ret.put(Keys.STATUS_CODE, false);
            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference || !preference.optBoolean(Preference.COMMENTABLE)) {
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
                final JSONObject page = pageRepository.get(id);

                if (null == page || !page.optBoolean(Page.PAGE_COMMENTABLE)) {
                    ret.put(Keys.MSG, langPropsService.get("notAllowCommentLabel"));

                    return ret;
                }
            }

            final String commentName = requestJSONObject.getString(Comment.COMMENT_NAME);

            if (MAX_COMMENT_NAME_LENGTH < commentName.length() || MIN_COMMENT_NAME_LENGTH > commentName.length()) {
                LOGGER.log(Level.WARNING, "Comment name is too long[{0}]", commentName);
                ret.put(Keys.MSG, langPropsService.get("nameTooLongLabel"));

                return ret;
            }

            final String commentEmail = requestJSONObject.getString(Comment.COMMENT_EMAIL).trim().toLowerCase();

            if (!Strings.isEmail(commentEmail)) {
                LOGGER.log(Level.WARNING, "Comment email is invalid[{0}]", commentEmail);
                ret.put(Keys.MSG, langPropsService.get("mailInvalidLabel"));

                return ret;
            }

            final String commentURL = requestJSONObject.optString(Comment.COMMENT_URL);

            if (!Strings.isURL(commentURL)) {
                LOGGER.log(Level.WARNING, "Comment URL is invalid[{0}]", commentURL);
                ret.put(Keys.MSG, langPropsService.get("urlInvalidLabel"));

                return ret;
            }

            final String commentContent = requestJSONObject.optString(Comment.COMMENT_CONTENT).replaceAll("\\n",
                SoloServletListener.ENTER_ESC);

            if (MAX_COMMENT_CONTENT_LENGTH < commentContent.length() || MIN_COMMENT_CONTENT_LENGTH > commentContent.length()) {
                LOGGER.log(Level.WARNING, "Comment conent length is invalid[{0}]", commentContent.length());
                ret.put(Keys.MSG, langPropsService.get("commentContentCannotEmptyLabel"));

                return ret;
            }

            ret.put(Keys.STATUS_CODE, true);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Checks add comment request[" + requestJSONObject.toString() + "] failed", e);

            ret.put(Keys.STATUS_CODE, false);
            ret.put(Keys.MSG, langPropsService.get("addFailLabel"));

            return ret;
        }
    }

    /**
     * Sends a notification mail to administrator for notifying the specified
     * article or page received the specified comment and original comment.
     *
     * @param articleOrPage the specified article or page
     * @param comment the specified comment
     * @param originalComment original comment, if not exists, set it as
     * {@code null}
     * @param preference the specified preference
     * @throws IOException io exception
     * @throws JSONException json exception
     */
    public static void sendNotificationMail(final JSONObject articleOrPage,
        final JSONObject comment,
        final JSONObject originalComment,
        final JSONObject preference)
        throws IOException, JSONException {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String commentId = comment.getString(Keys.OBJECT_ID);
        final String commentContent = comment.getString(Comment.COMMENT_CONTENT).replaceAll(SoloServletListener.ENTER_ESC, "<br/>");

        final String adminEmail = preference.getString(Preference.ADMIN_EMAIL);

        if (adminEmail.equalsIgnoreCase(commentEmail)) {
            LOGGER.log(Level.FINER, "Do not send comment notification mail to admin itself[{0}]", adminEmail);
            return;
        }

        if (null != originalComment && comment.has(Comment.COMMENT_ORIGINAL_COMMENT_ID)) {
            final String originalEmail = originalComment.getString(Comment.COMMENT_EMAIL);

            if (originalEmail.equalsIgnoreCase(adminEmail)) {
                LOGGER.log(Level.FINER, "Do not send comment notification mail to admin while the specified comment[{0}] is an reply",
                    commentId);
                return;
            }
        }

        final String blogTitle = preference.getString(Preference.BLOG_TITLE);
        boolean isArticle = true;
        String title = articleOrPage.optString(Article.ARTICLE_TITLE);

        if (Strings.isEmptyOrNull(title)) {
            title = articleOrPage.getString(Page.PAGE_TITLE);
            isArticle = false;
        }

        final String commentSharpURL = comment.getString(Comment.COMMENT_SHARP_URL);
        final Message message = new Message();

        message.setFrom(adminEmail);
        message.addRecipient(adminEmail);
        String mailSubject;
        String articleOrPageURL;
        String mailBody;

        if (isArticle) {
            mailSubject = blogTitle + ": New comment on article [" + title + "]";
            articleOrPageURL = Latkes.getServePath() + articleOrPage.getString(Article.ARTICLE_PERMALINK);
            mailBody = COMMENT_MAIL_HTML_BODY.replace("{articleOrPage}", "Article");
        } else {
            mailSubject = blogTitle + ": New comment on page [" + title + "]";
            articleOrPageURL = Latkes.getServePath() + articleOrPage.getString(Page.PAGE_PERMALINK);
            mailBody = COMMENT_MAIL_HTML_BODY.replace("{articleOrPage}", "Page");
        }

        message.setSubject(mailSubject);
        final String commentName = comment.getString(Comment.COMMENT_NAME);
        final String commentURL = comment.getString(Comment.COMMENT_URL);
        String commenter;

        if (!"http://".equals(commentURL)) {
            commenter = "<a target=\"_blank\" " + "href=\"" + commentURL + "\">" + commentName + "</a>";
        } else {
            commenter = commentName;
        }

        mailBody = mailBody.replace("{articleOrPageURL}", articleOrPageURL).replace("{title}", title).replace("{commentContent}", commentContent).replace("{commentSharpURL}", Latkes.getServePath() + commentSharpURL).replace(
            "{commenter}", commenter);
        message.setHtmlBody(mailBody);

        LOGGER.log(Level.FINER, "Sending a mail[mailSubject={0}, mailBody=[{1}] to admin[email={2}]",
            new Object[] {mailSubject, mailBody, adminEmail});
        MAIL_SVC.send(message);
    }

    /**
     * Gets the {@link Comments} singleton.
     *
     * @return the singleton
     */
    public static Comments getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Comments() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final Comments SINGLETON = new Comments();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
