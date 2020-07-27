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
