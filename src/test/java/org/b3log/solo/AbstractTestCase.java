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
package org.b3log.solo;

import org.b3log.latke.Latkes;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.config.Discoverer;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.solo.api.metaweblog.MetaWeblogAPI;
import org.b3log.solo.repository.*;
import org.b3log.solo.repository.impl.*;
import org.b3log.solo.service.*;
import org.testng.annotations.BeforeClass;

import java.sql.Connection;
import java.util.Collection;
import java.util.Locale;

/**
 * Abstract test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.3.0.10, Sep 6, 2017
 * @see #beforeClass()
 */
public abstract class AbstractTestCase {

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

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
        Latkes.initRuntimeEnv();
        Latkes.setLocale(Locale.SIMPLIFIED_CHINESE);

        final Collection<Class<?>> classes = Discoverer.discover("org.b3log.solo");
        Lifecycle.startApplication(classes);
        beanManager = Lifecycle.getBeanManager();

        final Connection connection = Connections.getConnection();
        connection.createStatement().execute("DROP ALL OBJECTS");
        connection.close();

        JdbcRepositories.initAllTables();
    }

    /**
     * After class.
     * <ul>
     * <li>Clears all caches</li>
     * </ul>
     *
     * @throws Exception
     */
    @BeforeClass
    public void afterClass() throws Exception {
        CacheFactory.clearAll();
    }

    /**
     * Gets category-tag repository.
     *
     * @return category-tag repository
     */
    public CategoryTagRepository getCategoryTagRepository() {
        return beanManager.getReference(CategoryTagRepositoryImpl.class);
    }

    /**
     * Gets category repository.
     *
     * @return category repository
     */
    public CategoryRepository getCategoryRepository() {
        return beanManager.getReference(CategoryRepositoryImpl.class);
    }

    /**
     * Gets user repository.
     *
     * @return user repository
     */
    public UserRepository getUserRepository() {
        return beanManager.getReference(UserRepositoryImpl.class);
    }

    /**
     * Gets link repository.
     *
     * @return link repository
     */
    public LinkRepository getLinkRepository() {
        return beanManager.getReference(LinkRepositoryImpl.class);
    }

    /**
     * Gets article repository.
     *
     * @return article repository
     */
    public ArticleRepository getArticleRepository() {
        return beanManager.getReference(ArticleRepositoryImpl.class);
    }

    /**
     * Gets tag repository.
     *
     * @return tag repository
     */
    public TagRepository getTagRepository() {
        return beanManager.getReference(TagRepositoryImpl.class);
    }

    /**
     * Gets tag-article repository.
     *
     * @return tag-article repository
     */
    public TagArticleRepository getTagArticleRepository() {
        return beanManager.getReference(TagArticleRepositoryImpl.class);
    }

    /**
     * Gets page repository.
     *
     * @return page repository
     */
    public PageRepository getPageRepository() {
        return beanManager.getReference(PageRepositoryImpl.class);
    }

    /**
     * Gets comment repository.
     *
     * @return comment repository
     */
    public CommentRepository getCommentRepository() {
        return beanManager.getReference(CommentRepositoryImpl.class);
    }

    /**
     * Gets archive date repository.
     *
     * @return archive date repository
     */
    public ArchiveDateRepository getArchiveDateRepository() {
        return beanManager.getReference(ArchiveDateRepositoryImpl.class);
    }

    /**
     * Archive date article repository.
     *
     * @return archive date article repository
     */
    public ArchiveDateArticleRepository getArchiveDateArticleRepository() {
        return beanManager.getReference(ArchiveDateArticleRepositoryImpl.class);
    }

    /**
     * Gets plugin repository.
     *
     * @return plugin repository
     */
    public PluginRepository getPluginRepository() {
        return beanManager.getReference(PluginRepositoryImpl.class);
    }

    /**
     * Gets option repository.
     *
     * @return option repository
     */
    public OptionRepository getOptionRepository() {
        return beanManager.getReference(OptionRepositoryImpl.class);
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
     * Gets preference query service.
     *
     * @return preference query service
     */
    public PreferenceQueryService getPreferenceQueryService() {
        return beanManager.getReference(PreferenceQueryService.class);
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


    public MetaWeblogAPI getMetaWeblogAPI() {
        return beanManager.getReference(MetaWeblogAPI.class);
    }
}
