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
package org.b3log.solo.event;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.repository.PluginRepository;
import org.b3log.solo.service.PluginMgmtService;

import java.util.List;

/**
 * This listener is responsible for refreshing plugin after every loaded.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 25, 2018
 * @since 0.3.1
 */
@Singleton
public class PluginRefresher extends AbstractEventListener<List<AbstractPlugin>> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PluginRefresher.class);

    @Override
    public void action(final Event<List<AbstractPlugin>> event) {
        final List<AbstractPlugin> plugins = event.getData();

        LOGGER.log(Level.DEBUG, "Processing an event [type={}, data={}] in listener [className={}]",
                event.getType(), plugins, PluginRefresher.class.getName());

        final BeanManager beanManager = BeanManager.getInstance();
        final PluginRepository pluginRepository = beanManager.getReference(PluginRepository.class);

        final Transaction transaction = pluginRepository.beginTransaction();
        try {
            final PluginMgmtService pluginMgmtService = beanManager.getReference(PluginMgmtService.class);
            pluginMgmtService.refresh(plugins);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Process plugin loaded event error", e);
        }
    }

    /**
     * Gets the event type {@linkplain PluginManager#PLUGIN_LOADED_EVENT}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return PluginManager.PLUGIN_LOADED_EVENT;
    }
}
