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
package org.b3log.solo.service;


import javax.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Statistic query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2012
 * @since 0.5.0
 */
@Service
public class StatisticQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticQueryService.class.getName());

    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;

    /**
     * Gets the online visitor count.
     * 
     * @return online visitor count
     */
    public static int getOnlineVisitorCount() {
        return StatisticMgmtService.ONLINE_VISITORS.size();
    }

    /**
     * Get blog comment count.
     *
     * @return blog comment count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getBlogCommentCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(Statistic.STATISTIC_BLOG_COMMENT_COUNT);
    }

    /**
     * Get blog comment(published article) count.
     *
     * @return blog comment count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getPublishedBlogCommentCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
    }

    /**
     * Gets blog statistic published article count.
     *
     * @return published blog article count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getPublishedBlogArticleCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT);
    }

    /**
     * Gets blog statistic article count.
     *
     * @return blog article count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getBlogArticleCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT);
    }

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
                LOGGER.log(Level.WARN, "Can not load statistic from repository");
                return null;
            }

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sets the statistic repository with the specified statistic repository.
     * 
     * @param statisticRepository the specified statistic repository
     */
    public void setStatisticRepository(final StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }
}
