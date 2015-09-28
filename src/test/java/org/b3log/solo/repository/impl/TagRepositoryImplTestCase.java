/*
 * Copyright (c) 2010-2015, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.b3log.solo.repository.TagRepository;
import org.json.JSONObject;
import org.testng.annotations.Test;

/**
 * {@link TagRepositoryImpl} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 30, 2011
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
        tag.put(Tag.TAG_REFERENCE_COUNT, 1);
        tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, 0);

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
        Assert.assertEquals(found.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT),
                            0);
        Assert.assertEquals(found.getInt(Tag.TAG_REFERENCE_COUNT), 1);

        final JSONObject notFound = tagRepository.getByTitle("");
        Assert.assertNull(notFound);
    }

    /**
     * Get Most Used Tags.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getMostUsedTags() throws Exception {
        final TagRepository tagRepository = getTagRepository();

        final JSONObject tag = new JSONObject();

        tag.put(Tag.TAG_TITLE, "tag title2");
        tag.put(Tag.TAG_REFERENCE_COUNT, 3);
        tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, 3);

        final Transaction transaction = tagRepository.beginTransaction();
        tagRepository.add(tag);
        transaction.commit();

        List<JSONObject> mostUsedTags = tagRepository.getMostUsedTags(3);
        Assert.assertNotNull(mostUsedTags);
        Assert.assertEquals(mostUsedTags.size(), 2);

        mostUsedTags = tagRepository.getMostUsedTags(1);
        Assert.assertNotNull(mostUsedTags);
        Assert.assertEquals(mostUsedTags.size(), 1);
        Assert.assertEquals(mostUsedTags.get(0).getInt(
                Tag.TAG_PUBLISHED_REFERENCE_COUNT), 3);
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
        Assert.assertEquals(tags.size(), 1);

        tags = tagRepository.getByArticleId("not found");
        Assert.assertNotNull(tags);
        Assert.assertEquals(tags.size(), 0);
    }

    private void addTagArticle() throws Exception {
        final TagArticleRepository tagArticleRepository =
                getTagArticleRepository();

        final JSONObject tagArticle = new JSONObject();

        tagArticle.put(Article.ARTICLE + "_" + Keys.OBJECT_ID, "article1 id");
        tagArticle.put(Tag.TAG + "_" + Keys.OBJECT_ID, "tag1 id");

        final Transaction transaction = tagArticleRepository.beginTransaction();
        tagArticleRepository.add(tagArticle);
        transaction.commit();
    }
}
