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
 * Category-Tag relation repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Mar 31, 2017
 * @since 2.0.0
 */
public interface CategoryTagRepository extends Repository {

    /**
     * Gets category-tag relations by the specified category id.
     *
     * @param categoryId     the specified category id
     * @param currentPageNum the specified current page number, MUST greater then {@code 0}
     * @param pageSize       the specified page size(count of a page contains objects), MUST greater then {@code 0}
     * @return for example      <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "",
     *         "category_oId": categoryId,
     *         "tag_oId": ""
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByCategoryId(final String categoryId, final int currentPageNum, final int pageSize)
            throws RepositoryException;

    /**
     * Gets category-tag relations by the specified tag id.
     *
     * @param tagId          the specified tag id
     * @param currentPageNum the specified current page number, MUST greater then {@code 0}
     * @param pageSize       the specified page size(count of a page contains objects), MUST greater then {@code 0}
     * @return for example      <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "",
     *         "category_oId": "",
     *         "tag_oId": tagId
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByTagId(final String tagId, final int currentPageNum, final int pageSize)
            throws RepositoryException;

    /**
     * Removes category-tag relations by the specified category id.
     *
     * @param categoryId the specified category id
     * @throws RepositoryException repository exception
     */
    void removeByCategoryId(final String categoryId) throws RepositoryException;

    /**
     * Removes category-tag relations by the specified tag id.
     *
     * @param tagId the specified tag id
     * @throws RepositoryException repository exception
     */
    void removeByTagId(final String tagId) throws RepositoryException;
}
