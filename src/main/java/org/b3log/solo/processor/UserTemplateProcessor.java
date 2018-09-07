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

import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.model.Option;
import org.b3log.solo.processor.renderer.SkinRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * User template processor.
 *
 * <p>
 * User can add a template (for example "links.ftl") then visits the page ("links.html").
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Sep 7, 2018
 * @since 0.4.5
 */
@RequestProcessor
public class UserTemplateProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleProcessor.class);

    /**
     * Filler.
     */
    @Inject
    private Filler filler;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Shows the user template page.
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws IOException io exception
     */
    @RequestProcessing(value = "/*.html", method = HTTPRequestMethod.GET)
    public void showPage(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final String requestURI = request.getRequestURI();
        String templateName = StringUtils.substringAfterLast(requestURI, "/");

        templateName = StringUtils.substringBefore(templateName, ".") + ".ftl";
        LOGGER.log(Level.DEBUG, "Shows page[requestURI={0}, templateName={1}]", requestURI, templateName);

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);

        context.setRenderer(renderer);
        renderer.setTemplateName(templateName);

        final Map<String, Object> dataModel = renderer.getDataModel();

        final Template template = Templates.getTemplate((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), templateName);

        if (null == template) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return;
            } catch (final IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }

        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));

            dataModel.putAll(langs);
            final JSONObject preference = preferenceQueryService.getPreference();

            filler.fillBlogHeader(request, response, dataModel, preference);
            filler.fillUserTemplate(request, template, dataModel, preference);
            filler.fillBlogFooter(request, dataModel, preference);
            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), dataModel);

            statisticMgmtService.incBlogViewCount(request, response);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }
}
