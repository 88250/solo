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
 * @version 1.0.0.0, Dec 31, 2011
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
     * Get By ArchiveDate Id.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByArchiveDateId() throws Exception {
        final ArchiveDateArticleRepository archiveDateArticleRepository = getArchiveDateArticleRepository();

        final JSONObject found = archiveDateArticleRepository.getByArchiveDateId("archiveDateId", 1, Integer.MAX_VALUE);
        Assert.assertNotNull(found);

        final JSONObject notFound = archiveDateArticleRepository.getByArchiveDateId("not found", 1, Integer.MAX_VALUE);
        Assert.assertNotNull(notFound);
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
