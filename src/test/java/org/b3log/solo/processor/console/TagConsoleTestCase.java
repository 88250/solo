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
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
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
     * getTags.
     *
     * @throws Exception exception
     */
    public void getTags() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/tags");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * getUnusedTags.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "getTags")
    public void getUnusedTags() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/tag/unused");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }

    /**
     * removeUnusedTags.
     */
    @Test(dependsOnMethods = "getUnusedTags")
    public void removeUnusedTags() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/console/tag/unused");
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
