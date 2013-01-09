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
package org.b3log.solo.plugin.symphony;


import org.b3log.latke.plugin.AbstractPlugin;


/**
 * Getting news from <a href="http://symphony.b3log.org">B3log Symphony</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 8, 2011
 */
public final class NewsGetter extends AbstractPlugin {

    /**
     * Default servial version uid.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getViewName() {
        return "admin-main.ftl";
    }
}
