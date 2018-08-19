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
 * This enumeration defines all page types language configuration keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Aug 28, 2012
 * @since 0.3.1
 */
public enum PageTypes {

    /**
     * Article.
     */
    ARTICLE("articleLabel", "Article"),
    /**
     * Tag articles.
     */
    TAG_ARTICLES("tagArticlesLabel", "TagArticles"),
    /**
     * Date articles.
     */
    DATE_ARTICLES("dateArticlesLabel", "DateArticles"),
    /**
     * Index.
     */
    INDEX("indexArticleLabel", "Index"),
    /**
     * Tags.
     */
    TAGS("allTagsLabel", "Tags"),
    /**
     * Author articles.
     */
    AUTHOR_ARTICLES("authorArticlesLabel", "AuthorArticles"),
    /**
     * Page.
     */
    PAGE("customizedPageLabel", "Page"),
    /**
     * Kill browser page.
     */
    KILL_BROWSER("killBrowserPageLabel", "KillBrowser"),
    /**
     * User template.
     */
    USER_TEMPLATE("userTemplatePageLabel", "UserTemplate");
    /**
     * Language label.
     */
    private final String langLabel;
    /**
     * Type name.
     */
    private final String typeName;
    
    /**
     * Gets the language label.
     * 
     * @return language label
     */
    public String getLangeLabel() {
        return langLabel;
    }
    
    /**
     * Gets the type name.
     * 
     * @return type name
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Constructs a page type with the specified language label and type name.
     * 
     * @param langLabel the specified language label
     * @param typeName the specified type name
     */
    PageTypes(final String langLabel, final String typeName) {
        this.langLabel = langLabel;
        this.typeName = typeName;
    }
}
