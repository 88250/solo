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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Requests;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.PageTypes;
import org.json.JSONObject;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.json.JSONException;


/**
 * Statistic management service.
 * 
 * <p>
 *   <b>Note</b>: The {@link #onlineVisitorCount online visitor counting} is NOT cluster-safe.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 18, 2012
 * @since 0.5.0
 */
@Service
public final class StatisticMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticMgmtService.class.getName());

    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;

    /**
     * Flush size.
     */
    private static final int FLUSH_SIZE = 30;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Repository cache prefix, refers to GAERepository#CACHE_KEY_PREFIX.
     */
    public static final String REPOSITORY_CACHE_KEY_PREFIX = "repository";

    /**
     * Online visitor cache.
     * 
     * <p>
     * &lt;ip, recentTime&gt;
     * </p>
     */
    public static final Map<String, Long> ONLINE_VISITORS = new HashMap<String, Long>();

    /**
     * Online visitor expiration in 5 minutes.
     */
    private static final int ONLINE_VISITOR_EXPIRATION = 300000;

    /**
     * Blog statistic view count +1.
     * 
     * <p>
     * If it is a search engine bot made the specified request, will NOT increment blog statistic view count.
     * </p>
     * 
     * <p>
     * There is a cron job (/console/stat/viewcnt) to flush the blog view count from cache to datastore.
     * </p>
     * 
     * @param request the specified request
     * @param response the specified response
     * @throws RepositoryException repository exception
     * @throws JSONException json exception 
     * @see Requests#searchEngineBotRequest(javax.servlet.http.HttpServletRequest) 
     */
    public void incBlogViewCount(final HttpServletRequest request, final HttpServletResponse response)
        throws RepositoryException, JSONException {
        if (Requests.searchEngineBotRequest(request)) {
            return;
        }

        if (Requests.hasBeenServed(request, response)) {
            return;
        }

        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            return;
        }

        LOGGER.log(Level.TRACE, "Before inc blog view count[statistic={0}]", statistic);

        int blogViewCnt = statistic.getInt(Statistic.STATISTIC_BLOG_VIEW_COUNT);

        ++blogViewCnt;
        statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, blogViewCnt);

        if (!Latkes.isDataCacheEnabled()) {
            final Transaction transaction = statisticRepository.beginTransaction();

            try {
                statisticRepository.update(Statistic.STATISTIC, statistic);

                transaction.commit();
            } catch (final RepositoryException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                LOGGER.log(Level.ERROR, "Updates blog view count failed", e);
            }
        } else {
            // Repository cache prefix, Refers to GAERepository#CACHE_KEY_PREFIX 
            statisticRepository.getCache().putAsync(REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC, statistic);
        }

        LOGGER.log(Level.DEBUG, "Inced blog view count[statistic={0}]", statistic);
    }

    /**
     * Blog statistic article count +1.
     *
     * @throws RepositoryException repository exception
     */
    public void incBlogArticleCount() throws RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, statistic.optInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic published article count +1.
     *
     * @throws RepositoryException repository exception
     */
    public void incPublishedBlogArticleCount() throws RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, statistic.optInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic article count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decBlogArticleCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, statistic.getInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic published article count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decPublishedBlogArticleCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, statistic.getInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incBlogCommentCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }
        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, statistic.getInt(Statistic.STATISTIC_BLOG_COMMENT_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment(published article) count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incPublishedBlogCommentCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }
        statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
            statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decBlogCommentCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, statistic.getInt(Statistic.STATISTIC_BLOG_COMMENT_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment(published article) count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decPublishedBlogCommentCount() throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
            statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Sets blog comment count with the specified count.
     *
     * @param count the specified count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setBlogCommentCount(final int count) throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, count);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Sets blog comment(published article) count with the specified count.
     *
     * @param count the specified count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setPublishedBlogCommentCount(final int count) throws JSONException, RepositoryException {
        final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, count);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Refreshes online visitor count for the specified request.
     * 
     * @param request the specified request
     */
    public void onlineVisitorCount(final HttpServletRequest request) {
        final String remoteAddr = Requests.getRemoteAddr(request);

        LOGGER.log(Level.DEBUG, "Current request [IP={0}]", remoteAddr);

        ONLINE_VISITORS.put(remoteAddr, System.currentTimeMillis());
        LOGGER.log(Level.DEBUG, "Current online visitor count [{0}]", ONLINE_VISITORS.size());
    }

    /**
     * Removes the expired online visitor.
     */
    public static void removeExpiredOnlineVisitor() {
        final long currentTimeMillis = System.currentTimeMillis();

        final Iterator<Map.Entry<String, Long>> iterator = ONLINE_VISITORS.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<String, Long> onlineVisitor = iterator.next();

            if (currentTimeMillis > (onlineVisitor.getValue() + ONLINE_VISITOR_EXPIRATION)) {
                iterator.remove();
                LOGGER.log(Level.TRACE, "Removed online visitor[ip={0}]", onlineVisitor.getKey());
            }
        }

        LOGGER.log(Level.DEBUG, "Current online visitor count [{0}]", ONLINE_VISITORS.size());
    }

    /**
     * Flushes the statistic to repository.
     * 
     * @throws ServiceException 
     */
    public void flushStatistic() throws ServiceException {
        if (!Latkes.isDataCacheEnabled()) {
            return;
        }

        final JSONObject statistic = (JSONObject) statisticRepository.getCache().get(REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC);

        if (null == statistic) {
            LOGGER.log(Level.INFO, "Not found statistic in cache, ignores sync");

            return;
        }

        final Transaction transaction = statisticRepository.beginTransaction();

        transaction.clearQueryCache(false);

        try {
            statisticRepository.getCache().remove(REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC);
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

                LOGGER.log(Level.DEBUG, "Updating article[id={0}, title={1}] view count",
                    new Object[] {articleId, cachedPage.optString(PageCaches.CACHED_TITLE)});

                final int oldViewCount = article.optInt(Article.ARTICLE_VIEW_COUNT);
                final int viewCount = oldViewCount + hitCount;

                article.put(Article.ARTICLE_VIEW_COUNT, viewCount);

                article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random()); // Updates random value

                articleRepository.update(articleId, article);

                cachedPage.put(PageCaches.CACHED_HIT_COUNT, 0);

                LOGGER.log(Level.DEBUG, "Updating article[id={0}, title={1}] view count from [{2}] to [{3}]",
                    new Object[] {articleId, article.optString(Article.ARTICLE_TITLE), oldViewCount, viewCount});
            }

            transaction.commit();

            LOGGER.log(Level.INFO, "Synchronized statistic from cache to repository[statistic={0}]", statistic);
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates statistic failed", e);
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
            LOGGER.log(Level.ERROR, "Updates statistic failed", e);
        }

        LOGGER.log(Level.DEBUG, "Updates statistic successfully");
    }

    /**
     * Sets the article repository with the specified article repository.
     * 
     * @param articleRepository the specified article repository
     */
    public void setArticleRepository(final ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Sets the statistic repository with the specified statistic repository.
     * 
     * @param statisticRepository the specified statistic repository
     */
    public void setStatisticRepository(final StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    /**
     * Sets the language service with the specified language service.
     * 
     * @param langPropsService the specified language service
     */
    public void setLangPropsService(final LangPropsService langPropsService) {
        this.langPropsService = langPropsService;
    }
}
