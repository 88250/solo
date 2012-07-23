/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.json.JSONObject;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;

/**
 * Statistic management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2012
 * @since 0.5.0
 */
public final class StatisticMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticMgmtService.class.getName());
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository = StatisticRepositoryImpl.getInstance();

    /**
     * Updates the statistic with the specified statistic.
     *
     * @param statistic the specified statistic
     * @throws ServiceException service exception
     */
    public void updateStatistic(final JSONObject statistic) throws ServiceException {
        final Transaction transaction = statisticRepository.beginTransaction();
        try {
            statisticRepository.update(Statistic.STATISTIC, statistic);
            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Updates statistic failed", e);
        }

        LOGGER.log(Level.FINER, "Updates statistic successfully");
    }

    /**
     * Gets the {@link StatisticMgmtService} singleton.
     *
     * @return the singleton
     */
    public static StatisticMgmtService getInstance() {
        return SingletonHolder.SINGLETON;


    }

    /**
     * Private constructor.
     */
    private StatisticMgmtService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 24, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final StatisticMgmtService SINGLETON = new StatisticMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
