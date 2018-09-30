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
