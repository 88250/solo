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


import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.CategoryTagRepository;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Tag management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Mar 31, 2017
 * @since 0.4.0
 */
@Service
public class TagMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagMgmtService.class);

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Category-tag repository.
     */
    @Inject
    private CategoryTagRepository categoryTagRepository;

    /**
     * Decrements reference count of every tag of an published article specified
     * by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException       json exception
     * @throws RepositoryException repository exception
     */
    public void decTagPublishedRefCount(final String articleId) throws JSONException, RepositoryException {
        final List<JSONObject> tags = tagRepository.getByArticleId(articleId);

        for (final JSONObject tag : tags) {
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);

            tag.put(Tag.TAG_REFERENCE_COUNT, refCnt);
            final int publishedRefCnt = tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);

            tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedRefCnt - 1);
            tagRepository.update(tagId, tag);
        }
    }

    /**
     * Removes all unused tags.
     *
     * @throws ServiceException if get tags failed, or remove failed
     */
    public void removeUnusedTags() throws ServiceException {
        final Transaction transaction = tagRepository.beginTransaction();

        try {
            final List<JSONObject> tags = tagQueryService.getTags();

            for (int i = 0; i < tags.size(); i++) {
                final JSONObject tag = tags.get(i);
                final int tagRefCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);

                if (0 == tagRefCnt) {
                    final String tagId = tag.getString(Keys.OBJECT_ID);

                    categoryTagRepository.removeByTagId(tagId);
                    tagRepository.remove(tagId);
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Removes unused tags failed", e);

            throw new ServiceException(e);
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

    /**
     * Sets the tag query service with the specified tag query service.
     *
     * @param tagQueryService the specified tag query service
     */
    public void setTagQueryService(final TagQueryService tagQueryService) {
        this.tagQueryService = tagQueryService;
    }
}
