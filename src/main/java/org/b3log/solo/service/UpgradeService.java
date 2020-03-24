/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.Server;
import org.b3log.solo.model.Option;
import org.b3log.solo.upgrade.*;
import org.json.JSONObject;

/**
 * Upgrade service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.18, Mar 24, 2020
 * @since 1.2.0
 */
@Service
public class UpgradeService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(UpgradeService.class);

    /**
     * Option Query Service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Upgrades if need.
     */
    public void upgrade() {
        try {
            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                return;
            }

            final String currentVer = preference.getString(Option.ID_C_VERSION); // 数据库中的版本
            if (Server.VERSION.equals(currentVer)) {
                // 如果数据库中的版本和运行时版本一致则说明已经是最新版
                return;
            }

            // 如果版本较老，则调用对应的升级程序进行升级，并贯穿升级下去直到最新版
            switch (currentVer) {
                case "2.9.9":
                    V299_300.perform();
                case "3.0.0":
                    V300_310.perform();
                case "3.1.0":
                    V310_320.perform();
                case "3.2.0":
                    V320_330.perform();
                case "3.3.0":
                    V330_340.perform();
                case "3.4.0":
                    V340_350.perform();
                case "3.5.0":
                    V350_360.perform();
                case "3.6.0":
                    V360_361.perform();
                case "3.6.1":
                    V361_362.perform();
                case "3.6.2":
                    V362_363.perform();
                case "3.6.3":
                    V363_364.perform();
                case "3.6.4":
                    V364_365.perform();
                case "3.6.5":
                    V365_366.perform();
                case "3.6.6":
                    V366_367.perform();
                case "3.6.7":
                    V367_368.perform();
                case "3.6.8":
                    V368_370.perform();
                case "3.7.0":
                    V370_380.perform();
                case "3.8.0":
                    V380_390.perform();
                case "3.9.0":
                    V390_400.perform();

                    break;
                default:
                    LOGGER.log(Level.ERROR, "Please upgrade to v3.0.0 first");
                    System.exit(-1);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed, please contact the Solo developers or reports this issue: https://github.com/88250/solo/issues/new", e);
            System.exit(-1);
        }
    }
}
