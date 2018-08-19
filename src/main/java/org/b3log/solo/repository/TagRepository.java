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
 * Tag repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Aug 12, 2010
 * @since 0.3.1
 */
public interface TagRepository extends Repository {

    /**
     * Gets tags of an article specified by the article id.
     * 
     * @param articleId the specified article id
     * @return a list of tags of the specified article, returns an empty list
     * if not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getByArticleId(final String articleId)
        throws RepositoryException;

    /**
     * Gets a tag by the specified tag title.
     *
     * @param tagTitle the specified tag title
     * @return a tag, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByTitle(final String tagTitle) throws RepositoryException;

    /**
     * Gets most used tags with the specified number.
     *
     * @param num the specified number
     * @return a list of most used tags, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getMostUsedTags(final int num) throws RepositoryException;
}
