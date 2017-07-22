/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
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
 * @version 1.0.0.0, Jul 6, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class ArticleCache {

    /**
     * Article cache.
     */
    private static final Cache ARTICLE_CACHE = CacheFactory.getCache(Article.ARTICLES);

    static {
        ARTICLE_CACHE.setMaxCount(128);
    }

    /**
     * Gets an article by the specified article id.
     *
     * @param id the specified article id
     * @return article, returns {@code null} if not found
     */
    public JSONObject getArticle(final String id) {
        final JSONObject article = ARTICLE_CACHE.get(id);
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
        final String articleId = article.optString(Keys.OBJECT_ID);

        ARTICLE_CACHE.put(articleId, JSONs.clone(article));
    }

    /**
     * Removes an article by the specified article id.
     *
     * @param id the specified article id
     */
    public void removeArticle(final String id) {
        ARTICLE_CACHE.remove(id);
    }
}