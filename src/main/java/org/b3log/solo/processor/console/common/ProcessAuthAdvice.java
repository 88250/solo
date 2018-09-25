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
package org.b3log.solo.processor.console.common;

import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.solo.service.UserQueryService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * The common auth check before advice for admin console.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.1, Sep 25, 2018
 */
@Named
@Singleton
public class ProcessAuthAdvice extends BeforeRequestProcessAdvice {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ProcessAuthAdvice.class);

    @Override
    public void doAdvice(final HTTPRequestContext context, final Map<String, Object> args) {
        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final UserQueryService userQueryService = beanManager.getReference(UserQueryService.class);

        if (!userQueryService.isAdminLoggedIn(context.getRequest())) {
            try {
                context.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (final IOException e) {
                LOGGER.log(Level.ERROR, "Response sends error failed", e);
            }
        }
    }
}
