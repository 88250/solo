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
import java.net.URI;
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
     * Generates static site.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/static-site")
    public void genStaticSite(final RequestContext context) {


        try {
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

            context.renderJSON(0);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates static site fialed", e);
            context.renderJSON(-1);
        }
    }

    private static void requestFile(final String uri) throws Exception {
        final String staticSitePath = StaticSiteProcessor.class.getResource("/" + STATIC_SITE).toURI().getPath();
        FileUtils.forceMkdirParent(new File(staticSitePath + uri));
        final OutputStream outputStream = new FileOutputStream(staticSitePath + uri);
        String html = Mocks.mockRequest(uri);
        IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
        outputStream.close();
    }

    private static void copySkin() throws Exception {
        final URI skins = StaticSiteProcessor.class.getResource("/skins").toURI();

        FileUtils.forceMkdir(new File(StaticSiteProcessor.class.getResource("/" + STATIC_SITE).getPath() + "/skins/"));
        final URI siteSkins = StaticSiteProcessor.class.getResource("/" + STATIC_SITE + "/skins").toURI();
        FileUtils.deleteDirectory(new File(siteSkins));
        FileUtils.copyDirectory(new File(skins), new File(siteSkins));
    }

    private static void copyJS() throws Exception {
        final URI js = StaticSiteProcessor.class.getResource("/js").toURI();

        FileUtils.forceMkdir(new File(StaticSiteProcessor.class.getResource("/" + STATIC_SITE).getPath() + "/js/"));
        final URI siteJS = StaticSiteProcessor.class.getResource("/" + STATIC_SITE + "/js").toURI();
        FileUtils.deleteDirectory(new File(siteJS));
        FileUtils.copyDirectory(new File(js), new File(siteJS));
    }

    private static void copyImages() throws Exception {
        final URI images = StaticSiteProcessor.class.getResource("/images").toURI();

        FileUtils.forceMkdir(new File(StaticSiteProcessor.class.getResource("/" + STATIC_SITE).getPath() + "/images/"));
        final URI siteImages = StaticSiteProcessor.class.getResource("/" + STATIC_SITE + "/images").toURI();
        FileUtils.deleteDirectory(new File(siteImages));
        FileUtils.copyDirectory(new File(images), new File(siteImages));
    }

    private static void copyFile(final String file) throws Exception {
        final String sourcePath = StaticSiteProcessor.class.getResource("/").toURI().getPath();
        FileUtils.forceMkdirParent(new File(sourcePath + "/" + file));
        final String staticSitePath = StaticSiteProcessor.class.getResource("/" + STATIC_SITE).toURI().getPath();
        FileUtils.copyFile(new File(sourcePath + "/" + file), new File(staticSitePath + "/" + file));
    }
}
