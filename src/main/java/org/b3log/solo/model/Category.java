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
package org.b3log.solo.model;

/**
 * This class defines all category model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Mar 30, 2017
 * @since 2.0.0
 */
public final class Category {

    /**
     * Category.
     */
    public static final String CATEGORY = "category";

    /**
     * Categories.
     */
    public static final String CATEGORIES = "categories";

    /**
     * Key of category title.
     */
    public static final String CATEGORY_TITLE = "categoryTitle";

    /**
     * Key of category URI.
     */
    public static final String CATEGORY_URI = "categoryURI";

    /**
     * Key of category description.
     */
    public static final String CATEGORY_DESCRIPTION = "categoryDescription";

    /**
     * Key of category order.
     */
    public static final String CATEGORY_ORDER = "categoryOrder";

    /**
     * Key of category tag count.
     */
    public static final String CATEGORY_TAG_CNT = "categoryTagCnt";

    //// Transient ////
    /**
     * Key of category tags.
     */
    public static final String CATEGORY_T_TAGS = "categoryTags";

    /**
     * Private constructor.
     */
    private Category() {
    }
}
