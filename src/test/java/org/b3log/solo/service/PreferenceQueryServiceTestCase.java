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
package org.b3log.solo.service;

import org.b3log.latke.model.User;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link PreferenceQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, May 29, 2018
 */
@Test(suiteName = "service")
public class PreferenceQueryServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Get Preference.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getPreference() throws Exception {
        final PreferenceQueryService preferenceQueryService = getPreferenceQueryService();
        final JSONObject preference = preferenceQueryService.getPreference();

        Assert.assertEquals(preference.getString(Option.ID_C_BLOG_TITLE), "Admin 的个人博客");
    }

    /**
     * Get Reply Notification Template.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getReplyNotificationTemplate() throws Exception {
        final PreferenceQueryService preferenceQueryService = getPreferenceQueryService();
        final JSONObject replyNotificationTemplate = preferenceQueryService.getReplyNotificationTemplate();

        Assert.assertEquals(replyNotificationTemplate.toString(), Option.DefaultPreference.DEFAULT_REPLY_NOTIFICATION_TEMPLATE);
    }
}
