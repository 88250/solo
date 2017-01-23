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
 * This class defines ext of user model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Oct 17, 2015
 * @since 0.4.1
 * @see org.b3log.latke.model.User
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
     * Private constructor.
     */
    private UserExt() {}
}
