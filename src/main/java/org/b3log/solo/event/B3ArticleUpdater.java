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
package org.b3log.solo.event;

import org.b3log.latke.ioc.Singleton;

/**
 * This listener is responsible for updating article to B3log Rhythm. Sees <a href="https://ld246.com/article/1546941897596">B3log 构思 - 分布式社区网络</a> for more details.
 * <p>
 * API spec: https://ld246.com/article/1457158841475
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.8, Nov 27, 2024
 * @since 0.6.0
 */
@Singleton
public class B3ArticleUpdater extends AbstractB3EventListener {

    /**
     * Gets the event type {@linkplain EventTypes#UPDATE_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.UPDATE_ARTICLE;
    }
}
