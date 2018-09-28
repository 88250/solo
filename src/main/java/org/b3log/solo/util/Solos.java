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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.SoloServletListener;
import org.json.JSONObject;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Solo utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.0, Sep 28, 2018
 * @since 2.8.0
 */
public final class Solos {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Solos.class);

    /**
     * Mail configuration (mail.properties).
     */
    private static final ResourceBundle mailConf = ResourceBundle.getBundle("mail");

    /**
     * B3log Rhythm address.
     */
    public static final String B3LOG_RHYTHM_SERVE_PATH;

    /**
     * B3log Symphony address.
     */
    public static final String B3LOG_SYMPHONY_SERVE_PATH;

    /**
     * Favicon API.
     */
    public static final String FAVICON_API;

    /**
     * Gravatar address.
     */
    public static final String GRAVATAR;

    /**
     * Local file upload dir path.
     */
    public static final String UPLOAD_DIR_PATH;

    /**
     * Mobile skin.
     */
    public static final String MOBILE_SKIN;

    /**
     * Solo User-Agent.
     */
    public static final String USER_AGENT = "Solo/" + SoloServletListener.VERSION + "; +https://github.com/b3log/solo";


    static {
        ResourceBundle solo;
        try {
            solo = ResourceBundle.getBundle("solo");
        } catch (final MissingResourceException e) {
            solo = ResourceBundle.getBundle("b3log"); // 2.8.0 向后兼容
        }

        B3LOG_RHYTHM_SERVE_PATH = solo.getString("rhythm.servePath");
        B3LOG_SYMPHONY_SERVE_PATH = solo.getString("symphony.servePath");
        FAVICON_API = solo.getString("faviconAPI");
        GRAVATAR = solo.getString("gravatar");
        String dir = solo.getString("uploadDir");
        if (StringUtils.isNotBlank(dir) && !StringUtils.endsWith(dir, "/")) {
            dir += "/";
        }
        UPLOAD_DIR_PATH = dir;

        String mobileSkin = "Medium";
        try {
            mobileSkin = solo.getString("mobile.skin");
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Loads [mobile.skin] in solo.props failed [" + e.getMessage() + "], using [" + mobileSkin + "] as the default mobile skin");
        }
        MOBILE_SKIN = mobileSkin;
    }

    /**
     * Whether user configures the mail.properties.
     *
     * @return {@code true} if user configured, returns {@code false} otherwise
     */
    public static boolean isConfigured() {
        try {
            return StringUtils.isNotBlank(mailConf.getString("mail.user")) &&
                    StringUtils.isNotBlank(mailConf.getString("mail.password")) &&
                    StringUtils.isNotBlank(mailConf.getString("mail.smtp.host")) &&
                    StringUtils.isNotBlank(mailConf.getString("mail.smtp.port"));
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Gets the Gravatar URL for the specified email with the specified size.
     *
     * @param email the specified email
     * @param size  the specified size
     * @return the Gravatar URL
     */
    public static String getGravatarURL(final String email, final String size) {
        return GRAVATAR + DigestUtils.md5Hex(email) + "?s=" + size;
    }

    /**
     * Clones a JSON object from the specified source object.
     *
     * @param src the specified source object
     * @return cloned object
     */
    public static JSONObject clone(final JSONObject src) {
        return new JSONObject(src, CollectionUtils.jsonArrayToArray(src.names(), String[].class));
    }

    /**
     * Private constructor.
     */
    private Solos() {
    }
}
