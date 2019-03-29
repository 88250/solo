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

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

import static org.b3log.solo.model.Skin.SKINS;
import static org.b3log.solo.model.Skin.SKIN_DIR_NAME;
import static org.b3log.solo.util.Skins.getSkinDirNames;

/**
 * Skin management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 29, 2019
 * @since 3.5.0
 */
@Service
public class SkinMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SkinMgmtService.class);

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
        Stopwatchs.start("Load Skins");

        LOGGER.debug("Loading skins....");
        final Set<String> skinDirNames = getSkinDirNames();
        LOGGER.log(Level.DEBUG, "Loaded skins [dirNames={0}]", skinDirNames);
        final JSONArray skinArray = new JSONArray();
        for (final String dirName : skinDirNames) {
            final JSONObject s = new JSONObject();
            final String name = Latkes.getSkinName(dirName);
            if (null == name) {
                LOGGER.log(Level.WARN, "The directory [{0}] does not contain any skin, ignored it", dirName);

                continue;
            }

            s.put(SKIN_DIR_NAME, dirName);
            skinArray.put(s);
        }

        final String currentSkinDirName = skin.optString(SKIN_DIR_NAME);
        if (!skinDirNames.contains(currentSkinDirName)) {
            LOGGER.log(Level.WARN, "Configured skin [dirName={0}] can not find, try to use " + "default skin [dirName="
                    + Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME + "] instead.", currentSkinDirName);
            if (!skinDirNames.contains(Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME)) {
                LOGGER.log(Level.ERROR, "Can not find default skin [dirName=" + Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME
                        + "], please redeploy your Solo and make sure contains the default skin. If you are using git, try to re-pull with 'git pull --recurse-submodules'");
                System.exit(-1);
            }

            skin.put(SKIN_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
            updateSkin(skin);
        }

        final String skinsString = skinArray.toString();
        if (!skinsString.equals(skin.getString(SKINS))) {
            LOGGER.debug("The skins directory has been changed, persists it into database");
            skin.put(SKINS, skinsString);
            updateSkin(skin);
        }

        LOGGER.debug("Loaded skins....");

        Stopwatchs.end();
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

            JSONObject mobileSkinDirNameOpt = optionRepository.get(Option.ID_C_MOBILE_SKIN_DIR_NAME);
            // TODO: 在 v3.5.0 发布后可移除判空
            if (null == mobileSkinDirNameOpt) {
                mobileSkinDirNameOpt = new JSONObject();
                mobileSkinDirNameOpt.put(Keys.OBJECT_ID, Option.ID_C_MOBILE_SKIN_DIR_NAME);
                mobileSkinDirNameOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_SKIN);
                mobileSkinDirNameOpt.put(Option.OPTION_VALUE, Option.DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME);
                optionRepository.add(mobileSkinDirNameOpt);
            } else {
                mobileSkinDirNameOpt.put(Option.OPTION_VALUE, skin.optString(Option.ID_C_MOBILE_SKIN_DIR_NAME));
                optionRepository.update(Option.ID_C_MOBILE_SKIN_DIR_NAME, mobileSkinDirNameOpt);
            }

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
