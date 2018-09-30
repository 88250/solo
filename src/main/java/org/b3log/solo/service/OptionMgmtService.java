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
package org.b3log.solo.service;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

/**
 * Option management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 16, 2013
 * @since 0.6.0
 */
@Service
public class OptionMgmtService {

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Adds or updates the specified option.
     *
     * @param option the specified option
     * @return option id
     * @throws ServiceException
     */
    public String addOrUpdateOption(final JSONObject option) throws ServiceException {
        final Transaction transaction = optionRepository.beginTransaction();

        try {
            String id = option.optString(Keys.OBJECT_ID);

            if (StringUtils.isBlank(id)) {
                id = optionRepository.add(option);
            } else {
                final JSONObject old = optionRepository.get(id);

                if (null == old) { // The id is specified by caller
                    id = optionRepository.add(option);
                } else {
                    old.put(Option.OPTION_CATEGORY, option.optString(Option.OPTION_CATEGORY));
                    old.put(Option.OPTION_VALUE, option.optString(Option.OPTION_VALUE));

                    optionRepository.update(id, old);
                }
            }

            transaction.commit();

            return id;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }
    }

    /**
     * Removes the option specified by the given option id.
     *
     * @param optionId the given option id
     * @throws ServiceException service exception
     */
    public void removeOption(final String optionId) throws ServiceException {
        final Transaction transaction = optionRepository.beginTransaction();

        try {
            optionRepository.remove(optionId);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }
    }

    /**
     * Sets the option repository with the specified option repository.
     *
     * @param optionRepository the specified option repository
     */
    public void setOptionRepository(final OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }
}
