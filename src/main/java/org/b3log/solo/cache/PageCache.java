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
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Page cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 25, 2018
 * @since 2.3.0
 */
@Singleton
public class PageCache {

    /**
     * Page cache.
     */
    private final Map<String, JSONObject> cache = new ConcurrentHashMap<>();

    /**
     * Gets a page by the specified page id.
     *
     * @param id the specified page id
     * @return page, returns {@code null} if not found
     */
    public JSONObject getPage(final String id) {
        final JSONObject page = cache.get(id);
        if (null == page) {
            return null;
        }

        return Solos.clone(page);
    }

    /**
     * Adds or updates the specified page.
     *
     * @param page the specified page
     */
    public void putPage(final JSONObject page) {
        final String pageId = page.optString(Keys.OBJECT_ID);

        cache.put(pageId, Solos.clone(page));
    }

    /**
     * Removes an page by the specified page id.
     *
     * @param id the specified page id
     */
    public void removePage(final String id) {
        cache.remove(id);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        cache.clear();
    }
}
