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
 * This class defines all comment model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Jan 18, 2013
 * @since 0.3.1
 */
public final class Comment {

    /**
     * Comment.
     */
    public static final String COMMENT = "comment";

    /**
     * Comments.
     */
    public static final String COMMENTS = "comments";

    /**
     * Key of comment.
     */
    public static final String COMMENT_CONTENT = "commentContent";

    /**
     * Key of comment name.
     */
    public static final String COMMENT_NAME = "commentName";

    /**
     * Key of comment email.
     */
    public static final String COMMENT_EMAIL = "commentEmail";

    /**
     * Key of comment URL.
     */
    public static final String COMMENT_URL = "commentURL";

    /**
     * Key of comment sharp URL.
     */
    public static final String COMMENT_SHARP_URL = "commentSharpURL";

    /**
     * Key of comment date.
     */
    public static final String COMMENT_DATE = "commentDate";

    /**
     * Key of comment time.
     */
    public static final String COMMENT_TIME = "commentTime";

    /**
     * Key of comment thumbnail URL.
     */
    public static final String COMMENT_THUMBNAIL_URL = "commentThumbnailURL";

    /**
     * Key of original comment id.
     */
    public static final String COMMENT_ORIGINAL_COMMENT_ID = "commentOriginalCommentId";

    /**
     * Key of original comment user name.
     */
    public static final String COMMENT_ORIGINAL_COMMENT_NAME = "commentOriginalCommentName";

    /**
     * Key of comment on type.
     */
    public static final String COMMENT_ON_TYPE = "commentOnType";

    /**
     * Key of comment on id.
     */
    public static final String COMMENT_ON_ID = "commentOnId";

    /**
     * Private constructor.
     */
    private Comment() {}
}
