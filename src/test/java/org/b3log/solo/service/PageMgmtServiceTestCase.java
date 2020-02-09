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
package org.b3log.solo.service;

import org.b3log.latke.Latkes;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Page;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link PageMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Apr 19, 2019
 */
@Test(suiteName = "service")
public class PageMgmtServiceTestCase extends AbstractTestCase {

    /**
     * Add Page.
     *
     * @throws Exception exception
     */
    public void addPage() throws Exception {
        final PageMgmtService pageMgmtService = getPageMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSONObject.put(Page.PAGE, page);

        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p1");
        page.put(Page.PAGE_TITLE, "page1 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");

        final String pageId = pageMgmtService.addPage(requestJSONObject);

        Assert.assertNotNull(pageId);
    }

    /**
     * Remove Page.
     *
     * @throws Exception exception
     */
    public void removePage() throws Exception {
        final PageMgmtService pageMgmtService = getPageMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSONObject.put(Page.PAGE, page);

        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p2");
        page.put(Page.PAGE_TITLE, "page2 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");

        final String pageId = pageMgmtService.addPage(requestJSONObject);
        Assert.assertNotNull(pageId);

        final PageQueryService pageQueryService = getPageQueryService();
        JSONObject result = pageQueryService.getPage(pageId);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(Page.PAGE).getString(Page.PAGE_TITLE), "page2 title");

        pageMgmtService.removePage(pageId);

        result = pageQueryService.getPage(pageId);
        Assert.assertNull(result);
    }

    /**
     * Update Page.
     *
     * @throws Exception exception
     */
    public void updatePage() throws Exception {
        final PageMgmtService pageMgmtService = getPageMgmtService();

        JSONObject requestJSONObject = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSONObject.put(Page.PAGE, page);

        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p3");
        page.put(Page.PAGE_TITLE, "page3 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");

        final String pageId = pageMgmtService.addPage(requestJSONObject);
        Assert.assertNotNull(pageId);

        final PageQueryService pageQueryService = getPageQueryService();
        JSONObject result = pageQueryService.getPage(pageId);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(Page.PAGE).getString(Page.PAGE_TITLE), "page3 title");

        page.put(Page.PAGE_TITLE, "updated page3 title");
        pageMgmtService.updatePage(requestJSONObject);

        result = pageQueryService.getPage(pageId);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(Page.PAGE).getString(Page.PAGE_TITLE), "updated page3 title");
    }

    /**
     * Change Order.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addPage")
    public void changeOrder() throws Exception {
        final PageMgmtService pageMgmtService = getPageMgmtService();

        JSONObject requestJSONObject = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSONObject.put(Page.PAGE, page);

        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p4");
        page.put(Page.PAGE_TITLE, "page4 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");

        final String pageId = pageMgmtService.addPage(requestJSONObject);
        Assert.assertNotNull(pageId);

        final int oldOrder = page.getInt(Page.PAGE_ORDER);
        pageMgmtService.changeOrder(pageId, "up");

        final JSONObject result = getPageQueryService().getPage(pageId);
        Assert.assertNotNull(result);
        Assert.assertTrue(oldOrder > result.getJSONObject(Page.PAGE).getInt(Page.PAGE_ORDER));
    }
}
