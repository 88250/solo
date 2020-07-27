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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.TextXmlRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.URLs;
import org.b3log.latke.util.XMLs;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Tag;
import org.b3log.solo.model.sitemap.Sitemap;
import org.b3log.solo.model.sitemap.URL;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONObject;

import java.util.List;

/**
 * Sitemap processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Mar 31, 2020
 * @since 0.3.1
 */
@Singleton
public class SitemapProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(SitemapProcessor.class);

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;

    /**
     * Returns the sitemap.
     *
     * @param context the specified context
     */
    public void sitemap(final RequestContext context) {
        final TextXmlRenderer renderer = new TextXmlRenderer();
        context.setRenderer(renderer);

        try {
            final Sitemap sitemap = new Sitemap();
            addArticles(sitemap);
            addNavigations(sitemap);
            addTags(sitemap);
            addArchives(sitemap);

            String content = sitemap.toString();
            content = XMLs.format(content);
            LOGGER.log(Level.INFO, "Generated sitemap");
            renderer.setContent(content);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates sitemap failed", e);
            context.sendError(500);
        }
    }

    /**
     * Adds articles into the specified sitemap.
     *
     * @param sitemap the specified sitemap
     * @throws Exception exception
     */
    private void addArticles(final Sitemap sitemap) throws Exception {
        final Query query = new Query().setPage(1, Integer.MAX_VALUE).
                setFilter(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED)).
                addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING).
                select(Article.ARTICLE_PERMALINK, Article.ARTICLE_UPDATED);
        final JSONObject articleResult = articleRepository.get(query);
        final List<JSONObject> articles = (List<JSONObject>) articleResult.opt(Keys.RESULTS);
        for (final JSONObject article : articles) {
            final String permalink = article.getString(Article.ARTICLE_PERMALINK);
            final URL url = new URL();
            url.setLoc(StringEscapeUtils.escapeXml(Latkes.getServePath() + permalink));
            final long updated = article.getLong(Article.ARTICLE_UPDATED);
            final String lastMod = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(updated);
            url.setLastMod(lastMod);
            sitemap.addURL(url);
        }
    }

    /**
     * Adds navigations into the specified sitemap.
     *
     * @param sitemap the specified sitemap
     * @throws Exception exception
     */
    private void addNavigations(final Sitemap sitemap) throws Exception {
        final JSONObject result = pageRepository.get(new Query());
        final List<JSONObject> pages = (List<JSONObject>) result.get(Keys.RESULTS);
        for (final JSONObject page : pages) {
            final String permalink = page.getString(Page.PAGE_PERMALINK);
            final URL url = new URL();
            // The navigation maybe a page or a link
            // Just filters for user mistakes tolerance
            if (!permalink.contains("://")) {
                url.setLoc(Latkes.getServePath() + permalink);
            } else {
                url.setLoc(permalink);
            }
            sitemap.addURL(url);
        }
    }

    /**
     * Adds tags (tag-articles) and tags wall (/tags.html) into the specified sitemap.
     *
     * @param sitemap the specified sitemap
     * @throws Exception exception
     */
    private void addTags(final Sitemap sitemap) throws Exception {
        final JSONObject result = tagRepository.get(new Query());
        final List<JSONObject> tags = (List<JSONObject>) result.opt(Keys.RESULTS);
        for (final JSONObject tag : tags) {
            final String link = URLs.encode(tag.getString(Tag.TAG_TITLE));
            final URL url = new URL();
            url.setLoc(Latkes.getServePath() + "/tags/" + link);
            sitemap.addURL(url);
        }

        // Tags wall
        final URL url = new URL();
        url.setLoc(Latkes.getServePath() + "/tags.html");
        sitemap.addURL(url);
    }

    /**
     * Adds archives (archive-articles) into the specified sitemap.
     *
     * @param sitemap the specified sitemap
     * @throws Exception exception
     */
    private void addArchives(final Sitemap sitemap) throws Exception {
        final JSONObject result = archiveDateRepository.get(new Query());
        final List<JSONObject> archiveDates = (List<JSONObject>) result.opt(Keys.RESULTS);
        for (final JSONObject archiveDate : archiveDates) {
            final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
            final String dateString = DateFormatUtils.format(time, "yyyy/MM");
            final URL url = new URL();
            url.setLoc(Latkes.getServePath() + "/archives/" + dateString);
            sitemap.addURL(url);
        }
    }
}
