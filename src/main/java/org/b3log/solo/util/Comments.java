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

import org.b3log.solo.model.Article;
import org.b3log.solo.model.Page;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.12, Jun 4, 2013
 * @since 0.3.1
 */
public final class Comments {

    /**
     * Gets comment sharp URL with the specified page and comment id.
     *
     * @param page      the specified page
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    public static String getCommentSharpURLForPage(final JSONObject page, final String commentId) throws JSONException {
        return page.getString(Page.PAGE_PERMALINK) + "#" + commentId;
    }

    /**
     * Gets comment sharp URL with the specified article and comment id.
     *
     * @param article   the specified article
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    public static String getCommentSharpURLForArticle(final JSONObject article, final String commentId) throws JSONException {
        final String articleLink = article.getString(Article.ARTICLE_PERMALINK);

        return articleLink + "#" + commentId;
    }

    /**
     * Private constructor.
     */
    private Comments() {
    }
}
