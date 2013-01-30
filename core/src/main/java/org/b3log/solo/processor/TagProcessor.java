/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.processor.renderer.FrontRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.TagQueryService;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Tags;
import org.b3log.solo.util.Users;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Tag processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.1.0, Aug 30, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class TagProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagProcessor.class.getName());

    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();

    /**
     * Article query service.
     */
    private ArticleQueryService articleQueryService = ArticleQueryService.getInstance();

    /**
     * Tag query service.
     */
    private TagQueryService tagQueryService = TagQueryService.getInstance();

    /**
     * Tag utilities.
     */
    private Tags tagUtils = Tags.getInstance();

    /**
     * Shows articles related with a tag with the specified context.
     * 
     * @param context the specified context
     * @throws IOException io exception 
     */
    @RequestProcessing(value = "/tags/**", method = HTTPRequestMethod.GET)
    public void showTagArticles(final HTTPRequestContext context) throws IOException {
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();

        context.setRenderer(renderer);

        renderer.setTemplateName("tag-articles.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try {
            String requestURI = request.getRequestURI();

            if (!requestURI.endsWith("/")) {
                requestURI += "/";
            }

            String tagTitle = getTagTitle(requestURI);
            final int currentPageNum = getCurrentPageNum(requestURI, tagTitle);

            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            LOGGER.log(Level.FINER, "Tag[title={0}, currentPageNum={1}]", new Object[] {tagTitle, currentPageNum});

            tagTitle = URLDecoder.decode(tagTitle, "UTF-8");
            final JSONObject result = tagQueryService.getTagByTitle(tagTitle);

            if (null == result) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final JSONObject tag = result.getJSONObject(Tag.TAG);
            final String tagId = tag.getString(Keys.OBJECT_ID);

            final JSONObject preference = preferenceQueryService.getPreference();

            Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME),
                dataModel);

            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            request.setAttribute(PageCaches.CACHED_OID, tagId);

            final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

            request.setAttribute(PageCaches.CACHED_TITLE,
                langs.get(PageTypes.TAG_ARTICLES.getLangeLabel()) + "  [" + langs.get("pageNumLabel") + "=" + currentPageNum + ", "
                + langs.get("tagLabel") + "=" + tagTitle + "]");
            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.TAG_ARTICLES.getLangeLabel()));
            request.setAttribute(PageCaches.CACHED_LINK, requestURI);

            final List<JSONObject> articles = articleQueryService.getArticlesByTag(tagId, currentPageNum, pageSize);

            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            final boolean hasMultipleUsers = Users.getInstance().hasMultipleUsers();

            if (hasMultipleUsers) {
                filler.setArticlesExProperties(articles, preference);
            } else {
                // All articles composed by the same author
                final JSONObject author = articleUtils.getAuthor(articles.get(0));

                filler.setArticlesExProperties(articles, author, preference);
            }

            final int tagArticleCount = tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
            final int pageCount = (int) Math.ceil((double) tagArticleCount / (double) pageSize);

            LOGGER.log(Level.FINEST, "Paginate tag-articles[currentPageNum={0}, pageSize={1}, pageCount={2}, windowSize={3}]",
                new Object[] {currentPageNum, pageSize, pageCount, windowSize});
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

            LOGGER.log(Level.FINEST, "tag-articles[pageNums={0}]", pageNums);
            
            Collections.sort(articles, Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
         
            fillPagination(dataModel, pageCount, currentPageNum, articles, pageNums);
            dataModel.put(Common.PATH, "/tags/" + URLEncoder.encode(tagTitle, "UTF-8"));
            dataModel.put(Keys.OBJECT_ID, tagId);
            dataModel.put(Tag.TAG, tag);

            dataModel.put(Keys.PAGE_TYPE, PageTypes.TAG_ARTICLES);
            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Fills pagination.
     * 
     * @param dataModel the specified data model
     * @param pageCount the specified page count
     * @param currentPageNum the specified current page number
     * @param articles the specified articles
     * @param pageNums the specified page numbers
     */
    private void fillPagination(final Map<String, Object> dataModel,
        final int pageCount, final int currentPageNum,
        final List<JSONObject> articles,
        final List<Integer> pageNums) {
        final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);

        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
        }
        dataModel.put(Article.ARTICLES, articles);
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
        dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
    }

    /**
     * Shows tags with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = { "/tags.html"}, method = HTTPRequestMethod.GET)
    public void showTags(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();

        context.setRenderer(renderer);

        renderer.setTemplateName("tags.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME),
                dataModel);

            request.setAttribute(PageCaches.CACHED_OID, "No id");
            final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

            request.setAttribute(PageCaches.CACHED_TITLE, langs.get(PageTypes.TAGS.getLangeLabel()));
            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.TAGS.getLangeLabel()));
            request.setAttribute(PageCaches.CACHED_LINK, "/tags.html");

            final List<JSONObject> tags = tagQueryService.getTags();

            tagUtils.removeForUnpublishedArticles(tags);
            Collections.sort(tags, Comparators.TAG_REF_CNT_COMPARATOR);

            dataModel.put(Tag.TAGS, tags);

            dataModel.put(Keys.PAGE_TYPE, PageTypes.TAGS);
            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Gets the request page number from the specified request URI and tag title.
     * 
     * @param requestURI the specified request URI
     * @param tagTitle the specified tag title
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI, final String tagTitle) {
        if (Strings.isEmptyOrNull(tagTitle)) {
            return -1;
        }
        
        final String pageNumString = requestURI.substring((Latkes.getContextPath() + "/tags/" + tagTitle + "/").length());

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Gets tag title from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return tag title
     */
    private static String getTagTitle(final String requestURI) {
        final String path = requestURI.substring((Latkes.getContextPath() + "/tags/").length());

        if (path.contains("/")) {
            return path.substring(0, path.indexOf("/"));
        } else {
            return path.substring(0);
        }
    }
}
