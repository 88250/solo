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

import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.util.Solos;

/**
 * The common auth check middleware for admin console.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 2.9.5
 */
@Singleton
public class ConsoleAdminAuthMidware {

    public void handle(final RequestContext context) {
        if (!Solos.isAdminLoggedIn(context)) {
            context.sendError(401);
            context.abort();

            return;
        }

        context.handle();
    }
}
