/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
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
 * This class defines all statistic model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 9, 2010
 */
public final class Statistic {

    /**
     * Statistic.
     */
    public static final String STATISTIC = "statistic";

    /**
     * Key of blog view count.
     */
    public static final String STATISTIC_BLOG_VIEW_COUNT = "statisticBlogViewCount";

    /**
     * Key of blog comment count.
     */
    public static final String STATISTIC_BLOG_COMMENT_COUNT = "statisticBlogCommentCount";

    /**
     * Key of blog comment(published article) count.
     */
    public static final String STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT = "statisticPublishedBlogCommentCount";

    /**
     * Key of blog article count.
     */
    public static final String STATISTIC_BLOG_ARTICLE_COUNT = "statisticBlogArticleCount";

    /**
     * Key of blog published article count.
     */
    public static final String STATISTIC_PUBLISHED_ARTICLE_COUNT = "statisticPublishedBlogArticleCount";

    /**
     * Private default constructor.
     */
    private Statistic() {}
}
