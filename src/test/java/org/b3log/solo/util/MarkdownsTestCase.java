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
package org.b3log.solo.util;

import org.apache.commons.io.IOUtils;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

/**
 * {@link org.b3log.solo.util.Markdowns} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Dec 16, 2017
 * @since 0.4.5
 */
public final class MarkdownsTestCase {

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

        markdownText = "# Solo Markdown Editor";
        html = Markdowns.toHTML(markdownText);

        final URL testFile = MarkdownsTestCase.class.getResource("/markdown_syntax.text");
        final String path = URLDecoder.decode(testFile.getPath(), "UTF-8");
        //System.out.println(path);

        final StringBuilder markdownTextBuilder = new StringBuilder();
        @SuppressWarnings("unchecked") final List<String> lines = IOUtils.readLines(new FileInputStream(path));

        for (final String line : lines) {
            markdownTextBuilder.append(line).append(Strings.LINE_SEPARATOR);
        }

        markdownText = markdownTextBuilder.toString();
        //System.out.println(markdownText);

        Stopwatchs.start("Markdowning");
        html = Markdowns.toHTML(markdownText);
        Stopwatchs.end();

        //System.out.println(html);

        //System.out.println("Stopwatch: ");
        //System.out.println(Stopwatchs.getTimingStat());

        // HTML entity test
        markdownText = "The first: &#39; <br/> The second: &AElig;";
        html = Markdowns.toHTML(markdownText);

        Assert.assertEquals(html, "<p>The first: ' <br> The second: Æ</p>");
    }
}
