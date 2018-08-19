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
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Page;
import org.b3log.solo.processor.renderer.SkinRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.CommentQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.util.Emotions;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Page processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.7, Jun 22, 2018
 * @since 0.3.1
 */
@RequestProcessor
public class PageProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageProcessor.class);

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

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
     * Comment query service.
     */
    @Inject
    private CommentQueryService commentQueryService;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Shows page with the specified context.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/page", method = HTTPRequestMethod.GET)
    public void showPage(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context.getRequest());
        context.setRenderer(renderer);
        renderer.setTemplateName("page.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Skins.fillLangs(preference.getString(Option.ID_C_LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), dataModel);

            // See PermalinkFilter#dispatchToArticleOrPageProcessor()
            final JSONObject page = (JSONObject) request.getAttribute(Page.PAGE);

            if (null == page) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String pageId = page.getString(Keys.OBJECT_ID);

            page.put(Common.COMMENTABLE, preference.getBoolean(Option.ID_C_COMMENTABLE) && page.getBoolean(Page.PAGE_COMMENTABLE));
            page.put(Common.PERMALINK, page.getString(Page.PAGE_PERMALINK));
            dataModel.put(Page.PAGE, page);
            final List<JSONObject> comments = commentQueryService.getComments(pageId);

            dataModel.put(Page.PAGE_COMMENTS_REF, comments);

            // Markdown
            if ("CodeMirror-Markdown".equals(page.optString(Page.PAGE_EDITOR_TYPE))) {
                Stopwatchs.start("Markdown Page[id=" + page.optString(Keys.OBJECT_ID) + "]");

                String content = page.optString(Page.PAGE_CONTENT);
                content = Emotions.convert(content);
                content = Markdowns.toHTML(content);
                page.put(Page.PAGE_CONTENT, content);

                Stopwatchs.end();
            }

            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, response, dataModel, preference);
            filler.fillBlogFooter(request, dataModel, preference);

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
