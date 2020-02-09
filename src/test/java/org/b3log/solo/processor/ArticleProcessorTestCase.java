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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link ArticleProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Feb 22, 2019
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class ArticleProcessorTestCase extends AbstractTestCase {

    /**
     * getArchivesArticlesByPage.
     */
    @Test
    public void getArchivesArticlesByPage() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/articles/archives/" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM"));
        request.setParameter("p", "1");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * getArticleContent.
     *
     * @throws Exception exception
     */
    @Test
    public void getArticleContent() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/get-article-content");
        request.setParameter("id", articleId);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "Solo 博客系统已经初始化完毕"));
    }

    /**
     * getArticlesByPage.
     */
    @Test
    public void getArticlesByPage() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/articles");
        request.setParameter("p", "1");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * getAuthorsArticlesByPage.
     *
     * @throws Exception exception
     */
    @Test
    public void getAuthorsArticlesByPage() throws Exception {
        final JSONObject admin = getUserRepository().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/articles/authors/" + userId);
        request.setParameter("p", "1");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * getRandomArticles.
     */
    @Test
    public void getRandomArticles() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/articles/random");
        request.setMethod("POST");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "{\"randomArticles"));
    }

    /**
     * getRelevantArticles.
     *
     * @throws Exception exception
     */
    @Test
    public void getRelevantArticles() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/article/id/" + articleId + "/relevant/articles");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "{\"relevantArticles\""));
    }

    /**
     * getTagArticlesByPage.
     */
    @Test
    public void getTagArticlesByPage() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/articles/tags/Solo");
        request.setParameter("p", "1");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * showArchiveArticles.
     */
    @Test
    public void showArchiveArticles() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/archives/" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM"));
        request.setParameter("p", "1");
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "Solo 的个人博客</title>"));
    }

    /**
     * showArticle.
     *
     * @throws Exception exception
     */
    @Test
    public void showArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);

        final MockRequest request = mockRequest();
        request.setRequestURI("/article");
        request.setAttribute(Article.ARTICLE, article);
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "Solo 的个人博客</title>"));
    }

    /**
     * showArticlePwdForm.
     *
     * @throws Exception exception
     */
    @Test
    public void showArticlePwdForm() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article-pwd");
        request.setParameter("articleId", articleId);
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>访问密码 - Solo 的个人博客</title>"));
    }
}
