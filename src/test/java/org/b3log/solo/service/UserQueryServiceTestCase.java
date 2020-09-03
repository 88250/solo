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
import org.b3log.latke.model.User;
import org.b3log.latke.util.URLs;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link UserQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/nanolikeyou">nanolikeyou</a>
 * @version 1.0.0.4, May 9, 2020
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

        final String id = userMgmtService.addUser(requestJSONObject);
        Assert.assertNotNull(id);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUser(id));
    }

    /**
     * Get User.
     */
    @Test(dependsOnMethods = "addUser")
    public void getUser() {
        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNull(userQueryService.getUser("not found"));
    }

    /**
     * Get User By Name.
     */
    @Test(dependsOnMethods = "addUser")
    public void getUserByName() {
        final UserQueryService userQueryService = getUserQueryService();

        final JSONObject user = userQueryService.getUserByName("user1name");
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

        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/20/10");
        final JSONObject result = userQueryService.getUsers(paginationRequest);
        final List<JSONObject> users = (List<JSONObject>) result.opt(User.USERS);
        Assert.assertEquals(users.size(), 2);
    }

    /**
     * Get Login URL.
     */
    public void getLoginURL() {
        final UserQueryService userQueryService = getUserQueryService();
        final String redirectURI = "/redirectURI";
        final String loginURL = userQueryService.getLoginURL(redirectURI);

        Assert.assertEquals(loginURL, "/start?referer=" + URLs.encode(Latkes.getServePath() + redirectURI));
    }

    /**
     * Get Logout URL.
     */
    public void getLogoutURL() {
        final UserQueryService userQueryService = getUserQueryService();
        final String logoutURL = userQueryService.getLogoutURL();

        Assert.assertEquals(logoutURL, "/logout?referer=" + URLs.encode(Latkes.getServePath()));
    }
}
