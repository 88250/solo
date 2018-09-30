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
package org.b3log.solo.processor.console;

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Requests;
import org.b3log.solo.service.PluginMgmtService;
import org.b3log.solo.service.PluginQueryService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Plugin console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.1.0.3, Sep 25, 2018
 * @since 0.4.0
 */
@RequestProcessor
@Before(adviceClass = ConsoleAdminAuthAdvice.class)
public class PluginConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginConsole.class);

    /**
     * Plugin query service.
     */
    @Inject
    private PluginQueryService pluginQueryService;

    /**
     * Plugin management service.
     */
    @Inject
    private PluginMgmtService pluginMgmtService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Sets a plugin's status with the specified plugin id, status.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context           the specified http request context
     * @param requestJSONObject the specified requeset json object
     * @throws Exception exception
     */
    @RequestProcessing(value = "/console/plugin/status/", method = HTTPRequestMethod.PUT)
    public void setPluginStatus(final HTTPRequestContext context, final JSONObject requestJSONObject) throws Exception {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final String pluginId = requestJSONObject.getString(Keys.OBJECT_ID);
        final String status = requestJSONObject.getString(Plugin.PLUGIN_STATUS);

        final JSONObject result = pluginMgmtService.setPluginStatus(pluginId, status);

        renderer.setJSONObject(result);
    }

    /**
     * Gets plugins by the specified request.
     * <p>
     * The request URI contains the pagination arguments. For example, the
     * request URI is /console/plugins/1/10/20, means the current page is 1, the
     * page size is 10, and the window size is 20.
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "plugins": [{
     *         "name": "",
     *         "version": "",
     *         "author": "",
     *         "status": "", // Enumeration name of {@link org.b3log.latke.plugin.PluginStatus}
     *      }, ....]
     * }
     * </pre>
     * </p>
     *
     * @param request  the specified http servlet request
     * @param response the specified http servlet response
     * @param context  the specified http request context
     * @throws Exception exception
     * @see Requests#PAGINATION_PATH_PATTERN
     */
    @RequestProcessing(value = "/console/plugins/*/*/*"/* Requests.PAGINATION_PATH_PATTERN */,
            method = HTTPRequestMethod.GET)
    public void getPlugins(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
            throws Exception {

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = request.getRequestURI();
            final String path = requestURI.substring((Latkes.getContextPath() + "/console/plugins/").length());

            final JSONObject requestJSONObject = Requests.buildPaginationRequest(path);

            final JSONObject result = pluginQueryService.getPlugins(requestJSONObject);

            renderer.setJSONObject(result);

            result.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * get the info of the specified pluginoId,just fot the plugin-setting.
     *
     * @param context           the specified http request context
     * @param requestJSONObject the specified request json object
     * @param renderer          the specified {@link ConsoleRenderer}
     */
    @RequestProcessing(value = "/console/plugin/toSetting", method = HTTPRequestMethod.POST)
    public void toSetting(final HTTPRequestContext context, final JSONObject requestJSONObject, final ConsoleRenderer renderer) {
        context.setRenderer(renderer);

        try {
            final String pluginId = requestJSONObject.getString(Keys.OBJECT_ID);
            final String setting = pluginQueryService.getPluginSetting(pluginId);

            renderer.setTemplateName("admin-plugin-setting.ftl");
            final Map<String, Object> dataModel = renderer.getDataModel();

            Keys.fillRuntime(dataModel);

            dataModel.put(Plugin.PLUGIN_SETTING, setting);
            dataModel.put(Keys.OBJECT_ID, pluginId);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            final JSONRenderer jsonRenderer = new JSONRenderer();
            jsonRenderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * update the setting of the plugin.
     *
     * @param context           the specified http request context
     * @param requestJSONObject the specified request json object
     * @param renderer          the specified {@link ConsoleRenderer}
     */
    @RequestProcessing(value = "/console/plugin/updateSetting", method = HTTPRequestMethod.POST)
    public void updateSetting(final HTTPRequestContext context, final JSONObject requestJSONObject, final JSONRenderer renderer) {
        context.setRenderer(renderer);

        final String pluginoId = requestJSONObject.optString(Keys.OBJECT_ID);
        final String settings = requestJSONObject.optString(Plugin.PLUGIN_SETTING);

        final JSONObject ret = pluginMgmtService.updatePluginSetting(pluginoId, settings);

        renderer.setJSONObject(ret);
    }
}
