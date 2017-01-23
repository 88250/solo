/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Strings;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

/**
 * <a href="http://en.wikipedia.org/wiki/Markdown">Markdown</a> utilities.
 *
 * <p>
 * Uses the <a href="http://markdown.tautua.org/">MarkdownPapers</a> as the converter.</p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Nov 3, 2016
 * @since 0.4.5
 */
public final class Markdowns {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Markdowns.class.getName());

    /**
     * Converts the specified markdown text to HTML.
     *
     * @param markdownText the specified markdown text
     * @return converted HTML, returns {@code null} if the specified markdown text is "" or {@code null}, returns
     * "Markdown error" if exception
     */
    public static String toHTML(final String markdownText) {
        if (Strings.isEmptyOrNull(markdownText)) {
            return "";
        }

        final PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.ALL_OPTIONALS | Extensions.ALL_WITH_OPTIONALS, 5000);
        String ret = pegDownProcessor.markdownToHtml(markdownText);

        if (!StringUtils.startsWith(ret, "<p>")) {
            ret = "<p>" + ret + "</p>";
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private Markdowns() {
    }
}
