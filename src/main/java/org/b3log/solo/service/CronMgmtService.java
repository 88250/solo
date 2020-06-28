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
package org.b3log.solo.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.util.Solos;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cron management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Jun 28, 2020
 * @since 2.9.7
 */
@Service
public class CronMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CronMgmtService.class);

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
     * Article management service.
     */
    @Inject
    private ArticleMgmtService articleMgmtService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Export service.
     */
    @Inject
    private ExportService exportService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

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
                Solos.reloadBlacklistIPs();
                articleMgmtService.updateArticlesRandomValue(20);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Executes cron failed", e);
            } finally {
                Stopwatchs.release();
            }
        }, delay, 1000 * 60 * 30, TimeUnit.MILLISECONDS);
        delay += 2000;

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            try {
                articleMgmtService.refreshGitHub();
                userMgmtService.refreshUSite();
                exportService.exportHacPai();
                exportService.exportGitHub();
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
