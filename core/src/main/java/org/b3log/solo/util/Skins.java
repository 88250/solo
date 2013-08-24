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
package org.b3log.solo.util;


import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.LangPropsServiceImpl;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import static org.b3log.solo.model.Skin.*;


/**
 * Skin utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.6, Jun 12, 2012
 * @since 0.3.1
 */
public final class Skins {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Skins.class.getName());

    /**
     * Properties map.
     */
    private static final Map<String, Map<String, String>> LANG_MAP = new HashMap<String, Map<String, String>>();

    /**
     * Private default constructor.
     */
    private Skins() {}

    /**
     * Fills the specified data model with the current skink's (WebRoot/skins/${skinName}/lang/lang_xx_XX.properties) and 
     * core language (WebRoot/WEB-INF/classes/lang_xx_XX.properties) configurations.
     * 
     * @param localeString the specified locale string
     * @param currentSkinDirName the specified current skin directory name
     * @param dataModel the specified data model
     * @throws ServiceException service exception 
     */
    public static void fillLangs(final String localeString, final String currentSkinDirName, final Map<String, Object> dataModel)
        throws ServiceException {
        Stopwatchs.start("Fill Skin Langs");

        try {
            final String langName = currentSkinDirName + "." + localeString;
            Map<String, String> langs = LANG_MAP.get(langName);

            if (null == langs) {
                LANG_MAP.clear(); // Collect unused skin languages

                LOGGER.log(Level.INFO, "Loading skin [dirName={0}, locale={1}]", new Object[] {currentSkinDirName, localeString});
                langs = new HashMap<String, String>();

                final String webRootPath = SoloServletListener.getWebRoot();

                final String language = Locales.getLanguage(localeString);
                final String country = Locales.getCountry(localeString);

                final Properties props = new Properties();

                props.load(
                    new FileReader(
                        webRootPath + "skins" + File.separator + currentSkinDirName + File.separator + Keys.LANGUAGE + File.separator
                        + Keys.LANGUAGE + '_' + language + '_' + country + ".properties"));
                final Set<Object> keys = props.keySet();

                for (final Object key : keys) {
                    langs.put((String) key, props.getProperty((String) key));
                }

                LANG_MAP.put(langName, langs);
                LOGGER.log(Level.INFO, "Loaded skin[dirName={0}, locale={1}, keyCount={2}]",
                    new Object[] {currentSkinDirName, localeString, langs.size()});
            }

            dataModel.putAll(langs); // Fills the current skin's language configurations
            
            // Fills the core language configurations
            final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
            final LangPropsService langPropsService = beanManager.getReference(LangPropsServiceImpl.class);

            dataModel.putAll(langPropsService.getAll(Latkes.getLocale()));
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Fills skin langs failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Sets the directory for template loading with the specified skin directory
     * name, and sets the directory for mobile request template loading.
     * 
     * @param skinDirName the specified skin directory name
     */
    public static void setDirectoryForTemplateLoading(final String skinDirName) {
        try {
            final String webRootPath = SoloServletListener.getWebRoot();
            final String skinPath = webRootPath + SKINS + File.separator + skinDirName;

            Templates.MAIN_CFG.setDirectoryForTemplateLoading(new File(skinPath));

            Templates.MOBILE_CFG.setDirectoryForTemplateLoading(new File(webRootPath + SKINS + File.separator + "mobile"));
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Loads skins error!", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets all skin directory names. Scans the
     * {@linkplain SoloServletListener#getWebRoot() Web root}/skins/ directory,
     * using the subdirectory of it as the skin directory name, for example,
     * <pre>
     * ${Web root}/skins/
     *     <b>default</b>/
     *     <b>mobile</b>/
     *     <b>classic</b>/
     * </pre>
     * Skips files that name starts with . and {@linkplain File#isHidden() 
     * hidden} files.
     *
     * @return a set of skin name, returns an empty set if not found
     */
    public static Set<String> getSkinDirNames() {
        final String webRootPath = SoloServletListener.getWebRoot();
        final File skins = new File(webRootPath + "skins" + File.separator);
        final File[] skinDirs = skins.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File file) {
                return file.isDirectory() && !file.getName().startsWith(".");
            }
        });

        final Set<String> ret = new HashSet<String>();

        if (null == skinDirs) {
            LOGGER.error("Skin directory is null");

            return ret;
        }

        for (int i = 0; i < skinDirs.length; i++) {
            final File file = skinDirs[i];

            ret.add(file.getName());
        }

        return ret;
    }

    /**
     * Gets the skin name for the specified skin directory name. The skin name
     * was configured in skin.properties file({@code name} as the key) under
     * skin directory specified by the given skin directory name.
     *
     * @param skinDirName the given skin directory name
     * @return skin name, returns {@code null} if not found or error occurs
     * @see #getSkinDirNames()
     */
    public static String getSkinName(final String skinDirName) {
        final String webRootPath = SoloServletListener.getWebRoot();
        final File skins = new File(webRootPath + "skins" + File.separator);
        final File[] skinDirs = skins.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File pathname) {
                return pathname.isDirectory() && pathname.getName().equals(skinDirName) ? true : false;
            }
        });

        if (null == skinDirs) {
            LOGGER.error("Skin directory is null");

            return null;
        }

        if (1 != skinDirs.length) {
            LOGGER.log(Level.ERROR, "Skin directory count[{0}]", skinDirs.length);

            return null;
        }

        try {
            final Properties ret = new Properties();
            final String skinPropsPath = skinDirs[0].getPath() + File.separator + "skin.properties";

            ret.load(new FileReader(skinPropsPath));

            return ret.getProperty("name");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Read skin configuration error[msg={0}]", e.getMessage());

            return null;
        }
    }
}
