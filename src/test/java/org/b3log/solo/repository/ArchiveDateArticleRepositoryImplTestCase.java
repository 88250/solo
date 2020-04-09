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

import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link ArchiveDateArticleRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 14, 2019
 */
@Test(suiteName = "repository")
public class ArchiveDateArticleRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Adds successfully.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final ArchiveDateArticleRepository archiveDateArticleRepository = getArchiveDateArticleRepository();

        final JSONObject archiveDateArticle = new JSONObject();

        archiveDateArticle.put(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID, "archiveDateId");
        archiveDateArticle.put(Article.ARTICLE + "_" + Keys.OBJECT_ID, "articleId");

        final Transaction transaction = archiveDateArticleRepository.beginTransaction();
        archiveDateArticleRepository.add(archiveDateArticle);
        transaction.commit();

        final JSONObject found = archiveDateArticleRepository.getByArticleId("articleId");
        Assert.assertNotNull(found);

        final JSONObject notFound = archiveDateArticleRepository.getByArticleId("not found");
        Assert.assertNull(notFound);
    }

    /**
     * Get By Archive Id.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByArticleId() throws Exception {
        final ArchiveDateArticleRepository archiveDateArticleRepository = getArchiveDateArticleRepository();

        Assert.assertNotNull(archiveDateArticleRepository.getByArticleId("articleId"));
        Assert.assertNull(archiveDateArticleRepository.getByArticleId("not found"));
    }
}
