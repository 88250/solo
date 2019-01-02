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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
import org.b3log.solo.model.Option;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

/**
 * {@link LoginProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.0, Feb 18, 2017
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class LoginProcessorTestCase extends AbstractTestCase {

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
     * showLogin.
     */
    @Test(dependsOnMethods = "init")
    public void showLogin() {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/login");
        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "<title>Admin 的个人博客 - 欢迎使用!</title>"));
    }

    /**
     * login.
     */
    @Test(dependsOnMethods = "init")
    public void login() {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/login");
        request.setMethod("POST");
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);

        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(User.USER_EMAIL, "test@gmail.com");
        requestJSON.put(User.USER_PASSWORD, "pass");
        final BufferedReader reader = new BufferedReader(new StringReader(requestJSON.toString()));
        request.setReader(reader);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "isLoggedIn\":true"));
    }

    /**
     * logout.
     */
    @Test(dependsOnMethods = "init")
    public void logout() {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/logout");
        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final List<Cookie> cookies = response.cookies();
        Assert.assertEquals(cookies.size(), 1);
        Assert.assertEquals(cookies.get(0).getName(), Solos.COOKIE_NAME);
        Assert.assertNull(cookies.get(0).getValue());
    }

    /**
     * showForgot.
     */
    @Test(dependsOnMethods = "init")
    public void showForgot() {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/forgot");
        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, " <title>Admin 的个人博客 - 忘记密码!</title>"));
    }

    /**
     * forgot.
     */
    @Test(dependsOnMethods = "init")
    public void forgot() {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/forgot");
        request.setMethod("POST");
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);

        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(User.USER_EMAIL, "test@gmail.com");
        final BufferedReader reader = new BufferedReader(new StringReader(requestJSON.toString()));
        request.setReader(reader);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "succeed\":true"));
    }
}
