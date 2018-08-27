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

import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

/**
 * Archive date repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.10, Aug 27, 2018
 * @since 0.3.1
 */
@Repository
public class ArchiveDateRepositoryImpl extends AbstractRepository implements ArchiveDateRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArchiveDateRepositoryImpl.class);

    /**
     * Public constructor.
     */
    public ArchiveDateRepositoryImpl() {
        super(ArchiveDate.ARCHIVE_DATE);
    }

    @Override
    public JSONObject getByArchiveDate(final String archiveDate) throws RepositoryException {
        long time = 0L;

        try {
            time = DateUtils.parseDate(archiveDate, new String[]{"yyyy/MM"}).getTime();
        } catch (final ParseException e) {
            return null;
        }

        LOGGER.log(Level.TRACE, "Archive date [{0}] parsed to time [{1}]", archiveDate, time);

        Query query = new Query().setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_TIME, FilterOperator.EQUAL, time)).
                setPageCount(1);
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

            LOGGER.log(Level.TRACE, "Fix archive date [{0}] parsed to time [{1}]", archiveDate, time);

            query = new Query().setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_TIME, FilterOperator.EQUAL, time)).
                    setPageCount(1);
            result = get(query);
            array = result.optJSONArray(Keys.RESULTS);
            if (0 == array.length()) {
                return null;
            }
        }

        return array.optJSONObject(0);
    }

    @Override
    public List<JSONObject> getArchiveDates() throws RepositoryException {
        final org.b3log.latke.repository.Query query = new Query().
                addSort(ArchiveDate.ARCHIVE_TIME, SortDirection.DESCENDING).setPageCount(1);
        final List<JSONObject> ret = getList(query);

        removeForUnpublishedArticles(ret);

        return ret;
    }

    /**
     * Removes archive dates of unpublished articles from the specified archive
     * dates.
     *
     * @param archiveDates the specified archive dates
     */
    private void removeForUnpublishedArticles(final List<JSONObject> archiveDates) {
        final Iterator<JSONObject> iterator = archiveDates.iterator();

        while (iterator.hasNext()) {
            final JSONObject archiveDate = iterator.next();

            if (0 == archiveDate.optInt(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
                iterator.remove();
            }
        }
    }
}
