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
import org.b3log.solo.model.Page;
import org.b3log.solo.util.JSONs;
import org.json.JSONObject;

/**
 * Page cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class PageCache {

    /**
     * Page cache.
     */
    private Cache cache = CacheFactory.getCache(Page.PAGES);

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

        return JSONs.clone(page);
    }

    /**
     * Adds or updates the specified page.
     *
     * @param page the specified page
     */
    public void putPage(final JSONObject page) {
        final String pageId = page.optString(Keys.OBJECT_ID);

        cache.put(pageId, JSONs.clone(page));
    }

    /**
     * Removes an page by the specified page id.
     *
     * @param id the specified page id
     */
    public void removePage(final String id) {
        cache.remove(id);
    }
}
