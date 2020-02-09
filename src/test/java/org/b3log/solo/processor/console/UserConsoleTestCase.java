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
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link UserConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Feb 8, 2019
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class UserConsoleTestCase extends AbstractTestCase {

    /**
     * updateUser.
     *
     * @throws Exception exception
     */
    public void updateUser() throws Exception {
        final JSONObject u = getUserRepository().getFirst(new Query());

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/user/");
        request.setMethod("PUT");
        request.setJSON(u);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getUser.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "updateUser")
    public void getUser() throws Exception {
        final JSONObject u = getUserRepository().getFirst(new Query());
        final String userId = u.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/user/" + userId);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getUsers.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getUser")
    public void getUsers() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/users/1/10/20");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * changeUserRole.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getUsers")
    public void changeUserRole() throws Exception {
        final JSONObject u = getUserRepository().getFirst(new Query());
        final String userId = u.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/changeRole/" + userId);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * removeUser.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "changeUserRole")
    public void removeUser() throws Exception {
        final JSONObject u = getUserRepository().getFirst(new Query());
        final String userId = u.optString(Keys.OBJECT_ID);

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/user/" + userId);
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
