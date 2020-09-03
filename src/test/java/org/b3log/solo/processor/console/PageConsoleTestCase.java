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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Page;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link PageConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Apr 19, 2019
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class PageConsoleTestCase extends AbstractTestCase {

    /**
     * addPage.
     */
    public void addPage() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/page/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSON.put(Page.PAGE, page);
        page.put(Page.PAGE_TITLE, "链滴");
        page.put(Page.PAGE_PERMALINK, "https://ld246.com");
        page.put(Page.PAGE_OPEN_TARGET, "");
        page.put(Page.PAGE_ICON, "");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * updatePage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addPage")
    public void updatePage() throws Exception {
        final JSONObject p = getPageRepository().getList(new Query()).get(0);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/page/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Page.PAGE, p);
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * changeOrder.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "updatePage")
    public void changeOrder() throws Exception {
        final JSONObject p = getPageRepository().getList(new Query()).get(0);
        final String pageId = p.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/page/order/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Keys.OBJECT_ID, pageId);
        requestJSON.put(Common.DIRECTION, "up");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * getPage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "changeOrder")
    public void getPage() throws Exception {
        final JSONObject p = getPageRepository().getList(new Query()).get(0);
        final String pageId = p.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/page/" + pageId);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * getPages.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getPage")
    public void getPages() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/pages/1/10/20");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * removePage.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getPages")
    public void removeLink() throws Exception {
        final JSONObject p = getPageRepository().getList(new Query()).get(0);
        final String pageId = p.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/page/" + pageId);
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }
}
