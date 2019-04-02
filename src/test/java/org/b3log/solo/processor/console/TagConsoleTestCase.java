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
 * {@link TagConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 11, 2018
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class TagConsoleTestCase extends AbstractTestCase {

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
     * getTags.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getTags() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/tags");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getUnusedTags.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getTags")
    public void getUnusedTags() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/tag/unused");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * removeUnusedTags.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getUnusedTags")
    public void removeUnusedTags() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/tag/unused");
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
