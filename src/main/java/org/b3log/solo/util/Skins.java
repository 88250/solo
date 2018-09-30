/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Skin;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Skin utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.6.1, Sep 26, 2018
 * @since 0.3.1
 */
public final class Skins {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Skins.class);

    /**
     * FreeMarker configuration.
     */
    public static final Configuration TEMPLATE_CFG;

    static {
        TEMPLATE_CFG = new Configuration(Configuration.VERSION_2_3_28);
        TEMPLATE_CFG.setDefaultEncoding("UTF-8");
        final ServletContext servletContext = SoloServletListener.getServletContext();
        TEMPLATE_CFG.setServletContextForTemplateLoading(servletContext, "");
        TEMPLATE_CFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        TEMPLATE_CFG.setLogTemplateExceptions(false);
    }

    /**
     * Properties map.
     */
    private static final Map<String, Map<String, String>> LANG_MAP = new HashMap<>();

    /**
     * Private constructor.
     */
    private Skins() {
    }

    /**
     * Gets a template with the specified template name.
     *
     * @param templateName the specified template name
     * @return template, returns {@code null} if not found
     */
    public static Template getTemplate(final String templateName) {
        try {
            return Skins.TEMPLATE_CFG.getTemplate(templateName);
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Gets console template [" + templateName + "] failed", e);

            return null;
        }
    }

    /**
     * Gets a skins template with the specified request and template name.
     *
     * @param request      the specified request
     * @param templateName the specified template name
     * @return template, returns {@code null} if not found
     */
    public static Template getSkinTemplate(final HttpServletRequest request, final String templateName) {
        String templateDirName = (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME);
        if (StringUtils.isBlank(templateDirName)) {
            templateDirName = Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
        }

        try {
            return Skins.TEMPLATE_CFG.getTemplate("skins/" + templateDirName + "/" + templateName);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Fills the specified data model with the current skink's (WebRoot/skins/${skinName}/lang/lang_xx_XX.properties)
     * and core language (WebRoot/WEB-INF/classes/lang_xx_XX.properties) configurations.
     *
     * @param localeString       the specified locale string
     * @param currentSkinDirName the specified current skin directory name
     * @param dataModel          the specified data model
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

                LOGGER.log(Level.DEBUG, "Loading skin [dirName={0}, locale={1}]", currentSkinDirName, localeString);
                langs = new HashMap<>();

                final String language = Locales.getLanguage(localeString);
                final String country = Locales.getCountry(localeString);

                final ServletContext servletContext = SoloServletListener.getServletContext();
                final InputStream inputStream = servletContext.getResourceAsStream(
                        "/skins/" + currentSkinDirName + "/lang/lang_" + language + '_' + country + ".properties");

                final Properties props = new Properties();
                props.load(inputStream);
                final Set<Object> keys = props.keySet();
                for (final Object key : keys) {
                    langs.put((String) key, props.getProperty((String) key));
                }

                LANG_MAP.put(langName, langs);
                LOGGER.log(Level.DEBUG, "Loaded skin[dirName={0}, locale={1}, keyCount={2}]", currentSkinDirName, localeString, langs.size());
            }

            dataModel.putAll(langs); // Fills the current skin's language configurations

            // Fills the core language configurations
            final BeanManager beanManager = BeanManager.getInstance();
            final LangPropsService langPropsService = beanManager.getReference(LangPropsService.class);

            dataModel.putAll(langPropsService.getAll(Latkes.getLocale()));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills skin langs failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Gets all skin directory names. Scans the /skins/ directory, using the subdirectory of it as the skin directory
     * name, for example,
     * <pre>
     * ${Web root}/skins/
     *     <b>default</b>/
     *     <b>mobile</b>/
     *     <b>classic</b>/
     * </pre>.
     *
     * @return a set of skin name, returns an empty set if not found
     */
    public static Set<String> getSkinDirNames() {
        final Set<String> ret = new HashSet<>();

        final ServletContext servletContext = SoloServletListener.getServletContext();
        final Set<String> resourcePaths = servletContext.getResourcePaths("/skins");
        for (final String path : resourcePaths) {
            final Path p = Paths.get(path);
            final Path file = p.getFileName();
            final String fileName = file.toString();
            if (fileName.startsWith(".") || fileName.endsWith(".md")) {
                continue;
            }

            ret.add(fileName);
        }

        return ret;
    }

    /**
     * Gets skin directory name from the specified request. Refers to https://github.com/b3log/solo/issues/12060 for
     * more details.
     *
     * @param request the specified request
     * @return directory name, or {@code "default"} if not found
     */
    public static String getSkinDirName(final HttpServletRequest request) {
        if (Requests.mobileRequest(request)) {
            return (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME); // resolved in listener
        }

        // 1. Get skin from query
        final String specifiedSkin = request.getParameter(Skin.SKIN);
        if ("default".equals(specifiedSkin)) {
            return "default";
        }

        if (StringUtils.isNotBlank(specifiedSkin)) {
            final Set<String> skinDirNames = Skins.getSkinDirNames();
            if (skinDirNames.contains(specifiedSkin)) {
                return specifiedSkin;
            } else {
                return null;
            }
        }

        // 2. Get skin from cookie
        final Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (final Cookie cookie : cookies) {
                if (Skin.SKIN.equals(cookie.getName())) {
                    final String skin = cookie.getValue();
                    final Set<String> skinDirNames = Skins.getSkinDirNames();

                    if (skinDirNames.contains(skin)) {
                        return skin;
                    }
                }
            }
        }

        return "default";
    }
}
