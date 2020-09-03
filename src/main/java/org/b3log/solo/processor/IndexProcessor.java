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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.io.IOUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.http.renderer.BinaryRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.URLs;
import org.b3log.solo.Server;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.InitService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Index processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/DASHU">DASHU</a>
 * @author <a href="http://vanessa.b3log.org">Vanessa</a>
 * @version 2.0.0.4, Jun 25, 2020
 * @since 0.3.1
 */
@Singleton
public class IndexProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(IndexProcessor.class);

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Initialization service.
     */
    @Inject
    private InitService initService;

    /**
     * Shows index with the specified context.
     *
     * @param context the specified context
     */
    public void showIndex(final RequestContext context) {
        final Request request = context.getRequest();
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            final int currentPageNum = Paginator.getPage(request);
            final JSONObject preference = optionQueryService.getPreference();
            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) context.attr(Keys.TEMPLATE_DIR_NAME), dataModel);

            dataModelService.fillIndexArticles(context, dataModel, currentPageNum, preference);
            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);

            dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final int previousPageNum = currentPageNum > 1 ? currentPageNum - 1 : 0;
            dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, previousPageNum);
            final Integer pageCount = (Integer) dataModel.get(Pagination.PAGINATION_PAGE_COUNT);
            final int nextPageNum = currentPageNum + 1 > pageCount ? pageCount : currentPageNum + 1;
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, nextPageNum);
            dataModel.put(Common.PATH, "");
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            context.sendError(404);
        }
    }

    /**
     * Favicon bytes cache. &lt;"/favicon.ico", bytes&gt;
     */
    private static final Cache<String, Object> FAVICON_CACHE = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * Shows favicon with the specified context.
     *
     * @param context the specified context
     */
    public void showFavicon(final RequestContext context) {
        final BinaryRenderer binaryRenderer = new BinaryRenderer("image/x-icon");
        context.setRenderer(binaryRenderer);
        final String key = "/favicon.ico";
        byte[] bytes = (byte[]) FAVICON_CACHE.getIfPresent(key);
        if (null != bytes) {
            binaryRenderer.setData(bytes);
            return;
        }

        final JSONObject preference = optionQueryService.getPreference();
        String faviconURL;
        if (null == preference) {
            faviconURL = Option.DefaultPreference.DEFAULT_FAVICON_URL;
        } else {
            faviconURL = preference.optString(Option.ID_C_FAVICON_URL);
        }

        try {
            final HttpResponse response = HttpRequest.get(faviconURL).header("User-Agent", Solos.USER_AGENT).connectionTimeout(3000).timeout(7000).send();
            if (200 == response.statusCode()) {
                bytes = response.bodyBytes();
            } else {
                throw new Exception();
            }
            binaryRenderer.setData(bytes);
        } catch (final Exception e) {
            try (final InputStream resourceAsStream = IndexProcessor.class.getResourceAsStream("/images/favicon.ico")) {
                bytes = IOUtils.toByteArray(resourceAsStream);
                binaryRenderer.setData(bytes);
            } catch (final Exception ex) {
                LOGGER.log(Level.ERROR, "Loads default favicon.ico failed", e);
                context.sendError(500);
                return;
            }
        }

        FAVICON_CACHE.put(key, bytes);
    }

    /**
     * Shows start page.
     *
     * @param context the specified context
     */
    public void showStart(final RequestContext context) {
        if (initService.isInited() && null != Solos.getCurrentUser(context)) {
            context.sendRedirect(Latkes.getServePath());
            return;
        }

        String referer = context.param("referer");
        if (StringUtils.isBlank(referer)) {
            referer = context.header("referer");
        }
        if (StringUtils.isBlank(referer) || !isInternalLinks(referer)) {
            referer = Latkes.getServePath();
        }

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "common-template/start.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final Request request = context.getRequest();
        final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));
        dataModel.putAll(langs);
        dataModel.put(Common.VERSION, Server.VERSION);
        dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
        dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        dataModel.put(Common.REFERER, URLs.encode(referer));
        Keys.fillRuntime(dataModel);
        dataModelService.fillFaviconURL(dataModel, optionQueryService.getPreference());
        dataModelService.fillUsite(dataModel);
        Solos.addGoogleNoIndex(context);
    }

    /**
     * Logout.
     *
     * @param context the specified context
     */
    public void logout(final RequestContext context) {
        final Request request = context.getRequest();
        Solos.logout(request, context.getResponse());
        Solos.addGoogleNoIndex(context);
        context.sendRedirect(Latkes.getServePath());
    }

    /**
     * Shows kill browser page with the specified context.
     *
     * @param context the specified context
     */
    public void showKillBrowser(final RequestContext context) {
        final Request request = context.getRequest();
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "common-template/kill-browser.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));
            dataModel.putAll(langs);
            final JSONObject preference = optionQueryService.getPreference();
            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
            Keys.fillServer(dataModel);
            Keys.fillRuntime(dataModel);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            context.sendError(404);
        }
    }

    /**
     * Preventing unvalidated redirects and forwards. See more at:
     * <a href="https://www.owasp.org/index.php/Unvalidated_Redirects_and_Forwards_Cheat_Sheet">https://www.owasp.org/index.php/
     * Unvalidated_Redirects_and_Forwards_Cheat_Sheet</a>.
     *
     * @return whether the destinationURL is an internal link
     */
    private boolean isInternalLinks(final String destinationURL) {
        return destinationURL.startsWith(Latkes.getServePath());
    }
}
