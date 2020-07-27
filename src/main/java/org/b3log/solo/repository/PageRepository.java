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
package org.b3log.solo.repository;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.cache.PageCache;
import org.b3log.solo.model.Page;
import org.json.JSONObject;

import java.util.List;

/**
 * Page repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.9, Jun 6, 2019
 * @since 0.3.1
 */
@Repository
public class PageRepository extends AbstractRepository {

    /**
     * Page cache.
     */
    @Inject
    private PageCache pageCache;

    /**
     * Public constructor.
     */
    public PageRepository() {
        super(Page.PAGE);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        super.remove(id);

        pageCache.removePage(id);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = pageCache.getPage(id);
        if (null != ret) {
            return ret;
        }

        ret = super.get(id);
        if (null == ret) {
            return null;
        }

        pageCache.putPage(ret);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject page, final String... propertyNames) throws RepositoryException {
        super.update(id, page, propertyNames);

        page.put(Keys.OBJECT_ID, id);
        pageCache.putPage(page);
    }

    /**
     * Gets a page by the specified permalink.
     *
     * @param permalink the specified permalink
     * @return page, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByPermalink(final String permalink) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Page.PAGE_PERMALINK, FilterOperator.EQUAL, permalink)).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets the maximum order.
     *
     * @return order number, returns {@code -1} if not found
     * @throws RepositoryException repository exception
     */
    public int getMaxOrder() throws RepositoryException {
        final Query query = new Query().addSort(Page.PAGE_ORDER, SortDirection.DESCENDING).setPageCount(1);
        final JSONObject result = getFirst(query);
        if (null == result) {
            return -1;
        }
        return result.optInt(Page.PAGE_ORDER);
    }

    /**
     * Gets the upper page of the page specified by the given id.
     *
     * @param id the given id
     * @return upper page, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getUpper(final String id) throws RepositoryException {
        final JSONObject page = get(id);
        if (null == page) {
            return null;
        }

        final Query query = new Query().setFilter(new PropertyFilter(Page.PAGE_ORDER, FilterOperator.LESS_THAN, page.optInt(Page.PAGE_ORDER))).
                addSort(Page.PAGE_ORDER, SortDirection.DESCENDING).setPage(1, 1).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets the under page of the page specified by the given id.
     *
     * @param id the given id
     * @return under page, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getUnder(final String id) throws RepositoryException {
        final JSONObject page = get(id);
        if (null == page) {
            return null;
        }

        final Query query = new Query().setFilter(new PropertyFilter(Page.PAGE_ORDER, FilterOperator.GREATER_THAN, page.optInt(Page.PAGE_ORDER))).
                addSort(Page.PAGE_ORDER, SortDirection.ASCENDING).setPage(1, 1).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets a page by the specified order.
     *
     * @param order the specified order
     * @return page, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByOrder(final int order) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Page.PAGE_ORDER, FilterOperator.EQUAL, order)).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets pages.
     *
     * @return a list of pages, returns an empty list if  not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getPages() throws RepositoryException {
        final Query query = new Query().addSort(Page.PAGE_ORDER, SortDirection.ASCENDING).setPageCount(1);
        return getList(query);
    }
}
