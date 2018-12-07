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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Crypts;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
import org.b3log.solo.model.Category;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.StringReader;

/**
 * {@link CategoryConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 15, 2018
 * @since 2.1.0
 */
@Test(suiteName = "processor")
public class CategoryConsoleTestCase extends AbstractTestCase {

    /**
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        super.init();
    }

    /**
     * addCategory.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void addCategory() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/category/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Category.CATEGORY_T_TAGS, "Solo");
        requestJSON.put(Category.CATEGORY_TITLE, "分类1");
        requestJSON.put(Category.CATEGORY_URI, "cate1");

        final BufferedReader reader = new BufferedReader(new StringReader(requestJSON.toString()));
        request.setReader(reader);

        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * updateCategory.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addCategory")
    public void updateCategory() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/category/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Category.CATEGORY_T_TAGS, "Solo");
        JSONObject category = getCategoryQueryService().getByTitle("分类1");
        requestJSON.put(Keys.OBJECT_ID, category.optString(Keys.OBJECT_ID));
        requestJSON.put(Category.CATEGORY_TITLE, "新的分类1");
        final BufferedReader reader = new BufferedReader(new StringReader(requestJSON.toString()));
        request.setReader(reader);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));

        category = getCategoryQueryService().getByTitle("分类1");
        Assert.assertNull(category);

        category = getCategoryQueryService().getByTitle("新的分类1");
        Assert.assertNotNull(category);
        Assert.assertEquals(category.optInt(Category.CATEGORY_TAG_CNT), 1); // https://github.com/b3log/solo/issues/12274
    }
}
