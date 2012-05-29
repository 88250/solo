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
package org.b3log.solo.processor;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Statistics processor.
 * 
 * <p>
 * Statistics of B3log Solo: 
 * 
 *   <ul>
 *     <li>{@link #viewCounter(org.b3log.latke.servlet.HTTPRequestContext) Blog/Article view counting}</li>
 *     <li>TODO: 88250, stat proc</li>
 *   </ul>
 * <p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.4, May 16, 2012
 * @since 0.4.0
 */
@RequestProcessor
public final class StatProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatProcessor.class.getName());
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository = StatisticRepositoryImpl.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Online visitor count refresher.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/onlineVisitorRefresh", method = HTTPRequestMethod.GET)
    public void onlineVisitorCountRefresher(final HTTPRequestContext context) {
        LOGGER.log(Level.INFO, "Refreshes online visitor count");

        context.setRenderer(new DoNothingRenderer());

        Statistics.removeExpiredOnlineVisitor();
    }

    /**
     * Increments Blog/Articles view counter.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/viewcnt", method = HTTPRequestMethod.GET)
    public void viewCounter(final HTTPRequestContext context) {
        LOGGER.log(Level.INFO, "Sync statistic from memcache to repository");

        context.setRenderer(new DoNothingRenderer());

        final JSONObject statistic = (JSONObject) statisticRepository.getCache().
                get(Statistics.REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC);
        if (null == statistic) {
            LOGGER.log(Level.INFO, "Not found statistic in memcache, ignores sync");

            return;
        }

        final Transaction transaction = statisticRepository.beginTransaction();
        transaction.clearQueryCache(false);
        try {
            // For blog view counter
            statisticRepository.update(Statistic.STATISTIC, statistic);

            // For article view counter
            final Set<String> cachedPageKeys = PageCaches.getKeys();
            for (final String cachedPageKey : cachedPageKeys) {
                final JSONObject cachedPage = PageCaches.get(cachedPageKey);
                if (null == cachedPage) {
                    continue;
                }

                final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
                if (!cachedPage.optString(AbstractCacheablePageAction.CACHED_TYPE).
                        equals(langs.get(PageTypes.ARTICLE))) { // Cached is not an article page
                    continue;
                }

                final int hitCount = cachedPage.optInt(PageCaches.CACHED_HIT_COUNT);
                if (2 > hitCount) {
                    // Skips for view count tiny-changes, reduces Datastore Write Quota for Solo GAE version
                    continue;
                }

                final String articleId = cachedPage.optString(AbstractCacheablePageAction.CACHED_OID);

                LOGGER.log(Level.FINER, "Updating article[id={0}, title={1}] view count",
                           new Object[]{articleId, cachedPage.optString(AbstractCacheablePageAction.CACHED_TITLE)});

                final JSONObject article = articleRepository.get(articleId);
                if (null == article) {
                    continue;
                }

                final int oldViewCount = article.optInt(Article.ARTICLE_VIEW_COUNT);
                final int viewCount = oldViewCount + hitCount;

                article.put(Article.ARTICLE_VIEW_COUNT, viewCount);

                articleRepository.update(articleId, article);

                cachedPage.put(PageCaches.CACHED_HIT_COUNT, 0);

                LOGGER.log(Level.FINER, "Updating article[id={0}, title={1}] view count from [{2}] to [{3}]",
                           new Object[]{articleId, cachedPage.optString(
                            AbstractCacheablePageAction.CACHED_TITLE), oldViewCount, viewCount});
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
}
