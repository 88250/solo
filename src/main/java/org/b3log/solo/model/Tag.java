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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.util.Strings;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class defines all tag model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.4, Jul 22, 2019
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

    //// Transient ////
    /**
     * Key of tag reference (published article) count.
     */
    public static final String TAG_T_PUBLISHED_REFERENCE_COUNT = "tagPublishedRefCount";

    /**
     * Tag title pattern string.
     */
    public static final String TAG_TITLE_PATTERN_STR = "[\\u4e00-\\u9fa5\\w&#+\\-.]+";

    /**
     * Tag title pattern.
     */
    public static final Pattern TAG_TITLE_PATTERN = Pattern.compile(TAG_TITLE_PATTERN_STR);

    /**
     * Max length of a tag.
     */
    public static final int MAX_LENGTH = 16;

    /**
     * Formats the specified tags.
     * <ul>
     * <li>Trims every tag</li>
     * <li>Deduplication</li>
     * </ul>
     *
     * @param tagStr      the specified tags
     * @param maxTagCount the specified max tag count
     * @return formatted tags string
     */
    public static String formatTags(final String tagStr, final int maxTagCount) {
        final String tagStr1 = tagStr.replaceAll("\\s+", "").replaceAll("，", ",").replaceAll("、", ",").
                replaceAll("；", ",").replaceAll(";", ",");
        String[] tagTitles = tagStr1.split(",");

        tagTitles = Strings.trimAll(tagTitles);

        // deduplication
        final Set<String> titles = new LinkedHashSet<>();
        for (final String tagTitle : tagTitles) {
            if (!exists(titles, tagTitle)) {
                titles.add(tagTitle);
            }
        }

        tagTitles = titles.toArray(new String[0]);

        int count = 0;
        final StringBuilder tagsBuilder = new StringBuilder();
        for (final String tagTitle : tagTitles) {
            String title = tagTitle.trim();
            if (StringUtils.isBlank(title)) {
                continue;
            }

            if (StringUtils.length(title) > MAX_LENGTH) {
                continue;
            }

            if (!TAG_TITLE_PATTERN.matcher(title).matches()) {
                continue;
            }

            tagsBuilder.append(title).append(",");
            count++;

            if (maxTagCount <= count) {
                break;
            }
        }
        if (tagsBuilder.length() > 0) {
            tagsBuilder.deleteCharAt(tagsBuilder.length() - 1);
        }

        return tagsBuilder.toString();
    }

    /**
     * Checks the specified title exists in the specified title set.
     *
     * @param titles the specified title set
     * @param title  the specified title to check
     * @return {@code true} if exists, returns {@code false} otherwise
     */
    private static boolean exists(final Set<String> titles, final String title) {
        for (final String setTitle : titles) {
            if (setTitle.equalsIgnoreCase(title)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Private constructor.
     */
    private Tag() {
    }
}
