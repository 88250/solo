/*
 * Copyright (c) 2010-2015, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Strings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Solo with embedded Jetty, <a href="https://github.com/b3log/solo/issues/12037">standalone mode</a>.
 *
 * <ul>
 * <li>Windows: java -cp WEB-INF/lib/*;WEB-INF/classes org.b3log.solo.Starter</li>
 * <li>Unix-like: java -cp WEB-INF/lib/*:WEB-INF/classes org.b3log.solo.Starter</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Dec 4, 2015
 * @since 1.2.0
 */
public final class Starter {

    static {
        try {
            Log.setLog(new Slf4jLog());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main.
     *
     * @param args the specified arguments
     * @throws java.lang.Exception if start failed
     */
    public static void main(String[] args) throws Exception {
        final Logger logger = Logger.getLogger(Starter.class);

        final Options options = new Options();
        final Option listenPortOpt = Option.builder().longOpt("listen_port")
                .hasArg().desc("listen port").build();
        options.addOption(listenPortOpt);

        final Option serverSchemeOpt = Option.builder().longOpt("server_scheme")
                .hasArg().desc("browser visit protocol").build();
        options.addOption(serverSchemeOpt);

        final Option serverHostOpt = Option.builder().longOpt("server_host")
                .hasArg().desc("browser visit domain name").build();
        options.addOption(serverHostOpt);

        final Option serverPortOpt = Option.builder().longOpt("server_port")
                .hasArg().desc("browser visit port").build();
        options.addOption(serverPortOpt);

        final Option staticServerSchemeOpt = Option.builder().longOpt("static_server_scheme")
                .hasArg().desc("browser visit static resource protocol").build();
        options.addOption(staticServerSchemeOpt);

        final Option staticServerHostOpt = Option.builder().longOpt("static_server_host")
                .hasArg().desc("browser visit static resource domain name").build();
        options.addOption(staticServerHostOpt);

        final Option staticServerPortOpt = Option.builder().longOpt("static_server_port")
                .hasArg().desc("browser visit static resource port").build();
        options.addOption(staticServerPortOpt);

        final Option contextPathOpt = Option.builder().longOpt("context_path")
                .hasArg().desc("context path").build();
        options.addOption(contextPathOpt);

        final Option staticPathOpt = Option.builder().longOpt("static_path")
                .hasArg().desc("static path").build();
        options.addOption(staticPathOpt);

        final CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine = commandLineParser.parse(options, args);

        String portArg = commandLine.getOptionValue("listen_port");
        if (!Strings.isNumeric(portArg)) {
            portArg = "8080";
        }

        String serverScheme = commandLine.getOptionValue("server_scheme");
        Latkes.setServerScheme(serverScheme);
        String serverHost = commandLine.getOptionValue("server_host");
        Latkes.setServerHost(serverHost);
        String serverPort = commandLine.getOptionValue("server_port");
        Latkes.setServerPort(serverPort);
        String staticServerScheme = commandLine.getOptionValue("static_server_scheme");
        Latkes.setStaticServerScheme(staticServerScheme);
        String staticServerHost = commandLine.getOptionValue("static_server_host");
        Latkes.setStaticServerHost(staticServerHost);
        String staticServerPort = commandLine.getOptionValue("static_server_port");
        Latkes.setStaticServerPort(staticServerPort);
        String contextPath = commandLine.getOptionValue("context_path");
        Latkes.setContextPath(contextPath);
        String staticPath = commandLine.getOptionValue("static_path");
        Latkes.setStaticPath(staticPath);

        logger.info("Standalone mode, see [https://github.com/b3log/solo/wiki/standalone_mode] for more details.");
        Latkes.initRuntimeEnv();

        String webappDirLocation = "src/main/webapp/"; // POM structure in dev env
        final File file = new File(webappDirLocation);
        if (!file.exists()) {
            webappDirLocation = "."; // production environment
        }

        final int port = Integer.valueOf(portArg);

        contextPath = Latkes.getContextPath();
        if (Strings.isEmptyOrNull(contextPath)) {
            contextPath = "/";
        }

        final Server server = new Server(port);
        final WebAppContext root = new WebAppContext();

        root.setContextPath(contextPath);
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);
        server.setHandler(root);
        server.start();

        serverScheme = Latkes.getServerScheme();
        serverHost = Latkes.getServerHost();
        serverPort = Latkes.getServerPort();
        contextPath = Latkes.getContextPath();

        try {
            Desktop.getDesktop().browse(new URI(serverScheme + "://" + serverHost + ":" + serverPort + contextPath));
        } catch (final Throwable e) {
            // Ignored
        }

        server.join();
    }

    /**
     * Private constructor.
     */
    private Starter() {
    }
}
