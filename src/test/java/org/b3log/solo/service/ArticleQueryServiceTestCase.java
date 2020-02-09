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
package org.b3log.solo.service;

import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link ArticleQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.2, Jan 28, 2019
 */
@Test(suiteName = "service")
public class ArticleQueryServiceTestCase extends AbstractTestCase {

    /**
     * Search articles.
     *
     * @throws Exception exception
     */
    public void searchKeyword() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();

        JSONObject result = articleQueryService.searchKeyword("初始化", 1, 20);
        Assert.assertNotNull(result);
        List<JSONObject> articles = (List<JSONObject>) result.opt(Article.ARTICLES);
        Assert.assertEquals(articles.size(), 1);

        result = articleQueryService.searchKeyword("不存在的", 1, 20);
        Assert.assertNotNull(result);
        articles = (List<JSONObject>) result.opt(Article.ARTICLES);
        Assert.assertEquals(articles.size(), 0);
    }

    /**
     * Get Recent Articles.
     *
     * @throws Exception exception
     */
    public void getRecentArticles() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();
        final List<JSONObject> articles = articleQueryService.getRecentArticles(10);

        Assert.assertEquals(articles.size(), 1);
    }

    /**
     * Get Article.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getRecentArticles")
    public void getArticle() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();
        final List<JSONObject> articles = articleQueryService.getRecentArticles(10);

        Assert.assertEquals(articles.size(), 1);

        final String articleId = articles.get(0).getString(Keys.OBJECT_ID);
        final JSONObject article = articleQueryService.getArticle(articleId);

        Assert.assertNotNull(article);
        Assert.assertEquals(article.optString(Article.ARTICLE_VIEW_COUNT), "");
    }

    /**
     * Get Article By Id.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getRecentArticles")
    public void getArticleById() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();
        final List<JSONObject> articles = articleQueryService.getRecentArticles(10);

        Assert.assertEquals(articles.size(), 1);

        final String articleId = articles.get(0).getString(Keys.OBJECT_ID);
        final JSONObject article = articleQueryService.getArticleById(articleId);

        Assert.assertNotNull(article);
    }

    /**
     * Get Article Content.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getRecentArticles")
    public void getArticleContent() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();

        final List<JSONObject> articles = articleQueryService.getRecentArticles(10);

        Assert.assertEquals(articles.size(), 1);

        final String articleId = articles.get(0).getString(Keys.OBJECT_ID);

        Assert.assertNotNull(articleQueryService.getArticleContent(null, articleId));
    }

    /**
     * Get Articles By Tag.
     *
     * @throws Exception exception
     */
    public void getArticlesByTag() throws Exception {
        final TagQueryService tagQueryService = getTagQueryService();

        JSONObject result = tagQueryService.getTagByTitle("Solo");
        Assert.assertNotNull(result);

        final JSONObject tag = result.getJSONObject(Tag.TAG);
        Assert.assertNotNull(tag);

        final String tagId = tag.getString(Keys.OBJECT_ID);

        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject articlesResult = articleQueryService.getArticlesByTag(tagId, 1, Integer.MAX_VALUE);
        Assert.assertNotNull(articlesResult);
        final List<JSONObject> articles = (List<JSONObject>) articlesResult.opt(Keys.RESULTS);
        Assert.assertEquals(articles.size(), 1);
    }

    /**
     * Get Archives By Archive Date.
     *
     * @throws Exception exception
     */
    public void getArticlesByArchiveDate() throws Exception {
        final ArchiveDateQueryService archiveDateQueryService = getArchiveDateQueryService();

        final List<JSONObject> archiveDates = archiveDateQueryService.getArchiveDates();

        Assert.assertNotNull(archiveDates);
        Assert.assertEquals(archiveDates.size(), 1);

        final JSONObject archiveDate = archiveDates.get(0);

        final ArticleQueryService articleQueryService = getArticleQueryService();
        List<JSONObject> articles =
                articleQueryService.getArticlesByArchiveDate(archiveDate.getString(Keys.OBJECT_ID), 1, Integer.MAX_VALUE);
        Assert.assertNotNull(articles);
        Assert.assertEquals(articles.size(), 1);

        articles = articleQueryService.getArticlesByArchiveDate("not found", 1, Integer.MAX_VALUE);
        Assert.assertNotNull(articles);
        Assert.assertTrue(articles.isEmpty());
    }
}
