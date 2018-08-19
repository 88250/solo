/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.util;

import org.b3log.latke.Keys;
import org.b3log.latke.model.Pagination;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Query result utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 25, 2011
 * @since 0.3.5
 */
public final class QueryResults {

    /**
     * Constructs a default query result.
     *
     * @return a default query result,
     * <pre>
     * {
     *     "sc": false
     * }
     * </pre>
     */
    public static JSONObject defaultResult() {
        return new JSONObject().put(Keys.STATUS_CODE, false);
    }

    /**
     * Constructs a default query results.
     *
     * @return a default query results,
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 0
     *     },
     *     "rslts": []
     * }
     * </pre>
     */
    public static JSONObject defaultResults() {
        final JSONObject ret = new JSONObject();
        final JSONObject pagination = new JSONObject();

        ret.put(Pagination.PAGINATION, pagination);
        pagination.put(Pagination.PAGINATION_PAGE_COUNT, 0);

        final JSONArray results = new JSONArray();

        ret.put(Keys.RESULTS, results);

        return ret;
    }

    /**
     * Private constructor.
     */
    private QueryResults() {
    }
}
