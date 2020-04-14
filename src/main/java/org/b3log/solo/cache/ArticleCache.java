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
package org.b3log.solo.cache;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.model.Article;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Article cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.0, Sep 20, 2019
 * @since 2.3.0
 */
@Singleton
public class ArticleCache {

    /**
     * Article id cache.
     */
    private final Map<String, JSONObject> idCache = new ConcurrentHashMap<>();

    /**
     * Article permalink cache.
     */
    private final Map<String, JSONObject> permalinkCache = new ConcurrentHashMap<>();

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

        return Solos.clone(article);
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

        return Solos.clone(article);
    }

    /**
     * Adds or updates the specified article.
     *
     * @param article the specified article
     */
    public void putArticle(final JSONObject article) {
        idCache.put(article.optString(Keys.OBJECT_ID), Solos.clone(article));
        permalinkCache.put(article.optString(Article.ARTICLE_PERMALINK), Solos.clone(article));
    }

    /**
     * Removes an article by the specified article id.
     *
     * @param id the specified article id
     */
    public void removeArticle(final String id) {
        final JSONObject article = idCache.get(id);
        if (null == article) {
            return;
        }
        final String permalink = article.optString(Article.ARTICLE_PERMALINK);
        idCache.remove(id);
        permalinkCache.remove(permalink);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        idCache.clear();
        permalinkCache.clear();
    }
}