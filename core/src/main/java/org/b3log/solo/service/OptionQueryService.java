/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.service;


import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.repository.impl.OptionRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Option query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Apr 16, 2013
 * @since 0.6.0
 */
public final class OptionQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OptionQueryService.class.getName());

    /**
     * Option repository.
     */
    private OptionRepository optionRepository = OptionRepositoryImpl.getInstance();

    /**
     * Gets an option with the specified option id.
     * 
     * @param optionId the specified option id
     * @return an option, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getOptionById(final String optionId) throws ServiceException {
        try {
            return optionRepository.get(optionId);
        } catch (final RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Gets options with the specified category.
     * 
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
     * @throws ServiceException service exception
     */
    public JSONObject getOptions(final String category) throws ServiceException {
        final Query query = new Query();

        query.setFilter(new PropertyFilter(Option.OPTION_CATEGORY, FilterOperator.EQUAL, category));

        try {
            final JSONObject result = optionRepository.get(query);
            final JSONArray options = result.getJSONArray(Keys.RESULTS);

            if (0 == options.length()) {
                return null;
            }

            final JSONObject ret = new JSONObject();

            for (int i = 0; i < options.length(); i++) {
                final JSONObject option = options.getJSONObject(i);

                ret.put(option.getString(Keys.OBJECT_ID), option.getString(Option.OPTION_VALUE));
            }

            return ret;
        } catch (final Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link OptionQueryService} singleton.
     *
     * @return the singleton
     */
    public static OptionQueryService getInstance() {
        return OptionQueryService.SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private OptionQueryService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Apr 16, 2013
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final OptionQueryService SINGLETON = new OptionQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
