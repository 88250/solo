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
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CategoryMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 12, 2017
 * @since 2.0.0
 */
@Test(suiteName = "service")
public class CategoryMgmtServiceTestCase extends AbstractTestCase {

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
     * Remove a category.
     *
     * @throws Exception exception
     */
    public void removeCategory() throws Exception {
        final CategoryMgmtService categoryMgmtService = getCategoryMgmtService();

        final JSONObject category = new JSONObject();
        category.put(Category.CATEGORY_TITLE, "category2 title");
        category.put(Category.CATEGORY_URI, "category2 uri");
        category.put(Category.CATEGORY_DESCRIPTION, "category2 description");

        final String categoryId = categoryMgmtService.addCategory(category);
        Assert.assertNotNull(categoryId);

        final CategoryQueryService categoryQueryService = getCategoryQueryService();
        JSONObject result = categoryQueryService.getCategory(categoryId);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(Category.CATEGORY_TITLE), "category2 title");

        categoryMgmtService.removeCategory(categoryId);

        result = categoryQueryService.getCategory(categoryId);
        Assert.assertNull(result);
    }

    /**
     * Update a category.
     *
     * @throws Exception exception
     */
    public void updateCategory() throws Exception {
        final CategoryMgmtService categoryMgmtService = getCategoryMgmtService();

        final JSONObject category = new JSONObject();
        category.put(Category.CATEGORY_TITLE, "category3 title");
        category.put(Category.CATEGORY_URI, "category3 uri");
        category.put(Category.CATEGORY_DESCRIPTION, "category3 description");

        final String categoryId = categoryMgmtService.addCategory(category);
        Assert.assertNotNull(categoryId);

        final CategoryQueryService categoryQueryService = getCategoryQueryService();
        JSONObject result = categoryQueryService.getCategory(categoryId);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(Category.CATEGORY_TITLE), "category3 title");

        category.put(Category.CATEGORY_TITLE, "updated category3 title");
        categoryMgmtService.updateCategory(categoryId, category);

        result = categoryQueryService.getCategory(categoryId);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(Category.CATEGORY_TITLE), "updated category3 title");
    }

    /**
     * Change Order.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addCategory")
    public void changeOrder() throws Exception {
        final CategoryMgmtService categoryMgmtService = getCategoryMgmtService();

        final JSONObject category = new JSONObject();
        category.put(Category.CATEGORY_TITLE, "category4 title");
        category.put(Category.CATEGORY_URI, "category4 uri");
        category.put(Category.CATEGORY_DESCRIPTION, "category4 description");

        final String categoryId = categoryMgmtService.addCategory(category);
        Assert.assertNotNull(categoryId);

        final int oldOrder = category.getInt(Category.CATEGORY_ORDER);
        categoryMgmtService.changeOrder(categoryId, "up");

        final JSONObject result = getCategoryQueryService().getCategory(categoryId);
        Assert.assertNotNull(result);
        Assert.assertTrue(oldOrder > result.getInt(Category.CATEGORY_ORDER));
    }
}
