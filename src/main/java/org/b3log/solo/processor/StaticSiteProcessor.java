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
package org.b3log.solo.processor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.annotation.Before;
import org.b3log.latke.http.annotation.RequestProcessing;
import org.b3log.latke.http.annotation.RequestProcessor;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.solo.processor.console.ConsoleAuthAdvice;
import org.b3log.solo.util.Mocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Static site processor. HTML 静态站点生成 https://github.com/88250/solo/issues/19
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jan 7, 2020
 * @since 3.9.0
 */
@RequestProcessor
@Before(ConsoleAuthAdvice.class)
public class StaticSiteProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticSiteProcessor.class);

    /**
     * Name of generate directory.
     */
    private static final String STATIC_SITE = "static-site";

    /**
     * Path of generate directory.
     */
    private static final String staticSitePath = StaticSiteProcessor.class.getResource("/" + STATIC_SITE).getPath();

    /**
     * Source directory path.
     */
    private static final String sourcePath = StaticSiteProcessor.class.getResource("/").getPath();

    /**
     * Generates static site.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/static-site")
    public void genStaticSite(final RequestContext context) {
        try {
            FileUtils.forceMkdir(new File(staticSitePath + "/skins/"));

            Latkes.setServerScheme("https");
            // TODO: 前端传入生成站点域名
            Latkes.setServerHost("88250.github.io");
            Latkes.setServerPort("");

            requestFile("/index.html");
            requestFile("/blog/info");
            requestFile("/manifest.json");

            Latkes.setServerScheme("http");
            Latkes.setServerHost("localhost");
            Latkes.setServerPort("8080");

            copySkin();
            copyJS();
            copyImages();
            copyFile("sw.js");
            copyFile("robots.txt");
            copyFile("CHANGE_LOGS.md");

            LOGGER.log(Level.INFO, "Static site generated [dir=" + staticSitePath + "]");

            context.renderJSON(0);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates static site failed", e);
            context.renderJSON(-1);
        }
    }

    private static void requestFile(final String uri) throws Exception {
        FileUtils.forceMkdirParent(new File(staticSitePath + uri));
        final OutputStream outputStream = new FileOutputStream(staticSitePath + uri);
        String html = Mocks.mockRequest(uri);
        IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
        outputStream.close();
    }

    private static void copySkin() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/skins"));
        FileUtils.forceMkdir(new File(staticSitePath + "/skins"));
        FileUtils.copyDirectory(new File(StaticSiteProcessor.class.getResource("/skins").toURI()), new File(staticSitePath + "/skins"));
    }

    private static void copyJS() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/js"));
        FileUtils.forceMkdir(new File(staticSitePath + "/js"));
        FileUtils.copyDirectory(new File(StaticSiteProcessor.class.getResource("/js").toURI()), new File(staticSitePath + "/js"));
    }

    private static void copyImages() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/images"));
        FileUtils.forceMkdir(new File(staticSitePath + "/images"));
        FileUtils.copyDirectory(new File(StaticSiteProcessor.class.getResource("/images").toURI()), new File(staticSitePath + "/images"));
    }

    private static void copyFile(final String file) throws Exception {
        FileUtils.forceMkdirParent(new File(staticSitePath + "/" + file));
        final String staticSitePath = StaticSiteProcessor.class.getResource("/" + STATIC_SITE).toURI().getPath();
        FileUtils.copyFile(new File(sourcePath + "/" + file), new File(staticSitePath + "/" + file));
    }
}
