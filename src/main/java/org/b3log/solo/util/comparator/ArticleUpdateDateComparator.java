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
package org.b3log.solo.util.comparator;

import org.b3log.solo.model.Article;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Date;

/**
 * Article comparator by update date.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 30, 2010
 */
public final class ArticleUpdateDateComparator implements Comparator<JSONObject> {

    /**
     * Package default constructor.
     */
    ArticleUpdateDateComparator() {
    }

    @Override
    public int compare(final JSONObject article1, final JSONObject article2) {
        try {
            final Date date1 = (Date) article1.get(Article.ARTICLE_UPDATE_DATE);
            final Date date2 = (Date) article2.get(Article.ARTICLE_UPDATE_DATE);

            return date2.compareTo(date1);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
