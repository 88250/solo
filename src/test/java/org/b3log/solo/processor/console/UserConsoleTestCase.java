/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Query;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.StringReader;

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
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        super.init();
    }

    /**
     * updateUser.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void updateUser() throws Exception {
        final JSONObject u = getUserRepository().getFirst(new Query());

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/user/");
        request.setMethod("PUT");
        final BufferedReader reader = new BufferedReader(new StringReader(u.toString()));
        request.setReader(reader);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
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

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/user/" + userId);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getUsers.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getUser")
    public void getUsers() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/users/1/10/20");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
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

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/changeRole/" + userId);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
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

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/user/" + userId);
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
