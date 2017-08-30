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
import org.b3log.solo.model.Statistic;
import org.b3log.solo.util.JSONs;
import org.json.JSONObject;

/**
 * Statistic cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Aug 30, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class StatisticCache {

    /**
     * Statistic cache.
     */
    private Cache cache = CacheFactory.getCache(Statistic.STATISTIC);

    /**
     * Gets an statistic by the specified statistic id.
     *
     * @param id the specified statistic id
     * @return statistic, returns {@code null} if not found
     */
    public JSONObject getStatistic(final String id) {
        final JSONObject statistic = cache.get(id);
        if (null == statistic) {
            return null;
        }

        return JSONs.clone(statistic);
    }

    /**
     * Adds or updates the specified statistic.
     *
     * @param statistic the specified statistic
     */
    public void putStatistic(final JSONObject statistic) {
        final String statisticId = statistic.optString(Keys.OBJECT_ID);

        cache.put(statisticId, JSONs.clone(statistic));
    }

    /**
     * Removes an statistic by the specified statistic id.
     *
     * @param id the specified statistic id
     */
    public void removeStatistic(final String id) {
        cache.remove(id);
    }
}
