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
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.solo.util.JSONs;
import org.json.JSONObject;

/**
 * User cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Aug 27, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class UserCache {

    /**
     * Id, User.
     */
    private Cache idCache = CacheFactory.getCache(User.USERS + "ID");

    /**
     * Email, User.
     */
    private Cache emailCache = CacheFactory.getCache(User.USERS + "Email");

    /**
     * Admin user.
     */
    private Cache adminCache = CacheFactory.getCache("adminUser");

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

        return JSONs.clone(user);
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

        return JSONs.clone(user);
    }

    /**
     * Adds or updates the specified user.
     *
     * @param user the specified user
     */
    public void putUser(final JSONObject user) {
        idCache.put(user.optString(Keys.OBJECT_ID), JSONs.clone(user));
        emailCache.put(user.optString(User.USER_EMAIL), JSONs.clone(user));
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
}
