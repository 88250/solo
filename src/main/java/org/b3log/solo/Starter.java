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
import java.util.ResourceBundle;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Solo with embedded Jetty, <a href="https://github.com/b3log/solo/issues/12037">standalone mode</a>.
 *
 * <ul>
 * <li>Windows: java -cp WEB-INF/lib/*;WEB-INF/classes org.b3log.solo.Solo</li>
 * <li>Unix-like: java -cp WEB-INF/lib/*:WEB-INF/classes org.b3log.solo.Solo</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 31, 2015
 * @since 1.2.0
 */
public final class Starter {

    /**
     * Main.
     *
     * @param args the specified arguments
     * @throws java.lang.Exception if start failed
     */
    public static void main(final String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/"; // POM structure in dev env

        final File file = new File(webappDirLocation);
        if (!file.exists()) {
            webappDirLocation = "."; // prod env
        }

        final ResourceBundle latke = ResourceBundle.getBundle("latke");

        final int port = Integer.valueOf(latke.getString("serverPort"));
        final String contextPath = latke.getString("contextPath");

        Server server = new Server(port);
        WebAppContext root = new WebAppContext();

        root.setContextPath(contextPath);
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        server.setHandler(root);

        server.start();

        final String scheme = latke.getString("serverScheme");
        final String host = latke.getString("serverHost");

        try {
            Desktop.getDesktop().browse(new URI(scheme + "://" + host + ":" + port + contextPath));
        } catch (final Throwable e) {
            e.printStackTrace();
        }

        server.join();
    }

    /**
     * Private constructor.
     */
    private Starter() {
    }
}
