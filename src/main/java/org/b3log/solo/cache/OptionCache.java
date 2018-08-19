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
import org.b3log.solo.model.Option;
import org.b3log.solo.util.JSONs;
import org.json.JSONObject;

/**
 * Option cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 16, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class OptionCache {

    /**
     * Option cache.
     */
    private Cache CACHE = CacheFactory.getCache(Option.OPTIONS);

    /**
     * Gets an option by the specified option id.
     *
     * @param id the specified option id
     * @return option, returns {@code null} if not found
     */
    public JSONObject getOption(final String id) {
        final JSONObject option = CACHE.get(id);
        if (null == option) {
            return null;
        }

        return JSONs.clone(option);
    }

    /**
     * Adds or updates the specified option.
     *
     * @param option the specified option
     */
    public void putOption(final JSONObject option) {
        CACHE.put(option.optString(Keys.OBJECT_ID), JSONs.clone(option));
    }

    /**
     * Removes an option by the specified option id.
     *
     * @param id the specified option id
     */
    public void removeOption(final String id) {
        CACHE.remove(id);
    }
}
