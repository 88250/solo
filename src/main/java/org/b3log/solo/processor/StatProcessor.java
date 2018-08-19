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
package org.b3log.solo.processor;

import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.solo.service.StatisticMgmtService;

/**
 * Statistics processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.1, Jul 25, 2018
 * @since 0.4.0
 */
@RequestProcessor
public class StatProcessor {

    /**
     * Online visitor count refresher.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/onlineVisitorRefresh", method = HTTPRequestMethod.GET)
    public void onlineVisitorCountRefresher(final HTTPRequestContext context) {
        context.setRenderer(new DoNothingRenderer());

        StatisticMgmtService.removeExpiredOnlineVisitor();
    }
}
