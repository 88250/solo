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


import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.json.JSONObject;


/**
 * Statistic query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2012
 * @since 0.5.0
 */
public final class StatisticQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticQueryService.class.getName());

    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository = StatisticRepositoryImpl.getInstance();

    /**
     * Gets the statistic.
     * 
     * @return statistic, returns {@code null} if not found
     * @throws ServiceException if repository exception
     */
    public JSONObject getStatistic() throws ServiceException {
        try {
            final JSONObject ret = statisticRepository.get(Statistic.STATISTIC);

            if (null == ret) {
                LOGGER.log(Level.WARNING, "Can not load statistic from repository");
                return null;
            }

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets the {@link StatisticQueryService} singleton.
     *
     * @return the singleton
     */
    public static StatisticQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private StatisticQueryService() {}

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
        private static final StatisticQueryService SINGLETON = new StatisticQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
