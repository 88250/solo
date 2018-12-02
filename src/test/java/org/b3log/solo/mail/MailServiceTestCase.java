/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.solo.mail;

import org.b3log.latke.Latkes;
import org.b3log.solo.mail.MailService.Message;
import org.testng.annotations.Test;

/**
 * {@link MailService} test case.
 *
 * @author <a href="https://hacpai.com/member/jiangzezhou">zezhou jiang</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Dec 2, 2018
 */
public final class MailServiceTestCase {

    static {
        Latkes.init();
    }

    /**
     * Tests mail sending.
     *
     * @throws Exception exception
     */
    @Test
    public void testSendMail() throws Exception {
        System.out.println("testSendMail");
        final MailService mailService = MailServiceFactory.getMailService();

        final Message message = new Message();
        message.setFrom("b3log.solo@gmail.com");
        message.setSubject("Latke Mail Service[local] Test");
        message.setHtmlBody("<htmL><body>测试</body><html>");
        message.addRecipient("d@b3log.org");

        mailService.send(message);

        Thread.sleep(10000); // Waiting for sending....
    }
}