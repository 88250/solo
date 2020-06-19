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
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

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
     * getSigns.
     */
    public void getSigns() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/signs/");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * getPreference.
     *
     * @throws Exception exception
     */
    public void getPreference() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/preference/");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }

    /**
     * updatePreference.
     */
    public void updatePreference() {
        final JSONObject p = getOptionQueryService().getPreference();

        final MockRequest request = mockRequest();
        request.setRequestURI("/console/preference/");
        request.setMethod("PUT");
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put(Option.CATEGORY_C_PREFERENCE, p);
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "code\":0"));
    }
}
