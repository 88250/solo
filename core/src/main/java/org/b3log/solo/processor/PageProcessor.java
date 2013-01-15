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


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.processor.renderer.FrontRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.CommentQueryService;
import org.b3log.solo.service.PageQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;


/**
 * Page processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.2, Apr 29, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class PageProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageProcessor.class.getName());

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Comment query service.
     */
    private CommentQueryService commentQueryService = CommentQueryService.getInstance();

    /**
     * Page query service.
     */
    private PageQueryService pageQueryService = PageQueryService.getInstance();

    /**
     * Shows page with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/page", method = HTTPRequestMethod.GET)
    public void showPage(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();

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

            Skins.fillSkinLangs(preference.getString(Preference.LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME),
                dataModel);

            final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.PAGE.getLangeLabel()));

            // See PermalinkFiler#dispatchToArticleOrPageProcessor()
            final JSONObject page = (JSONObject) request.getAttribute(Page.PAGE);

            if (null == page) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String pageId = page.getString(Keys.OBJECT_ID);

            request.setAttribute(PageCaches.CACHED_OID, pageId);
            request.setAttribute(PageCaches.CACHED_TITLE, page.getString(Page.PAGE_TITLE));
            request.setAttribute(PageCaches.CACHED_LINK, page.getString(Page.PAGE_PERMALINK));

            page.put(Common.COMMENTABLE, page.getBoolean(Page.PAGE_COMMENTABLE));
            page.put(Common.PERMALINK, page.getString(Page.PAGE_PERMALINK));
            dataModel.put(Page.PAGE, page);
            final List<JSONObject> comments = commentQueryService.getComments(pageId);

            dataModel.put(Page.PAGE_COMMENTS_REF, comments);

            // Markdown
            if ("CodeMirror-Markdown".equals(page.optString(Page.PAGE_EDITOR_TYPE))) {
                Stopwatchs.start("Markdown Page[id=" + page.optString(Keys.OBJECT_ID) + "]");

                final String content = page.optString(Page.PAGE_CONTENT);

                page.put(Page.PAGE_CONTENT, Markdowns.toHTML(content));

                Stopwatchs.end();
            }

            dataModel.put(Keys.PAGE_TYPE, PageTypes.PAGE);
            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);
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
