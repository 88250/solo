/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
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
package org.b3log.solo.repository.impl;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.cache.StatisticCache;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.json.JSONObject;

/**
 * Statistic repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Aug 30, 2017
 * @since 0.3.1
 */
@Repository
public class StatisticRepositoryImpl extends AbstractRepository implements StatisticRepository {

    /**
     * Public constructor.
     */
    public StatisticRepositoryImpl() {
        super(Statistic.STATISTIC);
    }

    /**
     * Statistic cache.
     */
    @Inject
    private StatisticCache statisticCache;

    @Override
    public void remove(final String id) throws RepositoryException {
        super.remove(id);

        statisticCache.removeStatistic(id);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = statisticCache.getStatistic(id);
        if (null != ret) {
            return ret;
        }

        ret = super.get(id);
        if (null == ret) {
            return null;
        }

        statisticCache.putStatistic(ret);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject statistic) throws RepositoryException {
        super.update(id, statistic);

        statistic.put(Keys.OBJECT_ID, id);
        statisticCache.putStatistic(statistic);
    }
}
