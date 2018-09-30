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
package org.b3log.solo.repository;

import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Category;
import org.b3log.solo.repository.CategoryRepository;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CategoryRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 12, 2017
 * @since 2.0.0
 */
@Test(suiteName = "repository")
public final class CategoryRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Tests.
     *
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final CategoryRepository categoryRepository = getCategoryRepository();

        final int category1Order = 1, category2Order = 2, category3Order = 3;

        JSONObject category1 = new JSONObject();
        category1.put(Category.CATEGORY_TITLE, "category title");
        category1.put(Category.CATEGORY_DESCRIPTION, "cateogry description");
        category1.put(Category.CATEGORY_URI, "category uri");
        category1.put(Category.CATEGORY_ORDER, category1Order);
        category1.put(Category.CATEGORY_TAG_CNT, 0);

        Transaction transaction = categoryRepository.beginTransaction();
        categoryRepository.add(category1);
        transaction.commit();

        Assert.assertNull(categoryRepository.getByTitle("title"));
        Assert.assertNotNull(categoryRepository.getByTitle("category title"));

        Assert.assertNull(categoryRepository.getByOrder(0));
        Assert.assertNotNull(categoryRepository.getByOrder(category1Order));

        final JSONObject category2 = new JSONObject();
        category2.put(Category.CATEGORY_TITLE, "category title");
        category2.put(Category.CATEGORY_DESCRIPTION, "cateogry description");
        category2.put(Category.CATEGORY_URI, "category uri");
        category2.put(Category.CATEGORY_ORDER, category2Order);
        category2.put(Category.CATEGORY_TAG_CNT, 0);

        transaction = categoryRepository.beginTransaction();
        final String category2Id = categoryRepository.add(category2);
        transaction.commit();

        Assert.assertEquals(categoryRepository.getMaxOrder(), category2Order);

        JSONObject category3 = new JSONObject();
        category3.put(Category.CATEGORY_TITLE, "category title");
        category3.put(Category.CATEGORY_DESCRIPTION, "cateogry description");
        category3.put(Category.CATEGORY_URI, "category uri");
        category3.put(Category.CATEGORY_ORDER, category3Order);
        category3.put(Category.CATEGORY_TAG_CNT, 0);

        transaction = categoryRepository.beginTransaction();
        categoryRepository.add(category3);
        transaction.commit();

        final int total = 3;
        Assert.assertEquals(categoryRepository.count(), total);

        category1 = categoryRepository.getUpper(category2Id);
        Assert.assertNotNull(category1);
        Assert.assertEquals(category1.getInt(Category.CATEGORY_ORDER), category1Order);

        category3 = categoryRepository.getUnder(category2Id);
        Assert.assertNotNull(category3);
        Assert.assertEquals(category3.getInt(Category.CATEGORY_ORDER), category3Order);
    }
}
