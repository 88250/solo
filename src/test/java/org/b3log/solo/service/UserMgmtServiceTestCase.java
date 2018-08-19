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
import org.apache.commons.codec.digest.DigestUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.AbstractTestCase;
import org.json.JSONObject;
import org.testng.annotations.Test;

/**
 * {@link UserMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://github.com/nanolikeyou">nanolikeyou</a>
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
        requestJSONObject.put(User.USER_EMAIL, "test1@gmail.com");
        requestJSONObject.put(User.USER_PASSWORD, "pass1");

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
        requestJSONObject.put(User.USER_EMAIL, "test2@gmail.com");
        requestJSONObject.put(User.USER_PASSWORD, "pass2");
        requestJSONObject.put(User.USER_ROLE, Role.ADMIN_ROLE);

        final String id = userMgmtService.addUser(requestJSONObject);
        Assert.assertNotNull(id);

        requestJSONObject.put(Keys.OBJECT_ID, id);
        requestJSONObject.put(User.USER_NAME, "user2newname");

        userMgmtService.updateUser(requestJSONObject);

        Assert.assertEquals(getUserQueryService().getUser(id).getJSONObject(
                User.USER).getString(User.USER_NAME), "user2newname");

        // Do not update password
        requestJSONObject.put(Keys.OBJECT_ID, id);
        requestJSONObject.put(User.USER_NAME, "user2name");
        requestJSONObject.put(User.USER_EMAIL, "test2@gmail.com");
        requestJSONObject.put(User.USER_PASSWORD, "pass2");

        userMgmtService.updateUser(requestJSONObject);

        Assert.assertEquals(getUserQueryService().getUser(id).getJSONObject(
                User.USER).getString(User.USER_PASSWORD), DigestUtils.md5Hex("pass2"));
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
        requestJSONObject.put(User.USER_EMAIL, "test1@gmail.com");
        requestJSONObject.put(User.USER_PASSWORD, "pass1");

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
        requestJSONObject.put(User.USER_NAME, "username");
        requestJSONObject.put(User.USER_EMAIL, "<script></script>");

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

        final JSONObject user = getUserQueryService().getUserByEmail("test1@gmail.com");
        Assert.assertNotNull(user);

        userMgmtService.removeUser(user.getString(Keys.OBJECT_ID));

        Assert.assertNull(getUserQueryService().getUserByEmail("test1@gmail.com"));
    }
}
