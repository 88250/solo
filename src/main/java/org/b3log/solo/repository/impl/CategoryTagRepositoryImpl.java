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
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.CategoryTagRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Category-Tag relation repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Mar 31, 2017
 * @since 2.0.0
 */
@Repository
public class CategoryTagRepositoryImpl extends AbstractRepository implements CategoryTagRepository {

    /**
     * Public constructor.
     */
    public CategoryTagRepositoryImpl() {
        super(Category.CATEGORY + "_" + Tag.TAG);
    }

    @Override
    public JSONObject getByCategoryId(final String categoryId, final int currentPageNum, final int pageSize)
            throws RepositoryException {
        final Query query = new Query().
                setFilter(new PropertyFilter(Category.CATEGORY + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, categoryId)).
                setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(1);

        return get(query);
    }

    @Override
    public JSONObject getByTagId(final String tagId, final int currentPageNum, final int pageSize)
            throws RepositoryException {
        final Query query = new Query().
                setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId)).
                setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(1);

        return get(query);
    }

    @Override
    public void removeByCategoryId(final String categoryId) throws RepositoryException {
        final Query query = new Query().
                setFilter(new PropertyFilter(Category.CATEGORY + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, categoryId));
        final JSONArray relations = get(query).optJSONArray(Keys.RESULTS);
        for (int i = 0; i < relations.length(); i++) {
            final JSONObject rel = relations.optJSONObject(i);
            remove(rel.optString(Keys.OBJECT_ID));
        }
    }

    @Override
    public void removeByTagId(final String tagId) throws RepositoryException {
        final Query query = new Query().
                setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId));
        final JSONArray relations = get(query).optJSONArray(Keys.RESULTS);
        for (int i = 0; i < relations.length(); i++) {
            final JSONObject rel = relations.optJSONObject(i);
            remove(rel.optString(Keys.OBJECT_ID));
        }
    }
}
