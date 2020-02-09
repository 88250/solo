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
     *
     * @throws Exception exception
     */
    public void addPage() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/page/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSON.put(Page.PAGE, page);
        page.put(Page.PAGE_TITLE, "黑客派");
        page.put(Page.PAGE_PERMALINK, "https://hacpai.com");
        page.put(Page.PAGE_OPEN_TARGET, "");
        page.put(Page.PAGE_ICON, "");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
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
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
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
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
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
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
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
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
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
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
