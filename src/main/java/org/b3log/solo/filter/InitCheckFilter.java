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
package org.b3log.solo.filter;

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.DispatcherServlet;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.renderer.HTTP500Renderer;
import org.b3log.solo.service.InitService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Checks initialization filter.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.2, Sep 13, 2016
 * @since 0.3.1
 */
public final class InitCheckFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InitCheckFilter.class);

    /**
     * Whether initialization info reported.
     */
    private static boolean initReported;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * If Solo has not been initialized, so redirects to /init.
     *
     * @param request  the specified request
     * @param response the specified response
     * @param chain    filter chain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String requestURI = httpServletRequest.getRequestURI();

        LOGGER.log(Level.TRACE, "Request[URI={0}]", requestURI);

        // If requests Latke Remote APIs, skips this filter 
        if (requestURI.startsWith(Latkes.getContextPath() + "/latke/remote")) {
            chain.doFilter(request, response);

            return;
        }

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final InitService initService = beanManager.getReference(InitService.class);

        if (initService.isInited()) {
            chain.doFilter(request, response);

            return;
        }

        if ("POST".equalsIgnoreCase(httpServletRequest.getMethod()) && (Latkes.getContextPath() + "/init").equals(requestURI)) {
            // Do initailization
            chain.doFilter(request, response);

            return;
        }

        if (!initReported) {
            LOGGER.log(Level.DEBUG, "Solo has not been initialized, so redirects to /init");
            initReported = true;
        }

        final HTTPRequestContext context = new HTTPRequestContext();

        context.setRequest((HttpServletRequest) request);
        context.setResponse((HttpServletResponse) response);

        request.setAttribute(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/init");
        request.setAttribute(Keys.HttpRequest.REQUEST_METHOD, HTTPRequestMethod.GET.name());

        final HttpControl httpControl = new HttpControl(DispatcherServlet.SYS_HANDLER.iterator(), context);

        try {
            httpControl.nextHandler();
        } catch (final Exception e) {
            context.setRenderer(new HTTP500Renderer(e));
        }

        DispatcherServlet.result(context);
    }

    @Override
    public void destroy() {
    }
}
