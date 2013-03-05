/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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


import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * This class defines all comment model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, Mar 5, 2013
 * @since 0.3.1
 */
public final class Preference {

    /**
     * Preference.
     */
    public static final String PREFERENCE = "preference";

    /**
     * Blog title.
     */
    public static final String BLOG_TITLE = "blogTitle";

    /**
     * Blog subtitle.
     */
    public static final String BLOG_SUBTITLE = "blogSubtitle";

    /**
     * Relevant articles display count.
     */
    public static final String RELEVANT_ARTICLES_DISPLAY_CNT = "relevantArticlesDisplayCount";

    /**
     * Random articles display count.
     */
    public static final String RANDOM_ARTICLES_DISPLAY_CNT = "randomArticlesDisplayCount";

    /**
     * External relevant articles display count.
     */
    public static final String EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT = "externalRelevantArticlesDisplayCount";

    /**
     * Recent article display count.
     */
    public static final String RECENT_ARTICLE_DISPLAY_CNT = "recentArticleDisplayCount";

    /**
     * Recent comment display count.
     */
    public static final String RECENT_COMMENT_DISPLAY_CNT = "recentCommentDisplayCount";

    /**
     * Most used tag display count.
     */
    public static final String MOST_USED_TAG_DISPLAY_CNT = "mostUsedTagDisplayCount";

    /**
     * Most comment article display count.
     */
    public static final String MOST_COMMENT_ARTICLE_DISPLAY_CNT = "mostCommentArticleDisplayCount";

    /**
     * Most view article display count.
     */
    public static final String MOST_VIEW_ARTICLE_DISPLAY_CNT = "mostViewArticleDisplayCount";

    /**
     * Article list display count.
     */
    public static final String ARTICLE_LIST_DISPLAY_COUNT = "articleListDisplayCount";

    /**
     * Article list pagination window size.
     */
    public static final String ARTICLE_LIST_PAGINATION_WINDOW_SIZE = "articleListPaginationWindowSize";

    /**
     * Blog host.
     */
    public static final String BLOG_HOST = "blogHost";

    /**
     * Administrator's email.
     */
    public static final String ADMIN_EMAIL = "adminEmail";

    /**
     * Locale string.
     */
    public static final String LOCALE_STRING = "localeString";

    /**
     * Time zone id.
     */
    public static final String TIME_ZONE_ID = "timeZoneId";

    /**
     * Notice board.
     */
    public static final String NOTICE_BOARD = "noticeBoard";

    /**
     * HTML head.
     */
    public static final String HTML_HEAD = "htmlHead";

    /**
     * Key of meta keywords.
     */
    public static final String META_KEYWORDS = "metaKeywords";

    /**
     * Key of meta description.
     */
    public static final String META_DESCRIPTION = "metaDescription";

    /**
     * Key of article update hint flag.
     */
    public static final String ENABLE_ARTICLE_UPDATE_HINT = "enableArticleUpdateHint";

    /**
     * Key of signs.
     */
    public static final String SIGNS = "signs";

    /**
     * Key of key of Solo.
     */
    public static final String KEY_OF_SOLO = "keyOfSolo";

    /**
     * Key of page cache enabled.
     */
    public static final String PAGE_CACHE_ENABLED = "pageCacheEnabled";

    /**
     * Key of allow visit draft via permalink.
     */
    public static final String ALLOW_VISIT_DRAFT_VIA_PERMALINK = "allowVisitDraftViaPermalink";

    /**
     * Key of version.
     */
    public static final String VERSION = "version";

    /**
     * Key of article list display style.
     * 
     * <p>
     * Optional values:
     *   <ul>
     *     <li>"titleOnly"</li>
     *     <li>"titleAndContent"</li>
     *     <li>"titleAndAbstract"</li>
     *   </ul>
     * </p>
     */
    public static final String ARTICLE_LIST_STYLE = "articleListStyle";

    /**
     * Key of reply notification template.
     */
    public static final String REPLY_NOTIFICATION_TEMPLATE = "replyNotificationTemplate";

    /**
     * Key of article/page comment-able.
     */
    public static final String COMMENTABLE = "commentable";

    /**
     * Key of feed (Atom/RSS) output mode.
     * 
     * <p>
     * Optional values:
     *   <ul>
     *     <li>"abstract"</li>
     *     <li>"fullContent"</li>
     *   </ul>
     * </p>
     */
    public static final String FEED_OUTPUT_MODE = "feedOutputMode";

    /**
     * Key of feed (Atom/RSS) output entry count.
     */
    public static final String FEED_OUTPUT_CNT = "feedOutputCnt";

    /**
     * Key of editor type.
     * 
     * Optional values: 
     * <p>
     *   <ul>
     *     <li>"tinyMCE"</li>
     *     <li>"CodeMirror-Markdown"</li>
     *   </ul>
     * </p>
     */
    public static final String EDITOR_TYPE = "editorType";

    /**
     * Private default constructor.
     */
    private Preference() {}

    /**
     * Default preference.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.1.0.8, Sep 18, 2012
     * @since 0.3.1
     */
    public static final class Default {

        /**
         * Logger.
         */
        private static final Logger LOGGER = Logger.getLogger(Default.class.getName());

        /**
         * Default recent article display count.
         */
        public static final int DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT = 10;

        /**
         * Default recent comment display count.
         */
        public static final int DEFAULT_RECENT_COMMENT_DISPLAY_COUNT = 10;

