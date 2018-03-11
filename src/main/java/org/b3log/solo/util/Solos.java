/*
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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

import org.apache.commons.lang.StringUtils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Solo utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 11, 2018
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
    }

    /**
     * Private constructor.
     */
    private Solos() {
    }
}
