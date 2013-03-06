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
package org.b3log.solo.processor;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.util.Statistics;


/**
 * Statistics processor.
 * 
 * <p>
 * Statistics of B3log Solo: 
 * 
 *   <ul>
 *     <li>{@link #viewCounter(org.b3log.latke.servlet.HTTPRequestContext) Blog/Article view counting}</li>
 *   </ul>
 * <p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.9, Mar 6, 2013
 * @since 0.4.0
 */
@RequestProcessor
public final class StatProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatProcessor.class.getName());

    /**
     * Statistic management service.
     */
    private StatisticMgmtService statisticMgmtService = StatisticMgmtService.getInstance();

    /**
     * Online visitor count refresher.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/onlineVisitorRefresh", method = HTTPRequestMethod.GET)
    public void onlineVisitorCountRefresher(final HTTPRequestContext context) {
        context.setRenderer(new DoNothingRenderer());

        Statistics.removeExpiredOnlineVisitor();
    }

    /**
     * Increments Blog/Articles view counter.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/viewcnt", method = HTTPRequestMethod.GET)
    public void viewCounter(final HTTPRequestContext context) {
        LOGGER.log(Level.INFO, "Sync statistic from memcache to repository");

        context.setRenderer(new DoNothingRenderer());

        try {
            statisticMgmtService.flushStatistic();
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Flushes statistic to repository failed", e);
        }
    }
}
