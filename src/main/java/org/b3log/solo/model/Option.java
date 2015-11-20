/*
 * Copyright (c) 2010-2015, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.model;

/**
 * This class defines option model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.1, Nov 20, 2015
 * @since 0.6.0
 */
public final class Option {

    /**
     * Option.
     */
    public static final String OPTION = "option";

    /**
     * Options.
     */
    public static final String OPTIONS = "options";

    /**
     * Key of option value.
     */
    public static final String OPTION_VALUE = "optionValue";

    /**
     * Key of option category.
     */
    public static final String OPTION_CATEGORY = "optionCategory";

    // oId constants
    /**
     * Key of broadcast chance expiration time.
     */
    public static final String ID_C_BROADCAST_CHANCE_EXPIRATION_TIME = "broadcastChanceExpirationTime";

    /**
     * Key of Qiniu access key.
     */
    public static final String ID_C_QINIU_ACCESS_KEY = "qiniuAccessKey";

    /**
     * Key of Qiniu secret key.
     */
    public static final String ID_C_QINIU_SECRET_KEY = "qiniuSecretKey";

    /**
     * Key of Qiniu domain.
     */
    public static final String ID_C_QINIU_DOMAIN = "qiniuDomain";

    /**
     * Key of Qiniu bucket.
     */
    public static final String ID_C_QINIU_BUCKET = "qiniuBucket";

    /**
     * Key of blog title.
     */
    public static final String ID_C_BLOG_TITLE = "blogTitle";

    /**
     * Key of blog subtitle.
     */
    public static final String ID_C_BLOG_SUBTITLE = "blogSubtitle";

    /**
     * Key of relevant articles display count.
     */
    public static final String ID_C_RELEVANT_ARTICLES_DISPLAY_CNT = "relevantArticlesDisplayCount";

    /**
     * Key of random articles display count.
     */
    public static final String ID_C_RANDOM_ARTICLES_DISPLAY_CNT = "randomArticlesDisplayCount";

    /**
     * Key of external relevant articles display count.
     */
    public static final String ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT = "externalRelevantArticlesDisplayCount";

    /**
     * Key of recent article display count.
     */
    public static final String ID_C_RECENT_ARTICLE_DISPLAY_CNT = "recentArticleDisplayCount";

    /**
     * Key of recent comment display count.
     */
    public static final String ID_C_RECENT_COMMENT_DISPLAY_CNT = "recentCommentDisplayCount";

    /**
     * Key of most used tag display count.
     */
    public static final String ID_C_MOST_USED_TAG_DISPLAY_CNT = "mostUsedTagDisplayCount";

    /**
     * Key of most comment article display count.
     */
    public static final String ID_C_MOST_COMMENT_ARTICLE_DISPLAY_CNT = "mostCommentArticleDisplayCount";

    /**
     * Key of most view article display count.
     */
    public static final String ID_C_MOST_VIEW_ARTICLE_DISPLAY_CNT = "mostViewArticleDisplayCount";

    /**
     * Key of article list display count.
     */
    public static final String ID_C_ARTICLE_LIST_DISPLAY_COUNT = "articleListDisplayCount";

    /**
     * Key of article list pagination window size.
     */
    public static final String ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE = "articleListPaginationWindowSize";

    /**
     * Key of administrator's email.
     */
    public static final String ID_C_ADMIN_EMAIL = "adminEmail";

    /**
     * Key of locale string.
     */
    public static final String ID_C_LOCALE_STRING = "localeString";

    /**
     * Key of time zone id.
     */
    public static final String ID_C_TIME_ZONE_ID = "timeZoneId";

    /**
     * Key of notice board.
     */
    public static final String ID_C_NOTICE_BOARD = "noticeBoard";

    /**
     * Key of HTML head.
     */
    public static final String ID_C_HTML_HEAD = "htmlHead";

    /**
     * Key of meta keywords.
     */
    public static final String ID_C_META_KEYWORDS = "metaKeywords";

    /**
     * Key of meta description.
     */
    public static final String ID_C_META_DESCRIPTION = "metaDescription";

    /**
     * Key of article update hint flag.
     */
    public static final String ID_C_ENABLE_ARTICLE_UPDATE_HINT = "enableArticleUpdateHint";

    /**
     * Key of signs.
     */
    public static final String ID_C_SIGNS = "signs";

    /**
     * Key of key of Solo.
     */
    public static final String ID_C_KEY_OF_SOLO = "keyOfSolo";

    /**
     * Key of allow visit draft via permalink.
     */
    public static final String ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK = "allowVisitDraftViaPermalink";
    
    /**
     * Key of allow register.
     */
    public static final String ID_C_ALLOW_REGISTER = "allowRegister";

    /**
     * Key of version.
     */
    public static final String ID_C_VERSION = "version";

    /**
     * Key of article list display style.
     *
     * <p>
     * Optional values:
     * <ul>
     * <li>"titleOnly"</li>
     * <li>"titleAndContent"</li>
     * <li>"titleAndAbstract"</li>
     * </ul>
     * </p>
     */
    public static final String ID_C_ARTICLE_LIST_STYLE = "articleListStyle";

    /**
     * Key of article/page comment-able.
     */
    public static final String ID_C_COMMENTABLE = "commentable";

    /**
     * Key of feed (Atom/RSS) output mode.
     *
     * <p>
     * Optional values:
     * <ul>
     * <li>"abstract"</li>
     * <li>"fullContent"</li>
     * </ul>
     * </p>
     */
    public static final String ID_C_FEED_OUTPUT_MODE = "feedOutputMode";

    /**
     * Key of feed (Atom/RSS) output entry count.
     */
    public static final String ID_C_FEED_OUTPUT_CNT = "feedOutputCnt";

    /**
     * Key of editor type.
     *
     * Optional values:
     * <p>
     * <ul>
     * <li>"tinyMCE"</li>
     * <li>"CodeMirror-Markdown"</li>
     * </ul>
     * </p>
     */
    public static final String ID_C_EDITOR_TYPE = "editorType";

    /**
     * Key of skins.
     */
    public static final String ID_C_SKINS = "skins";

    /**
     * Key of skin dir name.
     */
    public static final String ID_C_SKIN_DIR_NAME = "skinDirName";

    /**
     * Key of skin name.
     */
    public static final String ID_C_SKIN_NAME = "skinName";

    /**
     * Key of reply notification template body.
     */
    public static final String ID_C_REPLY_NOTI_TPL_BODY = "replyNotiTplBody";

    /**
     * Key of reply notification template subject.
     */
    public static final String ID_C_REPLY_NOTI_TPL_SUBJECT = "replyNotiTplSubject";

    /**
     * Key of footer content.
     */
    public static final String ID_C_FOOTER_CONTENT = "footerContent";

    // Category constants
    /**
     * Broadcast.
     */
    public static final String CATEGORY_C_BROADCAST = "broadcast";

    /**
     * Qiniu.
     */
    public static final String CATEGORY_C_QINIU = "qiniu";

    /**
     * Preference.
     */
    public static final String CATEGORY_C_PREFERENCE = "preference";

    /**
     * Private constructor.
     */
    private Option() {
    }
}
