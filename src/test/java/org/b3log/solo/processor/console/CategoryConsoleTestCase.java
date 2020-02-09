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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Common;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CategoryConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Dec 10, 2018
 * @since 2.1.0
 */
@Test(suiteName = "processor")
public class CategoryConsoleTestCase extends AbstractTestCase {

    /**
     * addCategory.
     *
     * @throws Exception exception
     */
    public void addCategory() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/category/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Category.CATEGORY_T_TAGS, "Solo");
        requestJSON.put(Category.CATEGORY_TITLE, "分类1");
        requestJSON.put(Category.CATEGORY_URI, "cate1");
        request.setJSON(requestJSON);

        mockAdminLogin(request);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getCategory.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addCategory")
    public void getCategory() throws Exception {
        final JSONObject category = getCategoryQueryService().getByTitle("分类1");

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/category/" + category.optString(Keys.OBJECT_ID));
        request.setMethod("GET");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * updateCategory.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addCategory")
    public void updateCategory() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/category/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Category.CATEGORY_T_TAGS, "Solo");
        JSONObject category = getCategoryQueryService().getByTitle("分类1");
        requestJSON.put(Keys.OBJECT_ID, category.optString(Keys.OBJECT_ID));
        requestJSON.put(Category.CATEGORY_TITLE, "新的分类1");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));

        category = getCategoryQueryService().getByTitle("分类1");
        Assert.assertNull(category);

        category = getCategoryQueryService().getByTitle("新的分类1");
        Assert.assertNotNull(category);
        Assert.assertEquals(category.optInt(Category.CATEGORY_TAG_CNT), 1); // https://github.com/b3log/solo/issues/12274
    }

    /**
     * getCategories.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "updateCategory")
    public void getCategories() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/categories/1/10/20");
        request.setMethod("GET");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * changeOrder.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getCategories")
    public void changeOrder() throws Exception {
        final JSONObject category = getCategoryQueryService().getByTitle("新的分类1");

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/category/order/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Keys.OBJECT_ID, category.optString(Keys.OBJECT_ID));
        requestJSON.put(Common.DIRECTION, "up");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * removeCategory.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "changeOrder")
    public void removeCategory() throws Exception {
        final JSONObject category = getCategoryQueryService().getByTitle("新的分类1");

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/category/" + category.optString(Keys.OBJECT_ID));
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
