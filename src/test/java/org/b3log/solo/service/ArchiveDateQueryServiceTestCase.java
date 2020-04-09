/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
