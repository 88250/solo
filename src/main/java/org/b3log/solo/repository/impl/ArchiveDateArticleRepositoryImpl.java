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
package org.b3log.solo.repository.impl;


import org.b3log.latke.Keys;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Archive date-Article relation repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Nov 9, 2011
 * @since 0.3.1
 */
@Repository
public class ArchiveDateArticleRepositoryImpl extends AbstractRepository implements ArchiveDateArticleRepository {

    /**
     * Public constructor.
     */
    public ArchiveDateArticleRepositoryImpl() {
        super(ArchiveDate.ARCHIVE_DATE + "_" + Article.ARTICLE);
    }

    @Override
    public JSONObject getByArchiveDateId(final String archiveDateId, final int currentPageNum, final int pageSize)
            throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, archiveDateId)).
                addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID, SortDirection.DESCENDING).
                setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(1);

        return get(query);
    }

    @Override
    public JSONObject getByArticleId(final String articleId) throws RepositoryException {
        final Query query = new Query();

        query.setFilter(new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId));

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }
}
