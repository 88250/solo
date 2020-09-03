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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.service.PluginMgmtService;
import org.b3log.solo.service.PluginQueryService;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.Statics;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

import java.util.Map;

/**
 * Plugin console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @version 2.0.0.2, Jun 19, 2020
 * @since 0.4.0
 */
@Singleton
public class PluginConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PluginConsole.class);

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
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void setPluginStatus(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject requestJSONObject = context.requestJSON();
        final String pluginId = requestJSONObject.getString(Keys.OBJECT_ID);
        final String status = requestJSONObject.getString(Plugin.PLUGIN_STATUS);
        final JSONObject result = pluginMgmtService.setPluginStatus(pluginId, status);
        renderer.setJSONObject(result);
        Statics.clear();
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
     *     "code": int,
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
     * @param context the specified request context
     * @throws Exception exception
     */
    public void getPlugins(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        try {
            final String requestURI = context.requestURI();
            final String path = requestURI.substring((Latkes.getContextPath() + "/console/plugins/").length());
            final JSONObject requestJSONObject = Solos.buildPaginationRequest(path);
            final JSONObject result = pluginQueryService.getPlugins(requestJSONObject);
            renderer.setJSONObject(result);
            result.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * get the info of the specified pluginoId,just fot the plugin-setting.
     *
     * @param context the specified request context
     */
    public void toSetting(final RequestContext context) {
        final ConsoleRenderer renderer = new ConsoleRenderer(context, "admin-plugin-setting.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final String pluginId = requestJSONObject.getString(Keys.OBJECT_ID);
            final String setting = pluginQueryService.getPluginSetting(pluginId);
            Keys.fillRuntime(dataModel);
            dataModel.put(Plugin.PLUGIN_SETTING, setting);
            dataModel.put(Keys.OBJECT_ID, pluginId);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            final JsonRenderer JsonRenderer = new JsonRenderer();
            JsonRenderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * update the setting of the plugin.
     *
     * @param context the specified request context
     */
    public void updateSetting(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject requestJSONObject = context.requestJSON();
        final String pluginoId = requestJSONObject.optString(Keys.OBJECT_ID);
        final String settings = requestJSONObject.optString(Plugin.PLUGIN_SETTING);
        final JSONObject ret = pluginMgmtService.updatePluginSetting(pluginoId, settings);
        renderer.setJSONObject(ret);
    }
}
