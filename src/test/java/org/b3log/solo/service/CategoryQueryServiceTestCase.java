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
package org.b3log.solo.service;

import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Category;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

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
     * Add a category.
     *
     * @throws Exception exception
     */
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

        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        final JSONObject result = categoryQueryService.getCategoris(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(((List<JSONObject>) result.opt(Category.CATEGORIES)).size(), 1);
    }
}
