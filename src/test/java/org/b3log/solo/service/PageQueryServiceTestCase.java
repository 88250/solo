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

import org.b3log.latke.Latkes;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link PageQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Apr 19, 2019
 */
@Test(suiteName = "service")
public class PageQueryServiceTestCase extends AbstractTestCase {

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
     * Get Pages.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addPage")
    public void getPages() throws Exception {
        final PageQueryService pageQueryService = getPageQueryService();

        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        final JSONObject result = pageQueryService.getPages(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(((List<JSONObject>) result.opt(Page.PAGES)).size(), 1);
    }
}
