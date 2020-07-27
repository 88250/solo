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

import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link OptionMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 29, 2019
 * @since 0.6.0
 */
@Test(suiteName = "service")
public class OptionMgmtServiceTestCase extends AbstractTestCase {

    /**
     * Add.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final OptionMgmtService optionMgmtService = getOptionMgmtService();

        final JSONObject option = new JSONObject();
        option.put(Keys.OBJECT_ID, Option.ID_C_BLOG_TITLE);
        option.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        option.put(Option.OPTION_VALUE, 0L);

        final String id = optionMgmtService.addOrUpdateOption(option);
        //System.out.println(id);
        Assert.assertNotNull(id);

        final JSONObject opt = getOptionQueryService().getOptionById(Option.ID_C_BLOG_TITLE);
        Assert.assertEquals(opt.getInt(Option.OPTION_VALUE), 0L);
    }

    /**
     * Update.
     *
     * @throws Exception exception
     */
    @Test
    public void update() throws Exception {
        final OptionMgmtService optionMgmtService = getOptionMgmtService();

        JSONObject option = new JSONObject();
        option.put(Keys.OBJECT_ID, Option.ID_C_BLOG_TITLE);
        option.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        option.put(Option.OPTION_VALUE, 0L);

        final String id = optionMgmtService.addOrUpdateOption(option); // Add
        //System.out.println(id);
        Assert.assertNotNull(id);

        option = new JSONObject();
        option.put(Keys.OBJECT_ID, Option.ID_C_BLOG_TITLE);
        option.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        option.put(Option.OPTION_VALUE, 1L);

        optionMgmtService.addOrUpdateOption(option); // Update

        final JSONObject opt = getOptionQueryService().getOptionById(Option.ID_C_BLOG_TITLE);
        Assert.assertEquals(opt.getInt(Option.OPTION_VALUE), 1L);
    }

    /**
     * Remove.
     *
     * @throws Exception exception
     */
    @Test
    public void remove() throws Exception {
        final OptionMgmtService optionMgmtService = getOptionMgmtService();

        final JSONObject option = new JSONObject();
        option.put(Keys.OBJECT_ID, Option.ID_C_BLOG_TITLE);
        option.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        option.put(Option.OPTION_VALUE, 0L);

        final String id = optionMgmtService.addOrUpdateOption(option);
        Assert.assertNotNull(id);

        optionMgmtService.removeOption(id);

        final JSONObject opt = getOptionQueryService().getOptionById(id);
        Assert.assertNull(opt);
    }
}
