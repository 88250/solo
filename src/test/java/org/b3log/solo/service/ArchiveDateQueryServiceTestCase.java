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

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.ArchiveDate;
import org.json.JSONObject;
import org.testng.Assert;

import java.util.Date;
import java.util.List;

/**
 * {@link ArchiveDateQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 11, 2012
 */
public class ArchiveDateQueryServiceTestCase extends AbstractTestCase {

    /**
     * Get Archive Dates.
     *
     * @throws Exception exception
     */
    public void getArchiveDates() throws Exception {
        final ArchiveDateQueryService archiveDateQueryService = getArchiveDateQueryService();

        final List<JSONObject> archiveDates = archiveDateQueryService.getArchiveDates();

        Assert.assertNotNull(archiveDates);
        Assert.assertEquals(archiveDates.size(), 1);
    }

    /**
     * Get By Archive Date String.
     *
     * @throws Exception exception
     */
    public void getByArchiveDateString() throws Exception {
        final ArchiveDateQueryService archiveDateQueryService = getArchiveDateQueryService();

        final String archiveDateString = DateFormatUtils.format(new Date(), "yyyy/MM");
        final JSONObject result = archiveDateQueryService.getByArchiveDateString(archiveDateString);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(ArchiveDate.ARCHIVE_DATE).getLong(ArchiveDate.ARCHIVE_TIME),
                DateUtils.parseDate(archiveDateString, new String[]{"yyyy/MM"}).getTime());
    }
}
