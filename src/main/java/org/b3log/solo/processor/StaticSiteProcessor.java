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
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.annotation.Before;
import org.b3log.latke.http.annotation.RequestProcessing;
import org.b3log.latke.http.annotation.RequestProcessor;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.processor.console.ConsoleAuthAdvice;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Mocks;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Static site processor. HTML 静态站点生成 https://github.com/88250/solo/issues/19
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 8, 2020
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

            // 切换至静态站点生成模式
            Latkes.setServerScheme("https");
            // TODO: 前端传入生成站点域名
//            Latkes.setServerHost("88250.github.io");
            Latkes.setServerHost("dl88250.gitee.io");
            Latkes.setServerPort("");
            Solos.GEN_STATIC_SITE = true;

            genURI("/index.html");
            genURI("/blog/info");
            genURI("/manifest.json");

            genArticles();
            genSkins();
            genJS();
            genImages();
            genFile("sw.js");
            genFile("robots.txt");
            genFile("CHANGE_LOGS.md");

            // 恢复之前的动态运行模式
            Latkes.setServerScheme("http");
            Latkes.setServerHost("localhost");
            Latkes.setServerPort("8080");
            Solos.GEN_STATIC_SITE = false;

            LOGGER.log(Level.INFO, "Static site generated [dir=" + staticSitePath + "]");
            context.renderJSON(0);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates static site failed", e);
            context.renderJSON(-1);
        }
    }

    private static void genArticles() throws Exception {
        final BeanManager beanManager = BeanManager.getInstance();
        final ArticleQueryService articleQueryService = beanManager.getReference(ArticleQueryService.class);
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        final JSONObject preference = optionQueryService.getPreference();
        final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
        final int windowSize = preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        int currentPageNum = 1;
        int count = 0;
        final int maxPage = 10240;
        while (count++ < maxPage) {
            final JSONObject requestJSONObject = Solos.buildPaginationRequest(String.valueOf(currentPageNum) + '/' + pageSize + '/' + windowSize);
            requestJSONObject.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
            requestJSONObject.put(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT, false);
            final JSONObject result = articleQueryService.getArticles(requestJSONObject);
            final List<JSONObject> articles = CollectionUtils.jsonArrayToList(result.getJSONArray(Article.ARTICLES));
            if (articles.isEmpty()) {
                break;
            }

            genPage("?p=" + currentPageNum);

            articles.parallelStream().forEach(article -> {
                final String permalink = article.optString(Article.ARTICLE_PERMALINK);
                try {
                    genArticle(permalink);
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Generates an article [uri=" + permalink + "] failed", e);
                }
            });

            currentPageNum++;
        }
    }

    private static void genPage(final String uri) throws Exception {
        String filePath = uri;
        filePath = StringUtils.replace(filePath, "?", "/");
        filePath = StringUtils.replace(filePath, "=", "/");
        FileUtils.forceMkdir(new File(staticSitePath + filePath));
        final OutputStream outputStream = new FileOutputStream(staticSitePath + filePath + "/index.html");
        String html = Mocks.mockRequest(uri);
        IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
        outputStream.close();
        LOGGER.log(Level.INFO, "Generated a page [" + uri + "]");
    }

    private static void genURI(final String uri) throws Exception {
        FileUtils.forceMkdirParent(new File(staticSitePath + uri));
        final OutputStream outputStream = new FileOutputStream(staticSitePath + uri);
        String html = Mocks.mockRequest(uri);
        IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
        outputStream.close();
        LOGGER.log(Level.INFO, "Generated a file [" + uri + "]");
    }

    private static void genArticle(final String permalink) throws Exception {
        if (!StringUtils.endsWithIgnoreCase(permalink, ".html") && !StringUtils.endsWithIgnoreCase(permalink, ".htm")) {
            FileUtils.forceMkdir(new File(staticSitePath + permalink));
            final String html = Mocks.mockRequest(permalink);
            final OutputStream outputStream = new FileOutputStream(staticSitePath + permalink + "/index.html");
            IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
            outputStream.close();
        } else {
            FileUtils.forceMkdirParent(new File(staticSitePath + permalink));
            final String html = Mocks.mockRequest(permalink);
            final OutputStream outputStream = new FileOutputStream(staticSitePath + permalink);
            IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
            outputStream.close();
        }
        LOGGER.log(Level.INFO, "Generated an article [" + permalink + "]");
    }

    private static void genSkins() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/skins"));
        FileUtils.forceMkdir(new File(staticSitePath + "/skins"));
        FileUtils.copyDirectory(new File(StaticSiteProcessor.class.getResource("/skins").toURI()), new File(staticSitePath + "/skins"));
        LOGGER.log(Level.INFO, "Generated skins");
    }

    private static void genJS() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/js"));
        FileUtils.forceMkdir(new File(staticSitePath + "/js"));
        FileUtils.copyDirectory(new File(StaticSiteProcessor.class.getResource("/js").toURI()), new File(staticSitePath + "/js"));
        LOGGER.log(Level.INFO, "Generated js");
    }

    private static void genImages() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/images"));
        FileUtils.forceMkdir(new File(staticSitePath + "/images"));
        FileUtils.copyDirectory(new File(StaticSiteProcessor.class.getResource("/images").toURI()), new File(staticSitePath + "/images"));
        LOGGER.log(Level.INFO, "Generated images");
    }

    private static void genFile(final String file) throws Exception {
        FileUtils.forceMkdirParent(new File(staticSitePath + "/" + file));
        final String staticSitePath = StaticSiteProcessor.class.getResource("/" + STATIC_SITE).toURI().getPath();
        FileUtils.copyFile(new File(sourcePath + "/" + file), new File(staticSitePath + "/" + file));
        LOGGER.log(Level.INFO, "Generated a file [" + file + "]");
    }
}
