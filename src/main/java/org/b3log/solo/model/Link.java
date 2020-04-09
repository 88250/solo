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
 * This class defines all link model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Oct 23, 2019
 * @since 0.3.1
 */
public final class Link {

    /**
     * Link.
     */
    public static final String LINK = "link";

    /**
     * Links.
     */
    public static final String LINKS = "links";

    /**
     * Key of title.
     */
    public static final String LINK_TITLE = "linkTitle";

    /**
     * Key of address.
     */
    public static final String LINK_ADDRESS = "linkAddress";

    /**
     * Key of description.
     */
    public static final String LINK_DESCRIPTION = "linkDescription";

    /**
     * Key of icon URL.
     */
    public static final String LINK_ICON = "linkIcon";

    /**
     * Key of order.
     */
    public static final String LINK_ORDER = "linkOrder";

    /**
     * Private constructor.
     */
    private Link() {
    }
}
