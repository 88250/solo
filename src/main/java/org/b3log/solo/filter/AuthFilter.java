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

import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication filter.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Sep 21, 2017
 * @since 0.3.1
 */
public final class AuthFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * If the specified request is NOT made by an authenticated user, sends error 403.
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
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final UserMgmtService userMgmtService = beanManager.getReference(UserMgmtService.class);
        final UserQueryService userQueryService = beanManager.getReference(UserQueryService.class);

        try {
            userMgmtService.tryLogInWithCookie(httpServletRequest, httpServletResponse);

            final JSONObject currentUser = userQueryService.getCurrentUser(httpServletRequest);
            if (null == currentUser) {
                LOGGER.debug("The request has been forbidden");
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            final String userRole = currentUser.optString(User.USER_ROLE);
            if (Role.VISITOR_ROLE.equals(userRole)) {
                LOGGER.warn("The request [Visitor] has been forbidden");
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            chain.doFilter(request, response);
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Auth filter failed", e);

            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void destroy() {
    }
}
