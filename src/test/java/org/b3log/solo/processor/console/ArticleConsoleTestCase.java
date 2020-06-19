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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Article;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link ArticleConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Feb 6, 2019
 * @since 2.9.7
 */
@Test(suiteName = "processor")
public class ArticleConsoleTestCase extends AbstractTestCase {

    /**
     * getArticleThumbs.
     */
    public void getArticleThumbs() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/thumbs");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * markdown2HTML.
     *
     * @throws Exception exception
     */
    public void markdown2HTML() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/markdown/2html");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("markdownText", "**Solo**");
        request.setJSON(requestJSON);

        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<p><strong>Solo<\\/strong><\\/p>"));
    }

    /**
     * getArticle.
     *
     * @throws Exception exception
     */
    public void getArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/" + articleId);
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * getArticles.
     *
     * @throws Exception exception
     */
    public void getArticles() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/articles/status/published/1/10/20");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * removeArticle.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "updateArticle")
    public void removeArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/" + articleId);
        request.setMethod("DELETE");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * cancelPublishArticle.
     *
     * @throws Exception exception
     */
    public void cancelPublishArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/unpublish/" + articleId);
        request.setMethod("PUT");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * cancelTopArticle.
     *
     * @throws Exception exception
     */
    public void cancelTopArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/canceltop/" + articleId);
        request.setMethod("PUT");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * putTopArticle.
     *
     * @throws Exception exception
     */
    public void putTopArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/puttop/" + articleId);
        request.setMethod("PUT");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * updateArticle.
     *
     * @throws Exception exception
     */
    public void updateArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Article.ARTICLE, article);
        request.setJSON(requestJSON);

        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }

    /**
     * addArticle.
     *
     * @throws Exception exception
     */
    public void addArticle() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
        article.put(Keys.OBJECT_ID, "");
        article.put(Article.ARTICLE_PERMALINK, "");

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Article.ARTICLE, article);
        request.setJSON(requestJSON);

        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"code\":0"));
    }
}
