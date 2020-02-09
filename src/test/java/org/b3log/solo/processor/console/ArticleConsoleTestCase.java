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
     *
     * @throws Exception exception
     */
    public void getArticleThumbs() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/thumbs");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
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
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/" + articleId);
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
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
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    /**
     * removeArticle.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "updateArticle")
    public void removeArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/" + articleId);
        request.setMethod("DELETE");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    /**
     * cancelPublishArticle.
     *
     * @throws Exception exception
     */
    public void cancelPublishArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/unpublish/" + articleId);
        request.setMethod("PUT");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    /**
     * cancelTopArticle.
     *
     * @throws Exception exception
     */
    public void cancelTopArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/canceltop/" + articleId);
        request.setMethod("PUT");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    /**
     * putTopArticle.
     *
     * @throws Exception exception
     */
    public void putTopArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article/puttop/" + articleId);
        request.setMethod("PUT");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    /**
     * updateArticle.
     *
     * @throws Exception exception
     */
    public void updateArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);

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
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    /**
     * addArticle.
     *
     * @throws Exception exception
     */
    public void addArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
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
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }
}
