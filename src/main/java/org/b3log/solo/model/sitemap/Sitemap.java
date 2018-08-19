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
package org.b3log.solo.model.sitemap;


import java.util.ArrayList;
import java.util.List;


/**
 * Sitemap.
 *
 * <p>
 * See <a href="http://www.sitemaps.org/protocol.php">Sitemap XML format</a> 
 * for more details.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Sep 22, 2011
 * @see URL
 * @since 0.3.1
 */
public final class Sitemap {

    /**
     * Start document.
     */
    private static final String START_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * Start URL set element.
     */
    private static final String START_URL_SET_ELEMENT = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";

    /**
     * End URL set element.
     */
    private static final String END_URL_SET_ELEMENT = "</urlset>";

    /**
     * URLs.
     */
    private List<URL> urls = new ArrayList<>();

    /**
     * Adds the specified url.
     * 
     * @param url the specified url
     */
    public void addURL(final URL url) {
        urls.add(url);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(START_DOCUMENT);
        stringBuilder.append(START_URL_SET_ELEMENT);

        for (final URL url : urls) {
            stringBuilder.append(url.toString());
        }

        stringBuilder.append(END_URL_SET_ELEMENT);

        return stringBuilder.toString();
    }
}
