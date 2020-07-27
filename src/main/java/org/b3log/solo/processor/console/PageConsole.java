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
package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Page;
import org.b3log.solo.service.PageMgmtService;
import org.b3log.solo.service.PageQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

import java.util.List;

/**
 * Page console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.0, Jun 5, 2020
 * @since 0.4.0
 */
@Singleton
public class PageConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PageConsole.class);

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Page query service.
     */
    @Inject
    private PageQueryService pageQueryService;

    /**
     * Page management service.
     */
    @Inject
    private PageMgmtService pageMgmtService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Updates a page by the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "page": {
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageOrder": int,
     *         "pagePermalink": "",
     *         "pageOpenTarget": "",
     *         "pageIcon": ""
     *     }
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void updatePage(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSON = context.requestJSON();
            pageMgmtService.updatePage(requestJSON);

            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Removes a page by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void removePage(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        try {
            final String pageId = context.pathVar("id");
            pageMgmtService.removePage(pageId);

            jsonObject.put(Keys.CODE, StatusCodes.SUCC);
            jsonObject.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));

        }
    }

    /**
     * Adds a page with the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "page": {
     *         "pageTitle": "",
     *         "pagePermalink": "" // optional
     *         "pageOpenTarget": "",
     *         "pageIcon": ""
     *     }
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "oId": "", // Generated page id
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void addPage(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSON = context.requestJSON();
            final String pageId = pageMgmtService.addPage(requestJSON);

            ret.put(Keys.OBJECT_ID, pageId);
            ret.put(Keys.MSG, langPropsService.get("addSuccLabel"));
            ret.put(Keys.CODE, StatusCodes.SUCC);
            renderer.setJSONObject(ret);
        } catch (final ServiceException e) { // May be permalink check exception
            LOGGER.log(Level.WARN, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Changes a page order by the specified page id and direction.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "oId": "",
     *     "direction": "" // "up"/"down"
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void changeOrder(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final String linkId = requestJSONObject.getString(Keys.OBJECT_ID);
            final String direction = requestJSONObject.getString(Common.DIRECTION);
            pageMgmtService.changeOrder(linkId, direction);
            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            renderer.setJSONObject(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Gets a page by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "page": {
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageOrder": int,
     *         "pagePermalink": "",
     *         "pageIcon": ""
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getPage(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final String pageId = context.pathVar("id");
            final JSONObject result = pageQueryService.getPage(pageId);
            if (null == result) {
                renderer.setJSONObject(new JSONObject().put(Keys.CODE, StatusCodes.ERR));
                return;
            }

            renderer.setJSONObject(result);
            result.put(Keys.CODE, StatusCodes.SUCC);
            result.put(Keys.MSG, langPropsService.get("getSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Gets pages by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "pages": [{
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageOrder": int,
     *         "pagePermalink": "",
     *         .{@link PageMgmtService...}
     *      }, ....]
     *     "code": 0
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getPages(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = context.requestURI();
            final String path = requestURI.substring((Latkes.getContextPath() + "/console/pages/").length());
            final JSONObject requestJSONObject = Solos.buildPaginationRequest(path);
            final JSONObject result = pageQueryService.getPages(requestJSONObject);
            final List<JSONObject> pages = (List<JSONObject>) result.opt(Page.PAGES);
            for (final JSONObject page : pages) {
                String title = page.optString(Page.PAGE_TITLE);
                title = StringEscapeUtils.escapeXml(title);
                page.put(Page.PAGE_TITLE, title);
            }

            result.put(Keys.CODE, StatusCodes.SUCC);
            renderer.setJSONObject(result);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }
}
