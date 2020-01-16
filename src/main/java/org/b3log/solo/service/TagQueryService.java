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
package org.b3log.solo.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONObject;

import java.util.List;

/**
 * Tag query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.7, Jun 20, 2019
 * @since 0.4.0
 */
@Service
public class TagQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(TagQueryService.class);

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Gets article count of a tag specified by the given tag id.
     *
     * @param tagId the given tag id
     * @return article count, returns {@code -1} if occurred an exception
     */
    public int getArticleCount(final String tagId) {
        return tagArticleRepository.getArticleCount(tagId);
    }

    /**
     * Gets a tag by the specified tag title.
     *
     * @param tagTitle the specified tag title
     * @return for example,      <pre>
     * {
     *     "tag": {
     *         "oId": "",
     *         "tagTitle": "",
     *         "tagPublishedRefCount": int
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getTagByTitle(final String tagTitle) throws ServiceException {
        try {
            final JSONObject tag = tagRepository.getByTitle(tagTitle);
            if (null == tag) {
                return null;
            }

            final JSONObject ret = new JSONObject();
            ret.put(Tag.TAG, tag);

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
     */
    public long getTagCount() {
        try {
            return tagRepository.count();
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets tags failed", e);

            return 0;
        }
    }

    /**
     * Gets all tags.
     *
     * @return for example,      <pre>
     * [
     *     {"tagTitle": "", ....},
     *     ....
     * ]
     * </pre>, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getTags() throws ServiceException {
        try {
            final Query query = new Query().setPageCount(1);

            final List<JSONObject> ret = tagRepository.getList(query);
            for (final JSONObject tag : ret) {
                final String tagId = tag.optString(Keys.OBJECT_ID);
                final int articleCount = tagArticleRepository.getPublishedArticleCount(tagId);
                tag.put(Tag.TAG_T_PUBLISHED_REFERENCE_COUNT, articleCount);
            }

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets tags failed", e);

            throw new ServiceException(e);
        }
    }
}
