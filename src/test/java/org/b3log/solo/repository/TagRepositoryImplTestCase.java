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

import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link TagRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 28, 2019
 */
@Test(suiteName = "repository")
public class TagRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Add.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final TagRepository tagRepository = getTagRepository();

        final JSONObject tag = new JSONObject();
        tag.put(Tag.TAG_TITLE, "tag title1");

        final Transaction transaction = tagRepository.beginTransaction();
        tagRepository.add(tag);
        transaction.commit();
    }

    /**
     * Get By Title.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByTitle() throws Exception {
        final TagRepository tagRepository = getTagRepository();

        final JSONObject found = tagRepository.getByTitle("tag title1");
        Assert.assertNotNull(found);
        Assert.assertEquals(found.getString(Tag.TAG_TITLE), "tag title1");

        final JSONObject notFound = tagRepository.getByTitle("");
        Assert.assertNull(notFound);
    }

    /**
     * Get By ArticleId.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByArticleId() throws Exception {
        addTagArticle();

        final TagRepository tagRepository = getTagRepository();

        List<JSONObject> tags = tagRepository.getByArticleId("article1 id");
        Assert.assertNotNull(tags);
        Assert.assertEquals(1, tags.size());

        tags = tagRepository.getByArticleId("not found");
        Assert.assertNotNull(tags);
        Assert.assertEquals(0, tags.size());
    }

    private void addTagArticle() throws Exception {
        final TagArticleRepository tagArticleRepository
                = getTagArticleRepository();

        final JSONObject tagArticle = new JSONObject();

        tagArticle.put(Article.ARTICLE + "_" + Keys.OBJECT_ID, "article1 id");
        tagArticle.put(Tag.TAG + "_" + Keys.OBJECT_ID, "tag1 id");

        final Transaction transaction = tagArticleRepository.beginTransaction();
        tagArticleRepository.add(tagArticle);
        transaction.commit();
    }
}
