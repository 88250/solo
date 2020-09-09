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
import org.b3log.solo.model.Link;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link LinkConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 23, 2019
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class LinkConsoleTestCase extends AbstractTestCase {

    /**
     * addLink.
     */
    public void addLink() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/link/");
        request.setMethod("POST");
        final JSONObject requestJSON = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSON.put(Link.LINK, link);
        link.put(Link.LINK_TITLE, "链滴");
        link.put(Link.LINK_ADDRESS, "https://ld246.com");
        link.put(Link.LINK_DESCRIPTION, "黑客与画家的社区");
        link.put(Link.LINK_ICON, "https://static.ld246.com/images/favicon.png");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * updateLink.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addLink")
    public void updateLink() throws Exception {
        final JSONObject l = getLinkRepository().getList(new Query()).get(0);
        final String linkId = l.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/link/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSON.put(Link.LINK, link);
        link.put(Keys.OBJECT_ID, linkId);
        link.put(Link.LINK_TITLE, "链滴");
        link.put(Link.LINK_ADDRESS, "https://ld246.com");
        link.put(Link.LINK_DESCRIPTION, "B3log 开源社区线上论坛");
        link.put(Link.LINK_ICON, "https://static.ld246.com/images/favicon.png");
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
    @Test(dependsOnMethods = "updateLink")
    public void changeOrder() throws Exception {
        final JSONObject l = getLinkRepository().getList(new Query()).get(0);
        final String linkId = l.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/link/order/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Keys.OBJECT_ID, linkId);
        requestJSON.put(Common.DIRECTION, "up");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * getLink.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "changeOrder")
    public void getLink() throws Exception {
        final JSONObject l = getLinkRepository().getList(new Query()).get(0);
        final String linkId = l.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/link/" + linkId);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * getLinks.
     */
    @Test(dependsOnMethods = "getLink")
    public void getLinks() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/links/1/10/20");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * removeLink.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getLinks")
    public void removeLink() throws Exception {
        final JSONObject l = getLinkRepository().getList(new Query()).get(0);
        final String linkId = l.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/link/" + linkId);
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }
}
