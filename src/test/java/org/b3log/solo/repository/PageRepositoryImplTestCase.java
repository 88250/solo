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

import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link PageRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Sep 22, 2017
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

        page.put(Page.PAGE_COMMENT_COUNT, 0);
        page.put(Page.PAGE_CONTENT, "page1 content");
        page.put(Page.PAGE_ORDER, 0);
        page.put(Page.PAGE_PERMALINK, "page1 permalink");
        page.put(Page.PAGE_TITLE, "page1 title");
        page.put(Page.PAGE_COMMENTABLE, true);
        page.put(Page.PAGE_TYPE, "page");
        page.put(Page.PAGE_OPEN_TARGET, "_self");
        page.put(Page.PAGE_EDITOR_TYPE, "");
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

        final JSONObject page1 = pageRepository.getByPermalink("page1 permalink");
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

        page.put(Page.PAGE_COMMENT_COUNT, 0);
        page.put(Page.PAGE_CONTENT, "page2 content");
        page.put(Page.PAGE_ORDER, 1);
        page.put(Page.PAGE_PERMALINK, "page2 permalink");
        page.put(Page.PAGE_TITLE, "page2 title");
        page.put(Page.PAGE_COMMENTABLE, true);
        page.put(Page.PAGE_TYPE, "page");
        page.put(Page.PAGE_OPEN_TARGET, "_self");
        page.put(Page.PAGE_EDITOR_TYPE, "");
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

        page.put(Page.PAGE_COMMENT_COUNT, 0);
        page.put(Page.PAGE_CONTENT, "page3 content");
        page.put(Page.PAGE_ORDER, 2);
        page.put(Page.PAGE_PERMALINK, "page3 permalink");
        page.put(Page.PAGE_TITLE, "page3 title");
        page.put(Page.PAGE_COMMENTABLE, true);
        page.put(Page.PAGE_TYPE, "page");
        page.put(Page.PAGE_OPEN_TARGET, "_self");
        page.put(Page.PAGE_EDITOR_TYPE, "");
        page.put(Page.PAGE_ICON, "");

        final Transaction transaction = pageRepository.beginTransaction();
        pageRepository.add(page);
        transaction.commit();

        final JSONObject page2 = pageRepository.getByPermalink("page2 permalink");
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
