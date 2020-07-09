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
import org.b3log.solo.model.Link;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link LinkQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Jul 9, 2020
 */
@Test(suiteName = "service")
public class LinkQueryServiceTestCase extends AbstractTestCase {

    /**
     * Add Link.
     *
     * @throws Exception exception
     */
    public void addLink() throws Exception {
        final LinkMgmtService linkMgmtService = getLinkMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSONObject.put(Link.LINK, link);

        link.put(Link.LINK_TITLE, "link1 title");
        link.put(Link.LINK_ADDRESS, "link1 address");
        link.put(Link.LINK_DESCRIPTION, "link1 description");
        link.put(Link.LINK_ICON, "link1 icon");

        final String linkId = linkMgmtService.addLink(requestJSONObject);
        Assert.assertNotNull(linkId);
    }

    /**
     * Get Links.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addLink")
    public void getLinks() throws Exception {
        final LinkQueryService linkQueryService = getLinkQueryService();

        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        final JSONObject result = linkQueryService.getLinks(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(((List<JSONObject>) result.opt(Link.LINKS)).size(), 1);
    }
}
