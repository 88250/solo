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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.URLs;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Skins;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Category processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 2.0.0
 */
@Singleton
public class CategoryProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CategoryProcessor.class);

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Category query service.
     */
    @Inject
    private CategoryQueryService categoryQueryService;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Gets category articles paged with the specified context.
     *
     * @param context the specified context
     */
    public void getCategoryArticlesByPage(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();

        final String categoryURI = context.pathVar("categoryURI");
        final int currentPageNum = Paginator.getPage(context);

        Stopwatchs.start("Get Category-Articles Paged [categoryURI=" + categoryURI + ", pageNum=" + currentPageNum + ']');
        try {
            final JSONObject category = categoryQueryService.getByURI(categoryURI);
            if (null == category) {
                context.sendError(404);

                return;
            }

            jsonObject.put(Keys.STATUS_CODE, true);
            final String categoryId = category.optString(Keys.OBJECT_ID);
            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            final JSONObject articlesResult = articleQueryService.getCategoryArticles(categoryId, currentPageNum, pageSize);
            final List<JSONObject> articles = (List<JSONObject>) articlesResult.opt(Keys.RESULTS);
            dataModelService.setArticlesExProperties(context, articles, preference);
            final int pageCount = articlesResult.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject result = new JSONObject();
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            result.put(Pagination.PAGINATION, pagination);
            result.put(Article.ARTICLES, articles);
            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, false);
            LOGGER.log(Level.ERROR, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Shows articles related with a category with the specified context.
     *
     * @param context the specified context
     */
    public void showCategoryArticles(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "category-articles.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final Response response = context.getResponse();

        try {
            String categoryURI = context.pathVar("categoryURI");
            categoryURI = URLs.encode(categoryURI);
            final int currentPageNum = Paginator.getPage(context);
            LOGGER.log(Level.DEBUG, "Category [URI={}, currentPageNum={}]", categoryURI, currentPageNum);
            final JSONObject category = categoryQueryService.getByURI(categoryURI);
            if (null == category) {
                context.sendError(404);

                return;
            }

            dataModel.put(Category.CATEGORY, category);

            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            final String categoryId = category.optString(Keys.OBJECT_ID);

            final JSONObject result = articleQueryService.getCategoryArticles(categoryId, currentPageNum, pageSize);
            final List<JSONObject> articles = (List<JSONObject>) result.opt(Keys.RESULTS);

            final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            if (0 == pageCount) {
                context.sendError(404);

                return;
            }

            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) context.attr(Keys.TEMAPLTE_DIR_NAME), dataModel);
            dataModelService.setArticlesExProperties(context, articles, preference);

            final List<Integer> pageNums = (List) result.optJSONObject(Pagination.PAGINATION).opt(Pagination.PAGINATION_PAGE_NUMS);
            fillPagination(dataModel, pageCount, currentPageNum, articles, pageNums);
            dataModel.put(Common.PATH, "/category/" + URLs.encode(categoryURI));

            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);

            statisticMgmtService.incBlogViewCount(context, response);
        } catch (final ServiceException | JSONException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            context.sendError(404);
        }
    }

    /**
     * Fills pagination.
     *
     * @param dataModel      the specified data model
     * @param pageCount      the specified page count
     * @param currentPageNum the specified current page number
     * @param articles       the specified articles
     * @param pageNums       the specified page numbers
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
}
