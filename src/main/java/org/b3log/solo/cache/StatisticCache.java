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

import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.model.Option;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Statistic cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 25, 2018
 * @since 2.4.0
 */
@Singleton
public class StatisticCache {

    /**
     * Statistic cache.
     */
    private final Map<String, JSONObject> cache = new ConcurrentHashMap<>();

    /**
     * Get the statistic.
     *
     * @return statistic
     */
    public JSONObject getStatistic() {
        return cache.get(Option.CATEGORY_C_STATISTIC);
    }

    /**
     * Adds or updates the specified statistic.
     *
     * @param statistic the specified statistic
     */
    public void putStatistic(final JSONObject statistic) {
        cache.put(Option.CATEGORY_C_STATISTIC, statistic);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        cache.clear();
    }
}
