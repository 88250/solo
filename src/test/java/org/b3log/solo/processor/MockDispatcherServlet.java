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

import org.b3log.latke.servlet.DispatcherServlet;
import org.b3log.latke.servlet.handler.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock dispatcher servlet for unit tests.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Dec 5, 2018
 * @since 1.7.0
 */
public class MockDispatcherServlet {

    /**
     * Handlers
     */
    private static final List<Handler> HANDLERS = new ArrayList<>();

    public void init() {
        HANDLERS.add(new RouteHandler());
        HANDLERS.add(new BeforeHandleHandler());
        HANDLERS.add(new ContextHandleHandler());
        HANDLERS.add(new AfterHandleHandler());
    }

    public void service(final HttpServletRequest req, final HttpServletResponse resp) {
        DispatcherServlet.handle(req, resp);
    }
}
