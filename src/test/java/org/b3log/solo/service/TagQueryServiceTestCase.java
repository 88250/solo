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
import org.b3log.solo.model.Tag;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link TagQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Nov 9, 2016
 */
@Test(suiteName = "service")
public class TagQueryServiceTestCase extends AbstractTestCase {

    /**
     * Get Tags.
     *
     * @throws Exception exception
     */
    public void getTags() throws Exception {
        final TagQueryService tagQueryService = getTagQueryService();

        final List<JSONObject> tags = tagQueryService.getTags();
        Assert.assertNotNull(tags);
        Assert.assertEquals(tags.size(), 1);
        Assert.assertEquals(tags.get(0).getString(Tag.TAG_TITLE), "Solo");
    }

    /**
     * Get Tag By Title.
     *
     * @throws Exception exception
     */
    public void getTagByTitle() throws Exception {
        final TagQueryService tagQueryService = getTagQueryService();

        final JSONObject result = tagQueryService.getTagByTitle("Solo");
        Assert.assertNotNull(result);

        final JSONObject tag = result.getJSONObject(Tag.TAG);
        Assert.assertNotNull(tag);
        Assert.assertEquals(tag.getString(Tag.TAG_TITLE), "Solo");

    }
}
