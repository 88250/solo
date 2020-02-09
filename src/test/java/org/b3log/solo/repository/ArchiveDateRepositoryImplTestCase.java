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
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.ArchiveDate;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link ArchiveDateRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Jan 28, 2019
 */
@Test(suiteName = "repository")
public class ArchiveDateRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Adds successfully.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final ArchiveDateRepository archiveDateRepository = getArchiveDateRepository();

        final JSONObject archiveDate = new JSONObject();
        archiveDate.put(ArchiveDate.ARCHIVE_TIME, DateUtils.parseDate("2011/12", new String[]{"yyyy/MM"}).getTime());

        final Transaction transaction = archiveDateRepository.beginTransaction();
        archiveDateRepository.add(archiveDate);
        transaction.commit();

        final List<JSONObject> archiveDates = archiveDateRepository.getArchiveDates();
        Assert.assertNotNull(archiveDates);
        Assert.assertEquals(archiveDates.size(), 2);
    }

    /**
     * Get By ArchiveDate.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByArchiveDate() throws Exception {
        final ArchiveDateRepository archiveDateRepository = getArchiveDateRepository();

        final JSONObject archiveDate = archiveDateRepository.getByArchiveDate("2011/12");
        Assert.assertNotNull(archiveDate);
    }
}
