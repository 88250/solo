/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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

import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link PreferenceMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, May 29, 2018
 */
@Test(suiteName = "service")
public class PreferenceMgmtServiceTestCase extends AbstractTestCase {

    /**
     * Update Preference.
     *
     * @throws Exception exception
     */
    public void updatePreference() throws Exception {
        final PreferenceMgmtService preferenceMgmtService = getPreferenceMgmtService();
        final OptionQueryService optionQueryService = getOptionQueryService();
        JSONObject preference = optionQueryService.getPreference();

        Assert.assertEquals(preference.getString(Option.ID_C_BLOG_TITLE), "Solo 的个人博客");

        preference.put(Option.ID_C_BLOG_TITLE, "updated blog title");
        preferenceMgmtService.updatePreference(preference);

        preference = optionQueryService.getPreference();
        Assert.assertEquals(preference.getString(Option.ID_C_BLOG_TITLE), "updated blog title");
    }
}
