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
package org.b3log.solo;

import io.netty.handler.codec.http.*;
import org.apache.commons.lang.RandomStringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.Response;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Discoverer;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.util.Crypts;
import org.b3log.solo.cache.*;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.processor.ErrorProcessor;
import org.b3log.solo.processor.MockDispatcher;
import org.b3log.solo.repository.*;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;

/**
 * Abstract test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 4.0.0.0, Feb 9, 2020
 * @since 2.9.7
 */
public abstract class AbstractTestCase {

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    /**
     * Before class.
     * <ol>
     * <li>Initializes Latke runtime</li>
     * <li>Instantiates repositories</li>
     * </ol>
     *
     * @throws Exception exception
     */
    @BeforeClass
    public void beforeClass() throws Exception {
        Latkes.init();

        final Collection<Class<?>> classes = Discoverer.discover("org.b3log.solo");
        BeanManager.start(classes);
        beanManager = BeanManager.getInstance();

        final Connection connection = Connections.getConnection();
        connection.createStatement().execute("DROP ALL OBJECTS");
        connection.close();

        JdbcRepositories.initAllTables();

        initSolo();
    }

    @BeforeMethod
    public void beforeMethod(final Method method) {
        System.out.println(method.getDeclaringClass().getSimpleName() + "#" + method.getName());
    }

    /**
     * After class.
     * <ul>
     * <li>Clears all caches</li>
     * </ul>
     */
    @AfterClass
    public void afterClass() {
        final ArticleCache articleCache = beanManager.getReference(ArticleCache.class);
        articleCache.clear();
        final CommentCache commentCache = beanManager.getReference(CommentCache.class);
        commentCache.clear();
        final OptionCache optionCache = beanManager.getReference(OptionCache.class);
        optionCache.clear();
        final PageCache pageCache = beanManager.getReference(PageCache.class);
        pageCache.clear();
        final StatisticCache statisticCache = beanManager.getReference(StatisticCache.class);
        statisticCache.clear();
        final UserCache userCache = beanManager.getReference(UserCache.class);
        userCache.clear();
    }

