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
