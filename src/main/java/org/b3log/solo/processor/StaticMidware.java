/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.util.Statics;

/**
 * 页面静态化. https://github.com/88250/solo/issues/107
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 14, 2020
 * @since 4.1.0
 */
@Singleton
public class StaticMidware {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(StaticMidware.class);

    public void handle(final RequestContext context) {
        final String html = Statics.get(context);
        if (null == html) {
            context.handle();
            return;
        }

        context.getResponse().setContentType("text/html; charset=utf-8");
        context.sendString(html);
        context.abort();
    }
}
