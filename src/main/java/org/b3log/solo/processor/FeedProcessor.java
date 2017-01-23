/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
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
package org.b3log.solo.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.CompositeFilter;
import org.b3log.latke.repository.CompositeFilterOperator;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.AtomRenderer;
import org.b3log.latke.servlet.renderer.RssRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Tag;
import org.b3log.solo.model.feed.atom.Category;
import org.b3log.solo.model.feed.atom.Entry;
import org.b3log.solo.model.feed.atom.Feed;
import org.b3log.solo.model.feed.rss.Channel;
import org.b3log.solo.model.feed.rss.Item;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Feed (Atom/RSS) processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://github.com/feroozkhanchintu">feroozkhanchintu</a>
 * @version 1.1.0.6, Sep 28, 2016
 * @since 0.3.1
 */
@RequestProcessor
public class FeedProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FeedProcessor.class.getName());

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Blog articles Atom output.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = {"/blog-articles-feed.do"}, method = {HTTPRequestMethod.GET, HTTPRequestMethod.HEAD})
    public void blogArticlesAtom(final HTTPRequestContext context) {
        final AtomRenderer renderer = new AtomRenderer();

        context.setRenderer(renderer);

        final Feed feed = new Feed();

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            final String blogSubtitle = preference.getString(Option.ID_C_BLOG_SUBTITLE);
            final int outputCnt = preference.getInt(Option.ID_C_FEED_OUTPUT_CNT);

            feed.setTitle(StringEscapeUtils.escapeXml(blogTitle));
            feed.setSubtitle(StringEscapeUtils.escapeXml(blogSubtitle));
            feed.setUpdated(new Date());
            feed.setAuthor(StringEscapeUtils.escapeXml(blogTitle));
            feed.setLink(Latkes.getServePath() + "/blog-articles-feed.do");
            feed.setId(Latkes.getServePath() + "/");

            final List<Filter> filters = new ArrayList<Filter>();

            filters.add(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true));
            filters.add(new PropertyFilter(Article.ARTICLE_VIEW_PWD, FilterOperator.EQUAL, ""));
            final Query query = new Query().setCurrentPageNum(1).setPageSize(outputCnt).setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters)).addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING).setPageCount(
                    1);

            final boolean hasMultipleUsers = userQueryService.hasMultipleUsers();
            String authorName = "";

            final JSONObject articleResult = articleRepository.get(query);
            final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);

            if (!hasMultipleUsers && 0 != articles.length()) {
                authorName = articleQueryService.getAuthor(articles.getJSONObject(0)).getString(User.USER_NAME);
            }

            final boolean isFullContent = "fullContent".equals(preference.getString(Option.ID_C_FEED_OUTPUT_MODE));

            for (int i = 0; i < articles.length(); i++) {
                Entry entry = getEntry(hasMultipleUsers, authorName, articles, isFullContent, i);
                feed.addEntry(entry);
            }

            renderer.setContent(feed.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get blog article feed error", e);

            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Entry getEntry(final boolean hasMultipleUsers, String authorName, final JSONArray articles,
            final boolean isFullContent, int i)
            throws org.json.JSONException, org.b3log.latke.service.ServiceException {
        final JSONObject article = articles.getJSONObject(i);
        final Entry ret = new Entry();
        final String title = StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_TITLE));
        ret.setTitle(title);
        final String summary = isFullContent ? StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_CONTENT))
                : StringEscapeUtils.escapeXml(article.optString(Article.ARTICLE_ABSTRACT));
        ret.setSummary(summary);
        final Date updated = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
        ret.setUpdated(updated);
        final String link = Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK);
        ret.setLink(link);
        ret.setId(link);
        if (hasMultipleUsers) {
            authorName = StringEscapeUtils.escapeXml(articleQueryService.getAuthor(article).getString(User.USER_NAME));
        }
        ret.setAuthor(authorName);
        final String tagsString = article.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        for (final String tagString : tagStrings) {
            final Category catetory = new Category();
            ret.addCatetory(catetory);
            final String tag = tagString;
            catetory.setTerm(tag);
        }

        return ret;
    }

    /**
     * Tag articles Atom output.
     *
     * @param context the specified context
     * @throws IOException io exception
     */
    @RequestProcessing(value = {"/tag-articles-feed.do"}, method = {HTTPRequestMethod.GET, HTTPRequestMethod.HEAD})
    public void tagArticlesAtom(final HTTPRequestContext context) throws IOException {
        final AtomRenderer renderer = new AtomRenderer();

        context.setRenderer(renderer);

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        final String queryString = request.getQueryString();

        if (Strings.isEmptyOrNull(queryString)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String oIdMap = queryString.split("&")[0];
        final String tagId = oIdMap.split("=")[1];

        final Feed feed = new Feed();

        try {
            final JSONObject tag = tagRepository.get(tagId);

            if (null == tag) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String tagTitle = tag.getString(Tag.TAG_TITLE);

            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            final String blogSubtitle = preference.getString(Option.ID_C_BLOG_SUBTITLE) + ", " + tagTitle;
            final int outputCnt = preference.getInt(Option.ID_C_FEED_OUTPUT_CNT);

            feed.setTitle(StringEscapeUtils.escapeXml(blogTitle));
            feed.setSubtitle(StringEscapeUtils.escapeXml(blogSubtitle));
            feed.setUpdated(new Date());
            feed.setAuthor(StringEscapeUtils.escapeXml(blogTitle));
            feed.setLink(Latkes.getServePath() + "/tag-articles-feed.do");
            feed.setId(Latkes.getServePath() + "/");

            final JSONObject tagArticleResult = tagArticleRepository.getByTagId(tagId, 1, outputCnt);
            final JSONArray tagArticleRelations = tagArticleResult.getJSONArray(Keys.RESULTS);

            if (0 == tagArticleRelations.length()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final List<JSONObject> articles = new ArrayList<JSONObject>();

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation = tagArticleRelations.getJSONObject(i);
                final String articleId = tagArticleRelation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);

                if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED) // Skips the unpublished article
                        && Strings.isEmptyOrNull(article.optString(Article.ARTICLE_VIEW_PWD))) { // Skips article with password
                    articles.add(article);
                }
            }

            final boolean hasMultipleUsers = userQueryService.hasMultipleUsers();
            String authorName = "";

            if (!hasMultipleUsers && !articles.isEmpty()) {
                authorName = articleQueryService.getAuthor(articles.get(0)).getString(User.USER_NAME);
            }

            final boolean isFullContent = "fullContent".equals(preference.getString(Option.ID_C_FEED_OUTPUT_MODE));

            for (int i = 0; i < articles.size(); i++) {
                Entry entry = getEntryForArticle(articles, hasMultipleUsers, authorName, isFullContent, i);
                feed.addEntry(entry);
            }

            renderer.setContent(feed.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get tag article feed error", e);

            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Entry getEntryForArticle(final List<JSONObject> articles, final boolean hasMultipleUsers, String authorName,
            final boolean isFullContent, int i)
            throws org.json.JSONException, org.b3log.latke.service.ServiceException {
        final JSONObject article = articles.get(i);
        final Entry ret = new Entry();
        final String title = StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_TITLE));
        ret.setTitle(title);
        final String summary = isFullContent ? StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_CONTENT))
                : StringEscapeUtils.escapeXml(article.optString(Article.ARTICLE_ABSTRACT));
        ret.setSummary(summary);
        final Date updated = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
        ret.setUpdated(updated);
        final String link = Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK);
        ret.setLink(link);
        ret.setId(link);
        if (hasMultipleUsers) {
            authorName = StringEscapeUtils.escapeXml(articleQueryService.getAuthor(article).getString(User.USER_NAME));
        }
        ret.setAuthor(authorName);
        final String tagsString = article.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        for (final String tagString : tagStrings) {
            final Category catetory = new Category();
            ret.addCatetory(catetory);
            catetory.setTerm(tagString);
        }

        return ret;
    }

    /**
     * Blog articles RSS output.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = {"/blog-articles-rss.do"}, method = {HTTPRequestMethod.GET, HTTPRequestMethod.HEAD})
    public void blogArticlesRSS(final HTTPRequestContext context) {
        final HttpServletResponse response = context.getResponse();
        final RssRenderer renderer = new RssRenderer();

        context.setRenderer(renderer);

        final Channel channel = new Channel();

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            final String blogSubtitle = preference.getString(Option.ID_C_BLOG_SUBTITLE);
            final int outputCnt = preference.getInt(Option.ID_C_FEED_OUTPUT_CNT);

            channel.setTitle(StringEscapeUtils.escapeXml(blogTitle));
            channel.setLastBuildDate(new Date());
            channel.setLink(Latkes.getServePath());
            channel.setAtomLink(Latkes.getServePath() + "/blog-articles-rss.do");
            channel.setGenerator("Solo, ver " + SoloServletListener.VERSION);
            final String localeString = preference.getString(Option.ID_C_LOCALE_STRING);
            final String country = Locales.getCountry(localeString).toLowerCase();
            final String language = Locales.getLanguage(localeString).toLowerCase();

            channel.setLanguage(language + '-' + country);
            channel.setDescription(blogSubtitle);

            final List<Filter> filters = new ArrayList<Filter>();

            filters.add(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true));
            filters.add(new PropertyFilter(Article.ARTICLE_VIEW_PWD, FilterOperator.EQUAL, ""));
            final Query query = new Query().setCurrentPageNum(1).setPageSize(outputCnt).setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters)).addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING).setPageCount(
                    1);

            final JSONObject articleResult = articleRepository.get(query);
            final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);

            final boolean hasMultipleUsers = userQueryService.hasMultipleUsers();
            String authorName = "";

            if (!hasMultipleUsers && 0 != articles.length()) {
                authorName = articleQueryService.getAuthor(articles.getJSONObject(0)).getString(User.USER_NAME);
            }

            final boolean isFullContent = "fullContent".equals(preference.getString(Option.ID_C_FEED_OUTPUT_MODE));

            for (int i = 0; i < articles.length(); i++) {
                Item item = getItem(articles, hasMultipleUsers, authorName, isFullContent, i);
                channel.addItem(item);
            }

            renderer.setContent(channel.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get blog article rss error", e);

            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Item getItem(final JSONArray articles, final boolean hasMultipleUsers, String authorName,
            final boolean isFullContent, int i)
            throws org.json.JSONException, org.b3log.latke.service.ServiceException {
        final JSONObject article = articles.getJSONObject(i);
        final Item ret = new Item();
        final String title = StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_TITLE));
        ret.setTitle(title);
        final String description = isFullContent
                ? StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_CONTENT))
                : StringEscapeUtils.escapeXml(article.optString(Article.ARTICLE_ABSTRACT));
        ret.setDescription(description);
        final Date pubDate = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
        ret.setPubDate(pubDate);
        final String link = Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK);
        ret.setLink(link);
        ret.setGUID(link);
        final String authorEmail = article.getString(Article.ARTICLE_AUTHOR_EMAIL);
        if (hasMultipleUsers) {
            authorName = StringEscapeUtils.escapeXml(articleQueryService.getAuthor(article).getString(User.USER_NAME));
        }
        ret.setAuthor(authorEmail + "(" + authorName + ")");
        final String tagsString = article.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        for (final String tagString : tagStrings) {
            final org.b3log.solo.model.feed.rss.Category catetory = new org.b3log.solo.model.feed.rss.Category();
            ret.addCatetory(catetory);
            final String tag = tagString;
            catetory.setTerm(tag);
        }

        return ret;
    }

    /**
     * Tag articles RSS output.
     *
     * @param context the specified context
     * @throws IOException io exception
     */
    @RequestProcessing(value = {"/tag-articles-rss.do"}, method = {HTTPRequestMethod.GET, HTTPRequestMethod.HEAD})
    public void tagArticlesRSS(final HTTPRequestContext context) throws IOException {
        final HttpServletResponse response = context.getResponse();
        final HttpServletRequest request = context.getRequest();

        final RssRenderer renderer = new RssRenderer();

        context.setRenderer(renderer);

        final String queryString = request.getQueryString();

        if (Strings.isEmptyOrNull(queryString)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String oIdMap = queryString.split("&")[0];
        final String tagId = oIdMap.split("=")[1];

        final Channel channel = new Channel();

        try {
            final JSONObject tag = tagRepository.get(tagId);

            if (null == tag) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String tagTitle = tag.getString(Tag.TAG_TITLE);

            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            final String blogSubtitle = preference.getString(Option.ID_C_BLOG_SUBTITLE) + ", " + tagTitle;
            final int outputCnt = preference.getInt(Option.ID_C_FEED_OUTPUT_CNT);

            channel.setTitle(StringEscapeUtils.escapeXml(blogTitle));
            channel.setLastBuildDate(new Date());
            channel.setLink(Latkes.getServePath());
            channel.setAtomLink(Latkes.getServePath() + "/tag-articles-rss.do");
            channel.setGenerator("Solo, ver " + SoloServletListener.VERSION);
            final String localeString = preference.getString(Option.ID_C_LOCALE_STRING);
            final String country = Locales.getCountry(localeString).toLowerCase();
            final String language = Locales.getLanguage(localeString).toLowerCase();

            channel.setLanguage(language + '-' + country);
            channel.setDescription(blogSubtitle);

            final JSONObject tagArticleResult = tagArticleRepository.getByTagId(tagId, 1, outputCnt);
            final JSONArray tagArticleRelations = tagArticleResult.getJSONArray(Keys.RESULTS);

            if (0 == tagArticleRelations.length()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final List<JSONObject> articles = new ArrayList<JSONObject>();

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation = tagArticleRelations.getJSONObject(i);
                final String articleId = tagArticleRelation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);

                if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED) // Skips the unpublished article
                        && Strings.isEmptyOrNull(article.optString(Article.ARTICLE_VIEW_PWD))) { // Skips article with password
                    articles.add(article);
                }
            }

            final boolean hasMultipleUsers = userQueryService.hasMultipleUsers();
            String authorName = "";

            if (!hasMultipleUsers && !articles.isEmpty()) {
                authorName = articleQueryService.getAuthor(articles.get(0)).getString(User.USER_NAME);
            }

            final boolean isFullContent = "fullContent".equals(preference.getString(Option.ID_C_FEED_OUTPUT_MODE));

            for (int i = 0; i < articles.size(); i++) {
                Item item = getItemForArticles(articles, hasMultipleUsers, authorName, isFullContent, i);
                channel.addItem(item);
            }

            renderer.setContent(channel.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get tag article rss error", e);

            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Item getItemForArticles(final List<JSONObject> articles, final boolean hasMultipleUsers, String authorName,
            final boolean isFullContent, int i)
            throws org.json.JSONException, org.b3log.latke.service.ServiceException {
        final JSONObject article = articles.get(i);
        final Item ret = new Item();
        final String title = StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_TITLE));
        ret.setTitle(title);
        final String description = isFullContent
                ? StringEscapeUtils.escapeXml(article.getString(Article.ARTICLE_CONTENT))
                : StringEscapeUtils.escapeXml(article.optString(Article.ARTICLE_ABSTRACT));
        ret.setDescription(description);
        final Date pubDate = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
        ret.setPubDate(pubDate);
        final String link = Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK);
        ret.setLink(link);
        ret.setGUID(link);
        final String authorEmail = article.getString(Article.ARTICLE_AUTHOR_EMAIL);
        if (hasMultipleUsers) {
            authorName = StringEscapeUtils.escapeXml(articleQueryService.getAuthor(article).getString(User.USER_NAME));
        }
        ret.setAuthor(authorEmail + "(" + authorName + ")");
        final String tagsString = article.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        for (final String tagString : tagStrings) {
            final org.b3log.solo.model.feed.rss.Category catetory = new org.b3log.solo.model.feed.rss.Category();
            ret.addCatetory(catetory);
            catetory.setTerm(tagString);
        }

        return ret;
    }
}
