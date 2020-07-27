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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link org.b3log.solo.service.PermalinkQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Apr 19, 2019
 * @since 0.6.1
 */
public final class PermalinkQueryServiceTestCase {

    /**
     * Test method for {@linkplain PermalinkQueryService#matchDefaultArticlePermalinkFormat(java.lang.String)}.
     */
    @Test
    public void matchDefaultArticlePermalinkFormat() {
        Assert.assertTrue(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/articles/1986/08/25/1234567890.html"));
        Assert.assertFalse(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/articles/1986/0/25/1234567890.html"));
        Assert.assertFalse(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/articles/1.html"));
        Assert.assertFalse(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/articles/1986/08/25/a.html"));
        Assert.assertFalse(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/articles/1986/aa/25/1234567890.html"));
        Assert.assertFalse(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/1986/aa/25/1234567890.html"));
        Assert.assertFalse(PermalinkQueryService.matchDefaultArticlePermalinkFormat("/articles/1986/08/25/1234567890html"));
    }
}
