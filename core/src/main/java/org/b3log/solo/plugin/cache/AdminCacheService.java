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
package org.b3log.solo.plugin.cache;


import org.b3log.solo.model.Common;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.Pagination;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.Users;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.model.Preference;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Requests;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.QueryResults;
import org.json.JSONObject;


/**
 * Admin cache service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, May 16, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class AdminCacheService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdminCacheService.class.getName());

    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Preference management service.
     */
    private PreferenceMgmtService preferenceMgmtService = PreferenceMgmtService.getInstance();

    /**
     * Gets page cache status with the specified http servlet request and http
     * servlet response.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "pageCacheEnabled": boolean,
     *     "pageCachedCnt": int
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception 
     */
    @RequestProcessing(value = "/console/plugins/admin-cache/status/", method = HTTPRequestMethod.GET)
    public void getPageCache(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
        throws Exception {
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        renderer.setJSONObject(ret);

        LOGGER.log(Level.INFO, "Cache status[cachedBytes={0}, cachedCount={1}]",
            new Object[] {PageCaches.getCache().getCachedBytes(), PageCaches.getCache().getCachedCount()});

        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            final boolean pageCacheEnabled = preference.getBoolean(Preference.PAGE_CACHE_ENABLED);

            ret.put(Preference.PAGE_CACHE_ENABLED, pageCacheEnabled);

            ret.put(Common.PAGE_CACHED_CNT, PageCaches.getKeys().size());

            ret.put(Keys.STATUS_CODE, true);

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, "Admin Cache plugin exception: " + e.getMessage());
        }
    }

    /**
     * Gets page cache list by the specified request json object.
     * 
     * <p>
     * The request URI contains the pagination arguments. For example, the 
     * request URI is /console/admin-cache/pages/1/10/20, means the 
     * current page is 1, the page size is 10, and the window size is 20.
     * </p>
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "pages": [{
     *         "link": "",
     *         "cachedType": "",
     *         "cachedTitle": "",
     *      }, ....]
     * }
     * </pre>
     * </p>
     * 
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/console/plugins/admin-cache/pages/*/*/*"/* Requests.PAGINATION_PATH_PATTERN */,
        method = HTTPRequestMethod.GET)
    public void getPages(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
        throws Exception {
        if (!userUtils.isLoggedIn(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        renderer.setJSONObject(ret);

        try {
            final String requestURI = request.getRequestURI();
            final String path = requestURI.substring("/console/plugins/admin-cache/pages/".length());

            final JSONObject requestJSONObject = Requests.buildPaginationRequest(path);

            final int currentPageNum = requestJSONObject.getInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(Pagination.PAGINATION_WINDOW_SIZE);

            List<String> keys = new ArrayList<String>(PageCaches.getKeys());
            // Paginates
            final int pageCount = (int) Math.ceil((double) keys.size() / (double) pageSize);
            final JSONObject pagination = new JSONObject();

            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final int start = pageSize * (currentPageNum - 1);
            int end = start + pageSize;

            end = end > keys.size() ? keys.size() : end;

            keys = keys.subList(start, end);

            // Retrives cached pages
            final List<JSONObject> pages = new ArrayList<JSONObject>();

            for (final String key : keys) {
                LOGGER.log(Level.FINER, "Cached page[key={0}]", key);

                JSONObject cachedPage = PageCaches.get(key);

                if (null != cachedPage) {
                    // Do a copy for properties removing and retrieving
                    cachedPage = new JSONObject(cachedPage, JSONObject.getNames(cachedPage));
                    cachedPage.remove(PageCaches.CACHED_CONTENT);
                    pages.add(cachedPage);
                }
            }

            ret.put(Page.PAGES, pages);

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, "Admin Cache plugin exception: " + e.getMessage());
        }
    }

    /**
     * Sets page cache states.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/console/plugins/admin-cache/enable/*", method = HTTPRequestMethod.PUT)
    public void setPageCache(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
        throws Exception {
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        renderer.setJSONObject(ret);

        try {
            final String path = request.getRequestURI().substring(
                (Latkes.getContextPath() + "/console/plugins/admin-cache/enable/").length());

            final boolean pageCacheEnabled = "true".equals(path) ? true : false;

            final JSONObject preference = preferenceQueryService.getPreference();

            preference.put(Preference.PAGE_CACHE_ENABLED, pageCacheEnabled);

            preferenceMgmtService.updatePreference(preference);

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sets page cache error: {0}", e.getMessage());

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, "Admin Cache plugin exception: " + e.getMessage());
        }
    }
}
