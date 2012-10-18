/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.solo.event.symphony;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.event.rhythm.ArticleSender;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONObject;

/**
 * This listener is responsible for sending comment to B3log Symphony.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 18, 2012
 * @since 0.5.5
 */
public final class CommentSender extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentSender.class.getName());
    /**
     * URL fetch service.
     */
    private final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();
    /**
     * B3log Symphony address.
     */
    public static final String B3LOG_SYMPHONY_ADDRESS = "http://symphony.b3log.org:80";
    /**
     * URL of adding comment to Symphony.
     */
    private static final URL ADD_COMMENT_URL;

    static {
        try {
            ADD_COMMENT_URL = new URL(B3LOG_SYMPHONY_ADDRESS + "/solo/comment");
        } catch (final MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Creates remote service address[symphony add comment] error!");
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject data = event.getData();
        LOGGER.log(Level.FINER, "Processing an event[type={0}, data={1}] in listener[className={2}]",
                new Object[]{event.getType(), data, ArticleSender.class.getName()});
        try {
            final JSONObject orginalComment = data.getJSONObject(Comment.COMMENT);

            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                throw new EventException("Not found preference");
            }

            final String blogHost = preference.getString(Preference.BLOG_HOST).toLowerCase();
            if (blogHost.contains("localhost")) {
                LOGGER.log(Level.INFO, "Blog Solo runs on local server, so should not send this comment[id={0}] to Symphony",
                        new Object[]{orginalComment.getString(Keys.OBJECT_ID)});
                return;
            }

            final HTTPRequest httpRequest = new HTTPRequest();
            httpRequest.setURL(ADD_COMMENT_URL);
            httpRequest.setRequestMethod(HTTPRequestMethod.PUT);
            final JSONObject requestJSONObject = new JSONObject();
            final JSONObject comment = new JSONObject();
            comment.put("commentAuthorName", comment.getString(Comment.COMMENT_NAME));
            comment.put("commentAuthorEmail", comment.getString(Comment.COMMENT_EMAIL));
            comment.put(Comment.COMMENT_CONTENT, comment.getString(Comment.COMMENT_CONTENT));
            comment.put("articleId", comment.getString(Comment.COMMENT_ON_ID));

            requestJSONObject.put(Comment.COMMENT, comment);
            requestJSONObject.put(Common.BLOG_VERSION, SoloServletListener.VERSION);
            requestJSONObject.put("runtimeEnv", Latkes.getRuntimeEnv().name());
            requestJSONObject.put(Preference.BLOG_TITLE, preference.getString(Preference.BLOG_TITLE));
            requestJSONObject.put(Preference.BLOG_HOST, blogHost);
            httpRequest.setPayload(requestJSONObject.toString().getBytes("UTF-8"));

            urlFetchService.fetchAsync(httpRequest);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sends an article to Rhythm error: {0}", e.getMessage());
        }

        LOGGER.log(Level.FINER, "Sent an article to Rhythm");
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_ARTICLE}.
     * 
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_ARTICLE;
    }
}
