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

import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.http.renderer.TextHtmlRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * User template processor.
 *
 * <p>
 * User can add a template (for example "links.ftl") then visits the page ("links.html").
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Apr 18, 2020
 * @since 0.4.5
 */
@Singleton
public class UserTemplateProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArticleProcessor.class);

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Shows the user template page.
     *
     * @param context the specified context
     */
    public void showPage(final RequestContext context) {
        final String requestURI = context.requestURI();
        final String templateName = context.pathVar("name") + ".ftl";
        if ("/CHANGE_LOGS.html".equals(requestURI)) {
            try {
                final TextHtmlRenderer renderer = new TextHtmlRenderer();
                context.setRenderer(renderer);
                try (final InputStream resourceAsStream = UserTemplateProcessor.class.getResourceAsStream("/CHANGE_LOGS.md")) {
                    final String content = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
                    renderer.setContent(Markdowns.toHTML(content));
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Renders CHANGE_LOGS failed", e);
            }
            return;
        }

        LOGGER.log(Level.DEBUG, "Shows page [requestURI={}, templateName={}]", requestURI, templateName);

        final Request request = context.getRequest();
        final Response response = context.getResponse();
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, templateName);

        final Map<String, Object> dataModel = renderer.getDataModel();
        final Template template = Skins.getSkinTemplate(context, templateName);
        if (null == template) {
            context.sendError(404);
            return;
        }

        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));
            dataModel.putAll(langs);
            final JSONObject preference = optionQueryService.getPreference();
            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
            dataModelService.fillUserTemplate(context, template, dataModel, preference);
            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) context.attr(Keys.TEMPLATE_DIR_NAME), dataModel);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            context.sendError(404);
        }
    }
}
