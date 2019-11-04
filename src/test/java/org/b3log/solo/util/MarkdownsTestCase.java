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
package org.b3log.solo.util;

import org.b3log.latke.Latkes;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * {@link org.b3log.solo.util.Markdowns} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.5, Feb 11, 2019
 * @since 0.4.5
 */
public final class MarkdownsTestCase {

    @BeforeClass
    public void beforeClass() {
        Latkes.init();
    }

    /**
     * Test method for {@linkplain Markdowns#toHTML(java.lang.String)}.
     *
     * @throws Exception exception
     */
    @Test
    public void toHTML() throws Exception {
        String markdownText = "";
        String html = Markdowns.toHTML(markdownText);

        Assert.assertEquals(html, "");

        markdownText = "Solo Markdown";
        html = Markdowns.toHTML(markdownText);

        Assert.assertEquals(html, "<p>Solo Markdown</p>");
    }
}
