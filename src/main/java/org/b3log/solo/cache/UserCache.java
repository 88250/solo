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
import org.b3log.latke.model.Role;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.2, Mar 3, 2019
 * @since 2.3.0
 */
@Singleton
public class UserCache {

    /**
     * Id, User.
     */
    private final Map<String, JSONObject> idCache = new ConcurrentHashMap<>();

    /**
     * Admin user.
     */
    private final Map<String, JSONObject> adminCache = new ConcurrentHashMap<>();

    /**
     * Gets the admin user.
     *
     * @return admin user
     */
    public JSONObject getAdmin() {
        return adminCache.get(Role.ADMIN_ROLE);
    }

    /**
     * Adds or updates the admin user.
     *
     * @param admin the specified admin user
     */
    public void putAdmin(final JSONObject admin) {
        adminCache.put(Role.ADMIN_ROLE, admin);
    }

    /**
     * Gets a user by the specified user id.
     *
     * @param userId the specified user id
     * @return user, returns {@code null} if not found
     */
    public JSONObject getUser(final String userId) {
        final JSONObject user = idCache.get(userId);
        if (null == user) {
            return null;
        }

        return Solos.clone(user);
    }

    /**
     * Adds or updates the specified user.
     *
     * @param user the specified user
     */
    public void putUser(final JSONObject user) {
        idCache.put(user.optString(Keys.OBJECT_ID), Solos.clone(user));
    }

    /**
     * Removes a user by the specified user id.
     *
     * @param id the specified user id
     */
    public void removeUser(final String id) {
        final JSONObject user = idCache.get(id);
        if (null == user) {
            return;
        }

        idCache.remove(id);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        idCache.clear();
        adminCache.clear();
    }
}
