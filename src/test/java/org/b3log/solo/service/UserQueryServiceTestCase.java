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

import junit.framework.Assert;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Requests;
import org.b3log.solo.AbstractTestCase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

/**
 * {@link UserQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://github.com/nanolikeyou">nanolikeyou</a>
 * @version 1.0.0.2, Aug 14, 2017
 */
@Test(suiteName = "service")
public class UserQueryServiceTestCase extends AbstractTestCase {

    /**
     * Add User.
     *
     * @throws Exception exception
     */
    @Test
    public void addUser() throws Exception {
        final UserMgmtService userMgmtService = getUserMgmtService();

        final JSONObject requestJSONObject = new JSONObject();

        requestJSONObject.put(User.USER_NAME, "user1name");
        requestJSONObject.put(User.USER_EMAIL, "test1@gmail.com");
        requestJSONObject.put(User.USER_PASSWORD, "pass1");

        final String id = userMgmtService.addUser(requestJSONObject);
        Assert.assertNotNull(id);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUser(id));
    }

    /**
     * Get User.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addUser")
    public void getUser() throws Exception {
        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNull(userQueryService.getUser("not found"));
    }

    /**
     * Get User By Email.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addUser")
    public void getUserByEmail() throws Exception {
        final UserQueryService userQueryService = getUserQueryService();

        final JSONObject user = userQueryService.getUserByEmail("test1@gmail.com");
        Assert.assertNotNull(user);
    }

    /**
     * Get Users.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addUser")
    public void getUsers() throws Exception {
        final UserQueryService userQueryService = getUserQueryService();

        final JSONObject paginationRequest = Requests.buildPaginationRequest("1/20/10");
        final JSONObject result = userQueryService.getUsers(paginationRequest);
        final JSONArray users = result.getJSONArray(User.USERS);
        Assert.assertEquals(1, users.length());
    }

    /**
     * Get Login URL.
     */
    public void getLoginURL() {
        final UserQueryService userQueryService = getUserQueryService();
        final String loginURL = userQueryService.getLoginURL("redirectURL");

        Assert.assertEquals(loginURL, "/login?goto=http%3A%2F%2Flocalhost%3A8080redirectURL");
    }

    /**
     * Get Logout URL.
     */
    public void getLogoutURL() {
        final UserQueryService userQueryService = getUserQueryService();
        final String logoutURL = userQueryService.getLogoutURL();

        Assert.assertEquals(logoutURL, "/logout?goto=http%3A%2F%2Flocalhost%3A8080%2F");
    }
}
