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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Query;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.InitService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link ArticleProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.2, May 29, 2018
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class ArticleProcessorTestCase extends AbstractTestCase {

    /**
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByEmailOrUserName("test@gmail.com"));
    }

    /**
     * getArchivesArticlesByPage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getArchivesArticlesByPage() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/articles/archives/"
                + DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM") + "/1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * getArticleContent.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getArticleContent() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/get-article-content");
        when(request.getParameter("id")).thenReturn(articleId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "<p>欢迎使用"));
    }

    /**
     * getArticlesByPage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getArticlesByPage() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/articles/1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * getAuthorsArticlesByPage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getAuthorsArticlesByPage() throws Exception {
        final JSONObject admin = getUserRepository().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/articles/authors/" + userId + "/1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * getRandomArticles.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getRandomArticles() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/get-random-articles.do");
        when(request.getMethod()).thenReturn("POST");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "{\"randomArticles"));
    }

    /**
     * getRelevantArticles.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getRelevantArticles() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/article/id/" + articleId + "/relevant/articles");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "{\"relevantArticles\""));
    }

    /**
     * getTagArticlesByPage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getTagArticlesByPage() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/articles/tags/Solo/1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "{\"sc\":true"));
    }

    /**
     * showArchiveArticles.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showArchiveArticles() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/archives/"
                + DateFormatUtils.format(System.currentTimeMillis(), "yyyy/MM") + "/1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "Admin 的个人博客</title>"));
    }

    /**
     * showArticle.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showArticle() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/pagepermalink");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());
        when(request.getAttribute(Article.ARTICLE)).thenReturn(article);

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        final HTTPRequestContext httpRequestContext = new HTTPRequestContext();
        httpRequestContext.setRequest(request);
        httpRequestContext.setResponse(response);

        final ArticleProcessor articleProcessor = BeanManager.getInstance().getReference(ArticleProcessor.class);
        articleProcessor.showArticle(httpRequestContext, request, response);

        final Map<String, Object> dataModel = httpRequestContext.getRenderer().getRenderDataModel();
        final JSONObject handledArticle = (JSONObject) dataModel.get(Article.ARTICLE);
        Assert.assertTrue(StringUtils.contains(handledArticle.optString(Keys.OBJECT_ID), article.optString(Keys.OBJECT_ID)));
    }

    /**
     * showArticlePwdForm.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showArticlePwdForm() throws Exception {
        final JSONObject article = getArticleRepository().get(new Query()).optJSONArray(Keys.RESULTS).optJSONObject(0);
        final String articleId = article.optString(Keys.OBJECT_ID);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/console/article-pwd");
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("articleId")).thenReturn(articleId);
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "<title>访问密码</title>"));
    }

    /**
     * showAuthorArticles.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showAuthorArticles() throws Exception {
        final JSONObject admin = getUserRepository().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/authors/" + userId + "/1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getAttribute(Keys.TEMAPLTE_DIR_NAME)).thenReturn(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        when(request.getAttribute(Keys.HttpRequest.START_TIME_MILLIS)).thenReturn(System.currentTimeMillis());

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(printWriter);

        dispatcherServlet.service(request, response);

        final String content = stringWriter.toString();
        Assert.assertTrue(StringUtils.contains(content, "Solo 示例") || StringUtils.isBlank(content));
    }
}
