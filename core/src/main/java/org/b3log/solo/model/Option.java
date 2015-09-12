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
 * @version 1.1.0.0, Sep 12, 2015
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
     * Private constructor.
     */
    private Option() {
    }
}
