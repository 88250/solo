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
