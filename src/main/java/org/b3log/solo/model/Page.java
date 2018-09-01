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
 * This class defines all page model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Sep 21, 2017
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
     * Key of content.
     */
    public static final String PAGE_CONTENT = "pageContent";

    /**
     * Key of order.
     */
    public static final String PAGE_ORDER = "pageOrder";

    /**
     * Key of comment count.
     */
    public static final String PAGE_COMMENT_COUNT = "pageCommentCount";

    /**
     * Key of permalink.
     */
    public static final String PAGE_PERMALINK = "pagePermalink";

    /**
     * Key of comments.
     */
    public static final String PAGE_COMMENTS_REF = "pageComments";

    /**
     * Key of comment-able.
     */
    public static final String PAGE_COMMENTABLE = "pageCommentable";

    /**
     * Key of page type.
     * <p>
     * Available values:
     * <ul>
     * <li>link</li>
     * No contents (pageContent), if users clicked, just jump to the given address specified by the permalink.
     * <li>page</li>
     * A normal customized page.
     * </ul>
     * </p>
     */
    public static final String PAGE_TYPE = "pageType";

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
     * Key of page editor type.
     */
    public static final String PAGE_EDITOR_TYPE = "pageEditorType";

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