    private void initSolo() {
        final InitService initService = getInitService();
        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_NAME, "Solo");
        requestJSONObject.put(UserExt.USER_B3_KEY, "pass");
        System.out.println("before init");
        initService.init(requestJSONObject);
        System.out.println("after init");
        final ErrorProcessor errorProcessor = beanManager.getReference(ErrorProcessor.class);
        Dispatcher.error("/error/{statusCode}", errorProcessor::showErrorPage);
        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByName("Solo"));
    }

    /**
     * Mocks admin login for console testing.
     *
     * @param request the specified request
     */
    public void mockAdminLogin(final MockRequest request) {
        final JSONObject adminUser = getUserQueryService().getAdmin();
        final String userId = adminUser.optString(Keys.OBJECT_ID);
        final JSONObject cookieJSONObject = new JSONObject();
        cookieJSONObject.put(Keys.OBJECT_ID, userId);
        final String random = RandomStringUtils.randomAlphanumeric(16);
        cookieJSONObject.put(Keys.TOKEN, "pass:" + random);
        final String cookieValue = Crypts.encryptByAES(cookieJSONObject.toString(), Solos.COOKIE_SECRET);
        request.addCookie(Solos.COOKIE_NAME, cookieValue);
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
    }

    /**
     * Gets a mock dispatcher and run service.
     *
     * @param request  the specified request
     * @param response the specified response
     * @return mock dispatcher
     */
    public MockDispatcher mockDispatcher(final Request request, final Response response) {
        final MockDispatcher ret = new MockDispatcher();
        ret.init();
        Server.routeProcessors();
        ret.handle(request, response);

        return ret;
    }

    /**
     * Gets a mock request.
     *
     * @return mock request
     */
    public MockRequest mockRequest() {
        final FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/a");
        return new MockRequest(req);
    }

    /**
     * Gets a mock response.
     *
     * @return mock response
     */
    public MockResponse mockResponse() {
        final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        final MockResponse response = new MockResponse(res);

        return response;
    }

    /**
     * Gets category-tag repository.
     *
     * @return category-tag repository
     */
    public CategoryTagRepository getCategoryTagRepository() {
        return beanManager.getReference(CategoryTagRepository.class);
    }

    /**
     * Gets category repository.
     *
     * @return category repository
     */
    public CategoryRepository getCategoryRepository() {
        return beanManager.getReference(CategoryRepository.class);
    }

    /**
     * Gets user repository.
     *
     * @return user repository
     */
    public UserRepository getUserRepository() {
        return beanManager.getReference(UserRepository.class);
    }

    /**
     * Gets link repository.
     *
     * @return link repository
     */
    public LinkRepository getLinkRepository() {
        return beanManager.getReference(LinkRepository.class);
    }

    /**
     * Gets article repository.
     *
     * @return article repository
     */
    public ArticleRepository getArticleRepository() {
        return beanManager.getReference(ArticleRepository.class);
    }

    /**
     * Gets tag repository.
     *
     * @return tag repository
     */
    public TagRepository getTagRepository() {
        return beanManager.getReference(TagRepository.class);
    }

    /**
     * Gets tag-article repository.
     *
     * @return tag-article repository
     */
    public TagArticleRepository getTagArticleRepository() {
        return beanManager.getReference(TagArticleRepository.class);
    }

    /**
     * Gets page repository.
     *
     * @return page repository
     */
    public PageRepository getPageRepository() {
        return beanManager.getReference(PageRepository.class);
    }

    /**
     * Gets comment repository.
     *
     * @return comment repository
     */
    public CommentRepository getCommentRepository() {
        return beanManager.getReference(CommentRepository.class);
    }

    /**
     * Gets archive date repository.
     *
     * @return archive date repository
     */
    public ArchiveDateRepository getArchiveDateRepository() {
        return beanManager.getReference(ArchiveDateRepository.class);
    }

    /**
     * Archive date article repository.
     *
     * @return archive date article repository
     */
    public ArchiveDateArticleRepository getArchiveDateArticleRepository() {
        return beanManager.getReference(ArchiveDateArticleRepository.class);
    }

    /**
     * Gets plugin repository.
     *
     * @return plugin repository
     */
    public PluginRepository getPluginRepository() {
        return beanManager.getReference(PluginRepository.class);
    }

    /**
     * Gets option repository.
     *
     * @return option repository
     */
    public OptionRepository getOptionRepository() {
        return beanManager.getReference(OptionRepository.class);
    }

    /**
     * Gets category query service.
     *
     * @return category query service
     */
    public CategoryQueryService getCategoryQueryService() {
        return beanManager.getReference(CategoryQueryService.class);
    }

    /**
     * Gets category management service.
     *
     * @return category management service
     */
    public CategoryMgmtService getCategoryMgmtService() {
        return beanManager.getReference(CategoryMgmtService.class);
    }

    /**
     * Gets initialization service.
     *
     * @return initialization service
     */
    public InitService getInitService() {
        return beanManager.getReference(InitService.class);
    }

    /**
     * Gets user management service.
     *
     * @return user management service
     */
    public UserMgmtService getUserMgmtService() {
        return beanManager.getReference(UserMgmtService.class);
    }

    /**
     * Gets user query service.
     *
     * @return user query service
     */
    public UserQueryService getUserQueryService() {
        return beanManager.getReference(UserQueryService.class);
    }

    /**
     * Gets article management service.
     *
     * @return article management service
     */
    public ArticleMgmtService getArticleMgmtService() {
        return beanManager.getReference(ArticleMgmtService.class);
    }

    /**
     * Gets article query service.
     *
     * @return article query service
     */
    public ArticleQueryService getArticleQueryService() {
        return beanManager.getReference(ArticleQueryService.class);
    }

    /**
     * Gets page management service.
     *
     * @return page management service
     */
    public PageMgmtService getPageMgmtService() {
        return beanManager.getReference(PageMgmtService.class);
    }

    /**
     * Gets page query service.
     *
     * @return page query service
     */
    public PageQueryService getPageQueryService() {
        return beanManager.getReference(PageQueryService.class);
    }

    /**
     * Gets link management service.
     *
     * @return link management service
     */
    public LinkMgmtService getLinkMgmtService() {
        return beanManager.getReference(LinkMgmtService.class);
    }

    /**
     * Gets link query service.
     *
     * @return link query service
     */
    public LinkQueryService getLinkQueryService() {
        return beanManager.getReference(LinkQueryService.class);
    }

    /**
     * Gets preference management service.
     *
     * @return preference management service
     */
    public PreferenceMgmtService getPreferenceMgmtService() {
        return beanManager.getReference(PreferenceMgmtService.class);
    }

    /**
     * Gets tag query service.
     *
     * @return tag query service
     */
    public TagQueryService getTagQueryService() {
        return beanManager.getReference(TagQueryService.class);
    }

    /**
     * Gets tag management service.
     *
     * @return tag management service
     */
    public TagMgmtService getTagMgmtService() {
        return beanManager.getReference(TagMgmtService.class);
    }

    /**
     * Gets comment query service.
     *
     * @return comment query service
     */
    public CommentQueryService getCommentQueryService() {
        return beanManager.getReference(CommentQueryService.class);
    }

    /**
     * Gets comment management service.
     *
     * @return comment management service
     */
    public CommentMgmtService getCommentMgmtService() {
        return beanManager.getReference(CommentMgmtService.class);
    }

    /**
     * Gets archive date query service.
     *
     * @return archive date query service
     */
    public ArchiveDateQueryService getArchiveDateQueryService() {
        return beanManager.getReference(ArchiveDateQueryService.class);
    }

    /**
     * Gets option management service.
     *
     * @return option management service
     */
    public OptionMgmtService getOptionMgmtService() {
        return beanManager.getReference(OptionMgmtService.class);
    }

    /**
     * Gets option query service.
     *
     * @return option query service
     */
    public OptionQueryService getOptionQueryService() {
        return beanManager.getReference(OptionQueryService.class);
    }
}
