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
package org.b3log.solo.service;


import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.plugin.PluginStatus;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.repository.PluginRepository;
import org.b3log.solo.repository.impl.PluginRepositoryImpl;
import org.json.JSONObject;


/**
 * Plugin management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2011
 * @since 0.4.0
 */
public final class PluginMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginMgmtService.class.getName());

    /**
     * Plugin repository.
     */
    private PluginRepository pluginRepository = PluginRepositoryImpl.getInstance();

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Sets a plugin's status with the specified plugin id, status.
     * 
     * @param pluginId the specified plugin id
     * @param status the specified status, see {@link PluginStatus}
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

        final PluginManager pluginManager = PluginManager.getInstance();
        final List<AbstractPlugin> plugins = pluginManager.getPlugins();

        final JSONObject ret = new JSONObject();

        for (final AbstractPlugin plugin : plugins) {
            if (plugin.getId().equals(pluginId)) {
                final Transaction transaction = pluginRepository.beginTransaction();

                try {
                    plugin.setStatus(PluginStatus.valueOf(status));

                    pluginRepository.update(pluginId, plugin.toJSONObject());

                    transaction.commit();

                    pluginManager.update(plugin);

                    ret.put(Keys.STATUS_CODE, true);
                    ret.put(Keys.MSG, langs.get("setSuccLabel"));

                    return ret;
                } catch (final Exception e) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    LOGGER.log(Level.SEVERE, "Set plugin status error", e);

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
     * @param setting the specified setting
     * @return the ret json
     */
    public JSONObject updatePluginSetting(final String pluginId, final String setting) {

        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

        final PluginManager pluginManager = PluginManager.getInstance();
        final List<AbstractPlugin> plugins = pluginManager.getPlugins();

        final JSONObject ret = new JSONObject();

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
                    LOGGER.log(Level.SEVERE, "Set plugin status error", e);
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
     * Gets the {@link PluginMgmtService} singleton.
     *
     * @return the singleton
     */
    public static PluginMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private PluginMgmtService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 27, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PluginMgmtService SINGLETON = new PluginMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }

}
