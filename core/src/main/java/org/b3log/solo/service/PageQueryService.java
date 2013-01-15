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
package org.b3log.solo.service;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Paginator;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Page query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2011
 * @since 0.4.0
 */
public final class PageQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageQueryService.class.getName());

    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();

    /**
     * Gets a page by the specified page id.
     *
     * @param pageId the specified page id
     * @return for example,
     * <pre>
     * {
     *     "page": {
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageContent": ""
     *         "pageOrder": int,
     *         "pagePermalink": "",
     *         "pageCommentCount": int,
     *         "pageCommentable": boolean,
     *         "pageType": "",
     *         "pageOpenTarget": ""
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getPage(final String pageId) throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject page = pageRepository.get(pageId);

            if (null == page) {
                return null;
            }

            ret.put(Page.PAGE, page);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets pages by the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10
     * }, see {@link Pagination} for more details
     * </pre>
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "pages": [{
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageCommentCount": int,
     *         "pageOrder": int,
     *         "pagePermalink": "",
     *         "pageCommentable": boolean,
     *         "pageType": "",
     *         "pageOpenTarget": ""
     *      }, ....]
     * }
     * </pre>
     * @throws ServiceException service exception
     * @see Pagination
     */
    public JSONObject getPages(final JSONObject requestJSONObject) throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final int currentPageNum = requestJSONObject.getInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(Pagination.PAGINATION_WINDOW_SIZE);

            final Query query = new Query().setCurrentPageNum(currentPageNum).setPageSize(pageSize).addSort(Page.PAGE_ORDER, SortDirection.ASCENDING).setPageCount(
                1);
            final JSONObject result = pageRepository.get(query);
            final int pageCount = result.getJSONObject(Pagination.PAGINATION).getInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject pagination = new JSONObject();
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final JSONArray pages = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < pages.length(); i++) { // remove unused properties
                final JSONObject page = pages.getJSONObject(i);

                page.remove(Page.PAGE_CONTENT);
            }

            ret.put(Pagination.PAGINATION, pagination);
            ret.put(Page.PAGES, pages);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets pages failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link PageQueryService} singleton.
     *
     * @return the singleton
     */
    public static PageQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private PageQueryService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 27, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PageQueryService SINGLETON = new PageQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
