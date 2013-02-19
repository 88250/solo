/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.solo.util;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * {@link org.b3log.solo.util.Permalinks} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 29, 2011
 */
public final class PermalinksTestCase {

    /**
     * Test method for {@linkplain Permalinks#matchDefaultArticlePermalinkFormat(java.lang.String)}.
     */
    @Test
    public void matchDefaultArticlePermalinkFormat() {
        Assert.assertTrue(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/articles/1986/08/25/1234567890.html"));
        Assert.assertFalse(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/articles/1986/0/25/1234567890.html"));
        Assert.assertFalse(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/articles/1.html"));
        Assert.assertFalse(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/articles/1986/08/25/a.html"));
        Assert.assertFalse(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/articles/1986/aa/25/1234567890.html"));
        Assert.assertFalse(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/1986/aa/25/1234567890.html"));
        Assert.assertFalse(
                Permalinks.matchDefaultArticlePermalinkFormat(
                "/articles/1986/08/25/1234567890html"));

    }

    /**
     * Test method for {@linkplain Permalinks#matchDefaultPagePermalinkFormat(java.lang.String)}.
     */
    @Test
    public void matchDefaultPagePermalinkFormat() {
        Assert.assertTrue(Permalinks.matchDefaultPagePermalinkFormat(
                "/pages/1234567890.html"));
        Assert.assertFalse(Permalinks.matchDefaultPagePermalinkFormat(
                "/pages.html"));
        Assert.assertFalse(Permalinks.matchDefaultPagePermalinkFormat(
                "/1234567890.html"));
        Assert.assertFalse(Permalinks.matchDefaultPagePermalinkFormat(
                "/pages/a1234567890.html"));
    }
}
