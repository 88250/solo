/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
