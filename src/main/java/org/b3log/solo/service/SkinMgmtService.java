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
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

import java.util.Set;

/**
 * Skin management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 2, 2020
 * @since 3.5.0
 */
@Service
public class SkinMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(SkinMgmtService.class);

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Loads skins.
     *
     * @param skin the specified skin
     * @throws Exception exception
     */
    public void loadSkins(final JSONObject skin) throws Exception {
        final Set<String> skinDirNames = Skins.getSkinDirNames();
        final String currentSkinDirName = skin.optString(Option.ID_C_SKIN_DIR_NAME);
        if (!skinDirNames.contains(currentSkinDirName)) {
            LOGGER.log(Level.WARN, "Not found skin [dirName={}] configured, try to use default skin [dirName="
                    + Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME + "] instead", currentSkinDirName);
            if (!skinDirNames.contains(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME)) {
                LOGGER.log(Level.ERROR, "Not found default skin [dirName=" + Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME
                        + "], please redeploy your Solo and make sure contains the default skin.");
                System.exit(-1);
            }

            skin.put(Option.ID_C_SKIN_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
            updateSkin(skin);
        }

        final String currentMobileSkinDirName = skin.optString(Option.ID_C_MOBILE_SKIN_DIR_NAME);
        if (!skinDirNames.contains(currentMobileSkinDirName)) {
            LOGGER.log(Level.WARN, "Not found mobile skin [dirName={}] configured, try to use default mobile skin [dirName="
                    + Option.DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME + "] instead", currentMobileSkinDirName);
            if (!skinDirNames.contains(Option.DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME)) {
                LOGGER.log(Level.ERROR, "Not found default mobile skin [dirName=" + Option.DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME
                        + "], please redeploy your Solo and make sure contains the default mobile skin.");
                System.exit(-1);
            }

            skin.put(Option.ID_C_MOBILE_SKIN_DIR_NAME, Option.DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME);
            updateSkin(skin);
        }
    }

    /**
     * Updates the skin with the specified skin.
     *
     * @param skin the specified skin
     * @throws ServiceException service exception
     */
    public void updateSkin(final JSONObject skin) throws ServiceException {
        final Transaction transaction = optionRepository.beginTransaction();
        try {
            final JSONObject skinDirNameOpt = optionRepository.get(Option.ID_C_SKIN_DIR_NAME);
            skinDirNameOpt.put(Option.OPTION_VALUE, skin.optString(Option.ID_C_SKIN_DIR_NAME));
            optionRepository.update(Option.ID_C_SKIN_DIR_NAME, skinDirNameOpt);

            final JSONObject mobileSkinDirNameOpt = optionRepository.get(Option.ID_C_MOBILE_SKIN_DIR_NAME);
            mobileSkinDirNameOpt.put(Option.OPTION_VALUE, skin.optString(Option.ID_C_MOBILE_SKIN_DIR_NAME));
            optionRepository.update(Option.ID_C_MOBILE_SKIN_DIR_NAME, mobileSkinDirNameOpt);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates skin failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        }
    }
}
