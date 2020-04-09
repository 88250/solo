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
 * This class defines all archive date model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Jan 28, 2019
 */
public final class ArchiveDate {

    /**
     * Archive date.
     */
    public static final String ARCHIVE_DATE = "archiveDate";

    /**
     * Archive dates.
     */
    public static final String ARCHIVE_DATES = "archiveDates";

    /**
     * Archive time.
     */
    public static final String ARCHIVE_TIME = "archiveTime";

    //// Transient ////
    /**
     * Key of archive date article count.
     */
    public static final String ARCHIVE_DATE_T_PUBLISHED_ARTICLE_COUNT = "archiveDatePublishedArticleCount";

    /**
     * Archive date year.
     */
    public static final String ARCHIVE_DATE_YEAR = "archiveDateYear";

    /**
     * Archive date month.
     */
    public static final String ARCHIVE_DATE_MONTH = "archiveDateMonth";

    /**
     * Private constructor.
     */
    private ArchiveDate() {
    }
}
