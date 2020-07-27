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
 * @version 1.0.1.5, Jun 28, 2020
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
        Assert.assertTrue(StringUtils.contains(content, "{\"code\":0"));
    }

    /**
     * getArticleContent.
     *
     * @throws Exception exception
     */
    @Test
    public void getArticleContent() throws Exception {
        final JSONObject article = getArticleRepository().getFirst(new Query());
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
        Assert.assertTrue(StringUtils.contains(content, "{\"code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "{\"code\":0"));
    }

    /**
     * getRandomArticles.
     */
    @Test
    public void getRandomArticles() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/articles/random.json");
        request.setMethod("GET");
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
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/article/relevant/" + articleId + ".json");
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
        Assert.assertTrue(StringUtils.contains(content, "{\"code\":0"));
    }

    /**
     * showArchiveArticles.
     */
    @Test
    public void showArchiveArticles() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/archives/" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM"));
        request.setParameter("p", "1");
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
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
        final JSONObject article = getArticleRepository().getFirst(new Query());

        final MockRequest request = mockRequest();
        request.setRequestURI("/article");
        request.setAttribute(Article.ARTICLE, article);
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
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
        final JSONObject article = getArticleRepository().getFirst(new Query());
        final String articleId = article.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/article-pwd");
        request.setParameter("articleId", articleId);
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>访问密码 - Solo 的个人博客</title>"));
    }
}
