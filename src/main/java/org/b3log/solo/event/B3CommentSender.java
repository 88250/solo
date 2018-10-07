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
package org.b3log.solo.event;

import jodd.http.HttpRequest;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

/**
 * This listener is responsible for sending comment to B3log Symphony. Sees <a href="https://hacpai.com/b3log">B3log 构思</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.3, Sep 25, 2018
 * @since 0.5.5
 */
@Singleton
public class B3CommentSender extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(B3CommentSender.class);

    /**
     * URL of adding comment to Symphony.
     */
    private static final String ADD_COMMENT_URL = Solos.B3LOG_SYMPHONY_SERVE_PATH + "/solo/comment";

    @Override
    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();

        LOGGER.log(Level.DEBUG, "Processing an event [type={0}, data={1}] in listener [className={2}]",
                event.getType(), data, B3ArticleSender.class.getName());
        try {
            final JSONObject originalComment = data.getJSONObject(Comment.COMMENT);

            final BeanManager beanManager = BeanManager.getInstance();
            final PreferenceQueryService preferenceQueryService = beanManager.getReference(PreferenceQueryService.class);

            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                throw new EventException("Not found preference");
            }

            if (Latkes.getServePath().contains("localhost") || Strings.isIPv4(Latkes.getServePath())) {
                LOGGER.log(Level.TRACE, "Solo runs on local server, so should not send this comment[id={0}] to Symphony",
                        originalComment.getString(Keys.OBJECT_ID));
                return;
            }

            final JSONObject requestJSONObject = new JSONObject();
            final JSONObject comment = new JSONObject();

            comment.put("commentId", originalComment.optString(Keys.OBJECT_ID));
            comment.put("commentAuthorName", originalComment.getString(Comment.COMMENT_NAME));
            comment.put("commentAuthorEmail", originalComment.getString(Comment.COMMENT_EMAIL));
            comment.put(Comment.COMMENT_CONTENT, originalComment.getString(Comment.COMMENT_CONTENT));
            comment.put("articleId", originalComment.getString(Comment.COMMENT_ON_ID));

            requestJSONObject.put(Comment.COMMENT, comment);
            requestJSONObject.put("clientVersion", SoloServletListener.VERSION);
            requestJSONObject.put("clientRuntimeEnv", "LOCAL");
            requestJSONObject.put("clientName", "Solo");
            requestJSONObject.put("clientHost", Latkes.getServePath());
            requestJSONObject.put("clientAdminEmail", preference.optString(Option.ID_C_ADMIN_EMAIL));
            requestJSONObject.put("userB3Key", preference.optString(Option.ID_C_KEY_OF_SOLO));

            HttpRequest.post(ADD_COMMENT_URL).bodyText(requestJSONObject.toString()).
                    header("User-Agent", Solos.USER_AGENT).contentTypeJson().sendAsync();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends a comment to Symphony error: {0}", e.getMessage());
        }

        LOGGER.log(Level.DEBUG, "Sent a comment to Symphony");
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_COMMENT_TO_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_COMMENT_TO_ARTICLE;
    }
}
