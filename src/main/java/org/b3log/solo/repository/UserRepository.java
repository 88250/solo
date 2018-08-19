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
package org.b3log.solo.repository;


import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONObject;


/**
 * User repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Nov 8, 2011
 * @since 0.3.1
 */
public interface UserRepository extends Repository {

    /**
     * Gets a user by the specified email.
     *
     * @param email the specified email
     * @return user, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getByEmail(final String email) throws RepositoryException;

    /**
     * Determine whether the specified email is administrator's.
     *
     * @param email the specified email
     * @return {@code true} if it is administrator's email, {@code false}
     * otherwise
     * @throws RepositoryException repository exception
     */
    boolean isAdminEmail(final String email) throws RepositoryException;

    /**
     * Gets the administrator user.
     *
     * @return administrator user, returns {@code null} if not found or error
     * @throws RepositoryException repository exception
     */
    JSONObject getAdmin() throws RepositoryException;
}
