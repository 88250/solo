/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.event;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.solo.Server;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

/**
 * This listener is responsible for sending article to B3log Rhythm. Sees <a href="https://ld246.com/article/1546941897596">B3log 构思 - 分布式社区网络</a> for more details.
 * <p>
 * API spec: https://ld246.com/article/1457158841475
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/armstrong">ArmstrongCN</a>
 * @version 1.0.2.27, Sep 3, 2020
 * @since 0.3.1
 */
@Singleton
public class B3ArticleSender extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(B3ArticleSender.class);

    @Override
    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();
        LOGGER.log(Level.DEBUG, "Processing an event [type={}, data={}] in listener [className={}]",
                event.getType(), data, B3ArticleSender.class.getName());

        pushArticleToRhy(data);
    }

    /**
     * Pushes the specified article data to B3log Rhythm.
     *
     * @param data the specified article data
     */
    public static void pushArticleToRhy(final JSONObject data) {
        try {
            final JSONObject originalArticle = data.getJSONObject(Article.ARTICLE);
            final String title = originalArticle.getString(Article.ARTICLE_TITLE);
            if (Article.ARTICLE_STATUS_C_PUBLISHED != originalArticle.optInt(Article.ARTICLE_STATUS)) {
                LOGGER.log(Level.INFO, "Ignored push a draft [title={}] to Rhy", title);
                return;
            }

            if (StringUtils.isNotBlank(originalArticle.optString(Article.ARTICLE_VIEW_PWD))) {
                LOGGER.log(Level.INFO, "Article [title={}] is a password article, ignored push to Rhy", title);
                return;
            }

            if (!originalArticle.optBoolean(Common.POST_TO_COMMUNITY)) {
                LOGGER.log(Level.INFO, "Article [title={}] push flag [postToCommunity] is [false], ignored push to Rhy", title);
                return;
            }

            final BeanManager beanManager = BeanManager.getInstance();
            final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
            final ArticleQueryService articleQueryService = beanManager.getReference(ArticleQueryService.class);
            final JSONObject preference = optionQueryService.getPreference();

            final JSONObject article = new JSONObject().
                    put("id", originalArticle.getString(Keys.OBJECT_ID)).
                    put("title", originalArticle.getString(Article.ARTICLE_TITLE)).
                    put("permalink", originalArticle.getString(Article.ARTICLE_PERMALINK)).
                    put("tags", originalArticle.getString(Article.ARTICLE_TAGS_REF)).
                    put("content", originalArticle.getString(Article.ARTICLE_CONTENT));
            final JSONObject author = articleQueryService.getAuthor(originalArticle);
            final JSONObject client = new JSONObject().
                    put("title", preference.getString(Option.ID_C_BLOG_TITLE)).
                    put("subTitle", preference.optString(Option.ID_C_BLOG_SUBTITLE)).
                    put("favicon", preference.optString(Option.ID_C_FAVICON_URL)).
                    put("host", Latkes.getServePath()).
                    put("name", "Solo").
                    put("ver", Server.VERSION).
                    put("userName", author.optString(User.USER_NAME)).
                    put("userB3Key", author.optString(UserExt.USER_B3_KEY));
            final JSONObject requestJSONObject = new JSONObject().
                    put("article", article).
                    put("client", client);
            final HttpResponse response = HttpRequest.post("https://rhythm.b3log.org/api/article").bodyText(requestJSONObject.toString()).
                    connectionTimeout(3000).timeout(7000).trustAllCerts(true).followRedirects(true).
                    contentTypeJson().header("User-Agent", Solos.USER_AGENT).send();
            response.charset("UTF-8");
            final JSONObject result = new JSONObject(response.bodyText());
            LOGGER.log(Level.INFO, "Pushed an article [title=" + title + "] to Rhy, result [" + result.optString(Keys.MSG) + "]");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Pushes an article to Rhy failed: " + e.getMessage());
        }
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