        /**
         * Default most used tag display count.
         */
        public static final int DEFAULT_MOST_USED_TAG_DISPLAY_COUNT = 20;

        /**
         * Default article list display count.
         */
        public static final int DEFAULT_ARTICLE_LIST_DISPLAY_COUNT = 20;

        /**
         * Default article list pagination window size.
         */
        public static final int DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE = 15;

        /**
         * Default most comment article display count.
         */
        public static final int DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT = 5;

        /**
         * Default blog title.
         */
        public static final String DEFAULT_BLOG_TITLE = "B3log Solo 示例";

        /**
         * Default blog subtitle.
         */
        public static final String DEFAULT_BLOG_SUBTITLE = "Java 开源博客";

        /**
         * Default skin directory name.
         */
        public static final String DEFAULT_SKIN_DIR_NAME = "ease";

        /**
         * Default language.
         */
        public static final String DEFAULT_LANGUAGE = "zh_CN";

        /**
         * Default time zone.
         * 
         * @see java.util.TimeZone#getAvailableIDs() 
         */
        public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

        /**
         * Default enable article update hint.
         */
        public static final boolean DEFAULT_ENABLE_ARTICLE_UPDATE_HINT = true;

        /**
         * Default enable post to Tencent microblog.
         */
        public static final boolean DEFAULT_ENABLE_POST_TO_TENCENT_MICROBLOG = false;

        /**
         * Default notice board.
         */
        public static final String DEFAULT_NOTICE_BOARD = "Open Source, Open Mind, <br/>Open Sight, Open Future!";

        /**
         * Default meta keywords..
         */
        public static final String DEFAULT_META_KEYWORDS = "Java 博客,GAE,b3log";

        /**
         * Default meta description..
         */
        public static final String DEFAULT_META_DESCRIPTION = "An open source blog with Java. Java 开源博客";

        /**
         * Default HTML head to append.
         */
        public static final String DEFAULT_HTML_HEAD = "";

        /**
         * Default relevant articles display count.
         */
        public static final int DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT = 5;

        /**
         * Default random articles display count.
         */
        public static final int DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT = 5;

        /**
         * Default external relevant articles display count.
         */
        public static final int DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT = 5;

        /**
         * Most view articles display count.
         */
        public static final int DEFAULT_MOST_VIEW_ARTICLES_DISPLAY_COUNT = 5;

        /**
         * Default signs.
         */
        public static final String DEFAULT_SIGNS;

        /**
         * Default page cache enabled.
         */
        public static final boolean DEFAULT_PAGE_CACHE_ENABLED;

        /**
         * Default allow visit draft via permalink.
         */
        public static final boolean DEFAULT_ALLOW_VISIT_DRAFT_VIA_PERMALINK = false;

        /**
         * Default allow comment article/page.
         */
        public static final boolean DEFAULT_COMMENTABLE = true;

        /**
         * Default administrator's password.
         */
        public static final String DEFAULT_ADMIN_PWD = "111111";

        /**
         * Default article list display style.
         */
        public static final String DEFAULT_ARTICLE_LIST_STYLE = "titleAndAbstract";

        /**
         * Default key of solo.
         */
        public static final String DEFAULT_KEY_OF_SOLO = "Your key";

        /**
         * Default reply notification template.
         */
        public static final String DEFAULT_REPLY_NOTIFICATION_TEMPLATE;

        /**
         * Default feed output mode.
         */
        public static final String DEFAULT_FEED_OUTPUT_MODE = "abstract";

        /**
         * Default feed output entry count.
         */
        public static final int DEFAULT_FEED_OUTPUT_CNT = 10;

        /**
         * Default editor type.
         */
        public static final String DEFAULT_EDITOR_TYPE = "tinyMCE";

        static {
            final JSONArray signs = new JSONArray();

            final int signLength = 4;

            try {
                for (int i = 0; i < signLength; i++) {
                    final JSONObject sign = new JSONObject();

                    sign.put(Keys.OBJECT_ID, i);
                    signs.put(sign);

                    sign.put(Sign.SIGN_HTML, "");
                }

                // Sign(id=0) is the 'empty' sign, used for article user needn't
                // a sign
                DEFAULT_SIGNS = signs.toString();

                final JSONObject replyNotificationTemplate = new JSONObject();

                replyNotificationTemplate.put(Keys.OBJECT_ID, Preference.REPLY_NOTIFICATION_TEMPLATE);
                replyNotificationTemplate.put("subject", "${blogTitle}: New reply of your comment");
                replyNotificationTemplate.put("body",
                    "Your comment on post[<a href='${postLink}'>" + "${postTitle}</a>] received an reply: <p>${replier}"
                    + ": <span><a href='${replyURL}'>${replyContent}</a></span></p>");
                DEFAULT_REPLY_NOTIFICATION_TEMPLATE = replyNotificationTemplate.toString();

                if (RuntimeEnv.BAE == Latkes.getRuntimeEnv()) {
                    DEFAULT_PAGE_CACHE_ENABLED = false; // https://github.com/b3log/b3log-solo/issues/73
                } else {
                    DEFAULT_PAGE_CACHE_ENABLED = true;
                }
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, "Creates sign error!", e);
                throw new IllegalStateException(e);
            }
        }

        /**
         * Private default constructor.
         */
        private Default() {}
    }
}
