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
package org.b3log.solo.util;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginStatus;
import org.b3log.latke.repository.Query;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.repository.impl.PluginRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Plugin utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Sep 11, 2011
 */
public final class Plugins {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Plugins.class.getName());

    /**
     * Plugin repository.
     */
    private static final PluginRepositoryImpl PLUGIN_REPOS = PluginRepositoryImpl.getInstance();

    /**
     * Updates datastore plugin descriptions with the specified plugins.
     * 
     * @param plugins the specified plugins
     * @throws Exception exception 
     */
    public static void refresh(final List<AbstractPlugin> plugins)
        throws Exception {
        final JSONObject result = PLUGIN_REPOS.get(new Query());
        final JSONArray pluginArray = result.getJSONArray(Keys.RESULTS);
        final List<JSONObject> persistedPlugins = CollectionUtils.jsonArrayToList(pluginArray);

        // Disables plugin repository cache to avoid remove all cache
        PLUGIN_REPOS.setCacheEnabled(false);

        try {
            // Reads plugin status from datastore and clear plugin datastore
            for (final JSONObject oldPluginDesc : persistedPlugins) {
                final String descId = oldPluginDesc.getString(Keys.OBJECT_ID);
                final AbstractPlugin plugin = get(plugins, descId);

                PLUGIN_REPOS.remove(descId);

                if (null != plugin) {
                    final String status = oldPluginDesc.getString(Plugin.PLUGIN_STATUS);
                    final String setting = oldPluginDesc.optString(Plugin.PLUGIN_SETTING);
                    
                    plugin.setStatus(PluginStatus.valueOf(status));
                    try {
                        if (StringUtils.isNotBlank(setting)) {
                            plugin.setSetting(new JSONObject(setting));
                        }
                    } catch (final JSONException e) {
                        LOGGER.log(Level.WARNING, "the formatter of the old config failed to convert to json", e);
                    }
                }
            }

            // Adds these plugins into datastore
            for (final AbstractPlugin plugin : plugins) {
                final JSONObject pluginDesc = plugin.toJSONObject();

                PLUGIN_REPOS.add(pluginDesc);

                LOGGER.log(Level.FINEST, "Refreshed plugin[{0}]", pluginDesc);
            }

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Refresh plugins failed", e);
        }

        PLUGIN_REPOS.setCacheEnabled(true);
    }

    /**
     * Gets a plugin in the specified plugins with the specified id.
     * 
     * @param plugins the specified plugins
     * @param id the specified id, must NOT be {@code null}
     * @return a plugin, returns {@code null} if not found
     */
    private static AbstractPlugin get(final List<AbstractPlugin> plugins,
        final String id) {
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
     * Private default constructor.
     */
    private Plugins() {}
}
