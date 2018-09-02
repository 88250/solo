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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Sitemap URL.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.0, May 18, 2018
 * @since 0.3.1
 */
public final class URL {

    /**
     * Start URL element.
     */
    private static final String START_URL_ELEMENT = "<url>";

    /**
     * End URL element.
     */
    private static final String END_URL_ELEMENT = "</url>";

    /**
     * Start loc element.
     */
    private static final String START_LOC_ELEMENT = "<loc>";

    /**
     * End loc element.
     */
    private static final String END_LOC_ELEMENT = "</loc>";

    /**
     * Start last mod element.
     */
    private static final String START_LAST_MOD_ELEMENT = "<lastmod>";

    /**
     * End last mod element.
     */
    private static final String END_LAST_MOD_ELEMENT = "</lastmod>";

    /**
     * Loc.
     */
    private String loc;

    /**
     * Last mod.
     */
    private String lastMod;

    /**
     * Gets the last modified.
     *
     * @return last modified
     */
    public String getLastMod() {
        return lastMod;
    }

    /**
     * Sets the last modified with the specified last modified.
     *
     * @param lastMod the specified modified
     */
    public void setLastMod(final String lastMod) {
        this.lastMod = lastMod;
    }

    /**
     * Gets the loc.
     *
     * @return loc
     */
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the loc with the specified loc.
     *
     * @param loc the specified loc
     */
    public void setLoc(final String loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(START_URL_ELEMENT);

        stringBuilder.append(START_LOC_ELEMENT);
        stringBuilder.append(StringEscapeUtils.escapeXml(loc));
        stringBuilder.append(END_LOC_ELEMENT);

        if (StringUtils.isNotBlank(lastMod)) {
            stringBuilder.append(START_LAST_MOD_ELEMENT);
            stringBuilder.append(lastMod);
            stringBuilder.append(END_LAST_MOD_ELEMENT);
        }

        stringBuilder.append(END_URL_ELEMENT);

        return stringBuilder.toString();
    }
}
