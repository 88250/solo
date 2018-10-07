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
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

/**
 * This listener is responsible for updating article to B3log Rhythm. Sees <a href="https://hacpai.com/b3log">B3log 构思</a> for more details.
 * <p>
 * The B3log Rhythm article update interface: http://rhythm.b3log.org/article (PUT).
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.5, Sep 25, 2018
 * @since 0.6.0
 */
@Singleton
public class B3ArticleUpdater extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(B3ArticleUpdater.class);

    /**
     * URL of updating article to Rhythm.
     */
    private static String UPDATE_ARTICLE_URL = Solos.B3LOG_RHYTHM_SERVE_PATH + "/article";

    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();

        LOGGER.log(Level.DEBUG, "Processing an event [type={0}, data={1}] in listener [className={2}]",
                event.getType(), data, B3ArticleUpdater.class.getName());
        try {
            final JSONObject originalArticle = data.getJSONObject(Article.ARTICLE);
            if (!originalArticle.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                LOGGER.log(Level.DEBUG, "Ignores post article[title={0}] to Rhythm", originalArticle.getString(Article.ARTICLE_TITLE));

                return;
            }

            final BeanManager beanManager = BeanManager.getInstance();
            final PreferenceQueryService preferenceQueryService = beanManager.getReference(PreferenceQueryService.class);
            final ArticleQueryService articleQueryService = beanManager.getReference(ArticleQueryService.class);

            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                throw new EventException("Not found preference");
            }

            if (StringUtils.isNotBlank(originalArticle.optString(Article.ARTICLE_VIEW_PWD))) {
                return;
            }

            if (Latkes.getServePath().contains("localhost") || Strings.isIPv4(Latkes.getServePath())) {
                LOGGER.log(Level.TRACE, "Solo runs on local server, so should not send this article[id={0}, title={1}] to Rhythm",
                        originalArticle.getString(Keys.OBJECT_ID), originalArticle.getString(Article.ARTICLE_TITLE));
                return;
            }

            final JSONObject author = articleQueryService.getAuthor(originalArticle);
            final String authorEmail = author.optString(User.USER_EMAIL);

            final JSONObject requestJSONObject = new JSONObject();
            final JSONObject article = new JSONObject();

            article.put(Keys.OBJECT_ID, originalArticle.getString(Keys.OBJECT_ID));
            article.put(Article.ARTICLE_TITLE, originalArticle.getString(Article.ARTICLE_TITLE));
            article.put(Article.ARTICLE_PERMALINK, originalArticle.getString(Article.ARTICLE_PERMALINK));
            article.put(Article.ARTICLE_TAGS_REF, originalArticle.getString(Article.ARTICLE_TAGS_REF));
            article.put(Article.ARTICLE_T_AUTHOR_EMAIL, authorEmail);
            article.put(Article.ARTICLE_CONTENT, originalArticle.getString(Article.ARTICLE_CONTENT));
            article.put(Article.ARTICLE_T_CREATE_DATE, originalArticle.getLong(Article.ARTICLE_CREATED));
            article.put(Article.ARTICLE_T_UPDATE_DATE, originalArticle.getLong(Article.ARTICLE_UPDATED));
            article.put(Common.POST_TO_COMMUNITY, originalArticle.getBoolean(Common.POST_TO_COMMUNITY));

            // Removes this property avoid to persist
            originalArticle.remove(Common.POST_TO_COMMUNITY);

            requestJSONObject.put(Article.ARTICLE, article);
            requestJSONObject.put(Common.BLOG_VERSION, SoloServletListener.VERSION);
            requestJSONObject.put(Common.BLOG, "Solo");
            requestJSONObject.put(Option.ID_C_BLOG_TITLE, preference.getString(Option.ID_C_BLOG_TITLE));
            requestJSONObject.put("blogHost", Latkes.getServePath());
            requestJSONObject.put("userB3Key", preference.optString(Option.ID_C_KEY_OF_SOLO));
            requestJSONObject.put("clientAdminEmail", preference.optString(Option.ID_C_ADMIN_EMAIL));
            requestJSONObject.put("clientRuntimeEnv", "LOCAL");

            HttpRequest.put(UPDATE_ARTICLE_URL).bodyText(requestJSONObject.toString()).
                    contentTypeJson().header("User-Agent", Solos.USER_AGENT).sendAsync();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sends an article to Rhythm error: {0}", e.getMessage());
        }

        LOGGER.log(Level.DEBUG, "Sent an article to Rhythm");
    }

    /**
     * Gets the event type {@linkplain EventTypes#UPDATE_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.UPDATE_ARTICLE;
    }
}
