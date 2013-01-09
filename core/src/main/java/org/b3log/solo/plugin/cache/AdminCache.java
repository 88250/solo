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
package org.b3log.solo.plugin.cache;


import org.b3log.latke.plugin.AbstractPlugin;


/**
 * Admin cache plugin.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.6, Aug 6, 2011
 * @since 0.3.1
 */
public final class AdminCache extends AbstractPlugin {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getViewName() {
        return "admin-index.ftl";
    }
}
