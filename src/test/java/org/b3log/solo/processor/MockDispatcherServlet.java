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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.handler.AdviceHandler;
import org.b3log.latke.servlet.handler.ArgsHandler;
import org.b3log.latke.servlet.handler.Handler;
import org.b3log.latke.servlet.handler.MethodInvokeHandler;
import org.b3log.latke.servlet.handler.RequestDispatchHandler;
import org.b3log.latke.servlet.handler.RequestPrepareHandler;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.HTTP404Renderer;
import org.b3log.latke.servlet.renderer.HTTP500Renderer;

/**
 * Mock dispatcher servlet for unit tests.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Apr 22, 2017
 * @since 1.7.0
 */
public class MockDispatcherServlet {

    /**
     * the holder of all the sys-handler.
     */
    private static final List<Handler> SYS_HANDLER = new ArrayList<>();

    public void init() throws ServletException {
        SYS_HANDLER.add(new RequestPrepareHandler());
        SYS_HANDLER.add(new RequestDispatchHandler());
        SYS_HANDLER.add(new ArgsHandler());
        SYS_HANDLER.add(new AdviceHandler());
        SYS_HANDLER.add(new MethodInvokeHandler());
    }

    public void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final HTTPRequestContext httpRequestContext = new HTTPRequestContext();

        httpRequestContext.setRequest(req);
        httpRequestContext.setResponse(resp);
        final HttpControl httpControl = new HttpControl(SYS_HANDLER.iterator(), httpRequestContext);

        try {
            httpControl.nextHandler();
        } catch (final Exception e) {
            httpRequestContext.setRenderer(new HTTP500Renderer(e));
        }

        result(httpRequestContext);
    }

    /**
     * To http repsonse.
     *
     * @param context {@link HTTPRequestContext}
     * @throws IOException IOException
     */
    public static void result(final HTTPRequestContext context) throws IOException {
        final HttpServletResponse response = context.getResponse();

        if (response.isCommitted()) { // Response sends redirect or error
            return;
        }

        AbstractHTTPResponseRenderer renderer = context.getRenderer();

        if (null == renderer) {
            renderer = new HTTP404Renderer();
        }

        renderer.render(context);
    }
}
