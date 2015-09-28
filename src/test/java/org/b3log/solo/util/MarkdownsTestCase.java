/*
 * Copyright (c) 2010-2015, b3log.org
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

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * {@link org.b3log.solo.util.Markdowns} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, May 16, 2013
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

        Assert.assertNull(html);

        markdownText = "# Solo Markdown Editor";
        html = Markdowns.toHTML(markdownText);

        System.out.println(html);

        System.out.println(MarkdownsTestCase.class.getResource("/"));
        final URL testFile = MarkdownsTestCase.class.getResource("/markdown_syntax.text");
        final String path = URLDecoder.decode(testFile.getPath(), "UTF-8");
        System.out.println(path);

        final StringBuilder markdownTextBuilder = new StringBuilder();
        @SuppressWarnings("unchecked")
        final List<String> lines = IOUtils.readLines(new FileInputStream(path));
        
        for (final String line : lines) {
            markdownTextBuilder.append(line).append(Strings.LINE_SEPARATOR);
        }
        
        markdownText = markdownTextBuilder.toString();
        System.out.println(markdownText);

        Stopwatchs.start("Markdowning");
        html = Markdowns.toHTML(markdownText);
        Stopwatchs.end();
        
        System.out.println(html);
        
        System.out.println("Stopwatch: ");
        System.out.println(Stopwatchs.getTimingStat());
        
        // HTML entity test
        markdownText = "The first: &#39; <br/> The second: &AElig;";
        html = Markdowns.toHTML(markdownText);
        
        System.out.println(html);
    }
}
