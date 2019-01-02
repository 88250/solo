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
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * {@link PreferenceConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 23, 2018
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class PreferenceConsoleTestCase extends AbstractTestCase {

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
     * getReplyNotificationTemplate.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getReplyNotificationTemplate() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/reply/notification/template");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * updateReplyNotificationTemplate.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getReplyNotificationTemplate")
    public void updateReplyNotificationTemplate() throws Exception {
        final JSONObject p = getPreferenceQueryService().getReplyNotificationTemplate();

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/reply/notification/template");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("replyNotificationTemplate", p);
        final BufferedReader reader = new BufferedReader(new StringReader(requestJSON.toString()));
        request.setReader(reader);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getSigns.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getSigns() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/signs/");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getPreference.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getPreference() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/preference/");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * updatePreference.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void updatePreference() throws Exception {
        final JSONObject p = getPreferenceQueryService().getPreference();

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/preference/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Option.CATEGORY_C_PREFERENCE, p);
        final BufferedReader reader = new BufferedReader(new StringReader(requestJSON.toString()));
        request.setReader(reader);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * updateOss.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void updateOss() throws Exception {
        final JSONObject p = new JSONObject();
        p.put(Option.ID_C_CLOUD_STORAGE_KEY, Option.CATEGORY_C_QINIU);
        p.put(Option.ID_C_QINIU_ACCESS_KEY, "1");
        p.put(Option.ID_C_QINIU_SECRET_KEY, "1");
        p.put(Option.ID_C_QINIU_DOMAIN, "1");
        p.put(Option.ID_C_QINIU_BUCKET, "1");

        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/preference/oss");
        request.setMethod("PUT");
        final BufferedReader reader = new BufferedReader(new StringReader(p.toString()));
        request.setReader(reader);

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getOssPreference.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "updateOss")
    public void getOssPreference() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/preference/oss");
        request.putParameter(Option.ID_C_CLOUD_STORAGE_KEY, Option.CATEGORY_C_QINIU);
        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

}
