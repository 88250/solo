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

import java.util.ResourceBundle;

/**
 * Mail utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 20, 2017
 * @since 2.3.0
 */
public final class Mails {

    /**
     * Mail configuration (mail.properties).
     */
    private static final ResourceBundle mailConf = ResourceBundle.getBundle("mail");

    /**
     * Private constructor.
     */
    private Mails() {
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
}
