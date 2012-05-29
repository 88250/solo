/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
 * This class defines all page types language configuration keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, May 22, 2012
 * @since 0.3.1
 */
public final class PageTypes {

    /**
     * Key of article label.
     */
    public static final String ARTICLE = "articleLabel";
    /**
     * Key of tag article label.
     */
    public static final String TAG_ARTICLES = "tagArticlesLabel";
    /**
     * Key of archive date articles label.
     */
    public static final String DATE_ARTICLES = "dateArticlesLabel";
    /**
     * Key of index articles label.
     */
    public static final String INDEX_ARTICLES = "indexArticleLabel";
    /**
     * Key of all tags label.
     */
    public static final String ALL_TAGS = "allTagsLabel";
    /**
     * Key of author articles label.
     */
    public static final String AUTHOR_ARTICLES = "authorArticlesLabel";
    /**
     * Key of customized page label.
     */
    public static final String PAGE = "customizedPageLabel";
    /**
     * Key of kill browser page label.
     */
    public static final String KILL_BROWSER_PAGE = "killBrowserPageLabel";
    /**
     * Key of user template page label.
     */
    public static final String USER_TEMPLATE_PAGE = "userTemplatePageLabel";

    /**
     * Private default constructor.
     */
    private PageTypes() {
    }
}
