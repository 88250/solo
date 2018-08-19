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

import org.apache.commons.lang.StringUtils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Solo utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Jul 31, 2018
 * @since 2.8.0
 */
public final class Solos {

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

        String mobileSkin = "mobile";
        try {
            mobileSkin = solo.getString("mobile.skin");
        } catch (final Exception e) {
            // ignored
        }
        MOBILE_SKIN = mobileSkin;
    }

    /**
     * Private constructor.
     */
    private Solos() {
    }
}
