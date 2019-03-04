/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.TextHtmlRenderer;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.service.StatisticQueryService;
import org.json.JSONObject;

/**
 * Provides patches on some special issues.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.21, Mar 3, 2019
 * @since 0.3.1
 */
@RequestProcessor
@Before(ConsoleAuthAdvice.class)
public class RepairConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepairConsole.class);

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Preference management service.
     */
    @Inject
    private PreferenceMgmtService preferenceMgmtService;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Restores the signs of preference to default.
     *
     * @param context the specified context
     */
    public void restoreSigns(final RequestContext context) {
        final TextHtmlRenderer renderer = new TextHtmlRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = optionQueryService.getPreference();
            preference.put(Option.ID_C_SIGNS, Option.DefaultPreference.DEFAULT_SIGNS);
            preferenceMgmtService.updatePreference(preference);

            renderer.setContent("Restore signs succeeded.");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            renderer.setContent("Restores signs failed, error msg [" + e.getMessage() + "]");
        }
    }
}
