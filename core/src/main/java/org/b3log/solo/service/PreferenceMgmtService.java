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
package org.b3log.solo.service;


import java.io.IOException;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.util.TimeZones;
import org.b3log.solo.util.Skins;
import org.json.JSONException;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.b3log.solo.model.Preference.*;
import static org.b3log.solo.model.Skin.SKINS;
import static org.b3log.solo.model.Skin.SKIN_DIR_NAME;
import static org.b3log.solo.model.Skin.SKIN_NAME;
import static org.b3log.solo.util.Skins.getSkinDirNames;
import static org.b3log.solo.util.Skins.getSkinName;
import static org.b3log.solo.util.Skins.setDirectoryForTemplateLoading;


/**
 * Preference management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Jul 18, 2013
 * @since 0.4.0
 */
@Service
public class PreferenceMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceMgmtService.class.getName());

    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Loads skins for the specified preference and initializes templates loading.
     * 
     * <p>
     * If the skins directory has been changed, persists the change into preference.
     * </p>
     *
     * @param preference the specified preference
     * @throws Exception exception
     */
    public void loadSkins(final JSONObject preference) throws Exception {
        Stopwatchs.start("Load Skins");

        LOGGER.info("Loading skins....");

        final Set<String> skinDirNames = getSkinDirNames();

        LOGGER.log(Level.DEBUG, "Loaded skins[dirNames={0}]", skinDirNames);
        final JSONArray skinArray = new JSONArray();

        for (final String dirName : skinDirNames) {
            final JSONObject skin = new JSONObject();
            final String name = getSkinName(dirName);

            if (null == name) {
                LOGGER.log(Level.WARN, "The directory[{0}] does not contain any skin, ignored it", dirName);

                continue;
            }

            skin.put(SKIN_NAME, name);
            skin.put(SKIN_DIR_NAME, dirName);

            skinArray.put(skin);
        }

        final String currentSkinDirName = preference.optString(SKIN_DIR_NAME);
        final String skinName = preference.optString(SKIN_NAME);

        LOGGER.log(Level.INFO, "Current skin[name={0}]", skinName);

        if (!skinDirNames.contains(currentSkinDirName)) {
            LOGGER.log(Level.WARN, "Configred skin[dirName={0}] can not find, try to use " + "default skin[dirName=ease] instead.",
                currentSkinDirName);
            if (!skinDirNames.contains("ease")) {
                LOGGER.log(Level.ERROR, "Can not find skin[dirName=ease]");

                throw new IllegalStateException(
                    "Can not find default skin[dirName=ease], please redeploy your B3log Solo and make sure contains this default skin!");
            }

            preference.put(SKIN_DIR_NAME, "ease");
            preference.put(SKIN_NAME, "ease");

            updatePreference(preference);
        }

        final String skinsString = skinArray.toString();

        if (!skinsString.equals(preference.getString(SKINS))) {
            LOGGER.log(Level.INFO, "The skins directory has been changed, persists " + "the change into preference");
            preference.put(SKINS, skinsString);
            updatePreference(preference);
        }

        setDirectoryForTemplateLoading(preference.getString(SKIN_DIR_NAME));

        final String localeString = preference.getString(Preference.LOCALE_STRING);

        if ("zh_CN".equals(localeString)) {
            TimeZones.setTimeZone("Asia/Shanghai");
        }

        LOGGER.info("Loaded skins....");

        Stopwatchs.end();
    }

    /**
     * Updates the reply notification template with the specified reply notification template.
     * 
     * @param replyNotificationTemplate the specified reply notification template
     * @throws ServiceException service exception
     */
    public void updateReplyNotificationTemplate(final JSONObject replyNotificationTemplate) throws ServiceException {
        final Transaction transaction = preferenceRepository.beginTransaction();

        try {
            preferenceRepository.update(Preference.REPLY_NOTIFICATION_TEMPLATE, replyNotificationTemplate);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates reply notification failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Updates the preference with the specified preference.
     *
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void updatePreference(final JSONObject preference) throws ServiceException {
        @SuppressWarnings("unchecked")
        final Iterator<String> keys = preference.keys();

        while (keys.hasNext()) {
            final String key = keys.next();

            if (preference.isNull(key)) {
                throw new ServiceException("A value is null of preference[key=" + key + "]");
            }
        }

        final Transaction transaction = preferenceRepository.beginTransaction();

        try {
            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            final String skinName = Skins.getSkinName(skinDirName);

            preference.put(Skin.SKIN_NAME, skinName);
            final Set<String> skinDirNames = Skins.getSkinDirNames();
            final JSONArray skinArray = new JSONArray();

            for (final String dirName : skinDirNames) {
                final JSONObject skin = new JSONObject();

                skinArray.put(skin);

                final String name = Skins.getSkinName(dirName);

                skin.put(Skin.SKIN_NAME, name);
                skin.put(Skin.SKIN_DIR_NAME, dirName);
            }
            final String webRootPath = SoloServletListener.getWebRoot();
            final String skinPath = webRootPath + Skin.SKINS + "/" + skinDirName;

            LOGGER.log(Level.DEBUG, "Skin path[{0}]", skinPath);

            preference.put(Skin.SKINS, skinArray.toString());

            final String timeZoneId = preference.getString(TIME_ZONE_ID);

            TimeZones.setTimeZone(timeZoneId);

            preference.put(Preference.SIGNS, preference.get(Preference.SIGNS).toString());

            final JSONObject oldPreference = preferenceRepository.get(Preference.PREFERENCE);
            final String adminEmail = oldPreference.getString(ADMIN_EMAIL);

            preference.put(ADMIN_EMAIL, adminEmail);

            final String version = oldPreference.optString(VERSION);

            if (!Strings.isEmptyOrNull(version)) {
                preference.put(VERSION, version);
            }

            final String localeString = preference.getString(Preference.LOCALE_STRING);

            LOGGER.log(Level.DEBUG, "Current locale[string={0}]", localeString);
            Latkes.setLocale(new Locale(Locales.getLanguage(localeString), Locales.getCountry(localeString)));

            preferenceRepository.update(Preference.PREFERENCE, preference);

            transaction.commit();

            Templates.MAIN_CFG.setDirectoryForTemplateLoading(new File(skinPath));
        } catch (final JSONException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.ERROR, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.ERROR, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        } catch (final IOException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.ERROR, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        }

        LOGGER.log(Level.DEBUG, "Updates preference successfully");
    }

    /**
     * Sets the preference repository with the specified preference repository.
     * 
     * @param preferenceRepository the specified preference repository
     */
    public void setPreferenceRepository(final PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    /**
     * Sets the language service with the specified language service.
     * 
     * @param langPropsService the specified language service
     */
    public void setLangPropsService(final LangPropsService langPropsService) {
        this.langPropsService = langPropsService;
    }
}
