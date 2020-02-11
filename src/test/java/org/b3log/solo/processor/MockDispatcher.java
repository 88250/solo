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

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.Response;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.http.handler.InvokeHandler;
import org.b3log.latke.http.handler.RouteHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock dispatcher for unit tests.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 1.7.0
 */
public class MockDispatcher {

    /**
     * Handlers
     */
    private static final List<Handler> HANDLERS = new ArrayList<>();

    public void init() {
        HANDLERS.add(new RouteHandler());
        HANDLERS.add(new InvokeHandler());
    }

    public void handle(final Request req, final Response resp) {
        Dispatcher.handle(req, resp);
    }
}
