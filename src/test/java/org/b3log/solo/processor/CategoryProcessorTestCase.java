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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CategoryProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 11, 2018
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class CategoryProcessorTestCase extends AbstractTestCase {

    /**
     * showCategoryArticles.
     */
    public void showCategoryArticles() {
        MockRequest request = mockRequest();
        request.setRequestURI("/console/category/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Category.CATEGORY_T_TAGS, "Solo");
        requestJSON.put(Category.CATEGORY_TITLE, "分类1");
        requestJSON.put(Category.CATEGORY_URI, "cate1");

        request.setJSON(requestJSON);

        mockAdminLogin(request);

        MockResponse response = mockResponse();
        mockDispatcher(request, response);

        request = mockRequest();
        request.setRequestURI("/category/cate1");
        request.setMethod("GET");
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>分类1 - Solo 的个人博客</title>"));
    }
}
