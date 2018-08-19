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

import java.util.List;


/**
 * Page repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Dec 31, 2011
 * @since 0.3.1
 */
public interface PageRepository extends Repository {

    /**
     * Gets a page by the specified permalink.
     *
     * @param permalink the specified permalink
     * @return page, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getByPermalink(final String permalink) throws RepositoryException;

    /**
     * Gets the maximum order.
     *
     * @return order number, returns {@code -1} if not found
     * @throws RepositoryException repository exception
     */
    int getMaxOrder() throws RepositoryException;

    /**
     * Gets the upper page of the page specified by the given id.
     * 
     * @param id the given id
     * @return upper page, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getUpper(final String id) throws RepositoryException;

    /**
     * Gets the under page of the page specified by the given id.
     * 
     * @param id the given id
     * @return under page, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getUnder(final String id) throws RepositoryException;

    /**
     * Gets a page by the specified order.
     *
     * @param order the specified order
     * @return page, returns {@code null} if not found
     * @throws RepositoryException repository exception 
     */
    JSONObject getByOrder(final int order) throws RepositoryException;

    /**
     * Gets pages.
     *
     * @return a list of pages, returns an empty list if  not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getPages() throws RepositoryException;
}
