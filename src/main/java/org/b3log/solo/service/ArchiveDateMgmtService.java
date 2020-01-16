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
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONObject;

import java.util.List;

/**
 * Archive date query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 20, 2019
 * @since 3.4.0
 */
@Service
public class ArchiveDateMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArchiveDateMgmtService.class);

    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;

    /**
     * Archive date-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;


    /**
     * Removes all unused archive dates.
     *
     * @return a list of archive dates, returns an empty list if not found
     */
    public void removeUnusedArchiveDates() {
        final Transaction transaction = archiveDateRepository.beginTransaction();
        try {
            final List<JSONObject> archiveDates = archiveDateRepository.getArchiveDates();
            for (final JSONObject archiveDate : archiveDates) {
                if (1 > archiveDate.optInt(ArchiveDate.ARCHIVE_DATE_T_PUBLISHED_ARTICLE_COUNT)) {
                    final String archiveDateId = archiveDate.optString(Keys.OBJECT_ID);
                    archiveDateRepository.remove(archiveDateId);
                }
            }
            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Gets archive dates failed", e);
        }
    }
}
