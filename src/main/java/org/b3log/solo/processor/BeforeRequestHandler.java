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

import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.util.Solos;

/**
 * Before request handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 12, 2020
 * @since 3.6.7
 */
public class BeforeRequestHandler implements Handler {

    @Override
    public void handle(final RequestContext context) {
        context.attr(Keys.HttpRequest.START_TIME_MILLIS, System.currentTimeMillis());
        final String remoteAddr = Requests.getRemoteAddr(context.getRequest());
        if (Solos.BLACKLIST_IPS.contains(remoteAddr)) {
            context.setStatus(429);
            context.setHeader("Retry-After", "600");
            context.sendString("Too Many Requests");
            context.abort();

            return;
        }

        Stopwatchs.start("Request Initialized [requestURI=" + context.requestURI() + "]");
    }
}
