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
package org.b3log.solo.service;

import org.b3log.latke.model.User;
import org.b3log.latke.util.Requests;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Link;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link LinkQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Nov 2, 2016
 */
@Test(suiteName = "service")
public class LinkQueryServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     * 
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Add Link.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void addLink() throws Exception {
        final LinkMgmtService linkMgmtService = getLinkMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject link = new JSONObject();
        requestJSONObject.put(Link.LINK, link);

        link.put(Link.LINK_TITLE, "link1 title");
        link.put(Link.LINK_ADDRESS, "link1 address");
        link.put(Link.LINK_DESCRIPTION, "link1 description");

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

        final JSONObject paginationRequest =
                Requests.buildPaginationRequest("1/10/20");
        final JSONObject result = linkQueryService.getLinks(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Link.LINKS).length(), 2);
    }
}
