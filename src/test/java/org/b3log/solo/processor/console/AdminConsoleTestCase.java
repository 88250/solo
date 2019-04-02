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
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
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
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        super.init();
    }

    /**
     * showAdminIndex.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showAdminIndex() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/admin-index.do");
        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();

        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "后台管理 - Solo 的个人博客"));
    }

    /**
     * showAdminFunctions.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showAdminFunctions() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/admin-article.do");
        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();

        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "<div class=\"form\">"));
    }

    /**
     * showAdminPreferenceFunction.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void showAdminPreferenceFunction() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/admin-preference.do");
        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();

        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "信息配置"));
    }

    /**
     * exportSQL.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void exportSQL() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/export/sql");
        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();

        mockDispatcherServletService(request, response);

        final long outputBytes = response.outputBytes();
        Assert.assertTrue(0 < outputBytes);
    }

    /**
     * exportJSON.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void exportJSON() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/export/json");
        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();

        mockDispatcherServletService(request, response);

        final long outputBytes = response.outputBytes();
        Assert.assertTrue(0 < outputBytes);
    }

    /**
     * exportHexo.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void exportHexo() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/export/hexo");
        mockAdminLogin(request);
        final MockHttpServletResponse response = mockResponse();

        mockDispatcherServletService(request, response);

        final long outputBytes = response.outputBytes();
        Assert.assertTrue(0 < outputBytes);
    }
}
