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
 * Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.10, Jul 16, 2017
 * @since 0.3.1
 */
public interface ArticleRepository extends Repository {

    /**
     * Gets published articles by the specified author email, current page number and page size.
     *
     * @param authorEmail    the specified author email
     * @param currentPageNum the specified current page number, MUST greater then {@code 0}
     * @param pageSize       the specified page size(count of a page contains objects), MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         // article keys....
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByAuthorEmail(final String authorEmail, final int currentPageNum, final int pageSize) throws RepositoryException;

    /**
     * Gets an article by the specified permalink.
     *
     * @param permalink the specified permalink
     * @return an article, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByPermalink(final String permalink) throws RepositoryException;

    /**
     * Gets post articles recently with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of articles recently, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getRecentArticles(final int fetchSize) throws RepositoryException;

    /**
     * Gets most commented and published articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most comment articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getMostCommentArticles(final int num) throws RepositoryException;

    /**
     * Gets most view count and published articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most view count articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getMostViewCountArticles(final int num) throws RepositoryException;

    /**
     * Gets the previous article(by create date) by the specified article id.
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": "",
     *     "articleAbstract: ""
     * }
     * </pre>
     * returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getPreviousArticle(final String articleId) throws RepositoryException;

    /**
     * Gets the next article(by create date, oId) by the specified article id.
     *
     * @param articleId the specified article id
     * @return the next article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": "",
     *     "articleAbstract: ""
     * }
     * </pre>
     * returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getNextArticle(final String articleId) throws RepositoryException;

    /**
     * Determines an article specified by the given article id is published.
     *
     * @param articleId the given article id
     * @return {@code true} if it is published, {@code false} otherwise
     * @throws RepositoryException repository exception
     */
    boolean isPublished(final String articleId) throws RepositoryException;
}
