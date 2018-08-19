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

import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.solo.model.Option;
import org.json.JSONObject;

/**
 * Preference cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 22, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class PreferenceCache {

    /**
     * Preference cache.
     */
    private Cache cache = CacheFactory.getCache(Option.CATEGORY_C_PREFERENCE);

    /**
     * Get the preference.
     *
     * @return preference
     */
    public JSONObject getPreference() {
        return cache.get(Option.CATEGORY_C_PREFERENCE);
    }

    /**
     * Adds or updates the specified preference.
     *
     * @param preference the specified preference
     */
    public void putPreference(final JSONObject preference) {
        cache.put(Option.CATEGORY_C_PREFERENCE, preference);
    }

    /**
     * Clears the preference.
     */
    public void clear() {
        cache.removeAll();
    }
}
