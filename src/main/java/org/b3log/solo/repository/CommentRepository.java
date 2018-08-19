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
 * Comment repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Oct 26, 2011
 * @since 0.3.1
 */
public interface CommentRepository extends Repository {

    /**
     * Gets post comments recently with the specified fetch.
     *
     * @param fetchSize the specified fetch size
     * @return a list of comments recently, returns an empty list if not found
     * @throws RepositoryException repository exception 
     */
    List<JSONObject> getRecentComments(final int fetchSize)
        throws RepositoryException;

    /**
     * Gets comments with the specified on id, current page number and 
     * page size.
     * 
     * @param onId the specified on id
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @return a list of comments, returns an empty list if not found
     * @throws RepositoryException repository exception 
     */
    List<JSONObject> getComments(final String onId,
        final int currentPageNum,
        final int pageSize) throws RepositoryException;

    /**
     * Removes comments with the specified on id.
     * 
     * @param onId the specified on id
     * @return removed count
     * @throws RepositoryException repository exception 
     */
    int removeComments(final String onId) throws RepositoryException;
}
