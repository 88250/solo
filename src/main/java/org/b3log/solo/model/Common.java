/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
 * This class defines all common model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/e">Dongxu Wang</a>
 * @version 1.7.0.8, Jan 18, 2020
 * @since 0.3.1
 */
public final class Common {

    /**
     * Key of skin cookie name.
     */
    public static final String COOKIE_NAME_SKIN = "skin";

    /**
     * Key of mobile skin cookie name.
     */
    public static final String COOKIE_NAME_MOBILE_SKIN = "mobile-skin";

    /**
     * Key of favicon URL.
     */
    public static final String FAVICON_URL = "faviconURL";

    /**
     * Key of URL.
     */
    public static final String URL = "url";

    /**
     * Key of referer.
     */
    public static final String REFERER = "referer";

    /**
     * Key of upload msg.
     */
    public static final String UPLOAD_MSG = "uploadMsg";

    /**
     * Key of upload URL.
     */
    public static final String UPLOAD_URL = "uploadURL";

    /**
     * Key of upload token.
     */
    public static final String UPLOAD_TOKEN = "uploadToken";

    /**
     * Key of Lute engine available.
     */
    public static final String LUTE_AVAILABLE = "luteAvailable";

    /**
     * Key of keyword.
     */
    public static final String KEYWORD = "keyword";

    /**
     * Key of data.
     */
    public static final String DATA = "data";

    /**
     * Key of direction.
     */
    public static final String DIRECTION = "direction";

    /**
     * Most used tags.
     */
    public static final String MOST_USED_TAGS = "mostUsedTags";

    /**
     * Most used categories.
     */
    public static final String MOST_USED_CATEGORIES = "mostUsedCategories";

    /**
     * Recent articles.
     */
    public static final String RECENT_ARTICLES = "recentArticles";

    /**
     * Previous article permalink.
     */
    public static final String PREVIOUS_ARTICLE_PERMALINK = "previousArticlePermalink";

    /**
     * Next article permalink.
     */
    public static final String NEXT_ARTICLE_PERMALINK = "nextArticlePermalink";

    /**
     * Previous article title.
     */
    public static final String PREVIOUS_ARTICLE_TITLE = "previousArticleTitle";

    /**
     * Previous article abstract.
     */
    public static final String PREVIOUS_ARTICLE_ABSTRACT = "previousArticleAbstract";

    /**
     * Next article title.
     */
    public static final String NEXT_ARTICLE_TITLE = "nextArticleTitle";

    /**
     * Next article abstract.
     */
    public static final String NEXT_ARTICLE_ABSTRACT = "nextArticleAbstract";

    /**
     * Is index.
     */
    public static final String IS_INDEX = "isIndex";

    /**
     * Key of path.
     */
    public static final String PATH = "path";

    /**
     * Version.
     */
    public static final String VERSION = "version";

    /**
     * Static resource version.
     */
    public static final String STATIC_RESOURCE_VERSION = "staticResourceVersion";

    /**
     * Year.
     */
    public static final String YEAR = "year";

    /**
     * Key of flag a comment is an reply or not.
     */
    public static final String IS_REPLY = "isReply";

    /**
     * Key of page navigations.
     */
    public static final String PAGE_NAVIGATIONS = "pageNavigations";

    /**
     * Key of relevant articles.
     */
    public static final String RELEVANT_ARTICLES = "relevantArticles";

    /**
     * Key of random articles.
     */
    public static final String RANDOM_ARTICLES = "randomArticles";

    /**
     * Key of has updated.
     */
    public static final String HAS_UPDATED = "hasUpdated";

    /**
     * Author name.
     */
    public static final String AUTHOR_NAME = "authorName";

    /**
     * Author thumbnail URL.
     */
    public static final String AUTHOR_THUMBNAIL_URL = "authorThumbnailURL";

    /**
     * Author id.
     */
    public static final String AUTHOR_ID = "authorId";

    /**
     * Author role.
     */
    public static final String AUTHOR_ROLE = "authorRole";

    /**
     * Key of current user.
     */
    public static final String CURRENT_USER = "currentUser";

    /**
     * Key of admin user.
     */
    public static final String ADMIN_USER = "adminUser";

    /**
     * Key of is logged in.
     */
    public static final String IS_LOGGED_IN = "isLoggedIn";

    /**
     * Key of favicon API.
     */
    public static final String FAVICON_API = "faviconAPI";

    /**
     * Key of is mobile request.
     */
    public static final String IS_MOBILE_REQUEST = "isMobileRequest";

    /**
     * Key of login URL.
     */
    public static final String LOGIN_URL = "loginURL";

    /**
     * Key of logout URL.
     */
    public static final String LOGOUT_URL = "logoutURL";

    /**
     * Key of is administrator.
     */
    public static final String IS_ADMIN = "isAdmin";

    /**
     * Key of is visitor.
     */
    public static final String IS_VISITOR = "isVisitor";

    /**
     * Key of URI.
     */
    public static final String URI = "URI";

    /**
     * Key of post to community.
     */
    public static final String POST_TO_COMMUNITY = "postToCommunity";

    /**
     * Key of month name.
     */
    public static final String MONTH_NAME = "monthName";

    /**
     * Key of comment title (article/page).
     */
    public static final String COMMENT_TITLE = "commentTitle";

    /**
     * /admin-index.do#main.
     */
    public static final String ADMIN_INDEX_URI = "/admin-index.do#main";

    /**
     * Key of type.
     */
    public static final String TYPE = "type";

    /**
     * Article comment type.
     */
    public static final String ARTICLE_COMMENT_TYPE = "articleComment";

    /**
     * Page comment type.
     */
    public static final String PAGE_COMMENT_TYPE = "pageComment";

    /**
     * Key of top bar replacement flag.
     */
    public static final String TOP_BAR = "topBarReplacement";

    /**
     * Key of unused tags.
     */
    public static final String UNUSED_TAGS = "unusedTags";

    /**
     * Key of online visitor count.
     */
    public static final String ONLINE_VISITOR_CNT = "onlineVisitorCnt";

    /**
     * Key of article sign.
     */
    public static final String ARTICLE_SIGN = "articleSign";

    /**
     * Key of permalink.
     */
    public static final String PERMALINK = "permalink";

    /**
     * Key of commentable.
     */
    public static final String COMMENTABLE = "commentable";

    /**
     * Key of articles view password.
     */
    public static final String ARTICLES_VIEW_PWD = "articlesViewPwd";

    /**
     * Key of Gravatar.
     */
    public static final String GRAVATAR = "gravatar";

    /**
     * Private constructor.
     */
    private Common() {
    }
}
