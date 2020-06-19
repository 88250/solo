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
package org.b3log.solo.util;

/**
 * Status code constants and utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jun 19, 2020
 * @since 4.2.0
 */
public final class StatusCodes {

    /**
     * Indicates an error occurred.
     */
    public static final int ERR = -1;

    /**
     * Indicates success.
     */
    public static final int SUCC = 0;

    /**
     * Indicates not found.
     */
    public static final int NOT_FOUND = 1;

    private StatusCodes() {
    }
}
