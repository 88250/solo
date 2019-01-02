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

import jodd.http.HttpRequest;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Option;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cron management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 2, 2018
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
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

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
                syncUser();
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

    /**
     * Sync user to https://hacpai.com.
     */
    public void syncUser() {
        if (Latkes.getServePath().contains("localhost") || Strings.isIPv4(Latkes.getServePath())) {
            return;
        }

        final JSONObject preference = preferenceQueryService.getPreference();
        if (null == preference) {
            return; // not init yet
        }

        try {
            final JSONObject requestJSONObject = new JSONObject();
            final JSONObject admin = userQueryService.getAdmin();
            requestJSONObject.put(User.USER_NAME, admin.getString(User.USER_NAME));
            requestJSONObject.put(User.USER_EMAIL, admin.getString(User.USER_EMAIL));
            requestJSONObject.put(User.USER_PASSWORD, admin.getString(User.USER_PASSWORD));
            requestJSONObject.put("userB3Key", preference.optString(Option.ID_C_KEY_OF_SOLO));
            requestJSONObject.put("clientHost", Latkes.getServePath());

            HttpRequest.post(Solos.B3LOG_SYMPHONY_SERVE_PATH + "/apis/user").bodyText(requestJSONObject.toString())
                    .header("User-Agent", Solos.USER_AGENT).contentTypeJson().sendAsync();
        } catch (final Exception e) {
            // ignored
        }
    }

}
