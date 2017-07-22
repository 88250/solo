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
package org.b3log.solo.cache;

import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.json.JSONObject;

/**
 * Preference cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 22, 2017
 * @since 2.3.0
 */
@Named
@Singleton
public class PreferenceCache {

    /**
     * Preference cache.
     */
    private JSONObject preference;

    /**
     * Get the preference.
     *
     * @return preference
     */
    public JSONObject getPreference() {
        return preference;
    }

    /**
     * Adds or updates the specified preference.
     *
     * @param preference the specified preference
     */
    public void putPreference(final JSONObject preference) {
        this.preference = preference;
    }

    /**
     * Clears the preference.
     */
    public void clear() {
        preference = null;
    }
}
