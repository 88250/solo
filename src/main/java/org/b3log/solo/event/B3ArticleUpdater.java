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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.Singleton;
import org.json.JSONObject;

/**
 * This listener is responsible for updating article to B3log Rhythm. Sees <a href="https://ld246.com/article/1546941897596">B3log 构思 - 分布式社区网络</a> for more details.
 * <p>
 * API spec: https://ld246.com/article/1457158841475
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.6, Feb 8, 2019
 * @since 0.6.0
 */
@Singleton
public class B3ArticleUpdater extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(B3ArticleUpdater.class);

    public void action(final Event<JSONObject> event) {
        final JSONObject data = event.getData();
        LOGGER.log(Level.DEBUG, "Processing an event [type={}, data={}] in listener [className={}]",
                event.getType(), data, B3ArticleUpdater.class.getName());

        B3ArticleSender.pushArticleToRhy(data);
    }

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
