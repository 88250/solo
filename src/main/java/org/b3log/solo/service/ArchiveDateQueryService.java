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


import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONObject;

import java.util.List;


/**
 * Archive date query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Feb 7, 2012
 * @since 0.4.0
 */
@Service
public class ArchiveDateQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArchiveDateQueryService.class);

    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;

    /**
     * Gets all archive dates.
     * 
     * @return a list of archive dates, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getArchiveDates() throws ServiceException {
        try {
            return archiveDateRepository.getArchiveDates();
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets archive dates failed", e);
            throw new ServiceException("Gets archive dates failed");
        }
    }

    /**
     * Gets an archive date by the specified archive date string.
     * 
     * @param archiveDateString the specified archive date string (yyyy/MM)
     * @return for example,
     * <pre>
     * {
     *     "archiveDate": {
     *         "oId": "",
     *         "archiveTime": "",
     *         "archiveDatePublishedArticleCount": int,
     *         "archiveDateArticleCount": int
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getByArchiveDateString(final String archiveDateString) throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject archiveDate = archiveDateRepository.getByArchiveDate(archiveDateString);
            if (null == archiveDate) {
                return null;
            }

            ret.put(ArchiveDate.ARCHIVE_DATE, archiveDate);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets archive date[string=" + archiveDateString + "] failed", e);
            throw new ServiceException("Gets archive date[string=" + archiveDateString + "] failed");
        }
    }

    /**
     * Sets archive date repository with the specified archive date repository.
     * 
     * @param archiveDateRepository the specified archive date repository
     */
    public void setArchiveDateRepository(final ArchiveDateRepository archiveDateRepository) {
        this.archiveDateRepository = archiveDateRepository;
    }
}
