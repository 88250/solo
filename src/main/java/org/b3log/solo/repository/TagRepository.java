/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.Tag;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Jun 20, 2019
 * @since 0.3.1
 */
@Repository
public class TagRepository extends AbstractRepository {

    /**
     * Public constructor.
     */
    public TagRepository() {
        super(Tag.TAG);
    }

    /**
     * Tag-Article relation repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Gets tags of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @return a list of tags of the specified article, returns an empty list
     * if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getByArticleId(final String articleId) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<>();

        final List<JSONObject> tagArticleRelations = tagArticleRepository.getByArticleId(articleId);
        for (final JSONObject tagArticleRelation : tagArticleRelations) {
            final String tagId = tagArticleRelation.optString(Tag.TAG + "_" + Keys.OBJECT_ID);
            final JSONObject tag = get(tagId);

            ret.add(tag);
        }

        return ret;
    }

    /**
     * Gets a tag by the specified tag title.
     *
     * @param tagTitle the specified tag title
     * @return a tag, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByTitle(final String tagTitle) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG_TITLE, FilterOperator.EQUAL, tagTitle)).setPageCount(1);

        final JSONObject ret = getFirst(query);
        if (null == ret) {
            return null;
        }

        final String tagId = ret.optString(Keys.OBJECT_ID);
        final int articleCount = tagArticleRepository.getPublishedArticleCount(tagId);
        ret.put(Tag.TAG_T_PUBLISHED_REFERENCE_COUNT, articleCount);

        return ret;
    }
}
