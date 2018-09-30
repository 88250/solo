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
package org.b3log.solo.service;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.plugin.PluginStatus;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.repository.PluginRepository;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Plugin management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Aug 27, 2018
 * @since 0.4.0
 */
@Service
public class PluginMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginMgmtService.class);

    /**
     * Plugin repository.
     */
    @Inject
    private PluginRepository pluginRepository;

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
     * Plugin manager.
     */
    @Inject
    private PluginManager pluginManager;

    /**
     * Updates datastore plugin descriptions with the specified plugins.
     *
     * @param plugins the specified plugins
     * @throws Exception exception
     */
    public void refresh(final List<AbstractPlugin> plugins) throws Exception {
        if (!initService.isInited()) {
            return;
        }

        final List<JSONObject> persistedPlugins = pluginRepository.getList(new Query());
        try {
            // Reads plugin status from datastore and clear plugin datastore
            for (final JSONObject oldPluginDesc : persistedPlugins) {
                final String descId = oldPluginDesc.getString(Keys.OBJECT_ID);
                final AbstractPlugin plugin = get(plugins, descId);

                pluginRepository.remove(descId);

                if (null != plugin) {
                    final String status = oldPluginDesc.getString(Plugin.PLUGIN_STATUS);
                    final String setting = oldPluginDesc.optString(Plugin.PLUGIN_SETTING);

                    plugin.setStatus(PluginStatus.valueOf(status));
                    try {
                        if (StringUtils.isNotBlank(setting)) {
                            plugin.setSetting(new JSONObject(setting));
                        }
                    } catch (final JSONException e) {
                        LOGGER.log(Level.WARN, "the formatter of the old config failed to convert to json", e);
                    }
                }
            }

            // Adds these plugins into datastore
            for (final AbstractPlugin plugin : plugins) {
                final JSONObject pluginDesc = plugin.toJSONObject();

                pluginRepository.add(pluginDesc);

                LOGGER.log(Level.TRACE, "Refreshed plugin[{0}]", pluginDesc);
            }

        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Refresh plugins failed", e);
        }
    }

    /**
     * Gets a plugin in the specified plugins with the specified id.
     *
     * @param plugins the specified plugins
     * @param id      the specified id, must NOT be {@code null}
     * @return a plugin, returns {@code null} if not found
     */
    private AbstractPlugin get(final List<AbstractPlugin> plugins, final String id) {
        if (null == id) {
            throw new IllegalArgumentException("id must not be null");
        }

        for (final AbstractPlugin plugin : plugins) {
            if (id.equals(plugin.getId())) {
                return plugin;
            }
        }

        return null;
    }

    /**
     * Sets a plugin's status with the specified plugin id, status.
     *
     * @param pluginId the specified plugin id
     * @param status   the specified status, see {@link PluginStatus}
     * @return for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     */
    public JSONObject setPluginStatus(final String pluginId, final String status) {
        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

        final List<AbstractPlugin> plugins = pluginManager.getPlugins();

        final JSONObject ret = new JSONObject();

        for (final AbstractPlugin plugin : plugins) {
            if (plugin.getId().equals(pluginId)) {
                final Transaction transaction = pluginRepository.beginTransaction();

                try {
                    plugin.setStatus(PluginStatus.valueOf(status));
                    pluginRepository.update(pluginId, plugin.toJSONObject());

                    transaction.commit();

                    plugin.changeStatus();

                    ret.put(Keys.STATUS_CODE, true);
                    ret.put(Keys.MSG, langs.get("setSuccLabel"));

                    return ret;
                } catch (final Exception e) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    LOGGER.log(Level.ERROR, "Set plugin status error", e);

                    ret.put(Keys.STATUS_CODE, false);
                    ret.put(Keys.MSG, langs.get("setFailLabel"));

                    return ret;
                }
            }
        }

        ret.put(Keys.STATUS_CODE, false);
        ret.put(Keys.MSG, langs.get("refreshAndRetryLabel"));

        return ret;
    }

    /**
     * updatePluginSetting.
     *
     * @param pluginId the specified pluginoId
     * @param setting  the specified setting
     * @return the ret json
     */
    public JSONObject updatePluginSetting(final String pluginId, final String setting) {
        final JSONObject ret = new JSONObject();

        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
        final List<AbstractPlugin> plugins = pluginManager.getPlugins();
        for (final AbstractPlugin plugin : plugins) {
            if (plugin.getId().equals(pluginId)) {
                final Transaction transaction = pluginRepository.beginTransaction();

                try {
                    final JSONObject pluginJson = plugin.toJSONObject();
                    pluginJson.put(Plugin.PLUGIN_SETTING, setting);
                    pluginRepository.update(pluginId, pluginJson);

                    transaction.commit();

                    ret.put(Keys.STATUS_CODE, true);
                    ret.put(Keys.MSG, langs.get("setSuccLabel"));

                    return ret;
                } catch (final Exception e) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                    LOGGER.log(Level.ERROR, "Set plugin status error", e);
                    ret.put(Keys.STATUS_CODE, false);
                    ret.put(Keys.MSG, langs.get("setFailLabel"));

                    return ret;
                }
            }
        }

        ret.put(Keys.STATUS_CODE, false);
        ret.put(Keys.MSG, langs.get("refreshAndRetryLabel"));

        return ret;

    }

    /**
     * Sets the plugin repository with the specified plugin repository.
     *
     * @param pluginRepository the specified plugin repository
     */
    public void setPluginRepository(final PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    /**
     * Sets the language service with the specified language service.
     *
     * @param langPropsService the specified language service
     */
    public void setLangPropsService(final LangPropsService langPropsService) {
        this.langPropsService = langPropsService;
    }
}
