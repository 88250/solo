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

/**
 * This class defines ext of user model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.0, Feb 8, 2019
 * @see org.b3log.latke.model.User
 * @since 0.4.1
 */
public final class UserExt {

    /**
     * Key of user avatar.
     */
    public static final String USER_AVATAR = "userAvatar";

    /**
     * Max user name length.
     */
    public static final int MAX_USER_NAME_LENGTH = 64;

    /**
     * Min user name length.
     */
    public static final int MIN_USER_NAME_LENGTH = 1;

    /**
     * Key of user B3 key.
     */
    public static final String USER_B3_KEY = "userB3Key";

    /**
     * Key of GitHub open id.
     */
    public static final String USER_GITHUB_ID = "userGitHubId";

    /**
     * Checks whether the specified name is invalid.
     * <p>
     * A valid user name:
     * <ul>
     * <li>length [1, 64]</li>
     * <li>content {a-z, A-Z, 0-9, -}</li>
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
            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || '-' == c) {
                continue;
            }

            return true;
        }

        return StringUtils.containsIgnoreCase(name, "admin");
    }

    /**
     * Private constructor.
     */
    private UserExt() {
    }
}
