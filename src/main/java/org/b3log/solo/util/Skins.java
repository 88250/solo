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
package org.b3log.solo.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Cookie;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Skin utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.7.0, Jan 14, 2020
 * @since 0.3.1
 */
public final class Skins {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Skins.class);

    /**
     * FreeMarker configuration.
     */
    public static final Configuration TEMPLATE_CFG;

    static {
        TEMPLATE_CFG = new Configuration(Configuration.VERSION_2_3_29);
        TEMPLATE_CFG.setDefaultEncoding("UTF-8");
        try {
            String path = Skins.class.getResource("/").getPath();
            if (StringUtils.contains(path, "/target/classes/") || StringUtils.contains(path, "/target/test-classes/")) {
                // 开发时使用源码目录
                path = StringUtils.replace(path, "/target/classes/", "/src/main/resources/");
                path = StringUtils.replace(path, "/target/test-classes/", "/src/main/resources/");
            }
            TEMPLATE_CFG.setDirectoryForTemplateLoading(new File(path));
            LOGGER.log(Level.INFO, "Loaded template from directory [" + path + "]");
        } catch (final Exception e) {
            TEMPLATE_CFG.setClassForTemplateLoading(Skins.class, "/");
            LOGGER.log(Level.INFO, "Loaded template from classpath");
        }
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
     * @param context      the specified request context
     * @param templateName the specified template name
     * @return template, returns {@code null} if not found
     */
    public static Template getSkinTemplate(final RequestContext context, final String templateName) {
        String templateDirName = (String) context.attr(Keys.TEMAPLTE_DIR_NAME);
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
     * Fills the specified data model with the current skin's (skins/${skinDirName}/lang/lang_xx_XX.properties)
     * and core language (classes/lang_xx_XX.properties) configurations.
     *
     * @param localeString       the specified locale string
     * @param currentSkinDirName the specified current skin directory name
     * @param dataModel          the specified data model
     * @throws ServiceException service exception
     */
    public static void fillLangs(final String localeString, String currentSkinDirName, final Map<String, Object> dataModel)
            throws ServiceException {
        Stopwatchs.start("Fill Skin Langs");

        try {
            // Fills the core language configurations
            final BeanManager beanManager = BeanManager.getInstance();
            final LangPropsService langPropsService = beanManager.getReference(LangPropsService.class);
            dataModel.putAll(langPropsService.getAll(Latkes.getLocale()));

            if (StringUtils.isBlank(currentSkinDirName)) {
                currentSkinDirName = Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
            }
            final String langName = currentSkinDirName + "." + localeString;
            Map<String, String> langs = LANG_MAP.get(langName);
            if (null == langs) {
                LANG_MAP.clear(); // Collect unused skin languages

                langs = new HashMap<>();
                final String language = Locales.getLanguage(localeString);
                final String country = Locales.getCountry(localeString);
                final InputStream inputStream = Skins.class.getResourceAsStream("/skins/" + currentSkinDirName + "/lang/lang_" + language + '_' + country + ".properties");
                if (null != inputStream) {
                    LOGGER.log(Level.DEBUG, "Loading skin [dirName={}, locale={}]", currentSkinDirName, localeString);
                    final Properties props = new Properties();
                    props.load(inputStream);
                    inputStream.close();
                    final Set<Object> keys = props.keySet();
                    for (final Object key : keys) {
                        String val = props.getProperty((String) key);
                        val = replaceVars(val);
                        langs.put((String) key, val);
                    }

                    LANG_MAP.put(langName, langs);
                    LOGGER.log(Level.DEBUG, "Loaded skin [dirName={}, locale={}, keyCount={}]", currentSkinDirName, localeString, langs.size());
                }
            }

            dataModel.putAll(langs); // Fills the current skin's language configurations
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

        try {
            final URI uri = Skins.class.getResource("/skins").toURI();
            Path resourcePath;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (final FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                }
                resourcePath = fileSystem.getPath("/skins");
            } else {
                resourcePath = Paths.get(uri);
            }
            final Stream<Path> walk = Files.walk(resourcePath, 1);
            for (final Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                final Path file = it.next().getFileName();
                String fileName = file.toString();
                fileName = StringUtils.replace(fileName, "/", "");
                if (fileName.startsWith(".") || fileName.endsWith(".md") || "skins".equals(fileName)) {
                    continue;
                }

                ret.add(fileName);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get skin dir names failed", e);
        }

        return ret;
    }

    /**
     * Gets skin directory name from the specified request.
     * Refers to <a href="https://github.com/b3log/solo/issues/12060">前台皮肤切换</a> for more details.
     *
     * @param context the specified request context
     * @return directory name, or {@code null} if not found
     */
    public static String getSkinDirName(final RequestContext context) {
        // 1. Get skin from query
        final String specifiedSkin = context.param(Option.CATEGORY_C_SKIN);
        if (StringUtils.isNotBlank(specifiedSkin)) {
            final Set<String> skinDirNames = Skins.getSkinDirNames();
            if (skinDirNames.contains(specifiedSkin)) {
                return specifiedSkin;
            } else {
                return null;
            }
        }

        // 2. Get skin from cookie
        return getSkinDirNameFromCookie(context.getRequest());
    }

    /**
     * Gets skin directory name from the specified request's cookie.
     *
     * @param request the specified request
     * @return directory name, or {@code null} if not found
     */
    public static String getSkinDirNameFromCookie(final Request request) {
        final Set<String> skinDirNames = Skins.getSkinDirNames();
        boolean isMobile = Solos.isMobile(request);
        String skin = null, mobileSkin = null;
        final Set<Cookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final Cookie cookie : cookies) {
                if (Common.COOKIE_NAME_SKIN.equals(cookie.getName()) && !isMobile) {
                    final String s = cookie.getValue();
                    if (skinDirNames.contains(s)) {
                        skin = s;
                        break;
                    }
                }
                if (Common.COOKIE_NAME_MOBILE_SKIN.equals(cookie.getName()) && isMobile) {
                    final String s = cookie.getValue();
                    if (skinDirNames.contains(s)) {
                        mobileSkin = s;
                        break;
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(skin)) {
            return skin;
        }
        if (StringUtils.isNotBlank(mobileSkin)) {
            return mobileSkin;
        }

        return null;
    }

    /**
     * Replaces all variables of the specified language value.
     *
     * <p>
     * Variables:
     * <ul>
     * <li>${servePath}</li>
     * <li>${staticServePath}</li>
     * </ul>
     * </p>
     *
     * @param langValue the specified language value
     * @return replaced value
     */
    private static String replaceVars(final String langValue) {
        String ret = StringUtils.replace(langValue, "${servePath}", Latkes.getServePath());
        ret = StringUtils.replace(ret, "${staticServePath}", Latkes.getStaticServePath());

        return ret;
    }
}
