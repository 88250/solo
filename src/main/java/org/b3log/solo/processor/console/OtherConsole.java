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
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.Server;
import org.b3log.solo.service.ArchiveDateMgmtService;
import org.b3log.solo.service.TagMgmtService;
import org.b3log.solo.util.StatusCodes;

/**
 * Other console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.1.0.1, Jun 19, 2020
 * @since 3.4.0
 */
@Singleton
public class OtherConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(OtherConsole.class);

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
     * Get log.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "log": "log lines"
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getLog(final RequestContext context) {
        context.renderJSON(StatusCodes.SUCC);
        final String content = Server.TAIL_LOGGER_WRITER.toString();
        context.renderJSONValue("log", content);
    }

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
    public void removeUnusedArchives(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);
        try {
            archiveDateMgmtService.removeUnusedArchiveDates();
            context.renderJSONValue(Keys.CODE, StatusCodes.SUCC).
                    renderMsg(langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes unused archives failed", e);
            context.renderMsg(langPropsService.get("removeFailLabel"));
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
    public void removeUnusedTags(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);
        try {
            tagMgmtService.removeUnusedTags();
            context.renderJSONValue(Keys.CODE, StatusCodes.SUCC).
                    renderMsg(langPropsService.get("removeSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes unused tags failed", e);
            context.renderMsg(langPropsService.get("removeFailLabel"));
        }
    }
}
