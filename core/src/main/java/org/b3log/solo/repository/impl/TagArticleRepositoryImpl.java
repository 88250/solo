/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.repository.impl;


import java.util.List;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.*;
import org.b3log.latke.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Tag-Article relation repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Nov 9, 2011
 * @since 0.3.1
 */
public final class TagArticleRepositoryImpl extends AbstractRepository implements TagArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagArticleRepositoryImpl.class.getName());

    /**
     * Singleton.
     */
    private static final TagArticleRepositoryImpl SINGLETON = new TagArticleRepositoryImpl(Tag.TAG + "_" + Article.ARTICLE);

    @Override
    public List<JSONObject> getByArticleId(final String articleId) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId)).setPageCount(
            1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(array);
    }

    @Override
    public JSONObject getByTagId(final String tagId, final int currentPageNum, final int pageSize)
        throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId)).addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID, SortDirection.DESCENDING).setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(
            1);

        return get(query);
    }

    /**
     * Gets the {@link TagArticleRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static TagArticleRepositoryImpl getInstance() {
        return SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private TagArticleRepositoryImpl(final String name) {
        super(name);
    }
}
