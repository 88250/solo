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
package org.b3log.solo.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

/**
 * Preference query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.5, Nov 6, 2018
 * @since 0.4.0
 */
@Service
public class PreferenceQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceQueryService.class);

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Optiona query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Gets the user preference.
     *
     * @return user preference, returns {@code null} if not found
     */
    public JSONObject getPreference() {
        try {
            return optionQueryService.getOptions(Option.CATEGORY_C_PREFERENCE);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets preference failed", e);

            return null;
        }
    }
}
