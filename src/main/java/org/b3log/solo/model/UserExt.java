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
 * This class defines ext of user model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, May 25, 2017
 * @see org.b3log.latke.model.User
 * @since 0.4.1
 */
public final class UserExt {

    /**
     * Key of user article count.
     */
    public static final String USER_ARTICLE_COUNT = "userArticleCount";

    /**
     * Key of user article count.
     */
    public static final String USER_PUBLISHED_ARTICLE_COUNT = "userPublishedArticleCount";

    /**
     * Key of user avatar.
     */
    public static final String USER_AVATAR = "userAvatar";

    /**
     * Max user name length.
     */
    public static final int MAX_USER_NAME_LENGTH = 20;

    /**
     * Min user name length.
     */
    public static final int MIN_USER_NAME_LENGTH = 1;

    /**
     * Private constructor.
     */
    private UserExt() {
    }

    /**
     * Checks whether the specified name is invalid.
     * <p>
     * A valid user name:
     * <ul>
     * <li>length [1, 20]</li>
     * <li>content {a-z, A-Z, 0-9}</li>
     * <li>Not contains "admin"/"Admin"</li>
     * </ul>
     * </p>
     *
     * @param name the specified name
     * @return {@code true} if it is invalid, returns {@code false} otherwise
     */
    public static boolean invalidUserName(final String name) {
        final int length = name.length();
        if (length < MIN_USER_NAME_LENGTH || length > MAX_USER_NAME_LENGTH) {
            return true;
        }

        char c;
        for (int i = 0; i < length; i++) {
            c = name.charAt(i);

            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || '0' <= c && c <= '9') {
                continue;
            }

            return true;
        }

        return name.contains("admin") || name.contains("Admin");
    }
}
