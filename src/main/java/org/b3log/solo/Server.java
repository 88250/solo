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

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.http.BaseServer;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.plugin.ViewLoadEventHandler;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.B3ArticleSender;
import org.b3log.solo.event.B3ArticleUpdater;
import org.b3log.solo.event.B3CommentSender;
import org.b3log.solo.event.PluginRefresher;
import org.b3log.solo.processor.*;
import org.b3log.solo.processor.console.*;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Markdowns;
import org.json.JSONObject;

/**
 * Server.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.0.0.1, Feb 21, 2020
 * @since 1.2.0
 */
public final class Server extends BaseServer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    /**
     * Solo version.
     */
    public static final String VERSION = "3.9.0";

    /**
     * Main.
     *
     * @param args the specified arguments
     */
    public static void main(final String[] args) {
        Stopwatchs.start("Booting");

        final Options options = new Options();
        final Option listenPortOpt = Option.builder("lp").longOpt("listen_port").argName("LISTEN_PORT").
                hasArg().desc("listen port, default is 8080").build();
        options.addOption(listenPortOpt);

        final Option serverSchemeOpt = Option.builder("ss").longOpt("server_scheme").argName("SERVER_SCHEME").
                hasArg().desc("browser visit protocol, default is http").build();
        options.addOption(serverSchemeOpt);

        final Option serverHostOpt = Option.builder("sh").longOpt("server_host").argName("SERVER_HOST").
                hasArg().desc("browser visit domain name, default is localhost").build();
        options.addOption(serverHostOpt);

        final Option serverPortOpt = Option.builder("sp").longOpt("server_port").argName("SERVER_PORT").
                hasArg().desc("browser visit port, default is 8080").build();
        options.addOption(serverPortOpt);

        final Option staticServerSchemeOpt = Option.builder("sss").longOpt("static_server_scheme").argName("STATIC_SERVER_SCHEME").
                hasArg().desc("browser visit static resource protocol, default is http").build();
        options.addOption(staticServerSchemeOpt);

        final Option staticServerHostOpt = Option.builder("ssh").longOpt("static_server_host").argName("STATIC_SERVER_HOST").
                hasArg().desc("browser visit static resource domain name, default is localhost").build();
        options.addOption(staticServerHostOpt);

        final Option staticServerPortOpt = Option.builder("ssp").longOpt("static_server_port").argName("STATIC_SERVER_PORT").
                hasArg().desc("browser visit static resource port, default is 8080").build();
        options.addOption(staticServerPortOpt);

        final Option runtimeModeOpt = Option.builder("rm").longOpt("runtime_mode").argName("RUNTIME_MODE").
                hasArg().desc("runtime mode (DEVELOPMENT/PRODUCTION), default is DEVELOPMENT").build();
        options.addOption(runtimeModeOpt);

        final Option luteHttpOpt = Option.builder("lute").longOpt("lute_http").argName("LUTE_HTTP").
                hasArg().desc("lute http URL, default is http://localhost:8249, see https://github.com/88250/lute-http for more details").build();
        options.addOption(luteHttpOpt);

        options.addOption("h", "help", false, "print help for the command");

        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(120);
        final CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine;

        final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        final String cmdSyntax = isWindows ? "java -cp \"lib/*;.\" org.b3log.solo.Server"
                : "java -cp \"lib/*:.\" org.b3log.solo.Server";
        final String header = "\nSolo 是一款小而美的博客系统，专为程序员设计。\n\n";
        final String footer = "\n提需求或报告缺陷请到项目网站: https://github.com/88250/solo\n\n";
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (final ParseException e) {
            helpFormatter.printHelp(cmdSyntax, header, options, footer, true);

            return;
        }

        if (commandLine.hasOption("h")) {
            helpFormatter.printHelp(cmdSyntax, header, options, footer, true);

            return;
        }

        String portArg = commandLine.getOptionValue("listen_port");
        if (!Strings.isNumeric(portArg)) {
            portArg = "8080";
        }

        try {
            Latkes.setScanPath("org.b3log.solo");
            Latkes.init();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Latke init failed, please configure latke.props or run with args, visit https://hacpai.com/article/1492881378588 for more details");

            System.exit(-1);
        }

        String serverScheme = commandLine.getOptionValue("server_scheme");
        if (null != serverScheme) {
            Latkes.setLatkeProperty("serverScheme", serverScheme);
        }
        String serverHost = commandLine.getOptionValue("server_host");
        if (null != serverHost) {
            Latkes.setLatkeProperty("serverHost", serverHost);
        }
        String serverPort = commandLine.getOptionValue("server_port");
        if (null != serverPort) {
            Latkes.setLatkeProperty("serverPort", serverPort);
        }
        String staticServerScheme = commandLine.getOptionValue("static_server_scheme");
        if (null != staticServerScheme) {
            Latkes.setLatkeProperty("staticServerScheme", staticServerScheme);
        }
        String staticServerHost = commandLine.getOptionValue("static_server_host");
        if (null != staticServerHost) {
            Latkes.setLatkeProperty("staticServerHost", staticServerHost);
        }
        String staticServerPort = commandLine.getOptionValue("static_server_port");
        if (null != staticServerPort) {
            Latkes.setLatkeProperty("staticServerPort", staticServerPort);
        }
        String runtimeMode = commandLine.getOptionValue("runtime_mode");
        if (null != runtimeMode) {
            Latkes.setRuntimeMode(Latkes.RuntimeMode.valueOf(runtimeMode));
        }
        String luteHttp = commandLine.getOptionValue("lute_http");
        if (null != luteHttp) {
            Markdowns.LUTE_ENGINE_URL = luteHttp;
            Markdowns.LUTE_AVAILABLE = true;
        }

        if (Latkes.isDocker()) {
            // Docker 环境需要填充默认值

            final String jdbcMinConns = System.getenv("JDBC_MIN_CONNS");
            if (StringUtils.isBlank(jdbcMinConns)) {
                Latkes.setLocalProperty("jdbc.minConnCnt", "5");
            }

            final String jdbcMaxConns = System.getenv("JDBC_MAX_CONNS");
            if (StringUtils.isBlank(jdbcMaxConns)) {
                Latkes.setLocalProperty("jdbc.maxConnCnt", "10");
            }

            final String jdbcTablePrefix = System.getenv("JDBC_TABLE_PREFIX");
            if (StringUtils.isBlank(jdbcTablePrefix)) {
                Latkes.setLocalProperty("jdbc.tablePrefix", "b3_solo");
            }
        }

        Dispatcher.startRequestHandler = new BeforeRequestHandler();
        Dispatcher.HANDLERS.add(1, new SkinHandler());
        Dispatcher.HANDLERS.add(2, new InitCheckHandler());
        Dispatcher.HANDLERS.add(3, new PermalinkHandler());
        Dispatcher.endRequestHandler = new AfterRequestHandler();

        routeProcessors();

        final Latkes.RuntimeDatabase runtimeDatabase = Latkes.getRuntimeDatabase();
        final String jdbcUsername = Latkes.getLocalProperty("jdbc.username");
        final String jdbcURL = Latkes.getLocalProperty("jdbc.URL");
        final boolean luteAvailable = Markdowns.LUTE_AVAILABLE;

        LOGGER.log(Level.INFO, "Solo is booting [ver=" + VERSION + ", os=" + Latkes.getOperatingSystemName() +
                ", isDocker=" + Latkes.isDocker() + ", inJar=" + Latkes.isInJar() + ", luteAvailable=" + luteAvailable + ", pid=" + Latkes.currentPID() +
                ", runtimeDatabase=" + runtimeDatabase + ", runtimeMode=" + Latkes.getRuntimeMode() + ", jdbc.username=" +
                jdbcUsername + ", jdbc.URL=" + jdbcURL + "]");

        validateSkin();

        final BeanManager beanManager = BeanManager.getInstance();

        final ErrorProcessor errorProcessor = beanManager.getReference(ErrorProcessor.class);
        Dispatcher.error("/error/{statusCode}", errorProcessor::showErrorPage);

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

        final CronMgmtService cronMgmtService = beanManager.getReference(CronMgmtService.class);
        cronMgmtService.start();

        final Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cronMgmtService.stop();
            server.shutdown();
            Latkes.shutdown();
        }));

        Stopwatchs.end();
        LOGGER.log(Level.DEBUG, "Stopwatch: {}{}", Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat());
        Stopwatchs.release();

        server.start(Integer.parseInt(portArg));
    }

    /**
     * Loads skin.
     * <p>
     * Loads skin from repository, loads skins from skin directory then sets it into preference if the skins changed.
     * </p>
     */
    private static void loadPreference() {
        Stopwatchs.start("Load Preference");

        LOGGER.debug("Loading preference....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        JSONObject skin;
        try {
            skin = optionQueryService.getSkin();
            if (null == skin) {
                return;
            }

            final SkinMgmtService skinMgmtService = beanManager.getReference(SkinMgmtService.class);
            skinMgmtService.loadSkins(skin);

            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                return;
            }

            final String showClodeBlockLn = preference.optString(org.b3log.solo.model.Option.ID_C_SHOW_CODE_BLOCK_LN);
            Markdowns.SHOW_CODE_BLOCK_LN = StringUtils.equalsIgnoreCase(showClodeBlockLn, "true");
            final String showToC = preference.optString(org.b3log.solo.model.Option.ID_C_SHOW_TOC);
            Markdowns.SHOW_TOC = StringUtils.equalsIgnoreCase(showToC, "true");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            System.exit(-1);
        }

        Stopwatchs.end();
    }

    /**
     * Register event handlers.
     */
    private static void registerEventHandlers() {
        Stopwatchs.start("Register Event Handlers");
        LOGGER.debug("Registering event handlers....");

        try {
            final BeanManager beanManager = BeanManager.getInstance();
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
     * Validates the default skin.
     *
     * <p>
     * 改进皮肤加载校验 https://github.com/b3log/solo/issues/12548
     * </p>
     */
    private static void validateSkin() {
        final String skinDirName = org.b3log.solo.model.Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
        final String skinName = Latkes.getSkinName(skinDirName);
        if (StringUtils.isBlank(skinName)) {
            LOGGER.log(Level.ERROR, "Can't load the default skins, please make sure skin [" + skinDirName + "] is under skins directory and structure correctly");

            System.exit(-1);
        }
    }

    public static void routeProcessors() {
        routeConsoleProcessors();
        routeIndexProcessors();
        Dispatcher.mapping();
    }

    /**
     * 前台路由.
     */
    private static void routeIndexProcessors() {
        final BeanManager beanManager = BeanManager.getInstance();

        final ArticleProcessor articleProcessor = beanManager.getReference(ArticleProcessor.class);
        final Dispatcher.RouterGroup articleGroup = Dispatcher.group();
        articleGroup.post("/console/markdown/2html", articleProcessor::markdown2HTML).
                get("/console/article-pwd", articleProcessor::showArticlePwdForm).
                post("/console/article-pwd", articleProcessor::onArticlePwdForm).
                post("/articles/random", articleProcessor::getRandomArticles).
                get("/article/id/{id}/relevant/articles", articleProcessor::getRelevantArticles).
                get("/get-article-content", articleProcessor::getArticleContent).
                get("/articles", articleProcessor::getArticlesByPage).
                get("/articles/tags/{tagTitle}", articleProcessor::getTagArticlesByPage).
                get("/articles/archives/{yyyy}/{MM}", articleProcessor::getArchivesArticlesByPage).
                get("/articles/authors/{author}", articleProcessor::getAuthorsArticlesByPage).
                get("/authors/{author}", articleProcessor::showAuthorArticles).
                get("/archives/{yyyy}/{MM}", articleProcessor::showArchiveArticles).
                get("/article", articleProcessor::showArticle);

        final B3Receiver b3Receiver = beanManager.getReference(B3Receiver.class);
        final Dispatcher.RouterGroup b3Group = Dispatcher.group();
        b3Group.router().post().put().uri("/apis/symphony/article").handler(b3Receiver::postArticle).
                put("/apis/symphony/comment", b3Receiver::addComment);

        final BlogProcessor blogProcessor = beanManager.getReference(BlogProcessor.class);
        final Dispatcher.RouterGroup blogGroup = Dispatcher.group();
        blogGroup.get("/manifest.json", blogProcessor::getPWAManifestJSON).
                get("/blog/info", blogProcessor::getBlogInfo).
                get("/blog/articles-tags", blogProcessor::getArticlesTags);

        final CategoryProcessor categoryProcessor = beanManager.getReference(CategoryProcessor.class);
        final Dispatcher.RouterGroup categoryGroup = Dispatcher.group();
        categoryGroup.get("/articles/category/{categoryURI}", categoryProcessor::getCategoryArticlesByPage).
                get("/category/{categoryURI}", categoryProcessor::showCategoryArticles);

        final CommentProcessor commentProcessor = beanManager.getReference(CommentProcessor.class);
        final Dispatcher.RouterGroup commentGroup = Dispatcher.group();
        commentGroup.post("/article/comments", commentProcessor::addArticleComment);

        final FeedProcessor feedProcessor = beanManager.getReference(FeedProcessor.class);
        final Dispatcher.RouterGroup feedGroup = Dispatcher.group();
        feedGroup.router().get().head().uri("/atom.xml").handler(feedProcessor::blogArticlesAtom).
                get().head().uri("/rss.xml").handler(feedProcessor::blogArticlesRSS);

        final IndexProcessor indexProcessor = beanManager.getReference(IndexProcessor.class);
        final Dispatcher.RouterGroup indexGroup = Dispatcher.group();
        indexGroup.router().get(new String[]{"", "/", "/index.html"}, indexProcessor::showIndex);
        indexGroup.get("/start", indexProcessor::showStart).
                get("/logout", indexProcessor::logout).
                get("/kill-browser", indexProcessor::showKillBrowser);

        final OAuthProcessor oAuthProcessor = beanManager.getReference(OAuthProcessor.class);
        final Dispatcher.RouterGroup oauthGroup = Dispatcher.group();
        oauthGroup.get("/login/redirect", oAuthProcessor::redirectAuth).
                get("/login/callback", oAuthProcessor::authCallback);

        final SearchProcessor searchProcessor = beanManager.getReference(SearchProcessor.class);
        final Dispatcher.RouterGroup searchGroup = Dispatcher.group();
        searchGroup.get("/opensearch.xml", searchProcessor::showOpensearchXML).
                get("/search", searchProcessor::search);

        final SitemapProcessor sitemapProcessor = beanManager.getReference(SitemapProcessor.class);
        final Dispatcher.RouterGroup sitemapGroup = Dispatcher.group();
        sitemapGroup.get("/sitemap.xml", sitemapProcessor::sitemap);

        final TagProcessor tagProcessor = beanManager.getReference(TagProcessor.class);
        final Dispatcher.RouterGroup tagGroup = Dispatcher.group();
        tagGroup.get("/tags/{tagTitle}", tagProcessor::showTagArticles);

        final UserTemplateProcessor userTemplateProcessor = beanManager.getReference(UserTemplateProcessor.class);
        final Dispatcher.RouterGroup userTemplateGroup = Dispatcher.group();
        userTemplateGroup.get("/{name}.html", userTemplateProcessor::showPage);
    }

    /**
     * 后台路由.
     */
    private static void routeConsoleProcessors() {
        final BeanManager beanManager = BeanManager.getInstance();

        final ConsoleAuthMidware consoleAuthMidware = beanManager.getReference(ConsoleAuthMidware.class);
        final ConsoleAdminAuthMidware consoleAdminAuthMidware = beanManager.getReference(ConsoleAdminAuthMidware.class);

        final AdminConsole adminConsole = beanManager.getReference(AdminConsole.class);
        final Dispatcher.RouterGroup adminConsoleGroup = Dispatcher.group();
        adminConsoleGroup.middlewares(consoleAuthMidware::handle);
        adminConsoleGroup.get("/admin-index.do", adminConsole::showAdminIndex).
                get("/admin-preference.do", adminConsole::showAdminPreferenceFunction).
                get("/console/export/sql", adminConsole::exportSQL).
                get("/console/export/json", adminConsole::exportJSON).
                get("/console/export/hexo", adminConsole::exportHexo);
        adminConsoleGroup.router().get(new String[]{"/admin-article.do",
                "/admin-article-list.do",
                "/admin-comment-list.do",
                "/admin-link-list.do",
                "/admin-page-list.do",
                "/admin-others.do",
                "/admin-draft-list.do",
                "/admin-user-list.do",
                "/admin-category-list.do",
                "/admin-theme-list.do",
                "/admin-plugin-list.do",
                "/admin-staticsite.do",
                "/admin-main.do",
                "/admin-about.do"}, adminConsole::showAdminFunctions);

        final ArticleConsole articleConsole = beanManager.getReference(ArticleConsole.class);
        final Dispatcher.RouterGroup articleConsoleGroup = Dispatcher.group();
        articleConsoleGroup.middlewares(consoleAuthMidware::handle);
        articleConsoleGroup.get("/console/article/push2rhy", articleConsole::pushArticleToCommunity).
                get("/console/thumbs", articleConsole::getArticleThumbs).
                get("/console/article/{id}", articleConsole::getArticle).
                get("/console/articles/status/{status}/{page}/{pageSize}/{windowSize}", articleConsole::getArticles).
                delete("/console/article/{id}", articleConsole::removeArticle).
                put("/console/article/unpublish/{id}", articleConsole::cancelPublishArticle).
                put("/console/article/canceltop/{id}", articleConsole::cancelTopArticle).
                put("/console/article/puttop/{id}", articleConsole::putTopArticle).
                put("/console/article/", articleConsole::updateArticle).
                post("/console/article/", articleConsole::addArticle);

        final CommentConsole commentConsole = beanManager.getReference(CommentConsole.class);
        final Dispatcher.RouterGroup commentConsoleGroup = Dispatcher.group();
        commentConsoleGroup.middlewares(consoleAuthMidware::handle);
        commentConsoleGroup.delete("/console/article/comment/{id}", commentConsole::removeArticleComment).
                get("/console/comments/{page}/{pageSize}/{windowSize}", commentConsole::getComments).
                get("/console/comments/article/{id}", commentConsole::getArticleComments);

        final TagConsole tagConsole = beanManager.getReference(TagConsole.class);
        final Dispatcher.RouterGroup tagConsoleGroup = Dispatcher.group();
        tagConsoleGroup.middlewares(consoleAuthMidware::handle);
        tagConsoleGroup.get("/console/tags", tagConsole::getTags).
                get("/console/tag/unused", tagConsole::getUnusedTags);

        final CategoryConsole categoryConsole = beanManager.getReference(CategoryConsole.class);
        final Dispatcher.RouterGroup categoryGroup = Dispatcher.group();
        categoryGroup.middlewares(consoleAdminAuthMidware::handle);
        categoryGroup.put("/console/category/order/", categoryConsole::changeOrder).
                get("/console/category/{id}", categoryConsole::getCategory).
                delete("/console/category/{id}", categoryConsole::removeCategory).
                put("/console/category/", categoryConsole::updateCategory).
                post("/console/category/", categoryConsole::addCategory).
                get("/console/categories/{page}/{pageSize}/{windowSize}", categoryConsole::getCategories);

        final LinkConsole linkConsole = beanManager.getReference(LinkConsole.class);
        final Dispatcher.RouterGroup linkConsoleGroup = Dispatcher.group();
        linkConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        linkConsoleGroup.delete("/console/link/{id}", linkConsole::removeLink).
                put("/console/link/", linkConsole::updateLink).
                put("/console/link/order/", linkConsole::changeOrder).
                post("/console/link/", linkConsole::addLink).
                get("/console/links/{page}/{pageSize}/{windowSize}", linkConsole::getLinks).
                get("/console/link/{id}", linkConsole::getLink);

        final PageConsole pageConsole = beanManager.getReference(PageConsole.class);
        final Dispatcher.RouterGroup pageConsoleGroup = Dispatcher.group();
        pageConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        pageConsoleGroup.put("/console/page/", pageConsole::updatePage).
                delete("/console/page/{id}", pageConsole::removePage).
                post("/console/page/", pageConsole::addPage).
                put("/console/page/order/", pageConsole::changeOrder).
                get("/console/page/{id}", pageConsole::getPage).
                get("/console/pages/{page}/{pageSize}/{windowSize}", pageConsole::getPages);

        final PluginConsole pluginConsole = beanManager.getReference(PluginConsole.class);
        final Dispatcher.RouterGroup pluginConsoleGroup = Dispatcher.group();
        pluginConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        pluginConsoleGroup.put("/console/plugin/status/", pluginConsole::setPluginStatus).
                get("/console/plugins/{page}/{pageSize}/{windowSize}", pluginConsole::getPlugins).
                post("/console/plugin/toSetting", pluginConsole::toSetting).
                post("/console/plugin/updateSetting", pluginConsole::updateSetting);

        final PreferenceConsole preferenceConsole = beanManager.getReference(PreferenceConsole.class);
        final Dispatcher.RouterGroup preferenceConsoleGroup = Dispatcher.group();
        preferenceConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        preferenceConsoleGroup.get("/console/signs/", preferenceConsole::getSigns).
                get("/console/preference/", preferenceConsole::getPreference).
                put("/console/preference/", preferenceConsole::updatePreference);

        final SkinConsole skinConsole = beanManager.getReference(SkinConsole.class);
        final Dispatcher.RouterGroup skinConsoleGroup = Dispatcher.group();
        skinConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        skinConsoleGroup.get("/console/skin", skinConsole::getSkin).
                put("/console/skin", skinConsole::updateSkin);

        final RepairConsole repairConsole = beanManager.getReference(RepairConsole.class);
        final Dispatcher.RouterGroup repairConsoleGroup = Dispatcher.group();
        repairConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        repairConsoleGroup.get("/fix/restore-signs", repairConsole::restoreSigns).
                get("/fix/archivedate-articles", repairConsole::cleanArchiveDateArticles);

        final OtherConsole otherConsole = beanManager.getReference(OtherConsole.class);
        final Dispatcher.RouterGroup otherConsoleGroup = Dispatcher.group();
        otherConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        otherConsoleGroup.delete("/console/archive/unused", otherConsole::removeUnusedArchives).
                delete("/console/tag/unused", otherConsole::removeUnusedTags);

        final UserConsole userConsole = beanManager.getReference(UserConsole.class);
        final Dispatcher.RouterGroup userConsoleGroup = Dispatcher.group();
        userConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        userConsoleGroup.put("/console/user/", userConsole::updateUser).
                delete("/console/user/{id}", userConsole::removeUser).
                get("/console/users/{page}/{pageSize}/{windowSize}", userConsole::getUsers).
                get("/console/user/{id}", userConsole::getUser).
                get("/console/changeRole/{id}", userConsole::changeUserRole);

        final StaticSiteConsole staticSiteConsole = beanManager.getReference(StaticSiteConsole.class);
        final Dispatcher.RouterGroup staticSiteConsoleGroup = Dispatcher.group();
        staticSiteConsoleGroup.middlewares(consoleAdminAuthMidware::handle);
        staticSiteConsoleGroup.put("/console/staticsite", staticSiteConsole::genSite);
    }

    /**
     * Private constructor.
     */
    private Server() {
    }
}
