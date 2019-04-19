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
