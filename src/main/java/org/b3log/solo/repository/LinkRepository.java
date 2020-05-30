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

import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.Link;
import org.json.JSONObject;

/**
 * Link repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Jan 15, 2019
 * @since 0.3.1
 */
@Repository
public class LinkRepository extends AbstractRepository {

    /**
     * Public constructor.
     */
    public LinkRepository() {
        super(Link.LINK);
    }

    /**
     * Gets a link by the specified address.
     *
     * @param address the specified address
     * @return link, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByAddress(final String address) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Link.LINK_ADDRESS, FilterOperator.EQUAL, address)).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets the maximum order.
     *
     * @return order number, returns {@code -1} if not found
     * @throws RepositoryException repository exception
     */
    public int getMaxOrder() throws RepositoryException {
        final Query query = new Query().addSort(Link.LINK_ORDER, SortDirection.DESCENDING);
        final JSONObject result = getFirst(query);
        if (null == result) {
            return -1;
        }
        return result.optInt(Link.LINK_ORDER);
    }

    /**
     * Gets the upper link of the link specified by the given id.
     *
     * @param id the given id
     * @return upper link, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getUpper(final String id) throws RepositoryException {
        final JSONObject link = get(id);
        if (null == link) {
            return null;
        }

        final Query query = new Query().setFilter(new PropertyFilter(Link.LINK_ORDER, FilterOperator.LESS_THAN, link.optInt(Link.LINK_ORDER))).
                addSort(Link.LINK_ORDER, SortDirection.DESCENDING).setPage(1, 1);
        return getFirst(query);
    }

    /**
     * Gets the under link of the link specified by the given id.
     *
     * @param id the given id
     * @return under link, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getUnder(final String id) throws RepositoryException {
        final JSONObject link = get(id);
        if (null == link) {
            return null;
        }

        final Query query = new Query().setFilter(new PropertyFilter(Link.LINK_ORDER, FilterOperator.GREATER_THAN, link.optInt(Link.LINK_ORDER))).
                addSort(Link.LINK_ORDER, SortDirection.ASCENDING).setPage(1, 1);
        return getFirst(query);
    }

    /**
     * Gets a link by the specified order.
     *
     * @param order the specified order
     * @return link, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByOrder(final int order) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Link.LINK_ORDER, FilterOperator.EQUAL, order));
        return getFirst(query);
    }
}
