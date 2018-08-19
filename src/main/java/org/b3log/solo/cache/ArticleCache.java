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
package org.b3log.solo.cache;

import org.b3log.latke.Keys;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.solo.model.Article;
import org.b3log.solo.util.JSONs;
import org.json.JSONObject;

/**
 * Article cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Aug 30, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class ArticleCache {

    /**
     * Article id cache.
     */
    private Cache idCache = CacheFactory.getCache(Article.ARTICLES);

    /**
     * Article permalink cache.
     */
    private Cache permalinkCache = CacheFactory.getCache(Article.ARTICLE_PERMALINK);

    /**
     * Gets an article by the specified article id.
     *
     * @param id the specified article id
     * @return article, returns {@code null} if not found
     */
    public JSONObject getArticle(final String id) {
        final JSONObject article = idCache.get(id);
        if (null == article) {
            return null;
        }

        return JSONs.clone(article);
    }

    /**
     * Gets an article by the specified article permalink.
     *
     * @param permalink the specified article permalink
     * @return article, returns {@code null} if not found
     */
    public JSONObject getArticleByPermalink(final String permalink) {
        final JSONObject article = permalinkCache.get(permalink);
        if (null == article) {
            return null;
        }

        return JSONs.clone(article);
    }

    /**
     * Adds or updates the specified article.
     *
     * @param article the specified article
     */
    public void putArticle(final JSONObject article) {
        idCache.put(article.optString(Keys.OBJECT_ID), JSONs.clone(article));
        permalinkCache.put(article.optString(Article.ARTICLE_PERMALINK), JSONs.clone(article));
    }

    /**
     * Removes an article by the specified article id.
     *
     * @param id the specified article id
     */
    public void removeArticle(final String id) {
        idCache.remove(id);
    }
}