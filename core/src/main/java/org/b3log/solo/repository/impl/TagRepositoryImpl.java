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


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.*;
import org.b3log.latke.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Tag repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.1, Nov 29, 2011
 * @since 0.3.1
 */
public final class TagRepositoryImpl extends AbstractRepository implements TagRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagRepositoryImpl.class.getName());

    /**
     * Singleton.
     */
    private static final TagRepositoryImpl SINGLETON = new TagRepositoryImpl(Tag.TAG);

    /**
     * Tag-Article relation repository.
     */
    private TagArticleRepositoryImpl tagArticleRepository = TagArticleRepositoryImpl.getInstance();

    @Override
    public JSONObject getByTitle(final String tagTitle) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG_TITLE, FilterOperator.EQUAL, tagTitle)).setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public List<JSONObject> getMostUsedTags(final int num) throws RepositoryException {
        final Query query = new Query().addSort(Tag.TAG_PUBLISHED_REFERENCE_COUNT, SortDirection.DESCENDING).setCurrentPageNum(1).setPageSize(num).setPageCount(
            1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(array);
    }

    @Override
    public List<JSONObject> getByArticleId(final String articleId) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();

        final List<JSONObject> tagArticleRelations = tagArticleRepository.getByArticleId(articleId);

        for (final JSONObject tagArticleRelation : tagArticleRelations) {
            final String tagId = tagArticleRelation.optString(Tag.TAG + "_" + Keys.OBJECT_ID);
            final JSONObject tag = get(tagId);

            ret.add(tag);
        }

        return ret;
    }

    /**
     * Gets the {@link TagRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static TagRepositoryImpl getInstance() {
        return SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private TagRepositoryImpl(final String name) {
        super(name);
    }
}
