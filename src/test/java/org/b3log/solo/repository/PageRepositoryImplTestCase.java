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

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Page;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link PageRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Apr 19, 2019
 */
@Test(suiteName = "repository")
public class PageRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Adds successfully.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final PageRepository pageRepository = getPageRepository();

        Assert.assertEquals(pageRepository.getMaxOrder(), -1);

        final JSONObject page = new JSONObject();
        page.put(Page.PAGE_ORDER, 0);
        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p1");
        page.put(Page.PAGE_TITLE, "page1 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");
        page.put(Page.PAGE_ICON, "");

        final Transaction transaction = pageRepository.beginTransaction();
        pageRepository.add(page);
        transaction.commit();

        final List<JSONObject> pages = pageRepository.getPages();
        Assert.assertNotNull(pages);
        Assert.assertEquals(pages.size(), 1);
    }

    /**
     * Get By Permalink.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getByPermalink() throws Exception {
        final PageRepository pageRepository = getPageRepository();

        final JSONObject page1 = pageRepository.getByPermalink(Latkes.getServePath() + "/p1");
        Assert.assertNotNull(page1);
        Assert.assertEquals(page1.getString(Page.PAGE_TITLE), "page1 title");

        Assert.assertNull(pageRepository.getByPermalink("not found"));
    }

    /**
     * Get Max Order.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "add")
    public void getMaxOrder() throws Exception {
        final PageRepository pageRepository = getPageRepository();

        final JSONObject page = new JSONObject();
        page.put(Page.PAGE_ORDER, 1);
        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p2");
        page.put(Page.PAGE_TITLE, "page2 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");
        page.put(Page.PAGE_ICON, "");

        final Transaction transaction = pageRepository.beginTransaction();
        pageRepository.add(page);
        transaction.commit();

        final int maxOrder = pageRepository.getMaxOrder();
        Assert.assertEquals(maxOrder, 1);
    }

    /**
     * Get Under and Upper.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = {"add", "getMaxOrder"})
    public void getUnderAndUpper() throws Exception {
        final PageRepository pageRepository = getPageRepository();

        final JSONObject page = new JSONObject();
        page.put(Page.PAGE_ORDER, 2);
        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p3");
        page.put(Page.PAGE_TITLE, "page3 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");
        page.put(Page.PAGE_ICON, "");

        final Transaction transaction = pageRepository.beginTransaction();
        pageRepository.add(page);
        transaction.commit();

        final JSONObject page2 = pageRepository.getByPermalink(Latkes.getServePath() + "/p2");
        Assert.assertNotNull(page2);

        final JSONObject page1 = pageRepository.getUpper(page2.getString(Keys.OBJECT_ID));
        Assert.assertNotNull(page1);

        final JSONObject page3 = pageRepository.getUnder(page2.getString(Keys.OBJECT_ID));
        Assert.assertNotNull(page3);

        final JSONObject notFound = pageRepository.getUpper(page1.getString(Keys.OBJECT_ID));
        Assert.assertNull(notFound);

        Assert.assertNull(pageRepository.getUpper("not found"));
        Assert.assertNull(pageRepository.getUnder("not found"));
    }

    /**
     * Get By Order.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = {"add", "getMaxOrder"})
    public void getByOrder() throws Exception {
        final PageRepository pageRepository = getPageRepository();

        final JSONObject page1 = pageRepository.getByOrder(0);
        Assert.assertNotNull(page1);
        Assert.assertEquals(page1.getString(Page.PAGE_TITLE), "page1 title");

        Assert.assertNull(pageRepository.getByOrder(Integer.MIN_VALUE));
    }
}
