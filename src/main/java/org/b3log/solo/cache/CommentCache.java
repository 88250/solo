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
import org.b3log.solo.util.Statics;
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
        Statics.clear();
    }

    /**
     * Removes a comment by the specified comment id.
     *
     * @param id the specified comment id
     */
    public void removeComment(final String id) {
        cache.remove(id);
        Statics.clear();
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        cache.clear();
        Statics.clear();
    }
}
