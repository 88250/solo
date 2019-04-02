/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.solo.service.ArchiveDateMgmtService;
import org.b3log.solo.service.TagMgmtService;
import org.json.JSONObject;

/**
 * Other console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 20, 2019
 * @since 3.4.0
 */
@RequestProcessor
public class OtherConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OtherConsole.class);

    /**
     * Tag management service.
     */
    @Inject
    private TagMgmtService tagMgmtService;

    /**
     * ArchiveDate maangement service.
     */
    @Inject
    private ArchiveDateMgmtService archiveDateMgmtService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Removes all unused archives.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    @Before(ConsoleAdminAuthAdvice.class)
    public void removeUnusedArchives(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        try {
            archiveDateMgmtService.removeUnusedArchiveDates();

            jsonObject.put(Keys.STATUS_CODE, true);
            jsonObject.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes unused archives failed", e);

            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
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
     * @param context the specified request context
     */
    @Before(ConsoleAdminAuthAdvice.class)
    public void removeUnusedTags(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        try {
            tagMgmtService.removeUnusedTags();

            jsonObject.put(Keys.STATUS_CODE, true);
            jsonObject.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes unused tags failed", e);

            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
        }
    }
}
