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
import org.b3log.latke.Latkes;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.http.BaseServer;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
 * @version 2.0.0.5, Nov 18, 2019
 * @since 1.2.0
 */
public final class Server extends BaseServer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Server.class);

    /**
     * Solo version.
     */
    public static final String VERSION = "3.6.7";

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
                hasArg().desc("lute http URL, default is http://localhost:8249, see https://github.com/b3log/lute-http for more details").build();
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

        routeConsoleProcessors();

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
        LOGGER.log(Level.DEBUG, "Stopwatch: {0}{1}", Strings.LINE_SEPARATOR, Stopwatchs.getTimingStat());
        Stopwatchs.release();

        server.start(Integer.valueOf(portArg));
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

    /**
     * 后台控制器使用函数式路由. https://github.com/b3log/solo/issues/12580
     */
    public static void routeConsoleProcessors() {
        final BeanManager beanManager = BeanManager.getInstance();
        final AdminConsole adminConsole = beanManager.getReference(AdminConsole.class);
        Dispatcher.get("/admin-index.do", adminConsole::showAdminIndex);
        Dispatcher.get("/admin-preference.do", adminConsole::showAdminPreferenceFunction);
        Dispatcher.route().get(new String[]{"/admin-article.do",
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
                "/admin-main.do",
                "/admin-about.do"}, adminConsole::showAdminFunctions);
        Dispatcher.get("/console/export/sql", adminConsole::exportSQL);
        Dispatcher.get("/console/export/json", adminConsole::exportJSON);
        Dispatcher.get("/console/export/hexo", adminConsole::exportHexo);

        final ArticleConsole articleConsole = beanManager.getReference(ArticleConsole.class);
        Dispatcher.get("/console/article/push2rhy", articleConsole::pushArticleToCommunity);
        Dispatcher.get("/console/thumbs", articleConsole::getArticleThumbs);
        Dispatcher.get("/console/article/{id}", articleConsole::getArticle);
        Dispatcher.get("/console/articles/status/{status}/{page}/{pageSize}/{windowSize}", articleConsole::getArticles);
        Dispatcher.delete("/console/article/{id}", articleConsole::removeArticle);
        Dispatcher.put("/console/article/unpublish/{id}", articleConsole::cancelPublishArticle);
        Dispatcher.put("/console/article/canceltop/{id}", articleConsole::cancelTopArticle);
        Dispatcher.put("/console/article/puttop/{id}", articleConsole::putTopArticle);
        Dispatcher.put("/console/article/", articleConsole::updateArticle);
        Dispatcher.post("/console/article/", articleConsole::addArticle);

        final CategoryConsole categoryConsole = beanManager.getReference(CategoryConsole.class);
        Dispatcher.put("/console/category/order/", categoryConsole::changeOrder);
        Dispatcher.get("/console/category/{id}", categoryConsole::getCategory);
        Dispatcher.delete("/console/category/{id}", categoryConsole::removeCategory);
        Dispatcher.put("/console/category/", categoryConsole::updateCategory);
        Dispatcher.post("/console/category/", categoryConsole::addCategory);
        Dispatcher.get("/console/categories/{page}/{pageSize}/{windowSize}", categoryConsole::getCategories);

        final CommentConsole commentConsole = beanManager.getReference(CommentConsole.class);
        Dispatcher.delete("/console/article/comment/{id}", commentConsole::removeArticleComment);
        Dispatcher.get("/console/comments/{page}/{pageSize}/{windowSize}", commentConsole::getComments);
        Dispatcher.get("/console/comments/article/{id}", commentConsole::getArticleComments);

        final LinkConsole linkConsole = beanManager.getReference(LinkConsole.class);
        Dispatcher.delete("/console/link/{id}", linkConsole::removeLink);
        Dispatcher.put("/console/link/", linkConsole::updateLink);
        Dispatcher.put("/console/link/order/", linkConsole::changeOrder);
        Dispatcher.post("/console/link/", linkConsole::addLink);
        Dispatcher.get("/console/links/{page}/{pageSize}/{windowSize}", linkConsole::getLinks);
        Dispatcher.get("/console/link/{id}", linkConsole::getLink);

        final PageConsole pageConsole = beanManager.getReference(PageConsole.class);
        Dispatcher.put("/console/page/", pageConsole::updatePage);
        Dispatcher.delete("/console/page/{id}", pageConsole::removePage);
        Dispatcher.post("/console/page/", pageConsole::addPage);
        Dispatcher.put("/console/page/order/", pageConsole::changeOrder);
        Dispatcher.get("/console/page/{id}", pageConsole::getPage);
        Dispatcher.get("/console/pages/{page}/{pageSize}/{windowSize}", pageConsole::getPages);

        final PluginConsole pluginConsole = beanManager.getReference(PluginConsole.class);
        Dispatcher.put("/console/plugin/status/", pluginConsole::setPluginStatus);
        Dispatcher.get("/console/plugins/{page}/{pageSize}/{windowSize}", pluginConsole::getPlugins);
        Dispatcher.post("/console/plugin/toSetting", pluginConsole::toSetting);
        Dispatcher.post("/console/plugin/updateSetting", pluginConsole::updateSetting);

        final PreferenceConsole preferenceConsole = beanManager.getReference(PreferenceConsole.class);
        Dispatcher.get("/console/signs/", preferenceConsole::getSigns);
        Dispatcher.get("/console/preference/", preferenceConsole::getPreference);
        Dispatcher.put("/console/preference/", preferenceConsole::updatePreference);

        final SkinConsole skinConsole = beanManager.getReference(SkinConsole.class);
        Dispatcher.get("/console/skin", skinConsole::getSkin);
        Dispatcher.put("/console/skin", skinConsole::updateSkin);

        final RepairConsole repairConsole = beanManager.getReference(RepairConsole.class);
        Dispatcher.get("/fix/restore-signs", repairConsole::restoreSigns);
        Dispatcher.get("/fix/archivedate-articles", repairConsole::cleanArchiveDateArticles);

        final TagConsole tagConsole = beanManager.getReference(TagConsole.class);
        Dispatcher.get("/console/tags", tagConsole::getTags);
        Dispatcher.get("/console/tag/unused", tagConsole::getUnusedTags);

        final OtherConsole otherConsole = beanManager.getReference(OtherConsole.class);
        Dispatcher.delete("/console/archive/unused", otherConsole::removeUnusedArchives);
        Dispatcher.delete("/console/tag/unused", otherConsole::removeUnusedTags);

        final UserConsole userConsole = beanManager.getReference(UserConsole.class);
        Dispatcher.put("/console/user/", userConsole::updateUser);
        Dispatcher.delete("/console/user/{id}", userConsole::removeUser);
        Dispatcher.get("/console/users/{page}/{pageSize}/{windowSize}", userConsole::getUsers);
        Dispatcher.get("/console/user/{id}", userConsole::getUser);
        Dispatcher.get("/console/changeRole/{id}", userConsole::changeUserRole);

        Dispatcher.mapping();
    }

    /**
     * Private constructor.
     */
    private Server() {
    }
}
