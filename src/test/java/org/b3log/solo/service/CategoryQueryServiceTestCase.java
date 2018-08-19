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
package org.b3log.solo.service;

import org.b3log.latke.model.User;
import org.b3log.latke.util.Requests;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Category;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CategoryQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 12, 2017
 * @since 2.0.0
 */
@Test(suiteName = "service")
public class CategoryQueryServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Add a category.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void addCategory() throws Exception {
        final CategoryMgmtService categoryMgmtService = getCategoryMgmtService();

        final JSONObject category = new JSONObject();
        category.put(Category.CATEGORY_TITLE, "category1 title");
        category.put(Category.CATEGORY_URI, "category1 uri");
        category.put(Category.CATEGORY_DESCRIPTION, "category1 description");

        final String categoryId = categoryMgmtService.addCategory(category);
        Assert.assertNotNull(categoryId);
    }

    /**
     * Get categories.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addCategory")
    public void getCategories() throws Exception {
        final CategoryQueryService categoryQueryService = getCategoryQueryService();

        final JSONObject paginationRequest = Requests.buildPaginationRequest("1/10/20");
        final JSONObject result = categoryQueryService.getCategoris(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Category.CATEGORIES).length(), 1);
    }
}
