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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.Server;

/**
 * Log console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 2, 2020
 * @since 4.1.0
 */
@Singleton
public class LogConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(LogConsole.class);

    /**
     * Get log.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "log": "log lines"
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getLog(final RequestContext context) {
        context.renderJSON(true);

        final String content = Server.TAIL_LOGGER_WRITER.toString();
        context.renderJSONValue("log", content);
    }
}
