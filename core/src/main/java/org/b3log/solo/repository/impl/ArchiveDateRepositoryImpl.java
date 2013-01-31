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


import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.*;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Archive date repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Jan 31, 2013
 * @since 0.3.1
 */
public final class ArchiveDateRepositoryImpl extends AbstractRepository implements ArchiveDateRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArchiveDateRepositoryImpl.class.getName());

    /**
     * Singleton.
     */
    private static final ArchiveDateRepositoryImpl SINGLETON = new ArchiveDateRepositoryImpl(ArchiveDate.ARCHIVE_DATE);

    @Override
    public JSONObject getByArchiveDate(final String archiveDate) throws RepositoryException {
        long time = 0L;

        try {
            time = DateUtils.parseDate(archiveDate, new String[] {"yyyy/MM"}).getTime();
        } catch (final ParseException e) {
            LOGGER.log(Level.SEVERE, "Can not parse archive date [" + archiveDate + "]", e);
            throw new RepositoryException("Can not parse archive date [" + archiveDate + "]");
        }

        LOGGER.log(Level.FINEST, "Archive date [{0}] parsed to time [{1}]", new Object[] {archiveDate, time});

        final Query query = new Query();

        query.setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_TIME, FilterOperator.EQUAL, time)).setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public List<JSONObject> getArchiveDates() throws RepositoryException {
        final org.b3log.latke.repository.Query query = new Query().addSort(ArchiveDate.ARCHIVE_TIME, SortDirection.DESCENDING).setPageCount(
            1);
        final JSONObject result = get(query);

        final JSONArray archiveDates = result.optJSONArray(Keys.RESULTS);
        final List<JSONObject> ret = CollectionUtils.jsonArrayToList(archiveDates);

        removeForUnpublishedArticles(ret);

        return ret;
    }

    /**
     * Removes archive dates of unpublished articles from the specified archive
     * dates.
     *
     * @param archiveDates the specified archive dates
     * @throws RepositoryException repository exception
     */
    private void removeForUnpublishedArticles(final List<JSONObject> archiveDates) throws RepositoryException {
        final Iterator<JSONObject> iterator = archiveDates.iterator();

        while (iterator.hasNext()) {
            final JSONObject archiveDate = iterator.next();

            if (0 == archiveDate.optInt(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
                iterator.remove();
            }
        }
    }

    /**
     * Gets the {@link ArchiveDateRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static ArchiveDateRepositoryImpl getInstance() {
        return SINGLETON;
    }

    /**
     * Private constructor.
     *
     * @param name the specified name
     */
    private ArchiveDateRepositoryImpl(final String name) {
        super(name);
    }
}
