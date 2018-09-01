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

import org.b3log.solo.util.Markdowns;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * This class defines all article model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.6, Jun 25, 2017
 * @since 0.3.1
 */
public final class Article {

    /**
     * Article.
     */
    public static final String ARTICLE = "article";

    /**
     * Articles.
     */
    public static final String ARTICLES = "articles";

    /**
     * Key of title.
     */
    public static final String ARTICLE_TITLE = "articleTitle";

    /**
     * Key of abstract.
     */
    public static final String ARTICLE_ABSTRACT = "articleAbstract";

    /**
     * Key of content.
     */
    public static final String ARTICLE_CONTENT = "articleContent";

    /**
     * Key of create date.
     */
    public static final String ARTICLE_CREATE_DATE = "articleCreateDate";

    /**
     * Key of create time.
     */
    public static final String ARTICLE_CREATE_TIME = "articleCreateTime";

    /**
     * Key of update date.
     */
    public static final String ARTICLE_UPDATE_DATE = "articleUpdateDate";

    /**
     * Key of update time.
     */
    public static final String ARTICLE_UPDATE_TIME = "articleUpdateTime";

    /**
     * Key of tags.
     */
    public static final String ARTICLE_TAGS_REF = "articleTags";

    /**
     * Key of comment count.
     */
    public static final String ARTICLE_COMMENT_COUNT = "articleCommentCount";

    /**
     * Key of view count.
     */
    public static final String ARTICLE_VIEW_COUNT = "articleViewCount";

    /**
     * Key of comments.
     */
    public static final String ARTICLE_COMMENTS_REF = "articleComments";

    /**
     * Key of sign id.
     */
    public static final String ARTICLE_SIGN_ID = "articleSignId";

    /**
     * Key of permalink.
     */
    public static final String ARTICLE_PERMALINK = "articlePermalink";

    /**
     * Key of put top.
     */
    public static final String ARTICLE_PUT_TOP = "articlePutTop";

    /**
     * Key of is published.
     */
    public static final String ARTICLE_IS_PUBLISHED = "articleIsPublished";

    /**
     * Key of author email.
     */
    public static final String ARTICLE_AUTHOR_EMAIL = "articleAuthorEmail";

    /**
     * Key of had been published.
     */
    public static final String ARTICLE_HAD_BEEN_PUBLISHED = "articleHadBeenPublished";

    /**
     * Key of random double.
     */
    public static final String ARTICLE_RANDOM_DOUBLE = "articleRandomDouble";

    /**
     * Key of comment-able.
     */
    public static final String ARTICLE_COMMENTABLE = "articleCommentable";

    /**
     * Key of view password.
     */
    public static final String ARTICLE_VIEW_PWD = "articleViewPwd";

    /**
     * Key of article editor type.
     */
    public static final String ARTICLE_EDITOR_TYPE = "articleEditorType";

    //// constants

    /**
     * Article abstract length.
     */
    private static final int ARTICLE_ABSTRACT_LENGTH = 500;

    /**
     * Private constructor.
     */
    private Article() {
    }

    /**
     * Gets the abstract of the specified content.
     *
     * @param content the specified content
     * @return the abstract
     */
    public static String getAbstract(final String content) {
        final String plainTextContent = Jsoup.clean(Markdowns.toHTML(content), Whitelist.none());
        if (plainTextContent.length() > ARTICLE_ABSTRACT_LENGTH) {
            return plainTextContent.substring(0, ARTICLE_ABSTRACT_LENGTH) + "....";
        }

        return plainTextContent;
    }
}
