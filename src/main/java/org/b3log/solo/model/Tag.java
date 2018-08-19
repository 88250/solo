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
 * This class defines all tag model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Dec 3, 2010
 */
public final class Tag {

    /**
     * Tag.
     */
    public static final String TAG = "tag";

    /**
     * Tags.
     */
    public static final String TAGS = "tags";

    /**
     * Key of title.
     */
    public static final String TAG_TITLE = "tagTitle";

    /**
     * Key of tag reference count.
     */
    public static final String TAG_REFERENCE_COUNT = "tagReferenceCount";

    /**
     * Key of tag reference(published article) count.
     */
    public static final String TAG_PUBLISHED_REFERENCE_COUNT = "tagPublishedRefCount";

    /**
     * Private default constructor.
     */
    private Tag() {}
}
