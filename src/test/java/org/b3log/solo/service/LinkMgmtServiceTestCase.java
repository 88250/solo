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

import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Link;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link LinkMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Oct 23, 2019
 */
@Test(suiteName = "service")
public class LinkMgmtServiceTestCase extends AbstractTestCase {

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
     * Remove Link.
     *
     * @throws Exception exception
     */
    public void removeLink() throws Exception {
        final LinkMgmtService linkMgmtService = getLinkMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSONObject.put(Link.LINK, link);

        link.put(Link.LINK_TITLE, "link2 title");
        link.put(Link.LINK_ADDRESS, "link2 address");
        link.put(Link.LINK_DESCRIPTION, "link2 description");
        link.put(Link.LINK_ICON, "link2 icon");

        final String linkId = linkMgmtService.addLink(requestJSONObject);
        Assert.assertNotNull(linkId);

        final LinkQueryService linkQueryService = getLinkQueryService();
        JSONObject result = linkQueryService.getLink(linkId);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(Link.LINK).
                getString(Link.LINK_TITLE), "link2 title");

        linkMgmtService.removeLink(linkId);

        result = linkQueryService.getLink(linkId);
        Assert.assertNull(result);
    }

    /**
     * Update Link.
     *
     * @throws Exception exception
     */
    public void updateLink() throws Exception {
        final LinkMgmtService linkMgmtService = getLinkMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSONObject.put(Link.LINK, link);

        link.put(Link.LINK_TITLE, "link3 title");
        link.put(Link.LINK_ADDRESS, "link3 address");
        link.put(Link.LINK_DESCRIPTION, "link3 description");
        link.put(Link.LINK_ICON, "link3 icon");

        final String linkId = linkMgmtService.addLink(requestJSONObject);
        Assert.assertNotNull(linkId);

        final LinkQueryService linkQueryService = getLinkQueryService();
        JSONObject result = linkQueryService.getLink(linkId);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(Link.LINK).
                getString(Link.LINK_TITLE), "link3 title");

        link.put(Link.LINK_TITLE, "updated link3 title");
        linkMgmtService.updateLink(requestJSONObject);

        result = linkQueryService.getLink(linkId);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONObject(Link.LINK).getString(
                Link.LINK_TITLE), "updated link3 title");
    }

    /**
     * Change Order.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addLink")
    public void changeOrder() throws Exception {
        final LinkMgmtService linkMgmtService = getLinkMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSONObject.put(Link.LINK, link);

        link.put(Link.LINK_TITLE, "link4 title");
        link.put(Link.LINK_ADDRESS, "link4 address");
        link.put(Link.LINK_DESCRIPTION, "link4 description");
        link.put(Link.LINK_ICON, "link4 icon");

        final String linkId = linkMgmtService.addLink(requestJSONObject);
        Assert.assertNotNull(linkId);

        final int oldOrder = link.getInt(Link.LINK_ORDER);
        linkMgmtService.changeOrder(linkId, "up");

        final JSONObject result = getLinkQueryService().getLink(linkId);
        Assert.assertNotNull(result);
        Assert.assertTrue(oldOrder > result.getJSONObject(Link.LINK).getInt(
                Link.LINK_ORDER));
    }
}
