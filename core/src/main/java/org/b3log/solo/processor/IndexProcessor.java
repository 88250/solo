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


import freemarker.template.Template;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Requests;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.processor.renderer.ConsoleRenderer;
import org.b3log.solo.processor.renderer.FrontRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;


/**
 * Index processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @version 1.1.1.2, Apr 1, 2013
 * @since 0.3.1
 */
@RequestProcessor
public final class IndexProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IndexProcessor.class.getName());

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
     * Shows index with the specified context.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     */
    @RequestProcessing(value = { "/\\d*", ""}, uriPatternsMode = URIPatternMode.REGEX, method = HTTPRequestMethod.GET)
    public void showIndex(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final AbstractFreeMarkerRenderer renderer = new FrontRenderer();

        context.setRenderer(renderer);

        renderer.setTemplateName("index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final String requestURI = request.getRequestURI();

        try {
            final int currentPageNum = getCurrentPageNum(requestURI);
            final JSONObject preference = preferenceQueryService.getPreference();

            Skins.fillSkinLangs(preference.optString(Preference.LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME),
                dataModel);

            final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

            request.setAttribute(PageCaches.CACHED_OID, "No id");
            request.setAttribute(PageCaches.CACHED_TITLE,
                langs.get(PageTypes.INDEX.getLangeLabel()) + "  [" + langs.get("pageNumLabel") + "=" + currentPageNum + "]");
            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.INDEX.getLangeLabel()));
            request.setAttribute(PageCaches.CACHED_LINK, requestURI);

            filler.fillIndexArticles(request, dataModel, currentPageNum, preference);
            dataModel.put(Keys.PAGE_TYPE, PageTypes.INDEX);

            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);

            dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);

            dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
            final Integer pageCount = (Integer) dataModel.get(Pagination.PAGINATION_PAGE_COUNT);

            if (pageCount == currentPageNum + 1) { // The next page is the last page
                dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
            } else {
                dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
            }

            dataModel.put(Common.PATH, "");
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Shows kill browser page with the specified context.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/kill-browser.html", method = HTTPRequestMethod.GET)
    public void showKillBrowser(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final AbstractFreeMarkerRenderer renderer = new KillBrowserRenderer();

        context.setRenderer(renderer);

        final Map<String, Object> dataModel = renderer.getDataModel();

        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));

            dataModel.putAll(langs);
            final JSONObject preference = preferenceQueryService.getPreference();

            filler.fillBlogFooter(dataModel, preference);
            Keys.fillServer(dataModel);
            Keys.fillRuntime(dataModel);
            filler.fillMinified(dataModel);
            
            dataModel.put(Keys.PAGE_TYPE, PageTypes.KILL_BROWSER);

            request.setAttribute(PageCaches.CACHED_OID, "No id");
            request.setAttribute(PageCaches.CACHED_TITLE, "Kill Browser Page");
            request.setAttribute(PageCaches.CACHED_TYPE, langs.get(PageTypes.KILL_BROWSER.getLangeLabel()));
            request.setAttribute(PageCaches.CACHED_LINK, request.getRequestURI());
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Show register page.
     *
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     */
    @RequestProcessing(value = "/register", method = HTTPRequestMethod.GET)
    public void register(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();

        context.setRenderer(renderer);

        renderer.setTemplateName("register.ftl");

        final Map<String, Object> dataModel = renderer.getDataModel();

        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));

            dataModel.putAll(langs);

            final JSONObject preference = preferenceQueryService.getPreference();

            filler.fillBlogFooter(dataModel, preference);
            filler.fillMinified(dataModel);
            Keys.fillServer(dataModel);

        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI) {
        final String pageNumString = StringUtils.substringAfter(requestURI, "/");

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Kill browser (kill-browser.ftl) HTTP response renderer.
     * 
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Sep 18, 2011
     * @see 0.3.1
     */
    private static final class KillBrowserRenderer extends AbstractFreeMarkerRenderer {

        /**
         * Logger.
         */
        private static final Logger LOGGER = Logger.getLogger(KillBrowserRenderer.class.getName());

        @Override
        public void render(final HTTPRequestContext context) {
            final HttpServletResponse response = context.getResponse();

            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            try {
                final Template template = ConsoleRenderer.TEMPLATE_CFG.getTemplate("kill-browser.ftl");

                final PrintWriter writer = response.getWriter();

                final StringWriter stringWriter = new StringWriter();

                template.setOutputEncoding("UTF-8");
                template.process(getDataModel(), stringWriter);

                final String pageContent = stringWriter.toString();

                context.getRequest().setAttribute(PageCaches.CACHED_CONTENT, pageContent);

                writer.write(pageContent);
                writer.flush();
                writer.close();
            } catch (final Exception e) {
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } catch (final IOException ex) {
                    LOGGER.log(Level.SEVERE, "Can not sned error 500!", ex);
                }
            }
        }

        @Override
        protected void afterRender(final HTTPRequestContext context) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected void beforeRender(final HTTPRequestContext context) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
