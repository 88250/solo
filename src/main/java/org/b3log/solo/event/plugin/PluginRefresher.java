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
package org.b3log.solo.event.plugin;

import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.repository.PluginRepository;
import org.b3log.solo.repository.impl.PluginRepositoryImpl;
import org.b3log.solo.service.PluginMgmtService;

import java.util.List;

/**
 * This listener is responsible for refreshing plugin after every loaded.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Nov 28, 2011
 * @since 0.3.1
 */
public final class PluginRefresher extends AbstractEventListener<List<AbstractPlugin>> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PluginRefresher.class);

    @Override
    public void action(final Event<List<AbstractPlugin>> event) throws EventException {
        final List<AbstractPlugin> plugins = event.getData();

        LOGGER.log(Level.DEBUG, "Processing an event[type={0}, data={1}] in listener[className={2}]",
                event.getType(), plugins, PluginRefresher.class.getName());

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final PluginRepository pluginRepository = beanManager.getReference(PluginRepositoryImpl.class);

        final Transaction transaction = pluginRepository.beginTransaction();
        try {
            final PluginMgmtService pluginMgmtService = beanManager.getReference(PluginMgmtService.class);
            pluginMgmtService.refresh(plugins);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Processing plugin loaded event error", e);
            throw new EventException(e);
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
