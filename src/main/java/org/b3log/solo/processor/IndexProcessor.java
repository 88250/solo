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
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Requests;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Skin;
import org.b3log.solo.processor.console.ConsoleRenderer;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Index processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @version 1.2.4.8, Sep 26, 2018
 * @since 0.3.1
 */
@RequestProcessor
public class IndexProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IndexProcessor.class);

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
     * Shows index with the specified context.
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = {"/\\d*", ""}, uriPatternsMode = URIPatternMode.REGEX, method = HTTPRequestMethod.GET)
    public void showIndex(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
        context.setRenderer(renderer);
        renderer.setTemplateName("index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final String requestURI = request.getRequestURI();

        try {
            final int currentPageNum = getCurrentPageNum(requestURI);
            final JSONObject preference = preferenceQueryService.getPreference();

            // https://github.com/b3log/solo/issues/12060
            String specifiedSkin = Skins.getSkinDirName(request);
            if (null != specifiedSkin) {
                if ("default".equals(specifiedSkin)) {
                    specifiedSkin = preference.optString(Option.ID_C_SKIN_DIR_NAME);

                    final Cookie cookie = new Cookie(Skin.SKIN, null);
                    cookie.setMaxAge(60 * 60); // 1 hour
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            } else {
                specifiedSkin = preference.optString(Option.ID_C_SKIN_DIR_NAME);
            }

            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), dataModel);

            dataModelService.fillIndexArticles(request, dataModel, currentPageNum, preference);
            dataModelService.fillCommon(request, response, dataModel, preference);

            dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final int previousPageNum = currentPageNum > 1 ? currentPageNum - 1 : 0;
            dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, previousPageNum);

            final Integer pageCount = (Integer) dataModel.get(Pagination.PAGINATION_PAGE_COUNT);
            final int nextPageNum = currentPageNum + 1 > pageCount ? pageCount : currentPageNum + 1;
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, nextPageNum);

            dataModel.put(Common.PATH, "");

            statisticMgmtService.incBlogViewCount(request, response);

            // https://github.com/b3log/solo/issues/12060
            if (!preference.optString(Skin.SKIN_DIR_NAME).equals(specifiedSkin) && !Requests.mobileRequest(request)) {
                final Cookie cookie = new Cookie(Skin.SKIN, specifiedSkin);
                cookie.setMaxAge(60 * 60); // 1 hour
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Shows kill browser page with the specified context.
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = "/kill-browser", method = HTTPRequestMethod.GET)
    public void showKillBrowser(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();
        context.setRenderer(renderer);
        renderer.setTemplateName("kill-browser.ftl");

        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));
            dataModel.putAll(langs);
            final JSONObject preference = preferenceQueryService.getPreference();
            dataModelService.fillCommon(request, response, dataModel, preference);
            Keys.fillServer(dataModel);
            Keys.fillRuntime(dataModel);
            dataModelService.fillMinified(dataModel);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Show register page.
     *
     * @param context  the specified context
     * @param request  the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = "/register", method = HTTPRequestMethod.GET)
    public void register(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();
        context.setRenderer(renderer);
        renderer.setTemplateName("register.ftl");

        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
            dataModel.putAll(langs);
            final JSONObject preference = preferenceQueryService.getPreference();
            dataModelService.fillCommon(request, response, dataModel, preference);
            dataModelService.fillMinified(dataModel);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Online visitor count refresher.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/onlineVisitorRefresh", method = HTTPRequestMethod.GET)
    public void onlineVisitorCountRefresher(final HTTPRequestContext context) {
        context.setRenderer(new DoNothingRenderer());

        StatisticMgmtService.removeExpiredOnlineVisitor();
    }

    /**
     * Gets the request page number from the specified request URI.
     *
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI) {
        final String pageNumString = StringUtils.substringAfterLast(requestURI, "/");

        return Requests.getCurrentPageNum(pageNumString);
    }
}
