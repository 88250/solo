/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
