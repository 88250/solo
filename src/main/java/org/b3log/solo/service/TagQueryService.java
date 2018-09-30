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
package org.b3log.solo.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Tag query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.4, Aug 27, 2018
 * @since 0.4.0
 */
@Service
public class TagQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagQueryService.class);

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Gets a tag by the specified tag title.
     *
     * @param tagTitle the specified tag title
     * @return for example,      <pre>
     * {
     *     "tag": {
     *         "oId": "",
     *         "tagTitle": "",
     *         "tagReferenceCount": int,
     *         "tagPublishedRefCount": int
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getTagByTitle(final String tagTitle) throws ServiceException {
        try {
            final JSONObject ret = new JSONObject();

            final JSONObject tag = tagRepository.getByTitle(tagTitle);
            if (null == tag) {
                return null;
            }

            ret.put(Tag.TAG, tag);
            LOGGER.log(Level.DEBUG, "Got an tag[title={0}]", tagTitle);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the count of tags.
     *
     * @return count of tags
     * @throws ServiceException service exception
     */
    public long getTagCount() throws ServiceException {
        try {
            return tagRepository.count();
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets tags failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets all tags.
     *
     * @return for example,      <pre>
     * [
     *     {"tagTitle": "", "tagReferenceCount": int, ....},
     *     ....
     * ]
     * </pre>, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getTags() throws ServiceException {
        try {
            final Query query = new Query().setPageCount(1);

            return tagRepository.getList(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets tags failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets top (reference count descending) tags.
     *
     * @param fetchSize the specified fetch size
     * @return for example,      <pre>
     * [
     *     {"tagTitle": "", "tagReferenceCount": int, ....},
     *     ....
     * ]
     * </pre>, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getTopTags(final int fetchSize) throws ServiceException {
        try {
            final Query query = new Query().setPageCount(1).setPageSize(fetchSize).
                    addSort(Tag.TAG_PUBLISHED_REFERENCE_COUNT, SortDirection.DESCENDING);

            return tagRepository.getList(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets top tags failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets bottom (reference count ascending) tags.
     *
     * @param fetchSize the specified fetch size
     * @return for example,      <pre>
     * [
     *     {"tagTitle": "", "tagReferenceCount": int, ....},
     *     ....
     * ]
     * </pre>, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getBottomTags(final int fetchSize) throws ServiceException {
        try {
            final Query query = new Query().setPageCount(1).setPageSize(fetchSize).
                    addSort(Tag.TAG_PUBLISHED_REFERENCE_COUNT, SortDirection.ASCENDING);

            return tagRepository.getList(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets bottom tags failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Removes tags of unpublished articles from the specified tags.
     *
     * @param tags the specified tags
     * @throws JSONException       json exception
     * @throws RepositoryException repository exception
     */
    public void removeForUnpublishedArticles(final List<JSONObject> tags) throws JSONException, RepositoryException {
        final Iterator<JSONObject> iterator = tags.iterator();
        while (iterator.hasNext()) {
            final JSONObject tag = iterator.next();

            if (0 == tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT)) {
                iterator.remove();
            }
        }
    }

    /**
     * Sets the tag repository with the specified tag repository.
     *
     * @param tagRepository the specified tag repository
     */
    public void setTagRepository(final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}
