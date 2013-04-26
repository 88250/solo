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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.b3log.solo.model.Preference.*;


/**
 * Preference management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Apr 1, 2013
 * @since 0.4.0
 */
public final class PreferenceMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceMgmtService.class.getName());

    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Updates the reply notification template with the specified reply 
     * notification template.
     * 
     * @param replyNotificationTemplate the specified reply notification 
     * template
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

            LOGGER.log(Level.SEVERE, "Updates reply notification failed", e);
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

        // TODO: checks preference 

        final Transaction transaction = preferenceRepository.beginTransaction();

        try {
            String blogHost = preference.getString(BLOG_HOST).toLowerCase().trim();

            if (StringUtils.startsWithIgnoreCase(blogHost, "http://")) {
                blogHost = blogHost.substring("http://".length());
            }
            if (blogHost.endsWith("/")) {
                blogHost = blogHost.substring(0, blogHost.length() - 1);
            }

            LOGGER.log(Level.FINER, "Blog Host[{0}]", blogHost);
            preference.put(BLOG_HOST, blogHost);

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

            LOGGER.log(Level.FINER, "Skin path[{0}]", skinPath);
            Templates.CACHE.clear();

            preference.put(Skin.SKINS, skinArray.toString());

            final String timeZoneId = preference.getString(TIME_ZONE_ID);

            TimeZones.setTimeZone(timeZoneId);

            preference.put(Preference.SIGNS, preference.get(Preference.SIGNS).toString());

            final JSONObject oldPreference = preferenceQueryService.getPreference();
            final String adminEmail = oldPreference.getString(ADMIN_EMAIL);

            preference.put(ADMIN_EMAIL, adminEmail);

            if (!preference.has(PAGE_CACHE_ENABLED)) {
                preference.put(PAGE_CACHE_ENABLED, oldPreference.getBoolean(PAGE_CACHE_ENABLED));
            } else {
                if (RuntimeEnv.BAE == Latkes.getRuntimeEnv()) {
                    // XXX: Ignores user's setting, uses default
                    // https://github.com/b3log/b3log-solo/issues/73
                    preference.put(PAGE_CACHE_ENABLED, Default.DEFAULT_PAGE_CACHE_ENABLED);
                }
            }

            final String maxPageCntStr = Latkes.getMaxPageCacheCnt();

            if (Integer.valueOf(maxPageCntStr) <= 0) {
                preference.put(PAGE_CACHE_ENABLED, false);
            }

            final boolean pageCacheEnabled = preference.getBoolean(Preference.PAGE_CACHE_ENABLED);

            Templates.enableCache(pageCacheEnabled);

            final String version = oldPreference.optString(VERSION);

            if (!Strings.isEmptyOrNull(version)) {
                preference.put(VERSION, version);
            }

            final String localeString = preference.getString(Preference.LOCALE_STRING);

            LOGGER.log(Level.FINER, "Current locale[string={0}]", localeString);
            Latkes.setLocale(new Locale(Locales.getLanguage(localeString), Locales.getCountry(localeString)));

            preferenceRepository.update(Preference.PREFERENCE, preference);

            transaction.commit();

            Templates.MAIN_CFG.setDirectoryForTemplateLoading(new File(skinPath));

            if (preference.getBoolean(PAGE_CACHE_ENABLED)) {
                Latkes.enablePageCache();
            } else {
                Latkes.disablePageCache();
            }
        } catch (final JSONException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        } catch (final IOException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        }

        LOGGER.log(Level.FINER, "Updates preference successfully");
    }

    /**
     * Gets the {@link PreferenceMgmtService} singleton.
     *
     * @return the singleton
     */
    public static PreferenceMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private PreferenceMgmtService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 24, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PreferenceMgmtService SINGLETON = new PreferenceMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
