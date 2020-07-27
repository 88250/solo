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
package org.b3log.solo.processor.console;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.*;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Mocks;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Static site console request processing. HTML 静态站点生成 https://github.com/88250/solo/issues/19
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.2, Mar 29, 2020
 * @since 3.9.0
 */
@Singleton
public class StaticSiteConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(StaticSiteConsole.class);

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Mock request scheme.
     */
    private static String scheme;

    /**
     * Mock request host.
     */
    private static String host;

    /**
     * Generates static site.
     *
     * @param context the specified request context
     */
    public synchronized void genSite(final RequestContext context) {
        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final String url = requestJSONObject.optString(Common.URL);
            if (!Strings.isURL(url)) {
                context.renderJSON(StatusCodes.ERR);
                context.renderMsg("Invalid site URL");
                return;
            }

            if (Latkes.isInJar()) {
                context.renderJSON(StatusCodes.ERR);
                context.renderMsg("Do not support this feature while running in Jar");
                return;
            }

            FileUtils.deleteDirectory(new File(staticSitePath));
            FileUtils.forceMkdir(new File(staticSitePath));

            final URL u = new URL(url);
            scheme = u.getProtocol();
            host = u.getHost();
            if (-1 != u.getPort()) {
                host += ":" + u.getPort();
            }
            Solos.GEN_STATIC_SITE = true;

            genURI("/tags.html");
            genURI("/archives.html");
            genURI("/links.html");
            genURI("/categories.html");
            genURI("/index.html");
            genURI("/blog/info");
            genURI("/manifest.json");
            genURI("/rss.xml");
            genURI("/articles/random.json");

            genArticles();
            genTags();
            genArchives();
            genCategories();
            genSkins();
            genJS();
            genImages();
            genPlugins();
            genFile("sw.js");
            genFile("robots.txt");
            genFile("CHANGE_LOGS.md");

            Solos.GEN_STATIC_SITE = false;

            LOGGER.log(Level.INFO, "Static site generated [dir=" + staticSitePath + "]");

            String siteGenedLabel = langPropsService.get("siteGenedLabel");
            siteGenedLabel = siteGenedLabel.replace("{dir}", staticSitePath);
            context.renderJSON(StatusCodes.SUCC);
            context.renderMsg(siteGenedLabel);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates static site failed", e);

            context.renderJSON(StatusCodes.ERR);
            context.renderMsg(langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Source root directory path.
     */
    private static String rootPath;

    /**
     * Path of generate directory.
     */
    private static String staticSitePath;


    static {
        if (!Latkes.isInJar()) {
            rootPath = StaticSiteConsole.class.getResource("/repository.json").getPath();
            rootPath = StringUtils.substringBeforeLast(rootPath, "/repository.json");
            staticSitePath = rootPath + "/static-site";
        } else {
            LOGGER.log(Level.INFO, "Do not support export static site when running in jar");
        }
    }

    private static void genCategories() {
        final BeanManager beanManager = BeanManager.getInstance();
        final CategoryQueryService categoryQueryService = beanManager.getReference(CategoryQueryService.class);
        final List<JSONObject> categories = categoryQueryService.getMostTagCategory(Integer.MAX_VALUE);
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        final JSONObject preference = optionQueryService.getPreference();
        final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

        categories.parallelStream().forEach(category -> {
            final String categoryURI = category.optString(Category.CATEGORY_URI);
            try {
                final int articleCount = categoryQueryService.getPublishedArticleCount(category.optString(Keys.OBJECT_ID));
                final int pageCount = (int) Math.ceil((double) articleCount / pageSize);
                int count = 0;
                while (count++ < pageCount) {
                    genPage("/category/" + categoryURI);
                    genPage("/category/" + categoryURI + "?p=" + count);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Generates a category [uri=" + categoryURI + "] failed", e);
            }
        });
    }

    private static void genArchives() throws Exception {
        final BeanManager beanManager = BeanManager.getInstance();
        final ArchiveDateQueryService archiveDateQueryService = beanManager.getReference(ArchiveDateQueryService.class);
        final List<JSONObject> archiveDates = archiveDateQueryService.getArchiveDates();
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        final JSONObject preference = optionQueryService.getPreference();
        final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

        archiveDates.parallelStream().forEach(archiveDate -> {
            final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
            final String dateString = DateFormatUtils.format(time, "yyyy/MM");
            try {
                final int articleCount = archiveDateQueryService.getArchiveDatePublishedArticleCount(archiveDate.optString(Keys.OBJECT_ID));
                final int pageCount = (int) Math.ceil((double) articleCount / pageSize);
                int count = 0;
                while (count++ < pageCount) {
                    genPage("/archives/" + dateString);
                    genPage("/archives/" + dateString + "?p=" + count);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Generates an archive [date=" + archiveDate + "] failed", e);
            }
        });
    }

    private static void genTags() throws Exception {
        final BeanManager beanManager = BeanManager.getInstance();
        final TagQueryService tagQueryService = beanManager.getReference(TagQueryService.class);
        final List<JSONObject> tags = tagQueryService.getTags();
        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        final JSONObject preference = optionQueryService.getPreference();
        final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

        tags.parallelStream().forEach(tag -> {
            final String tagTitle = tag.optString(Tag.TAG_TITLE);
            try {
                final int articleCount = tagQueryService.getArticleCount(tag.optString(Keys.OBJECT_ID));
                final int pageCount = (int) Math.ceil((double) articleCount / pageSize);
                int count = 0;
                while (count++ < pageCount) {
                    genPage("/tags/" + tagTitle);
                    genPage("/tags/" + tagTitle + "?p=" + count);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Generates a tag [title=" + tagTitle + "] failed", e);
            }
        });
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
            final JSONObject result = articleQueryService.getArticles(requestJSONObject);
            final List<JSONObject> articles = (List<JSONObject>) result.opt(Article.ARTICLES);
            if (articles.isEmpty()) {
                break;
            }

            genPage("?p=" + currentPageNum);

            articles.parallelStream().forEach(article -> {
                final String permalink = article.optString(Article.ARTICLE_PERMALINK);
                try {
                    genArticle(permalink);
                    genURI("/article/relevant/" + article.optString(Keys.OBJECT_ID) + ".json");
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
        String html = Mocks.mockRequest(uri, scheme, host);
        IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
        outputStream.close();
        LOGGER.log(Level.INFO, "Generated a page [" + uri + "]");
    }

    private static void genURI(final String uri) throws Exception {
        FileUtils.forceMkdirParent(new File(staticSitePath + uri));
        final OutputStream outputStream = new FileOutputStream(staticSitePath + uri);
        String html = Mocks.mockRequest(uri, scheme, host);
        IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
        outputStream.close();
        LOGGER.log(Level.INFO, "Generated a file [" + uri + "]");
    }

    private static void genArticle(final String permalink) throws Exception {
        if (!StringUtils.endsWithIgnoreCase(permalink, ".html") && !StringUtils.endsWithIgnoreCase(permalink, ".htm")) {
            FileUtils.forceMkdir(new File(staticSitePath + permalink));
            final String html = Mocks.mockRequest(permalink, scheme, host);
            final OutputStream outputStream = new FileOutputStream(staticSitePath + permalink + "/index.html");
            IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
            outputStream.close();
        } else {
            FileUtils.forceMkdirParent(new File(staticSitePath + permalink));
            final String html = Mocks.mockRequest(permalink, scheme, host);
            final OutputStream outputStream = new FileOutputStream(staticSitePath + permalink);
            IOUtils.write(html, outputStream, StandardCharsets.UTF_8);
            outputStream.close();
        }
        LOGGER.log(Level.INFO, "Generated an article [" + permalink + "]");
    }

    private static void genSkins() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/skins"));
        FileUtils.forceMkdir(new File(staticSitePath + "/skins"));
        FileUtils.copyDirectory(new File(rootPath + "/skins"), new File(staticSitePath + "/skins"));
        LOGGER.log(Level.INFO, "Generated skins");
    }

    private static void genJS() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/js"));
        FileUtils.forceMkdir(new File(staticSitePath + "/js"));
        FileUtils.copyDirectory(new File(rootPath + "/js"), new File(staticSitePath + "/js"));
        LOGGER.log(Level.INFO, "Generated js");
    }

    private static void genImages() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/images"));
        FileUtils.forceMkdir(new File(staticSitePath + "/images"));
        FileUtils.copyDirectory(new File(rootPath + "/images"), new File(staticSitePath + "/images"));
        LOGGER.log(Level.INFO, "Generated images");
    }

    private static void genPlugins() throws Exception {
        FileUtils.deleteDirectory(new File(staticSitePath + "/plugins"));
        FileUtils.forceMkdir(new File(staticSitePath + "/plugins"));
        FileUtils.copyDirectory(new File(rootPath + "/plugins"), new File(staticSitePath + "/plugins"));
        genURI("/plugins/kanbanniang/assets/model.json");
        LOGGER.log(Level.INFO, "Generated plugins");

    }

    private static void genFile(final String file) throws Exception {
        FileUtils.forceMkdirParent(new File(staticSitePath + "/" + file));
        FileUtils.copyFile(new File(rootPath + "/" + file), new File(staticSitePath + "/" + file));
        LOGGER.log(Level.INFO, "Generated a file [" + file + "]");
    }

}
