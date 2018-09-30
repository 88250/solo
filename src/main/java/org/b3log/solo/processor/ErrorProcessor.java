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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Common;
import org.b3log.solo.processor.console.ConsoleRenderer;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Error processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Sep 25, 2018
 * @since 0.4.5
 */
@RequestProcessor
public class ErrorProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleProcessor.class);

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

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
     * Handles the error.
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = "/error/{statusCode}", method = HTTPRequestMethod.GET)
    public void showErrorPage(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response,
                              final String statusCode)
            throws Exception {
        if (StringUtils.equals("GET", request.getMethod())) {
            final String requestURI = request.getRequestURI();
            String templateName = StringUtils.substringAfterLast(requestURI, "/");
            templateName = StringUtils.substringBefore(templateName, ".") + ".ftl";

            final ConsoleRenderer renderer = new ConsoleRenderer();
            context.setRenderer(renderer);
            renderer.setTemplateName("error/" + templateName);

            final Map<String, Object> dataModel = renderer.getDataModel();
            try {
                final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));
                dataModel.putAll(langs);
                final JSONObject preference = preferenceQueryService.getPreference();
                dataModelService.fillCommon(request, response, dataModel, preference);
                dataModel.put(Common.LOGIN_URL, userQueryService.getLoginURL(Common.ADMIN_INDEX_URI));
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, e.getMessage(), e);

                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            context.renderJSON().renderMsg(statusCode);
        }
    }
}
