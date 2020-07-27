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
 * @version 1.1.0.1, Feb 27, 2020
 * @since 2.1.0
 */
@Test(suiteName = "processor")
public class CategoryConsoleTestCase extends AbstractTestCase {

    /**
     * addCategory.
     */
    public void addCategory() {
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        requestJSON.put(Category.CATEGORY_URI, "new-cate-1");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));

        category = getCategoryQueryService().getByTitle("分类1");
        Assert.assertNull(category);

        category = getCategoryQueryService().getByTitle("新的分类1");
        Assert.assertNotNull(category);
        Assert.assertEquals(category.optInt(Category.CATEGORY_TAG_CNT), 1); // https://github.com/b3log/solo/issues/12274
    }

    /**
     * getCategories.
     */
    @Test(dependsOnMethods = "updateCategory")
    public void getCategories() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/categories/1/10/20");
        request.setMethod("GET");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }
}
