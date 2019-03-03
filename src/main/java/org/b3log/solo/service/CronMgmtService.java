/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Stopwatchs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cron management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 29, 2019
 * @since 2.9.7
 */
@Service
public class CronMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CronMgmtService.class);

    /**
     * Cron thread pool.
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Page management service.
     */
    @Inject
    private PageMgmtService pageMgmtService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Start all cron tasks.
     */
    public void start() {
        long delay = 10000;

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            try {
                StatisticMgmtService.removeExpiredOnlineVisitor();
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Executes cron failed", e);
            } finally {
                Stopwatchs.release();
            }
        }, delay, 1000 * 60 * 10, TimeUnit.MILLISECONDS);
        delay += 2000;

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            try {
                pageMgmtService.refreshGitHub();
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Executes cron failed", e);
            } finally {
                Stopwatchs.release();
            }
        }, delay, 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
        delay += 2000;
    }

    /**
     * Stop all cron tasks.
     */
    public void stop() {
        SCHEDULED_EXECUTOR_SERVICE.shutdown();
    }
}
