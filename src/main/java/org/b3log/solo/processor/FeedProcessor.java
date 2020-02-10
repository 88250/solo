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

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AtomRenderer;
import org.b3log.latke.http.renderer.RssRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.solo.Server;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.atom.Category;
import org.b3log.solo.model.atom.Entry;
import org.b3log.solo.model.atom.Feed;
import org.b3log.solo.model.rss.Channel;
import org.b3log.solo.model.rss.Item;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Markdowns;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Feed (Atom/RSS) processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/nanolikeyou">nanolikeyou</a>
 * @version 3.0.0.0, Feb 9, 2020
 * @since 0.3.1
 */
@Singleton
public class FeedProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(FeedProcessor.class);

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
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Blog articles Atom output.
     *
     * @param context the specified context
     */
    public void blogArticlesAtom(final RequestContext context) {
        final AtomRenderer renderer = new AtomRenderer();
        context.setRenderer(renderer);

        final Feed feed = new Feed();
        try {
            final JSONObject preference = optionQueryService.getPreference();
            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            final String blogSubtitle = preference.getString(Option.ID_C_BLOG_SUBTITLE);
            final int outputCnt = preference.getInt(Option.ID_C_FEED_OUTPUT_CNT);
            feed.setTitle(blogTitle);
            feed.setSubtitle(blogSubtitle);
            feed.setUpdated(new Date());
            feed.setAuthor(blogTitle);
            feed.setLink(Latkes.getServePath() + "/atom.xml");
            feed.setId(Latkes.getServePath() + "/");

            final List<Filter> filters = new ArrayList<>();
            filters.add(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED));
            filters.add(new PropertyFilter(Article.ARTICLE_VIEW_PWD, FilterOperator.EQUAL, ""));
            final Query query = new Query().setPage(1, outputCnt).
                    setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters)).
                    addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).setPageCount(1);
            final JSONObject articleResult = articleRepository.get(query);
            final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);
            final boolean isFullContent = "fullContent".equals(preference.getString(Option.ID_C_FEED_OUTPUT_MODE));
            for (int i = 0; i < articles.length(); i++) {
                final Entry entry = getEntry(articles, isFullContent, i);
                feed.addEntry(entry);
            }

            renderer.setContent(feed.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get blog article feed error", e);

            context.sendError(500);
        }
    }

    private Entry getEntry(final JSONArray articles, final boolean isFullContent, int i)
            throws JSONException, ServiceException {
        final JSONObject article = articles.getJSONObject(i);
        final Entry ret = new Entry();
        final String title = article.getString(Article.ARTICLE_TITLE);
        ret.setTitle(title);
        final String summary = isFullContent ? article.getString(Article.ARTICLE_CONTENT)
                : article.optString(Article.ARTICLE_ABSTRACT);
        ret.setSummary(summary);
        final long updated = article.getLong(Article.ARTICLE_UPDATED);
        ret.setUpdated(new Date(updated));
        final String link = Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK);
        ret.setLink(link);
        ret.setId(link);
        final String authorName = articleQueryService.getAuthor(article).getString(User.USER_NAME);
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
     * @throws Exception exception
     */
    public void blogArticlesRSS(final RequestContext context) {
        final RssRenderer renderer = new RssRenderer();
        context.setRenderer(renderer);

        final Channel channel = new Channel();

        try {
            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                context.sendError(404);

                return;
            }

            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            final String blogSubtitle = preference.getString(Option.ID_C_BLOG_SUBTITLE);
            final int outputCnt = preference.getInt(Option.ID_C_FEED_OUTPUT_CNT);

            channel.setTitle(blogTitle);
            channel.setLastBuildDate(new Date());
            channel.setLink(Latkes.getServePath());
            channel.setAtomLink(Latkes.getServePath() + "/rss.xml");
            channel.setGenerator("Solo, v" + Server.VERSION + ", https://solo.b3log.org");
            final String localeString = preference.getString(Option.ID_C_LOCALE_STRING);
            final String country = Locales.getCountry(localeString).toLowerCase();
            final String language = Locales.getLanguage(localeString).toLowerCase();
            channel.setLanguage(language + '-' + country);
            channel.setDescription(blogSubtitle);

            final List<Filter> filters = new ArrayList<>();
            filters.add(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED));
            filters.add(new PropertyFilter(Article.ARTICLE_VIEW_PWD, FilterOperator.EQUAL, ""));
            final Query query = new Query().setPageCount(1).setPage(1, outputCnt).
                    setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters)).
                    addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING);
            final JSONObject articleResult = articleRepository.get(query);
            final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);
            final boolean isFullContent = "fullContent".equals(preference.getString(Option.ID_C_FEED_OUTPUT_MODE));
            for (int i = 0; i < articles.length(); i++) {
                final Item item = getItem(articles, isFullContent, i);
                channel.addItem(item);
            }

            renderer.setContent(channel.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get blog article rss error", e);

            context.sendError(500);
        }
    }

    private Item getItem(final JSONArray articles, final boolean isFullContent, int i) throws JSONException, ServiceException {
        final JSONObject article = articles.getJSONObject(i);
        final Item ret = new Item();
        String title = article.getString(Article.ARTICLE_TITLE);
        title = EmojiParser.parseToAliases(title);
        ret.setTitle(title);
        String description = isFullContent
                ? article.getString(Article.ARTICLE_CONTENT)
                : article.optString(Article.ARTICLE_ABSTRACT);
        description = EmojiParser.parseToAliases(description);
        description = Markdowns.toHTML(description);
        ret.setDescription(description);
        final long pubDate = article.getLong(Article.ARTICLE_UPDATED);
        ret.setPubDate(new Date(pubDate));
        final String link = Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK);
        ret.setLink(link);
        ret.setGUID(link);
        final String authorName = articleQueryService.getAuthor(article).getString(User.USER_NAME);
        ret.setAuthor(authorName);
        final String tagsString = article.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        for (final String tagString : tagStrings) {
            final org.b3log.solo.model.rss.Category catetory = new org.b3log.solo.model.rss.Category();
            ret.addCatetory(catetory);
            catetory.setTerm(tagString);
        }

        return ret;
    }
}
