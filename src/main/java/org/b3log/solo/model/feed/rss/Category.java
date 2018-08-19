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
package org.b3log.solo.model.feed.rss;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Category.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.0, Jul 5, 2018
 * @since 0.3.1
 */
public final class Category {

    /**
     * Category element.
     */
    private static final String CATEGORY_ELEMENT = "<category>${term}</category>";

    /**
     * Term.
     */
    private String term;

    /**
     * Gets the term.
     *
     * @return term
     */
    public String getTerm() {
        return term;
    }

    /**
     * Sets the term with the specified term.
     *
     * @param term the specified term
     */
    public void setTerm(final String term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return CATEGORY_ELEMENT.replace("${term}", StringEscapeUtils.escapeXml(term));
    }
}
