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
package org.b3log.solo.mail;

/**
 * Mail service factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.3, Dec 3, 2018
 */
public final class MailServiceFactory {

    /**
     * Mail service.
     */
    private static final MailService MAIL_SERVICE;

    static {
        try {
            final Class<MailService> mailServiceClass = (Class<MailService>) Class.forName(
                    "org.b3log.solo.mail.local.LocalMailService");
            MAIL_SERVICE = mailServiceClass.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException("Can not initialize mail service!", e);
        }
    }

    /**
     * Private constructor.
     */
    private MailServiceFactory() {
    }

    /**
     * Gets mail service.
     *
     * @return mail service
     */
    public static MailService getMailService() {
        return MAIL_SERVICE;
    }
}
