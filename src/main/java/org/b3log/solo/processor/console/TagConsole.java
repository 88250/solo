/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.processor.console;


import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Tag;
import org.b3log.solo.service.TagMgmtService;
import org.b3log.solo.service.TagQueryService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Tag console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Oct 24, 2011
 * @since 0.4.0
 */
@RequestProcessor
public class TagConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagConsole.class);

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Tag management service.
     */
    @Inject
    private TagMgmtService tagMgmtService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Gets all tags.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "tags": [
     *         {"tagTitle": "", tagReferenceCount": int, ....},
     *         ....
     *     ]
     * }
     * </pre>
     * </p>
     *
     * @param request  the specified http servlet request
     * @param response the specified http servlet response
     * @param context  the specified http request context
     * @throws IOException io exception
     */
    @RequestProcessing(value = "/console/tags", method = HTTPRequestMethod.GET)
    public void getTags(final HttpServletRequest request,
                        final HttpServletResponse response,
                        final HTTPRequestContext context)
            throws IOException {
        if (!userQueryService.isLoggedIn(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        try {
            jsonObject.put(Tag.TAGS, tagQueryService.getTags());

            jsonObject.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets tags failed", e);

            jsonObject.put(Keys.STATUS_CODE, false);
        }
    }

    /**
     * Gets all unused tags.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "unusedTags": [
     *         {"tagTitle": "", tagReferenceCount": int, ....},
     *         ....
     *     ]
     * }
     * </pre>
     * </p>
     *
     * @param request  the specified http servlet request
     * @param response the specified http servlet response
     * @param context  the specified http request context
     * @throws IOException io exception
     */
    @RequestProcessing(value = "/console/tag/unused",
            method = HTTPRequestMethod.GET)
    public void getUnusedTags(final HttpServletRequest request,
                              final HttpServletResponse response,
                              final HTTPRequestContext context)
            throws IOException {
        if (!userQueryService.isLoggedIn(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        final List<JSONObject> unusedTags = new ArrayList<JSONObject>();

        try {
            jsonObject.put(Common.UNUSED_TAGS, unusedTags);

            final List<JSONObject> tags = tagQueryService.getTags();

            for (int i = 0; i < tags.size(); i++) {
                final JSONObject tag = tags.get(i);
                final int tagRefCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);

                if (0 == tagRefCnt) {
                    unusedTags.add(tag);
                }
            }

            jsonObject.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets unused tags failed", e);

            jsonObject.put(Keys.STATUS_CODE, false);
        }
    }

    /**
     * Removes all unused tags.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param request  the specified http servlet request
     * @param response the specified http servlet response
     * @param context  the specified http request context
     * @throws IOException io exception
     */
    @RequestProcessing(value = "/console/tag/unused",
            method = HTTPRequestMethod.DELETE)
    public void removeUnusedTags(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final HTTPRequestContext context)
            throws IOException {
        if (!userQueryService.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        try {
            tagMgmtService.removeUnusedTags();

            jsonObject.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes unused tags failed", e);

            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
        }
    }
}
