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
package org.b3log.solo.processor.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.solo.model.Tag;
import org.b3log.solo.service.TagQueryService;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

/**
 * Tag console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Jun 19, 2020
 * @since 0.4.0
 */
@Singleton
public class TagConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(TagConsole.class);

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Gets all tags.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "tags": [
     *         {"tagTitle": "", ....},
     *         ....
     *     ]
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getTags(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        try {
            jsonObject.put(Tag.TAGS, tagQueryService.getTags());
            jsonObject.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets tags failed", e);
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
        }
    }
}
