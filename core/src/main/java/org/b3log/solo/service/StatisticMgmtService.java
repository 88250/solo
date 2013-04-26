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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.PageTypes;
import org.json.JSONObject;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.util.Statistics;


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
     * Flush size.
     */
    private static final int FLUSH_SIZE = 30;

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Flushes the statistic to repository.
     * 
     * @throws ServiceException 
     */
    public void flushStatistic() throws ServiceException {
        final JSONObject statistic = (JSONObject) statisticRepository.getCache().get(
            Statistics.REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC);

        if (null == statistic) {
            LOGGER.log(Level.INFO, "Not found statistic in cache, ignores sync");

            return;
        }

        final Transaction transaction = statisticRepository.beginTransaction();

        transaction.clearQueryCache(false);

        try {
            statisticRepository.getCache().remove(Statistics.REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC);
            // For blog view counter
            statisticRepository.update(Statistic.STATISTIC, statistic);

            // For article view counter
            final Set<String> keys = PageCaches.getKeys();
            final List<String> keyList = new ArrayList<String>(keys);

            final int size = keys.size() > FLUSH_SIZE ? FLUSH_SIZE : keys.size(); // Flush FLUSH_SIZE articles at most
            final List<Integer> idx = CollectionUtils.getRandomIntegers(0, keys.size(), size);

            final Set<String> cachedPageKeys = new HashSet<String>();

            for (final Integer i : idx) {
                cachedPageKeys.add(keyList.get(i));
            }

            for (final String cachedPageKey : cachedPageKeys) {
                final JSONObject cachedPage = PageCaches.get(cachedPageKey);

                if (null == cachedPage) {
                    continue;
                }

                final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());

                if (!cachedPage.optString(PageCaches.CACHED_TYPE).equals(langs.get(PageTypes.ARTICLE.getLangeLabel()))) { // Cached is not an article page
                    continue;
                }

                final int hitCount = cachedPage.optInt(PageCaches.CACHED_HIT_COUNT);
                final String articleId = cachedPage.optString(PageCaches.CACHED_OID);

                final JSONObject article = articleRepository.get(articleId);

                if (null == article) {
                    continue;
                }

                LOGGER.log(Level.FINER, "Updating article[id={0}, title={1}] view count",
                    new Object[] {articleId, cachedPage.optString(PageCaches.CACHED_TITLE)});

                final int oldViewCount = article.optInt(Article.ARTICLE_VIEW_COUNT);
                final int viewCount = oldViewCount + hitCount;

                article.put(Article.ARTICLE_VIEW_COUNT, viewCount);

                article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random()); // Updates random value

                articleRepository.update(articleId, article);

                cachedPage.put(PageCaches.CACHED_HIT_COUNT, 0);

                LOGGER.log(Level.FINER, "Updating article[id={0}, title={1}] view count from [{2}] to [{3}]",
                    new Object[] {articleId, article.optString(Article.ARTICLE_TITLE), oldViewCount, viewCount});
            }

            transaction.commit();

            LOGGER.log(Level.INFO, "Synchronized statistic from cache to repository[statistic={0}]", statistic);
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Updates statistic failed", e);
        }
    }

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
    private StatisticMgmtService() {}

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
        private SingletonHolder() {}
    }
}
