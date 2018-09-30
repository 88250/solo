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
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Comment cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Sep 25, 2018
 * @since 2.3.0
 */
@Singleton
public class CommentCache {

    /**
     * Comment cache.
     */
    private final Map<String, JSONObject> cache = new ConcurrentHashMap<>();

    /**
     * Gets a comment by the specified comment id.
     *
     * @param id the specified comment id
     * @return comment, returns {@code null} if not found
     */
    public JSONObject getComment(final String id) {
        final JSONObject comment = cache.get(id);
        if (null == comment) {
            return null;
        }

        return Solos.clone(comment);
    }

    /**
     * Adds or updates the specified comment.
     *
     * @param comment the specified comment
     */
    public void putComment(final JSONObject comment) {
        cache.put(comment.optString(Keys.OBJECT_ID), Solos.clone(comment));
    }

    /**
     * Removes a comment by the specified comment id.
     *
     * @param id the specified comment id
     */
    public void removeComment(final String id) {
        cache.remove(id);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        cache.clear();
    }
}
