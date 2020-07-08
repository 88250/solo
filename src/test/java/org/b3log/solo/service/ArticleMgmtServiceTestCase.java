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
package org.b3log.solo.service;

import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link ArticleMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Sep 16, 2018
 */
@Test(suiteName = "service")
public class ArticleMgmtServiceTestCase extends AbstractTestCase {

    /**
     * Add Article.
     *
     * @throws Exception exception
     */
    public void addArticle() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        final JSONObject admin = getUserQueryService().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        article.put(Article.ARTICLE_AUTHOR_ID, userId);
        article.put(Article.ARTICLE_TITLE, "article1 title");
        article.put(Article.ARTICLE_ABSTRACT, "article1 abstract");
        article.put(Article.ARTICLE_CONTENT, "article1 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article1 permalink");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");

        final String articleId = articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);
    }

    /**
     * Add Article without permalink.
     *
     * @throws Exception exception
     */
    public void addArticleWithoutPermalink() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        final JSONObject admin = getUserQueryService().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        article.put(Article.ARTICLE_AUTHOR_ID, userId);
        article.put(Article.ARTICLE_TITLE, "article1 title");
        article.put(Article.ARTICLE_ABSTRACT, "article1 abstract");
        article.put(Article.ARTICLE_CONTENT, "article1 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");

        final String articleId = articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);
    }

    /**
     * Update Article.
     *
     * @throws Exception exception
     */
    public void updateArticle() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        final JSONObject admin = getUserQueryService().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        article.put(Article.ARTICLE_AUTHOR_ID, userId);
        article.put(Article.ARTICLE_TITLE, "article2 title");
        article.put(Article.ARTICLE_ABSTRACT, "article2 abstract");
        article.put(Article.ARTICLE_CONTENT, "article2 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article2 permalink");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");

        final String articleId = articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);

        article.put(Keys.OBJECT_ID, articleId);
        article.put(Article.ARTICLE_TITLE, "updated article2 title");

        articleMgmtService.updateArticle(requestJSONObject);

        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject updated = articleQueryService.getArticleById(articleId);
        Assert.assertNotNull(updated);
        Assert.assertEquals(updated.getString(Article.ARTICLE_TITLE), "updated article2 title");
    }

    /**
     * Remove Article.
     *
     * @throws Exception exception
     */
    public void removeArticle() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        final JSONObject admin = getUserQueryService().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        article.put(Article.ARTICLE_AUTHOR_ID, userId);
        article.put(Article.ARTICLE_TITLE, "article3 title");
        article.put(Article.ARTICLE_ABSTRACT, "article3 abstract");
        article.put(Article.ARTICLE_CONTENT, "article3 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article3 permalink");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");

        final String articleId = articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);

        articleMgmtService.removeArticle(articleId);

        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject updated = articleQueryService.getArticleById(articleId);
        Assert.assertNull(updated);
    }

    /**
     * Top Article.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addArticle")
    public void topArticle() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();
        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        final List<JSONObject> articles = (List<JSONObject>) articleQueryService.getArticles(paginationRequest).opt(Article.ARTICLES);

        Assert.assertNotEquals(articles.size(), 0);
        final JSONObject article = articles.get(0);

        final String articleId = article.getString(Keys.OBJECT_ID);
        articleMgmtService.topArticle(articleId, true);
        articleMgmtService.topArticle(articleId, false);
    }

    /**
     * Cancel Publish Article.
     *
     * @throws Exception exception
     */
    public void cancelPublishArticle() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        final JSONObject admin = getUserQueryService().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        article.put(Article.ARTICLE_AUTHOR_ID, userId);
        article.put(Article.ARTICLE_TITLE, "article4 title");
        article.put(Article.ARTICLE_ABSTRACT, "article4 abstract");
        article.put(Article.ARTICLE_CONTENT, "article4 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article4 permalink");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_VIEW_PWD, "");

        final String articleId = articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);

        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        List<JSONObject> articles = (List<JSONObject>) articleQueryService.getArticles(paginationRequest).opt(Article.ARTICLES);

        int articleCount = articles.size();
        Assert.assertNotEquals(articleCount, 0);

        articleMgmtService.cancelPublishArticle(articleId);
        articles = (List<JSONObject>) articleQueryService.getArticles(paginationRequest).opt(Article.ARTICLES);
        Assert.assertEquals(articles.size(), articleCount - 1);
    }

    /**
     * Update Articles Random Value.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addArticle")
    public void updateArticlesRandomValue() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();
        final ArticleQueryService articleQueryService = getArticleQueryService();

        List<JSONObject> articles = articleQueryService.getRecentArticles(10);
        Assert.assertNotEquals(articles.size(), 0);

        final JSONObject article = articles.get(0);
        final String articleId = article.getString(Keys.OBJECT_ID);
        double randomValue = article.getDouble(Article.ARTICLE_RANDOM_DOUBLE);
        articleMgmtService.updateArticlesRandomValue(Integer.MAX_VALUE);

        //Assert.assertNotEquals(articleQueryService.getArticleById(articleId).
        //        getDouble(Article.ARTICLE_RANDOM_DOUBLE), randomValue);
    }
}
