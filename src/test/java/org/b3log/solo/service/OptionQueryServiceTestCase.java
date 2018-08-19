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

import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Option;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link OptionQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jul 16, 2017
 * @since 0.6.0
 */
@Test(suiteName = "service")
public class OptionQueryServiceTestCase extends AbstractTestCase {

    /**
     * Gets.
     *
     * @throws Exception exception
     */
    @Test
    public void get() throws Exception {
        // Check
        final OptionQueryService optionQueryService = getOptionQueryService();

        JSONObject options = optionQueryService.getOptions(Option.CATEGORY_C_BROADCAST);
        Assert.assertNull(options);

        // Add one
        final OptionMgmtService optionMgmtService = getOptionMgmtService();

        JSONObject option = new JSONObject();
        option.put(Keys.OBJECT_ID, Option.ID_C_BROADCAST_CHANCE_EXPIRATION_TIME);
        option.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_BROADCAST);
        option.put(Option.OPTION_VALUE, 5L);

        final String id = optionMgmtService.addOrUpdateOption(option);
        Assert.assertNotNull(id);

        // Check again

        option = optionQueryService.getOptionById(Option.ID_C_BROADCAST_CHANCE_EXPIRATION_TIME);
        Assert.assertNotNull(option);

        options = optionQueryService.getOptions(Option.CATEGORY_C_BROADCAST);
        Assert.assertNotNull(options);
        Assert.assertEquals(options.optLong(Option.ID_C_BROADCAST_CHANCE_EXPIRATION_TIME), 5L);
    }
}
