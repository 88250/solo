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
 * This class defines all page model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, Apr 19, 2019
 * @since 0.3.1
 */
public final class Page {

    /**
     * Page.
     */
    public static final String PAGE = "page";

    /**
     * Pages.
     */
    public static final String PAGES = "pages";

    /**
     * Key of title.
     */
    public static final String PAGE_TITLE = "pageTitle";

    /**
     * Key of order.
     */
    public static final String PAGE_ORDER = "pageOrder";

    /**
     * Key of permalink.
     */
    public static final String PAGE_PERMALINK = "pagePermalink";

    /**
     * Key of open target.
     * <p>
     * Available values:
     * <ul>
     * <li>_blank</li>
     * Opens the linked document in a new window or tab.
     * <li>_self</li>
     * Opens the linked document in the same frame as it was clicked (this is default).
     * <li>_parent</li>
     * Opens the linked document in the parent frame.
     * <li>_top</li>
     * Opens the linked document in the full body of the window.
     * <li><i>frame name</i></li>
     * Opens the linked document in a named frame.
     * </ul>
     * See <a href="http://www.w3schools.com/tags/att_a_target.asp">here</a> for more details.
     * </p>
     */
    public static final String PAGE_OPEN_TARGET = "pageOpenTarget";

    /**
     * Key of icon URL.
     */
    public static final String PAGE_ICON = "pageIcon";

    /**
     * Private constructor.
     */
    private Page() {
    }
}
