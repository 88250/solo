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

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.ArchiveDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

/**
 * Archive date repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Sep 11, 2019
 * @since 0.3.1
 */
@Repository
public class ArchiveDateRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArchiveDateRepository.class);

    /**
     * Archive date-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;

    /**
     * Public constructor.
     */
    public ArchiveDateRepository() {
        super(ArchiveDate.ARCHIVE_DATE.toLowerCase());
    }

    /**
     * Gets an archive date by the specified archive date string.
     *
     * @param archiveDate the specified archive date stirng (yyyy/MM)
     * @return an archive date, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByArchiveDate(final String archiveDate) throws RepositoryException {
        long time;
        try {
            time = DateUtils.parseDate(archiveDate, new String[]{"yyyy/MM"}).getTime();
        } catch (final ParseException e) {
            return null;
        }

        LOGGER.log(Level.TRACE, "Archive date [{}] parsed to time [{}]", archiveDate, time);

        Query query = new Query().setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_TIME, FilterOperator.EQUAL, time)).setPageCount(1);
        JSONObject result = get(query);
        JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (0 == array.length()) {
            // Try to fix wired timezone issue: https://github.com/b3log/solo/issues/12435
            try {
                time = DateUtils.parseDate(archiveDate, new String[]{"yyyy/MM"}).getTime();
                time += 60 * 1000 * 60 * 8;
            } catch (final ParseException e) {
                return null;
            }

            LOGGER.log(Level.TRACE, "Fix archive date [{}] parsed to time [{}]", archiveDate, time);

            query = new Query().setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_TIME, FilterOperator.EQUAL, time)).setPageCount(1);
            result = get(query);
            array = result.optJSONArray(Keys.RESULTS);
            if (0 == array.length()) {
                return null;
            }
        }

        return array.optJSONObject(0);
    }

    /**
     * Get archive dates.
     *
     * @return a list of archive date, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getArchiveDates() throws RepositoryException {
        final Query query = new Query().addSort(ArchiveDate.ARCHIVE_TIME, SortDirection.DESCENDING).setPageCount(1);
        // TODO: Performance issue
        final List<JSONObject> ret = getList(query);
        for (final JSONObject archiveDate : ret) {
            final String archiveDateId = archiveDate.optString(Keys.OBJECT_ID);
            final int publishedArticleCount = archiveDateArticleRepository.getPublishedArticleCount(archiveDateId);
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_T_PUBLISHED_ARTICLE_COUNT, publishedArticleCount);
        }

        return ret;
    }
}
