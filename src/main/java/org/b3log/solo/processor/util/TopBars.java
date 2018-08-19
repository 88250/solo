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
package org.b3log.solo.processor.util;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Common;
import org.b3log.solo.processor.renderer.ConsoleRenderer;
import org.b3log.solo.service.StatisticQueryService;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Top bar utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:dongxu.wang@acm.org">Dongxu Wang</a>
 * @version 1.0.1.6, Aug 2, 2018
 * @since 0.3.5
 */
@Service
public class TopBars {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TopBars.class);

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * Generates top bar HTML.
     *
     * @param request  the specified request
     * @param response the specified response
     * @return top bar HTML
     * @throws ServiceException service exception
     */
    public String getTopBarHTML(final HttpServletRequest request, final HttpServletResponse response)
            throws ServiceException {
        Stopwatchs.start("Gens Top Bar HTML");

        try {
            final Template topBarTemplate = ConsoleRenderer.TEMPLATE_CFG.getTemplate("top-bar.ftl");
            final StringWriter stringWriter = new StringWriter();

            final Map<String, Object> topBarModel = new HashMap<String, Object>();

            userMgmtService.tryLogInWithCookie(request, response);
            final JSONObject currentUser = userQueryService.getCurrentUser(request);

            Keys.fillServer(topBarModel);
            topBarModel.put(Common.IS_LOGGED_IN, false);

            topBarModel.put(Common.IS_MOBILE_REQUEST, Requests.mobileRequest(request));
            topBarModel.put("mobileLabel", langPropsService.get("mobileLabel"));

            topBarModel.put("onlineVisitor1Label", langPropsService.get("onlineVisitor1Label"));
            topBarModel.put(Common.ONLINE_VISITOR_CNT, StatisticQueryService.getOnlineVisitorCount());

            if (null == currentUser) {
                topBarModel.put(Common.LOGIN_URL, userQueryService.getLoginURL(Common.ADMIN_INDEX_URI));
                topBarModel.put("loginLabel", langPropsService.get("loginLabel"));
                topBarModel.put("registerLabel", langPropsService.get("registerLabel"));

                topBarTemplate.process(topBarModel, stringWriter);

                return stringWriter.toString();
            }

            topBarModel.put(Common.IS_LOGGED_IN, true);
            topBarModel.put(Common.LOGOUT_URL, userQueryService.getLogoutURL());
            topBarModel.put(Common.IS_ADMIN, Role.ADMIN_ROLE.equals(currentUser.getString(User.USER_ROLE)));
            topBarModel.put(Common.IS_VISITOR, Role.VISITOR_ROLE.equals(currentUser.getString(User.USER_ROLE)));

            topBarModel.put("adminLabel", langPropsService.get("adminLabel"));
            topBarModel.put("logoutLabel", langPropsService.get("logoutLabel"));

            final String userName = currentUser.getString(User.USER_NAME);

            topBarModel.put(User.USER_NAME, userName);

            topBarTemplate.process(topBarModel, stringWriter);

            return stringWriter.toString();
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Gens top bar HTML failed", e);
            throw new ServiceException(e);
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Gens top bar HTML failed", e);
            throw new ServiceException(e);
        } catch (final TemplateException e) {
            LOGGER.log(Level.ERROR, "Gens top bar HTML failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }
}
