/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.processor;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Common;
import org.b3log.solo.processor.renderer.ConsoleRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONObject;


/**
 * Error processor.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jun 20, 2012
 * @since 0.4.5
 */
@RequestProcessor
public final class ErrorProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleProcessor.class.getName());

    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * User service.
     */
    private static UserService userService = UserServiceFactory.getUserService();

    /**
     * Shows the user template page.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws IOException io exception 
     */
    @RequestProcessing(value = "/error/*.html", method = HTTPRequestMethod.GET)
    public void showErrorPage(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        final String requestURI = request.getRequestURI();
        String templateName = StringUtils.substringAfterLast(requestURI, "/");

        templateName = StringUtils.substringBefore(templateName, ".") + ".ftl";
        LOGGER.log(Level.FINE, "Shows error page[requestURI={0}, templateName={1}]", new Object[] {requestURI, templateName});

        final ConsoleRenderer renderer = new ConsoleRenderer();

        context.setRenderer(renderer);
        renderer.setTemplateName("error" + File.separatorChar + templateName);

        final Map<String, Object> dataModel = renderer.getDataModel();

        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));

            dataModel.putAll(langs);
            final JSONObject preference = preferenceQueryService.getPreference();

            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);

            dataModel.put(Common.LOGIN_URL, userService.createLoginURL(Common.ADMIN_INDEX_URI));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }
}
