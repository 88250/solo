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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.solo.service.InitService;

/**
 * Checks initialization handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Dec 14, 2019
 * @since 3.2.0
 */
public class InitCheckHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(InitCheckHandler.class);

    /**
     * Whether initialization info reported.
     */
    private static boolean initReported;

    @Override
    public void handle(final RequestContext context) {
        final String requestURI = context.requestURI();
        final boolean isSpiderBot = (boolean) context.attr(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT);
        LOGGER.log(Level.TRACE, "Request [URI={}]", requestURI);

        // 禁止直接获取 robots.txt https://github.com/b3log/solo/issues/12543
        if (requestURI.startsWith("/robots.txt") && !isSpiderBot) {
            context.sendError(403);
            return;
        }

        final BeanManager beanManager = BeanManager.getInstance();
        final InitService initService = beanManager.getReference(InitService.class);
        if (initService.isInited()) {
            context.handle();
            return;
        }

        if (StringUtils.startsWith(requestURI, Latkes.getContextPath() + "/login/")) {
            // Do initialization
            context.handle();
            return;
        }

        if (!initReported) {
            LOGGER.log(Level.DEBUG, "Solo has not been initialized, so redirects to /start");
            initReported = true;
        }

        context.attr(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/start");
        context.handle();
    }
}
