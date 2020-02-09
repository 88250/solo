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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.http.Cookie;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.util.Solos;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * {@link IndexProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class IndexProcessorTestCase extends AbstractTestCase {

    /**
     * showIndex.
     */
    public void showIndex() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>Solo 的个人博客</title>"));
    }

    /**
     * showKillBrowser.
     */
    public void showKillBrowser() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/kill-browser");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>Kill IE! - Solo 的个人博客</title>"));
    }

    /**
     * logout.
     */
    public void logout() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/logout");
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final Set<Cookie> cookies = response.getCookies();
        Assert.assertEquals(cookies.size(), 1);
        final Cookie first = cookies.iterator().next();
        Assert.assertEquals(first.getName(), Solos.COOKIE_NAME);
        Assert.assertEquals(first.getValue(), "");
    }
}
