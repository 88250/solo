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
package org.b3log.solo.mail.local;

import org.apache.commons.lang.StringUtils;
import org.b3log.solo.mail.MailService.Message;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeUtility;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Email sender.
 *
 * @author <a href="https://hacpai.com/member/jiangzezhou">zezhou jiang</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.4, Feb 25, 2015
 */
final class MailSender {

    /**
     * Mail configurations.
     *
     * <ul>
     * <li>mail.user</li>
     * <li>mail.password</li>
     * <li>mail.smtp.host</li>
     * <li>mail.smtp.auth</li>
     * <li>mail.smtp.port</li>
     * <li>mail.smtp.starttls.enable</li>
     * <li>mail.debug</li>
     * <li>mail.smtp.socketFactory.class</li>
     * <li>mail.smtp.socketFactory.fallback</li>
     * <li>mail.smtp.socketFactory.port</li>
     * </ul>
     */
    private final ResourceBundle mailProperties = ResourceBundle.getBundle("mail");

    /**
     * Create session based on the mail properties.
     *
     * @return session session from mail properties
     */
    private Session getSession() {
        final Properties props = new Properties();

        props.setProperty("mail.smtp.host", mailProperties.getString("mail.smtp.host"));

        String auth = "true";
        if (mailProperties.containsKey("mail.smtp.auth")) {
            auth = mailProperties.getString("mail.smtp.auth");
        }
        props.setProperty("mail.smtp.auth", auth);

        props.setProperty("mail.smtp.port", mailProperties.getString("mail.smtp.port"));

        String starttls = "true";
        if (mailProperties.containsKey("mail.smtp.starttls.enable")) {
            starttls = mailProperties.getString("mail.smtp.starttls.enable");
        }
        props.put("mail.smtp.starttls.enable", starttls);

        props.put("mail.debug", mailProperties.getString("mail.debug"));
        props.put("mail.smtp.socketFactory.class", mailProperties.getString("mail.smtp.socketFactory.class"));
        props.put("mail.smtp.socketFactory.fallback", mailProperties.getString("mail.smtp.socketFactory.fallback"));
        props.put("mail.smtp.socketFactory.port", mailProperties.getString("mail.smtp.socketFactory.port"));

        return Session.getInstance(props, new SMTPAuthenticator());
    }

    /**
     * Converts the specified message into a {@link javax.mail.Message
     * javax.mail.Message}.
     *
     * @param message the specified message
     * @return a {@link javax.mail.internet.MimeMessage}
     * @throws Exception if converts error
     */
    public javax.mail.Message convert2JavaMailMsg(final Message message) throws Exception {
        if (null == message) {
            return null;
        }

        if (StringUtils.isBlank(message.getFrom())) {
            throw new MessagingException("Null from");
        }

        if (null == message.getRecipients() || message.getRecipients().isEmpty()) {
            throw new MessagingException("Null recipients");
        }

        final MimeMessage ret = new MimeMessage(getSession());

        ret.setFrom(new InternetAddress(message.getFrom()));
        final String subject = message.getSubject();

        ret.setSubject(MimeUtility.encodeText(subject != null ? subject : "", "UTF-8", "B"));
        final String htmlBody = message.getHtmlBody();

        ret.setContent(htmlBody != null ? htmlBody : "", "text/html;charset=UTF-8");
        ret.addRecipients(RecipientType.TO, transformRecipients(message.getRecipients()));

        return ret;
    }

    /**
     * Transport recipients to InternetAddress array.
     *
     * @param recipients the set of all recipients
     * @return InternetAddress array of all recipients internetAddress
     * @throws MessagingException messagingException from javax.mail
     */
    private InternetAddress[] transformRecipients(final Set<String> recipients) throws MessagingException {
        if (recipients.isEmpty()) {
            throw new MessagingException("recipients of mail should not be empty");
        }

        final InternetAddress[] ret = new InternetAddress[recipients.size()];
        int i = 0;

        for (String recipient : recipients) {
            ret[i] = new InternetAddress(recipient);
            i++;
        }

        return ret;
    }

    /**
     * Sends email.
     *
     * @param message the specified message
     * @throws Exception message exception
     */
    void sendMail(final Message message) throws Exception {
        final javax.mail.Message msg = convert2JavaMailMsg(message);

        Transport.send(msg);
    }

    /**
     * Inner class for Authenticator.
     */
    private class SMTPAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailProperties.getString("mail.user"), mailProperties.getString("mail.password"));
        }
    }
}
