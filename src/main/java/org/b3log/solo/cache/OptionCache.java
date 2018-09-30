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
import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.model.Option;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Option cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Sep 25, 2018
 * @since 2.3.0
 */
@Singleton
public class OptionCache {

    /**
     * Option cache.
     */
    private final Map<String, JSONObject> cache = new ConcurrentHashMap<>();

    /**
     * Category option caches.
     */
    private final Map<String, JSONObject> categoryCache = new ConcurrentHashMap<>();

    /**
     * Removes a category cache specified by the given category.
     *
     * @param category the given category
     */
    public void removeCategory(final String category) {
        categoryCache.remove(category);
    }

    /**
     * Gets merged options as a JSON object for the specified category
     *
     * @param category the specified category
     * @return merged options
     */
    public JSONObject getCategory(final String category) {
        JSONObject ret = categoryCache.get(category);
        if (null == ret) {
            return null;
        }

        return Solos.clone(ret);
    }

    /**
     * Puts the specified merged options with the specified category.
     *
     * @param category      the specified category
     * @param mergedOptions the specified merged options
     */
    public void putCategory(final String category, final JSONObject mergedOptions) {
        categoryCache.put(category, mergedOptions);
    }

    /**
     * Gets an option by the specified option id.
     *
     * @param id the specified option id
     * @return option, returns {@code null} if not found
     */
    public JSONObject getOption(final String id) {
        final JSONObject option = cache.get(id);
        if (null == option) {
            return null;
        }

        return Solos.clone(option);
    }

    /**
     * Adds or updates the specified option.
     *
     * @param option the specified option
     */
    public void putOption(final JSONObject option) {
        cache.put(option.optString(Keys.OBJECT_ID), Solos.clone(option));

        final String category = option.optString(Option.OPTION_CATEGORY);
        removeCategory(category);
    }

    /**
     * Removes an option by the specified option id.
     *
     * @param id the specified option id
     */
    public void removeOption(final String id) {
        final JSONObject option = getOption(id);
        if (null == option) {
            return;
        }

        final String category = option.optString(Option.OPTION_CATEGORY);
        removeCategory(category);

        cache.remove(id);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        cache.clear();
        categoryCache.clear();
    }
}
