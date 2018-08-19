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
 * Link repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Nov 2, 2011
 * @since 0.3.1
 */
public interface LinkRepository extends Repository {

    /**
     * Gets a link by the specified address.
     *
     * @param address the specified address
     * @return link, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByAddress(final String address) throws RepositoryException;

    /**
     * Gets the maximum order.
     * 
     * @return order number, returns {@code -1} if not found
     * @throws RepositoryException repository exception
     */
    int getMaxOrder() throws RepositoryException;

    /**
     * Gets the upper link of the link specified by the given id.
     * 
     * @param id the given id
     * @return upper link, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getUpper(final String id) throws RepositoryException;

    /**
     * Gets the under link of the link specified by the given id.
     * 
     * @param id the given id
     * @return under link, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getUnder(final String id) throws RepositoryException;

    /**
     * Gets a link by the specified order.
     *
     * @param order the specified order
     * @return link, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getByOrder(final int order) throws RepositoryException;
}
