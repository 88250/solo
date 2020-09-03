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

import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.AbstractTestCase;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link UserMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/nanolikeyou">nanolikeyou</a>
 * @version 1.0.0.5, Aug 2, 2018
 */
@Test(suiteName = "service")
public class UserMgmtServiceTestCase extends AbstractTestCase {

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
    }

    /**
     * Update User.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addUser")
    public void updateUser() throws Exception {
        final UserMgmtService userMgmtService = getUserMgmtService();

        JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_NAME, "user2name");
        requestJSONObject.put(User.USER_ROLE, Role.ADMIN_ROLE);

        final String id = userMgmtService.addUser(requestJSONObject);
        Assert.assertNotNull(id);

        requestJSONObject.put(Keys.OBJECT_ID, id);
        requestJSONObject.put(User.USER_NAME, "user2newname");

        userMgmtService.updateUser(requestJSONObject);

        Assert.assertEquals(getUserQueryService().getUser(id).getJSONObject(
                User.USER).getString(User.USER_NAME), "user2newname");
    }

    /**
     * Valid User.
     *
     * @throws Exception exception
     */
    @Test
    public void validUser() throws Exception {
        final UserMgmtService userMgmtService = getUserMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_NAME, "user1 name");

        try {
            final String id = userMgmtService.addUser(requestJSONObject);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ServiceException);
        }
    }

    /**
     * Valid XSS username.
     *
     * @throws Exception exception
     */
    @Test(expectedExceptions = ServiceException.class)
    public void XSSUser() throws Exception {
        final UserMgmtService userMgmtService = getUserMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_NAME, "<script></script>");

        final String id = userMgmtService.addUser(requestJSONObject);
    }

    /**
     * Remove User.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addUser")
    public void removeUser() throws Exception {
        final UserMgmtService userMgmtService = getUserMgmtService();

        final JSONObject user = getUserQueryService().getUserByName("user1name");
        Assert.assertNotNull(user);

        userMgmtService.removeUser(user.getString(Keys.OBJECT_ID));

        Assert.assertNull(getUserQueryService().getUserByName("user1name"));
    }
}
