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
package org.b3log.solo.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

/**
 * Option query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Jun 13, 2019
 * @since 0.6.0
 */
@Service
public class OptionQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(OptionQueryService.class);

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Gets the skin.
     *
     * @return skin, returns {@code null} if not found
     */
    public JSONObject getSkin() {
        try {
            return getOptions(Option.CATEGORY_C_SKIN);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets skin failed", e);

            return null;
        }
    }

    /**
     * Gets the user preference.
     *
     * @return user preference, returns {@code null} if not found
     */
    public JSONObject getPreference() {
        try {
            return getOptions(Option.CATEGORY_C_PREFERENCE);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets preference failed", e);

            return null;
        }
    }

    /**
     * Gets an option with the specified option id.
     *
     * @param optionId the specified option id
     * @return an option, returns {@code null} if not found
     */
    public JSONObject getOptionById(final String optionId) {
        try {
            return optionRepository.get(optionId);
        } catch (final RepositoryException e) {
            return null;
        }
    }

    /**
     * Gets options with the specified category.
     * <p>
     * All options with the specified category will be merged into one json object as the return value.
     * </p>
     *
     * @param category the specified category
     * @return all options with the specified category, for example,
     * <pre>
     * {
     *     "${optionId}": "${optionValue}",
     *     ....
     * }
     * </pre>, returns {@code null} if not found
     */
    public JSONObject getOptions(final String category) {
        try {
            return optionRepository.getOptions(category);
        } catch (final Exception e) {
            return null;
        }
    }
}
