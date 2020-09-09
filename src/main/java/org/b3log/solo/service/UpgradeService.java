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
import org.b3log.solo.Server;
import org.b3log.solo.model.Option;
import org.b3log.solo.upgrade.*;
import org.json.JSONObject;

/**
 * Upgrade service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.23, Sep 9, 2020
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
                case "4.0.0":
                    V400_410.perform();
                case "4.1.0":
                    V410_420.perform();
                case "4.2.0":
                    V420_430.perform();
                case "4.3.0":
                    V430_431.perform();
                    break;
                default:
                    LOGGER.log(Level.ERROR, "Please upgrade to v3.6.2 first");
                    System.exit(-1);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed, please contact the Solo developers or reports this issue: https://github.com/88250/solo/issues/new", e);
            System.exit(-1);
        }
    }
}
