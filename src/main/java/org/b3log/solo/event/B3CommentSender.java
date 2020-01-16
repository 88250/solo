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
package org.b3log.solo.event;

import jodd.http.HttpRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.solo.Server;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

/**
 * This listener is responsible for sending comment to B3log Rhythm. Sees <a href="https://hacpai.com/b3log">B3log 构思</a> for more details.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.7, Jan 1, 2020
 * @since 0.5.5
 */
@Singleton
public class B3CommentSender extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(B3CommentSender.class);

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    @Override
    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();

        LOGGER.log(Level.DEBUG, "Processing an event [type={}, data={}] in listener [className={}]",
                event.getType(), data, B3ArticleSender.class.getName());
        try {
            final JSONObject originalComment = data.getJSONObject(Comment.COMMENT);

            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                LOGGER.log(Level.ERROR, "Not found preference");

                return;
            }

            if (Solos.isLocalServer()) {
                return;
            }

            final String articleId = originalComment.getString(Comment.COMMENT_ON_ID);
            final JSONObject article = articleRepository.get(articleId);
            final String articleAuthorId = article.optString(Article.ARTICLE_AUTHOR_ID);
            final JSONObject articleAuthor = userRepository.get(articleAuthorId);

            final JSONObject comment = new JSONObject().
                    put("id", originalComment.optString(Keys.OBJECT_ID)).
                    put("articleId", articleId).
                    put("content", originalComment.getString(Comment.COMMENT_CONTENT)).
                    put("authorName", originalComment.optString(Comment.COMMENT_NAME));
            final JSONObject client = new JSONObject().
                    put("title", preference.getString(Option.ID_C_BLOG_TITLE)).
                    put("host", Latkes.getServePath()).
                    put("name", "Solo").
                    put("ver", Server.VERSION).
                    put("userName", articleAuthor.optString(User.USER_NAME)).
                    put("userB3Key", articleAuthor.optString(UserExt.USER_B3_KEY));
            final JSONObject requestJSONObject = new JSONObject().
                    put("comment", comment).
                    put("client", client);

            HttpRequest.post("https://rhythm.b3log.org/api/comment").bodyText(requestJSONObject.toString()).
                    connectionTimeout(3000).timeout(7000).trustAllCerts(true).
                    header("User-Agent", Solos.USER_AGENT).contentTypeJson().sendAsync();
            LOGGER.log(Level.DEBUG, "Pushed a comment to Sym");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Pushes a comment to Sym failed: " + e.getMessage());
        }
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
