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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class defines all comment model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.2, Apr 19, 2019
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
     * Key of comment URL.
     */
    public static final String COMMENT_URL = "commentURL";

    /**
     * Key of comment sharp URL.
     */
    public static final String COMMENT_SHARP_URL = "commentSharpURL";

    /**
     * Key of comment created at.
     */
    public static final String COMMENT_CREATED = "commentCreated";

    /**
     * Key of comment date.
     */
    public static final String COMMENT_T_DATE = "commentDate";

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
     * Key of comment on id.
     */
    public static final String COMMENT_ON_ID = "commentOnId";

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
        return article.getString(Article.ARTICLE_PERMALINK) + "#" + commentId;
    }

    /**
     * Private constructor.
     */
    private Comment() {
    }
}
