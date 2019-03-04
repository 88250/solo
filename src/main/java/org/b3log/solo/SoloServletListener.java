/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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

import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.plugin.ViewLoadEventHandler;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.servlet.DispatcherServlet;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.B3ArticleSender;
import org.b3log.solo.event.B3ArticleUpdater;
import org.b3log.solo.event.B3CommentSender;
import org.b3log.solo.event.PluginRefresher;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Skin;
import org.b3log.solo.processor.InitCheckHandler;
import org.b3log.solo.processor.PermalinkHandler;
import org.b3log.solo.processor.console.*;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;

/**
 * Solo Servlet listener.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.10.0.10, Feb 16, 2019
 * @since 0.3.1
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SoloServletListener.class);

    /**
     * Solo version.
     */
    public static final String VERSION = "3.2.0";

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Latkes.setScanPath("org.b3log.solo");
        super.contextInitialized(servletContextEvent);
        DispatcherServlet.HANDLERS.add(0, new InitCheckHandler());
        DispatcherServlet.HANDLERS.add(1, new PermalinkHandler());

        beanManager = BeanManager.getInstance();
        routeConsoleProcessors();
        Stopwatchs.start("Context Initialized");

        validateSkin();

        final InitService initService = beanManager.getReference(InitService.class);
        initService.initTables();

        if (initService.isInited()) {
            // Upgrade check https://github.com/b3log/solo/issues/12040
            final UpgradeService upgradeService = beanManager.getReference(UpgradeService.class);
            upgradeService.upgrade();

            // Import check https://github.com/b3log/solo/issues/12293
            final ImportService importService = beanManager.getReference(ImportService.class);
            importService.importMarkdowns();

            final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
            final Transaction transaction = optionRepository.beginTransaction();
            try {
                loadPreference();

                if (transaction.isActive()) {
                    transaction.commit();
                }
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }

        registerEventHandlers();

        final PluginManager pluginManager = beanManager.getReference(PluginManager.class);
        pluginManager.load();

        if (initService.isInited()) {
            LOGGER.info("Solo is running");
        }

        Stopwatchs.end();
        LOGGER.log(Level.DEBUG, "Stopwatch: {0}{1}", Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat());

        final CronMgmtService cronMgmtService = beanManager.getReference(CronMgmtService.class);
        cronMgmtService.start();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);

        final CronMgmtService cronMgmtService = beanManager.getReference(CronMgmtService.class);
        cronMgmtService.stop();

        LOGGER.info("Destroyed the context");
    }

    @Override
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
        super.sessionDestroyed(httpSessionEvent);
    }

    @Override
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestEvent.getServletRequest();
        Requests.log(httpServletRequest, Level.DEBUG, LOGGER);

        final String requestURI = httpServletRequest.getRequestURI();
        Stopwatchs.start("Request Initialized [requestURI=" + requestURI + "]");
        fillBotAttrs(httpServletRequest);
        if (!Solos.isBot(httpServletRequest)) {
            final StatisticMgmtService statisticMgmtService = beanManager.getReference(StatisticMgmtService.class);
            statisticMgmtService.onlineVisitorCount(httpServletRequest);
        }

        resolveSkinDir(httpServletRequest);
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        Stopwatchs.end();

        LOGGER.log(Level.DEBUG, "Stopwatch: {0}{1}", Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat());
        Stopwatchs.release();

        super.requestDestroyed(servletRequestEvent);
    }

    /**
     * Loads preference.
     * <p>
     * Loads preference from repository, loads skins from skin directory then sets it into preference if the skins
     * changed.
     * </p>
     */
    private void loadPreference() {
        Stopwatchs.start("Load Preference");

        LOGGER.debug("Loading preference....");

        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        JSONObject preference;
        try {
            preference = optionQueryService.getPreference();
            if (null == preference) {
                return;
            }

            final PreferenceMgmtService preferenceMgmtService = beanManager.getReference(PreferenceMgmtService.class);
            preferenceMgmtService.loadSkins(preference);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            System.exit(-1);
        }

        Stopwatchs.end();
    }

    /**
     * Register event handlers.
     */
    private void registerEventHandlers() {
        Stopwatchs.start("Register Event Handlers");
        LOGGER.debug("Registering event handlers....");

        try {
            final EventManager eventManager = beanManager.getReference(EventManager.class);
            final PluginRefresher pluginRefresher = beanManager.getReference(PluginRefresher.class);
            eventManager.registerListener(pluginRefresher);
            eventManager.registerListener(new ViewLoadEventHandler());
            final B3ArticleSender articleSender = beanManager.getReference(B3ArticleSender.class);
            eventManager.registerListener(articleSender);
            final B3ArticleUpdater articleUpdater = beanManager.getReference(B3ArticleUpdater.class);
            eventManager.registerListener(articleUpdater);
            final B3CommentSender commentSender = beanManager.getReference(B3CommentSender.class);
            eventManager.registerListener(commentSender);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Register event handlers failed", e);

            System.exit(-1);
        }

        LOGGER.debug("Registered event handlers");
        Stopwatchs.end();
    }

    /**
     * Resolve skin (template) for the specified HTTP servlet request.
     * 前台皮肤切换 https://github.com/b3log/solo/issues/12060
     *
     * @param httpServletRequest the specified HTTP servlet request
     */
    private void resolveSkinDir(final HttpServletRequest httpServletRequest) {
        String skin = Skins.getSkinDirNameFromCookie(httpServletRequest);
        if (StringUtils.isBlank(skin)) {
            try {
                final InitService initService = beanManager.getReference(InitService.class);
                if (initService.isInited()) {
                    final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
                    final JSONObject preference = optionQueryService.getPreference();
                    if (null != preference) {
                        skin = preference.getString(Skin.SKIN_DIR_NAME);
                    }
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Resolves skin failed", e);
            }
        }
        if (StringUtils.isBlank(skin)) {
            skin = Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
        }
        if (Solos.isMobile(httpServletRequest)) {
            skin = Solos.MOBILE_SKIN;
        }

        httpServletRequest.setAttribute(Keys.TEMAPLTE_DIR_NAME, skin);
    }

    private static void fillBotAttrs(final HttpServletRequest request) {
        final String userAgentStr = request.getHeader("User-Agent");
        final UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        BrowserType browserType = userAgent.getBrowser().getBrowserType();

        if (StringUtils.containsIgnoreCase(userAgentStr, "mobile")
                || StringUtils.containsIgnoreCase(userAgentStr, "MQQBrowser")
                || StringUtils.containsIgnoreCase(userAgentStr, "iphone")
                || StringUtils.containsIgnoreCase(userAgentStr, "MicroMessenger")
                || StringUtils.containsIgnoreCase(userAgentStr, "CFNetwork")
                || StringUtils.containsIgnoreCase(userAgentStr, "Android")) {
            browserType = BrowserType.MOBILE_BROWSER;
        } else if (StringUtils.containsIgnoreCase(userAgentStr, "Iframely")
                || StringUtils.containsIgnoreCase(userAgentStr, "Google")
                || StringUtils.containsIgnoreCase(userAgentStr, "BUbiNG")
                || StringUtils.containsIgnoreCase(userAgentStr, "ltx71")
                || StringUtils.containsIgnoreCase(userAgentStr, "py")) {
            browserType = BrowserType.ROBOT;
        }

        request.setAttribute(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT, BrowserType.ROBOT == browserType);
        request.setAttribute(Keys.HttpRequest.IS_MOBILE_BOT, BrowserType.MOBILE_BROWSER == browserType);
    }

    /**
     * Validates the default skin.
     *
     * <p>
     * 改进皮肤加载校验 https://github.com/b3log/solo/issues/12548
     * </p>
     */
    private static void validateSkin() {
        final String skinDirName = Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
        final String skinName = Latkes.getSkinName(skinDirName);
        if (StringUtils.isBlank(skinName)) {
            LOGGER.log(Level.ERROR, "Can't load the default skins, please make sure skin [" + skinDirName + "] is under skins directory and structure correctly");

            System.exit(-1);
        }
    }

    /**
     * 后台控制器使用函数式路由. https://github.com/b3log/solo/issues/12580
     */
    public static void routeConsoleProcessors() {
        final BeanManager beanManager = BeanManager.getInstance();
        final AdminConsole adminConsole = beanManager.getReference(AdminConsole.class);
        DispatcherServlet.get("/admin-index.do", adminConsole::showAdminIndex);
        DispatcherServlet.get("/admin-preference.do", adminConsole::showAdminPreferenceFunction);
        DispatcherServlet.route().get(new String[]{"/admin-article.do",
                "/admin-article-list.do",
                "/admin-comment-list.do",
                "/admin-link-list.do",
                "/admin-page-list.do",
                "/admin-others.do",
                "/admin-draft-list.do",
                "/admin-user-list.do",
                "/admin-category-list.do",
                "/admin-plugin-list.do",
                "/admin-main.do",
                "/admin-about.do"}, adminConsole::showAdminFunctions);
        DispatcherServlet.get("/console/export/sql", adminConsole::exportSQL);
        DispatcherServlet.get("/console/export/json", adminConsole::exportJSON);
        DispatcherServlet.get("/console/export/hexo", adminConsole::exportHexo);

        final ArticleConsole articleConsole = beanManager.getReference(ArticleConsole.class);
        DispatcherServlet.get("/console/article/push2rhy", articleConsole::pushArticleToCommunity);
        DispatcherServlet.get("/console/thumbs", articleConsole::getArticleThumbs);
        DispatcherServlet.get("/console/article/{id}", articleConsole::getArticle);
        DispatcherServlet.get("/console/articles/status/{status}/{page}/{pageSize}/{windowSize}", articleConsole::getArticles);
        DispatcherServlet.delete("/console/article/{id}", articleConsole::removeArticle);
        DispatcherServlet.put("/console/article/unpublish/{id}", articleConsole::cancelPublishArticle);
        DispatcherServlet.put("/console/article/canceltop/{id}", articleConsole::cancelTopArticle);
        DispatcherServlet.put("/console/article/puttop/{id}", articleConsole::putTopArticle);
        DispatcherServlet.put("/console/article/", articleConsole::updateArticle);
        DispatcherServlet.post("/console/article/", articleConsole::addArticle);

        final CategoryConsole categoryConsole = beanManager.getReference(CategoryConsole.class);
        DispatcherServlet.put("/console/category/order/", categoryConsole::changeOrder);
        DispatcherServlet.get("/console/category/{id}", categoryConsole::getCategory);
        DispatcherServlet.delete("/console/category/{id}", categoryConsole::removeCategory);
        DispatcherServlet.put("/console/category/", categoryConsole::updateCategory);
        DispatcherServlet.post("/console/category/", categoryConsole::addCategory);
        DispatcherServlet.get("/console/categories/{page}/{pageSize}/{windowSize}", categoryConsole::getCategories);

        final CommentConsole commentConsole = beanManager.getReference(CommentConsole.class);
        DispatcherServlet.delete("/console/page/comment/{id}", commentConsole::removePageComment);
        DispatcherServlet.delete("/console/article/comment/{id}", commentConsole::removeArticleComment);
        DispatcherServlet.get("/console/comments/{page}/{pageSize}/{windowSize}", commentConsole::getComments);
        DispatcherServlet.get("/console/comments/article/{id}", commentConsole::getArticleComments);
        DispatcherServlet.get("/console/comments/page/{id}", commentConsole::getPageComments);

        final LinkConsole linkConsole = beanManager.getReference(LinkConsole.class);
        DispatcherServlet.delete("/console/link/{id}", linkConsole::removeLink);
        DispatcherServlet.put("/console/link/", linkConsole::updateLink);
        DispatcherServlet.put("/console/link/order/", linkConsole::changeOrder);
        DispatcherServlet.post("/console/link/", linkConsole::addLink);
        DispatcherServlet.get("/console/links/{page}/{pageSize}/{windowSize}", linkConsole::getLinks);
        DispatcherServlet.get("/console/link/{id}", linkConsole::getLink);

        final PageConsole pageConsole = beanManager.getReference(PageConsole.class);
        DispatcherServlet.put("/console/page/", pageConsole::updatePage);
        DispatcherServlet.delete("/console/page/{id}", pageConsole::removePage);
        DispatcherServlet.post("/console/page/", pageConsole::addPage);
        DispatcherServlet.put("/console/page/order/", pageConsole::changeOrder);
        DispatcherServlet.get("/console/page/{id}", pageConsole::getPage);
        DispatcherServlet.get("/console/pages/{page}/{pageSize}/{windowSize}", pageConsole::getPages);

        final PluginConsole pluginConsole = beanManager.getReference(PluginConsole.class);
        DispatcherServlet.put("/console/plugin/status/", pluginConsole::setPluginStatus);
        DispatcherServlet.get("/console/plugins/{page}/{pageSize}/{windowSize}", pluginConsole::getPlugins);
        DispatcherServlet.post("/console/plugin/toSetting", pluginConsole::toSetting);
        DispatcherServlet.post("/console/plugin/updateSetting", pluginConsole::updateSetting);

        final PreferenceConsole preferenceConsole = beanManager.getReference(PreferenceConsole.class);
        DispatcherServlet.get("/console/signs/", preferenceConsole::getSigns);
        DispatcherServlet.get("/console/preference/", preferenceConsole::getPreference);
        DispatcherServlet.put("/console/preference/", preferenceConsole::updatePreference);

        final RepairConsole repairConsole = beanManager.getReference(RepairConsole.class);
        DispatcherServlet.get("/fix/restore-signs", repairConsole::restoreSigns);

        final TagConsole tagConsole = beanManager.getReference(TagConsole.class);
        DispatcherServlet.get("/console/tags", tagConsole::getTags);
        DispatcherServlet.get("/console/tag/unused", tagConsole::getUnusedTags);
        DispatcherServlet.delete("/console/tag/unused", tagConsole::removeUnusedTags);

        final UserConsole userConsole = beanManager.getReference(UserConsole.class);
        DispatcherServlet.put("/console/user/", userConsole::updateUser);
        DispatcherServlet.delete("/console/user/{id}", userConsole::removeUser);
        DispatcherServlet.get("/console/users/{page}/{pageSize}/{windowSize}", userConsole::getUsers);
        DispatcherServlet.get("/console/user/{id}", userConsole::getUser);
        DispatcherServlet.get("/console/changeRole/{id}", userConsole::changeUserRole);

        DispatcherServlet.mapping();
    }
}
