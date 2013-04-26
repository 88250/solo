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
package org.b3log.solo.event.cache;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.service.StatisticMgmtService;


/**
 * This listener is responsible for handling remove cache event.
 * 
 * <p>
 * Flush the statistic to repository.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 28, 2011
 * @since 0.3.1
 */
public final class RemoveCacheListener extends AbstractEventListener<Void> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoveCacheListener.class.getName());

    /**
     * Statistic management service.
     */
    private StatisticMgmtService statisticMgmtService = StatisticMgmtService.getInstance();

    @Override
    public void action(final Event<Void> event) throws EventException {
        LOGGER.log(Level.FINER, "Processing an event[type={0} in listener[className={2}]",
            new Object[] {event.getType(), RemoveCacheListener.class.getName()});

        try {
            statisticMgmtService.flushStatistic();
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Flushes statistic to repository failed", e);
        }
    }

    /**
     * Gets the event type {@linkplain PageCaches#REMOVE_CACHE}.
     * 
     * @return event type
     */
    @Override
    public String getEventType() {
        return PageCaches.REMOVE_CACHE;
    }
}
