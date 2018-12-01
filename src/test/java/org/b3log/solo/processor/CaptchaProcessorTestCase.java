/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
import org.b3log.solo.AbstractTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link CaptchaProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 2, 2016
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class CaptchaProcessorTestCase extends AbstractTestCase {

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
     * get.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void get() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(mock(ServletContext.class));
        when(request.getRequestURI()).thenReturn("/captcha");
        when(request.getMethod()).thenReturn("GET");

        final MockDispatcherServlet dispatcherServlet = new MockDispatcherServlet();
        dispatcherServlet.init();

        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {

            private long size;

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(final WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                size++;
            }

            @Override
            public String toString() {
                return "size: " + String.valueOf(size);
            }
        });

        dispatcherServlet.service(request, response);

        Assert.assertTrue(StringUtils.startsWith(response.getOutputStream().toString(), "size: "));
    }
}
