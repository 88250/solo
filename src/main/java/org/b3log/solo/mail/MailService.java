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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Mail service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2011
 */
public interface MailService {

    /**
     * Sends the specified message as a mail asynchronously.
     *
     * @param message the specified message
     * @throws IOException if internal errors
     */
    void send(final Message message) throws IOException;

    /**
     * Mail message.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.0, Aug 8, 2011
     */
    class Message {

        /**
         * From.
         */
        private String from;

        /**
         * Recipients.
         */
        private Set<String> recipients = new HashSet<>();

        /**
         * HTML body.
         */
        private String htmlBody;

        /**
         * Subject.
         */
        private String subject;

        /**
         * Gets the recipients.
         *
         * @return recipients
         */
        public Set<String> getRecipients() {
            return Collections.unmodifiableSet(recipients);
        }

        /**
         * Adds the specified recipient.
         *
         * @param recipient the specified recipient
         */
        public void addRecipient(final String recipient) {
            recipients.add(recipient);
        }

        /**
         * Gets the HTML body.
         *
         * @return HTML body
         */
        public String getHtmlBody() {
            return htmlBody;
        }

        /**
         * Sets the HTML body with the specified HTML body.
         *
         * @param htmlBody the specified HTML body
         */
        public void setHtmlBody(final String htmlBody) {
            this.htmlBody = htmlBody;
        }

        /**
         * Gets the from.
         *
         * @return from
         */
        public String getFrom() {
            return from;
        }

        /**
         * Sets the from with the specified from.
         *
         * @param from the specified from
         */
        public void setFrom(final String from) {
            this.from = from;
        }

        /**
         * Gets the subject.
         *
         * @return subject
         */
        public String getSubject() {
            return subject;
        }

        /**
         * Sets the subject with the specified subject.
         *
         * @param subject the specified subject
         */
        public void setSubject(final String subject) {
            this.subject = subject;
        }
    }
}
