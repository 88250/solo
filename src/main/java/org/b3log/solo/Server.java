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
package org.b3log.solo;

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
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
import org.b3log.solo.event.PluginRefresher;
import org.b3log.solo.processor.*;
import org.b3log.solo.processor.console.*;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Statics;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.0.1.16, Mar 22, 2021
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
    public static final String VERSION = "4.3.1";

    /**
     * In-Memory tail logger writer.
     */
    public static final TailStringWriter TAIL_LOGGER_WRITER = new TailStringWriter();

    /**
     * Initializes In-Memory logger. 后台增加服务端日志浏览 https://github.com/88250/solo/issues/91
     */
    public static void initInMemoryLogger() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final StringLayout layout = PatternLayout.newBuilder().withPattern("[%-5p]-[%d{yyyy-MM-dd HH:mm:ss}]-[%c:%L]: %m%n").build();
        final Appender appender = WriterAppender.createAppender(layout, null, TAIL_LOGGER_WRITER, "InMemoryTail", true, true);
        appender.start();
        config.addAppender(appender);
        config.getRootLogger().addAppender(appender, Level.TRACE, null);
        ctx.updateLoggers();
    }

    public static class TailStringWriter extends StringWriter {

        private final AtomicInteger count = new AtomicInteger();

        @Override
        public void flush() {
            super.flush();
            if (2048 <= count.incrementAndGet()) {
                super.getBuffer().setLength(0);
                count.set(0);
            }
        }
    }

    /**
     * Main.
     *
     * @param args the specified arguments
     */
    public static void main(final String[] args) {
        System.setProperty("https.protocols", "TLSv1.2");
        initInMemoryLogger();
        Stopwatchs.start("Booting");

        final Options options = new Options();
        final Option listenPortOpt = Option.builder().longOpt("listen_port").argName("LISTEN_PORT").hasArg().desc("listen port, default is 8080").build();
        options.addOption(listenPortOpt);

        final Option unixDomainSocketPathOpt = Option.builder().longOpt("unix_domain_socket_path").argName("UNIX_DOMAIN_SOCKET_PATH").hasArg().desc("unix domain socket path").build();
        options.addOption(unixDomainSocketPathOpt);

        final Option serverSchemeOpt = Option.builder().longOpt("server_scheme").argName("SERVER_SCHEME").hasArg().desc("browser visit protocol, default is http").build();
        options.addOption(serverSchemeOpt);

        final Option serverHostOpt = Option.builder().longOpt("server_host").argName("SERVER_HOST").hasArg().desc("browser visit domain name, default is localhost").build();
        options.addOption(serverHostOpt);

        final Option serverPortOpt = Option.builder().longOpt("server_port").argName("SERVER_PORT").hasArg().desc("browser visit port, default is 8080").build();
        options.addOption(serverPortOpt);

        final Option staticServerSchemeOpt = Option.builder().longOpt("static_server_scheme").argName("STATIC_SERVER_SCHEME").hasArg().desc("browser visit static resource protocol, default is http").build();
        options.addOption(staticServerSchemeOpt);

        final Option staticServerHostOpt = Option.builder().longOpt("static_server_host").argName("STATIC_SERVER_HOST").hasArg().desc("browser visit static resource domain name, default is localhost").build();
        options.addOption(staticServerHostOpt);

        final Option staticServerPortOpt = Option.builder().longOpt("static_server_port").argName("STATIC_SERVER_PORT").hasArg().desc("browser visit static resource port, default is 8080").build();
        options.addOption(staticServerPortOpt);

        final Option staticPathOpt = Option.builder().longOpt("static_path").argName("STATIC_PATH").hasArg().desc("browser visit static resource path, default is empty").build();
        options.addOption(staticPathOpt);

        final Option runtimeModeOpt = Option.builder().longOpt("runtime_mode").argName("RUNTIME_MODE").hasArg().desc("runtime mode (DEVELOPMENT/PRODUCTION), default is DEVELOPMENT").build();
        options.addOption(runtimeModeOpt);

        final Option luteHttpOpt = Option.builder().longOpt("lute_http").argName("LUTE_HTTP").hasArg().desc("lute http URL, default is http://localhost:8249, see https://github.com/88250/lute-http for more details").build();
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

        try {
            Latkes.setScanPath("org.b3log.solo");
            Latkes.init();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Latke init failed, please configure latke.props or run with args, visit https://ld246.com/article/1492881378588 for more details");

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
        String staticPath = commandLine.getOptionValue("static_path");
        if (null != staticPath) {
            if (StringUtils.equals(staticServerHost, "cdn.jsdelivr.net")) {
                // 如果使用了 jsDelivr，则需要加上版本号避免 CDN 缓存问题 https://github.com/88250/solo/issues/83
                // /gh/88250/solo/src/main/resources => /gh/88250/solo@version/src/main/resources
                if (!StringUtils.contains(staticPath, "@")) {
                    String gitCommit = System.getenv("git_commit");
                    if (StringUtils.isBlank(gitCommit)) {
                        gitCommit = Server.VERSION;
                    }
                    LOGGER.log(Level.INFO, "Git commit [" + gitCommit + "]");
                    staticPath = StringUtils.replace(staticPath, "/solo/", "/solo@" + gitCommit + "/");
                }
            }
            Latkes.setLatkeProperty("staticPath", staticPath);
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

        Statics.clear();

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

        final String unixDomainSocketPath = commandLine.getOptionValue("unix_domain_socket_path");
        if (StringUtils.isNotBlank(unixDomainSocketPath)) {
            server.start(unixDomainSocketPath);
        } else {
            String portArg = commandLine.getOptionValue("listen_port");
            if (!Strings.isNumeric(portArg)) {
                portArg = "8080";
            }
            server.start(Integer.parseInt(portArg));
        }
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

            Markdowns.loadMarkdownOption(preference);
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
        Dispatcher.startRequestHandler = new BeforeRequestHandler();
        Dispatcher.HANDLERS.add(1, new SkinHandler());
        Dispatcher.HANDLERS.add(2, new InitCheckHandler());
        Dispatcher.HANDLERS.add(3, new PermalinkHandler());
        Dispatcher.endRequestHandler = new AfterRequestHandler();

        routeConsoleProcessors();
        routeIndexProcessors();
        Dispatcher.mapping();
    }

    /**
     * 前台路由.
     */
    private static void routeIndexProcessors() {
        final BeanManager beanManager = BeanManager.getInstance();
        final StaticMidware staticMidware = beanManager.getReference(StaticMidware.class);

        final ArticleProcessor articleProcessor = beanManager.getReference(ArticleProcessor.class);
        final Dispatcher.RouterGroup articleGroup = Dispatcher.group();
        articleGroup.post("/console/markdown/2html", articleProcessor::markdown2HTML).
                get("/console/article-pwd", articleProcessor::showArticlePwdForm).
                post("/console/article-pwd", articleProcessor::onArticlePwdForm).
                get("/articles/random.json", articleProcessor::getRandomArticles).
                get("/article/relevant/{id}.json", articleProcessor::getRelevantArticles).
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
        b3Group.post("/apis/symphony/article", b3Receiver::receiveArticle);

        final BlogProcessor blogProcessor = beanManager.getReference(BlogProcessor.class);
        final Dispatcher.RouterGroup blogGroup = Dispatcher.group();
        blogGroup.get("/manifest.json", blogProcessor::getPWAManifestJSON).
                get("/blog/info", blogProcessor::getBlogInfo).
                get("/blog/articles-tags", blogProcessor::getArticlesTags);

        final CategoryProcessor categoryProcessor = beanManager.getReference(CategoryProcessor.class);
        final Dispatcher.RouterGroup categoryGroup = Dispatcher.group();
        categoryGroup.middlewares(staticMidware::handle);
        categoryGroup.get("/articles/category/{categoryURI}", categoryProcessor::getCategoryArticlesByPage).
                get("/category/{categoryURI}", categoryProcessor::showCategoryArticles);

        final FeedProcessor feedProcessor = beanManager.getReference(FeedProcessor.class);
        final Dispatcher.RouterGroup feedGroup = Dispatcher.group();
        feedGroup.middlewares(staticMidware::handle);
        feedGroup.router().get().head().uri("/atom.xml").handler(feedProcessor::blogArticlesAtom).
                get().head().uri("/rss.xml").handler(feedProcessor::blogArticlesRSS);

        final IndexProcessor indexProcessor = beanManager.getReference(IndexProcessor.class);
        final Dispatcher.RouterGroup indexGroup = Dispatcher.group();
        indexGroup.middlewares(staticMidware::handle);
        indexGroup.router().get(new String[]{"", "/", "/index.html"}, indexProcessor::showIndex);
        indexGroup.get("/favicon.ico", indexProcessor::showFavicon).
                get("/start", indexProcessor::showStart).
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
        sitemapGroup.middlewares(staticMidware::handle);
        sitemapGroup.get("/sitemap.xml", sitemapProcessor::sitemap);

        final TagProcessor tagProcessor = beanManager.getReference(TagProcessor.class);
        final Dispatcher.RouterGroup tagGroup = Dispatcher.group();
        tagGroup.middlewares(staticMidware::handle);
        tagGroup.get("/tags/{tagTitle}", tagProcessor::showTagArticles);

        final UserTemplateProcessor userTemplateProcessor = beanManager.getReference(UserTemplateProcessor.class);
        final Dispatcher.RouterGroup userTemplateGroup = Dispatcher.group();
        userTemplateGroup.middlewares(staticMidware::handle);
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
                get("/console/export/hexo", adminConsole::exportHexo).
                post("/console/import/markdown-zip", adminConsole::importMarkdownZip);
        adminConsoleGroup.router().get(new String[]{"/admin-article.do",
                "/admin-article-list.do",
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

        final TagConsole tagConsole = beanManager.getReference(TagConsole.class);
        final Dispatcher.RouterGroup tagConsoleGroup = Dispatcher.group();
        tagConsoleGroup.middlewares(consoleAuthMidware::handle);
        tagConsoleGroup.get("/console/tags", tagConsole::getTags);

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
        otherConsoleGroup.get("/console/log", otherConsole::getLog);

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

        final FetchUploadProcessor fetchUploadProcessor = beanManager.getReference(FetchUploadProcessor.class);
        Dispatcher.post("/upload/fetch", fetchUploadProcessor::fetchUpload, consoleAuthMidware::handle);
    }

    /**
     * Private constructor.
     */
    private Server() {
    }
}
