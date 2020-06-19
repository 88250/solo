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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * getUsers.
     */
    @Test(dependsOnMethods = "getUser")
    public void getUsers() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/users/1/10/20");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
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
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }
}
