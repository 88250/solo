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
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * {@link ArticleRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Jul 8, 2020
 */
@Test(suiteName = "repository")
public final class ArticleRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Adds successfully.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final ArticleRepository articleRepository = getArticleRepository();

        final JSONObject article = new JSONObject();

        article.put(Article.ARTICLE_TITLE, "article title1");
        article.put(Article.ARTICLE_ABSTRACT, "article abstract");
        article.put(Article.ARTICLE_ABSTRACT_TEXT, "article abstract text");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2");
        article.put(Article.ARTICLE_AUTHOR_ID, "1");
        article.put(Article.ARTICLE_CONTENT, "article content");
        article.put(Article.ARTICLE_PERMALINK, "article permalink1");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Article.ARTICLE_PUT_TOP, false);
        article.put(Article.ARTICLE_CREATED, new Date().getTime());
        article.put(Article.ARTICLE_UPDATED, new Date().getTime());
        article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");
        article.put(Article.ARTICLE_IMG1_URL, Article.getArticleImg1URL(article));

        final Transaction transaction = articleRepository.beginTransaction();
        articleRepository.add(article);
        transaction.commit();

        final List<JSONObject> results = (List<JSONObject>) articleRepository.getByAuthorId("1", 1, Integer.MAX_VALUE).opt(Keys.RESULTS);

        Assert.assertEquals(results.size(), 1);
    }

    /**
     * Get by permalink.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByPermalink() throws Exception {
        final ArticleRepository articleRepository = getArticleRepository();
        final JSONObject article = articleRepository.getByPermalink("article permalink1");

        Assert.assertNotNull(article);
        Assert.assertEquals(article.getString(Article.ARTICLE_TITLE), "article title1");

        Assert.assertNull(articleRepository.getByPermalink("not found"));
    }

    /**
     * Get by permalink.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = {"add"})
    public void previousAndNext() throws Exception {
        final ArticleRepository articleRepository = getArticleRepository();

        final JSONObject article = new JSONObject();

        article.put(Article.ARTICLE_TITLE, "article title2");
        article.put(Article.ARTICLE_ABSTRACT, "article abstract");
        article.put(Article.ARTICLE_ABSTRACT_TEXT, "article abstract text");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2");
        article.put(Article.ARTICLE_AUTHOR_ID, "1");
        article.put(Article.ARTICLE_CONTENT, "article content");
        article.put(Article.ARTICLE_PERMALINK, "article permalink2");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Article.ARTICLE_PUT_TOP, false);
        article.put(Article.ARTICLE_CREATED, new Date().getTime());
        article.put(Article.ARTICLE_UPDATED, new Date().getTime());
        article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");
        article.put(Article.ARTICLE_IMG1_URL, Article.getArticleImg1URL(article));

        final Transaction transaction = articleRepository.beginTransaction();
        articleRepository.add(article);
        transaction.commit();

        Assert.assertEquals(articleRepository.count(), 3);

        JSONObject previousArticle = articleRepository.getPreviousArticle(article.getString(Keys.OBJECT_ID));

        Assert.assertNotNull(previousArticle);
        Assert.assertEquals(previousArticle.getString(Article.ARTICLE_TITLE), "article title1");
        Assert.assertEquals(previousArticle.getString(Article.ARTICLE_PERMALINK), "article permalink1");
        Assert.assertNull(previousArticle.opt(Keys.OBJECT_ID));

        previousArticle = articleRepository.getByPermalink(previousArticle.getString(Article.ARTICLE_PERMALINK));

        final JSONObject nextArticle = articleRepository.getNextArticle(previousArticle.getString(Keys.OBJECT_ID));
        Assert.assertNotNull(previousArticle);
        Assert.assertEquals(nextArticle.getString(Article.ARTICLE_TITLE), "article title2");
    }

    /**
     * Get Randomly.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = {"add", "previousAndNext"})
    public void getRandomly() throws Exception {
        final ArticleRepository articleRepository = getArticleRepository();

        List<JSONObject> articles = articleRepository.getRandomly(3);
        Assert.assertNotNull(articles);
    }

    /**
     * Get Recent Articles.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = {"add", "previousAndNext"})
    public void getRecentArticles() throws Exception {
        final ArticleRepository articleRepository = getArticleRepository();

        Assert.assertEquals(articleRepository.count(), 3);

        List<JSONObject> recentArticles = articleRepository.getRecentArticles(3);
        Assert.assertNotNull(recentArticles);
        Assert.assertEquals(recentArticles.size(), 3);

        Assert.assertEquals(recentArticles.get(0).getString(Article.ARTICLE_TITLE), "article title2");
        Assert.assertEquals(recentArticles.get(1).getString(Article.ARTICLE_TITLE), "article title1");
    }

    /**
     * Is Published.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = {"add"})
    public void isPublished() throws Exception {
        final ArticleRepository articleRepository = getArticleRepository();

        final List<JSONObject> all = (List<JSONObject>) articleRepository.get(new Query()).opt(Keys.RESULTS);
        Assert.assertNotNull(all);

        final JSONObject article = all.get(0);
        Assert.assertTrue(articleRepository.isPublished(article.getString(Keys.OBJECT_ID)));

        final JSONObject published = articleRepository.getByPermalink("article permalink1");
        Assert.assertNotNull(published);
        Assert.assertEquals(Article.ARTICLE_STATUS_C_PUBLISHED, published.optInt(Article.ARTICLE_STATUS));

        Assert.assertFalse(articleRepository.isPublished("not found"));
    }
}
