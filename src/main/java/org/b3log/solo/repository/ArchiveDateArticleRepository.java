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
 * Archive date-Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Nov 9, 2011
 * @since 0.3.1
 */
public interface ArchiveDateArticleRepository extends Repository {

    /**
     * Gets archive date-article relations by the specified archive date id.
     *
     * @param archiveDateId the specified archive date id
     * @param currentPageNum the specified current page number, MUST greater
     * then {@code 0}
     * @param pageSize the specified page size(count of a page contains objects),
     * MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "",
     *         "archiveDate_oId": "",
     *         "article_oId": ""
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByArchiveDateId(final String archiveDateId,
        final int currentPageNum,
        final int pageSize)
        throws RepositoryException;

    /**
     * Gets an archive date-article relations by the specified article id.
     *
     * @param articleId the specified article id
     * @return for example
     * <pre>
     * {
     *     "archiveDate_oId": "",
     *     "article_oId": articleId
     * }, returns {@code null} if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByArticleId(final String articleId)
        throws RepositoryException;
}
