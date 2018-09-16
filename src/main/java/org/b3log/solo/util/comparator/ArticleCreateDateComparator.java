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

/**
 * Article comparator by create date.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 16, 2018
 */
public final class ArticleCreateDateComparator implements Comparator<JSONObject> {

    /**
     * Package default constructor.
     */
    ArticleCreateDateComparator() {
    }

    @Override
    public int compare(final JSONObject article1, final JSONObject article2) {
        try {
            final long date1 = article1.getLong(Article.ARTICLE_CREATED);
            final long date2 = article2.getLong(Article.ARTICLE_CREATED);

            return (int) (date2 - date1);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
