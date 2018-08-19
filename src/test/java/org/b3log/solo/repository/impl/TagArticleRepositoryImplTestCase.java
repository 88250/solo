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

import java.util.List;
import junit.framework.Assert;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagArticleRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

/**
 * {@link TagArticleRepositoryImpl} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 30, 2011
 */
@Test(suiteName = "repository")
public class TagArticleRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Add.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final TagArticleRepository tagArticleRepository = getTagArticleRepository();

        final JSONObject tagArticle = new JSONObject();

        tagArticle.put(Article.ARTICLE + "_" + Keys.OBJECT_ID, "article1 id");
        tagArticle.put(Tag.TAG + "_" + Keys.OBJECT_ID, "tag1 id");

        final Transaction transaction = tagArticleRepository.beginTransaction();
        tagArticleRepository.add(tagArticle);
        transaction.commit();
    }

    /**
     * Get By ArticleId.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByArticleId() throws Exception {
        final TagArticleRepository tagArticleRepository
                = getTagArticleRepository();

        final List<JSONObject> tagArticle
                = tagArticleRepository.getByArticleId("article1 id");
        Assert.assertNotNull(tagArticle);

        Assert.assertEquals(0, tagArticleRepository.getByArticleId("").size());
    }

    /**
     * Get By TagId.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByTagId() throws Exception {
        final TagArticleRepository tagArticleRepository
                = getTagArticleRepository();

        final JSONArray results
                = tagArticleRepository.getByTagId("tag1 id", 1, Integer.MAX_VALUE).
                getJSONArray(Keys.RESULTS);
        Assert.assertEquals(1, results.length());
    }
}
