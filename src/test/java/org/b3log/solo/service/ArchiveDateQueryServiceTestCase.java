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

import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.model.User;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.ArchiveDate;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link ArchiveDateQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 11, 2012
 */
public class ArchiveDateQueryServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     * 
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Get Archive Dates.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
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
    @Test(dependsOnMethods = "init")
    public void getByArchiveDateString() throws Exception {
        final ArchiveDateQueryService archiveDateQueryService = getArchiveDateQueryService();

        final String archiveDateString = DateFormatUtils.format(new Date(), "yyyy/MM");
        final JSONObject result = archiveDateQueryService.getByArchiveDateString(archiveDateString);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(ArchiveDate.ARCHIVE_DATE).getLong(ArchiveDate.ARCHIVE_TIME), 
                            DateUtils.parseDate(archiveDateString, new String[] {"yyyy/MM"}).getTime());
    }
}
