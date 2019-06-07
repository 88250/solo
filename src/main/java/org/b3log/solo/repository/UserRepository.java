/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
package org.b3log.solo.repository;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.cache.UserCache;
import org.json.JSONObject;

/**
 * User repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.3, Jun 6, 2019
 * @since 0.3.1
 */
@Repository
public class UserRepository extends AbstractRepository {

    /**
     * User cache.
     */
    @Inject
    private UserCache userCache;

    /**
     * Public constructor.
     */
    public UserRepository() {
        super(User.USER);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        super.remove(id);

        userCache.removeUser(id);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = userCache.getUser(id);
        if (null != ret) {
            return ret;
        }

        ret = super.get(id);
        if (null == ret) {
            return null;
        }

        userCache.putUser(ret);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject user, final String... propertyNames) throws RepositoryException {
        super.update(id, user, propertyNames);

        user.put(Keys.OBJECT_ID, id);
        userCache.putUser(user);

        if (Role.ADMIN_ROLE.equals(user.optString(User.USER_ROLE))) {
            userCache.putAdmin(user);
        }
    }

    /**
     * Gets a user by the specified username.
     *
     * @param userName the specified username
     * @return user, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByUserName(final String userName) throws RepositoryException {
        return getFirst(new Query().setFilter(new PropertyFilter(User.USER_NAME, FilterOperator.EQUAL, userName)));
    }

    /**
     * Gets the administrator user.
     *
     * @return administrator user, returns {@code null} if not found or error
     * @throws RepositoryException repository exception
     */
    public JSONObject getAdmin() throws RepositoryException {
        JSONObject ret = userCache.getAdmin();
        if (null != ret) {
            return ret;
        }

        ret = getFirst(new Query().setFilter(new PropertyFilter(User.USER_ROLE, FilterOperator.EQUAL, Role.ADMIN_ROLE)));
        if (null == ret) {
            return null;
        }

        userCache.putAdmin(ret);

        return ret;
    }
}
