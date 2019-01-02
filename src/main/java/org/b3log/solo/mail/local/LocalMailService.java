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

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.solo.mail.MailService;

/**
 * Implementation of the {@link MailService} interface.
 *
 * @author <a href="https://hacpai.com/member/jiangzezhou">zezhou jiang</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Dec 2, 2018
 */
public final class LocalMailService implements MailService {

    @Override
    public void send(final Message message) {
        new Thread(() -> {
            try {
                new MailSender().sendMail(message);
            } catch (final Exception e) {
                Logger.getLogger(LocalMailService.class).log(Level.ERROR, "Sends mail failed", e);
            }
        }).start();
    }
}
