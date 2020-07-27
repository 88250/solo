/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.model;

/**
 * This class defines all category model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, Sep 11, 2019
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
     * Key of category published article count.
     */
    public static final String CATEGORY_T_PUBLISHED_ARTICLE_COUNT = "categoryPublishedArticleCount";

    /**
     * Private constructor.
     */
    private Category() {
    }
}
