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
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Sep 25, 2018
 * @since 2.3.0
 */
@Singleton
public class UserCache {

    /**
     * Id, User.
     */
    private final Map<String, JSONObject> idCache = new ConcurrentHashMap<>();

    /**
     * Email, User.
     */
    private final Map<String, JSONObject> emailCache = new ConcurrentHashMap<>();

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
     * Gets a user by the specified user email.
     *
     * @param userEmail the specified user email
     * @return user, returns {@code null} if not found
     */
    public JSONObject getUserByEmail(final String userEmail) {
        final JSONObject user = emailCache.get(userEmail);
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
        emailCache.put(user.optString(User.USER_EMAIL), Solos.clone(user));
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

        final String email = user.optString(User.USER_EMAIL);
        emailCache.remove(email);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        idCache.clear();
        emailCache.clear();
        adminCache.clear();
    }
}
