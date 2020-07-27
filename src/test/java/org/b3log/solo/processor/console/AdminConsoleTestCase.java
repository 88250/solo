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
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link AdminConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Feb 22, 2019
 * @since 2.9.7
 */
@Test(suiteName = "processor")
public class AdminConsoleTestCase extends AbstractTestCase {

    /**
     * showAdminIndex.
     */
    public void showAdminIndex() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/admin-index.do");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "后台管理 - Solo 的个人博客"));
    }

    /**
     * showAdminFunctions.
     */
    public void showAdminFunctions() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/admin-article.do");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<div class=\"form\">"));
    }

    /**
     * showAdminPreferenceFunction.
     */
    public void showAdminPreferenceFunction() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/admin-preference.do");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "信息配置"));
    }

    /**
     * exportSQL.
     */
    public void exportSQL() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/export/sql");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final long outputBytes = response.getBytes().length;
        Assert.assertTrue(0 < outputBytes);
    }

    /**
     * exportJSON.
     */
    public void exportJSON() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/export/json");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final long outputBytes = response.getBytes().length;
        Assert.assertTrue(0 < outputBytes);
    }

    /**
     * exportHexo.
     */
    public void exportHexo() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/export/hexo");
        mockAdminLogin(request);
        final MockResponse response = mockResponse();

        mockDispatcher(request, response);

        final long outputBytes = response.getBytes().length;
        Assert.assertTrue(0 < outputBytes);
    }
}
