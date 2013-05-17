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
package org.b3log.solo.event.comment;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONObject;


/**
 * This listener is responsible for processing page comment reply.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, May 17, 2013
 * @since 0.3.1
 */
public final class PageCommentReplyNotifier extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageCommentReplyNotifier.class.getName());

    /**
     * Comment repository.
     */
    private CommentRepository commentRepository = CommentRepositoryImpl.getInstance();

    /**
     * Mail service.
     */
    private MailService mailService = MailServiceFactory.getMailService();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();
        final JSONObject comment = eventData.optJSONObject(Comment.COMMENT);
        final JSONObject page = eventData.optJSONObject(Page.PAGE);

        LOGGER.log(Level.FINER, "Processing an event[type={0}, data={1}] in listener[className={2}]",
            new Object[] {event.getType(), eventData, PageCommentReplyNotifier.class.getName()});
        final String originalCommentId = comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);

        if (Strings.isEmptyOrNull(originalCommentId)) {
            LOGGER.log(Level.FINER, "This comment[id={0}] is not a reply", comment.optString(Keys.OBJECT_ID));
            return;
        }

        try {
            final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
            final JSONObject originalComment = commentRepository.get(originalCommentId);
            final String originalCommentEmail = originalComment.getString(Comment.COMMENT_EMAIL);

            if (originalCommentEmail.equalsIgnoreCase(commentEmail)) {
                return;
            }

            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                throw new EventException("Not found preference");
            }

            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            final String adminEmail = preference.getString(Preference.ADMIN_EMAIL);

            final String commentContent = comment.getString(Comment.COMMENT_CONTENT).replaceAll(SoloServletListener.ENTER_ESC, "<br/>");
            final String commentSharpURL = comment.getString(Comment.COMMENT_SHARP_URL);
            final Message message = new Message();

            message.setFrom(adminEmail);
            message.addRecipient(originalCommentEmail);
            final JSONObject replyNotificationTemplate = preferenceQueryService.getReplyNotificationTemplate();
            final String mailSubject = replyNotificationTemplate.getString("subject").replace("${blogTitle}", blogTitle);

            message.setSubject(mailSubject);
            final String pageTitle = page.getString(Page.PAGE_TITLE);
            final String pageLink = Latkes.getServePath() + page.getString(Page.PAGE_PERMALINK);
            final String commentName = comment.getString(Comment.COMMENT_NAME);
            final String commentURL = comment.getString(Comment.COMMENT_URL);
            String commenter;

            if (!"http://".equals(commentURL)) {
                commenter = "<a target=\"_blank\" " + "href=\"" + commentURL + "\">" + commentName + "</a>";
            } else {
                commenter = commentName;
            }

            final String mailBody = replyNotificationTemplate.getString("body").replace("${postLink}", pageLink).replace("${postTitle}", pageTitle).replace("${replier}", commenter).replace("${replyURL}", Latkes.getServePath() + commentSharpURL).replace(
                "${replyContent}", commentContent);

            message.setHtmlBody(mailBody);
            LOGGER.log(Level.FINER, "Sending a mail[mailSubject={0}, mailBody=[{1}] to [{2}]",
                new Object[] {mailSubject, mailBody, originalCommentEmail});
            mailService.send(message);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException("Reply notifier error!");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_COMMENT_TO_PAGE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_COMMENT_TO_PAGE;
    }
}
