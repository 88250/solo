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

import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Requests;
import org.b3log.solo.cache.StatisticCache;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Statistic management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.1, Apr 1, 2018
 * @since 0.5.0
 */
@Service
public class StatisticMgmtService {

    /**
     * Online visitor cache.
     * <p>
     * &lt;ip, recentTime&gt;
     * </p>
     */
    public static final Map<String, Long> ONLINE_VISITORS = new ConcurrentHashMap<>();

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticMgmtService.class);

    /**
     * Online visitor expiration in 5 minutes.
     */
    private static final int ONLINE_VISITOR_EXPIRATION = 300000;

    /**
     * Cookie expiry of "visited".
     */
    private static final int COOKIE_EXPIRY = 60 * 60 * 24; // 24 hours

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

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
     * Statistic cache.
     */
    @Inject
    private StatisticCache statisticCache;

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
     * Determines whether the specified request has been served.
     * <p>
     * A "served request" is a request a URI as former one. For example, if a client is request "/test", all requests from the client
     * subsequent in 24 hours will be treated as served requests, requested URIs save in client cookie (name: "visited").
     * </p>
     * <p>
     * If the specified request has not been served, appends the request URI in client cookie.
     * </p>
     * <p>
     * Sees this issue (https://github.com/b3log/solo/issues/44) for more details.
     * </p>
     *
     * @param request  the specified request
     * @param response the specified response
     * @return {@code true} if the specified request has been served, returns {@code false} otherwise
     */
    public static boolean hasBeenServed(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (null == cookies || 0 == cookies.length) {
            return false;
        }

        Cookie cookie;
        boolean needToCreate = true;
        boolean needToAppend = true;
        JSONArray cookieJSONArray = null;

        try {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];

                if (!"visited".equals(cookie.getName())) {
                    continue;
                }

                final String value = URLDecoder.decode(cookie.getValue(), "UTF-8");
                cookieJSONArray = new JSONArray(value);
                if (null == cookieJSONArray || 0 == cookieJSONArray.length()) {
                    return false;
                }

                needToCreate = false;

                for (int j = 0; j < cookieJSONArray.length(); j++) {
                    final String visitedURL = cookieJSONArray.optString(j);

                    if (request.getRequestURI().equals(visitedURL)) {
                        needToAppend = false;
                        return true;
                    }
                }
            }

            if (needToCreate) {
                final StringBuilder builder = new StringBuilder("[").append("\"").append(request.getRequestURI()).append("\"]");
                final Cookie c = new Cookie("visited", URLEncoder.encode(builder.toString(), "UTF-8"));
                c.setMaxAge(COOKIE_EXPIRY);
                c.setPath("/");
                response.addCookie(c);
            } else if (needToAppend) {
                cookieJSONArray.put(request.getRequestURI());

                final Cookie c = new Cookie("visited", URLEncoder.encode(cookieJSONArray.toString(), "UTF-8"));
                c.setMaxAge(COOKIE_EXPIRY);
                c.setPath("/");
                response.addCookie(c);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Parses cookie failed, clears the cookie[name=visited]");

            final Cookie c = new Cookie("visited", null);
            c.setMaxAge(0);
            c.setPath("/");

            response.addCookie(c);
        }

        return false;
    }

    /**
     * Blog statistic view count +1.
     * <p>
     * If it is a search engine bot made the specified request, will NOT increment blog statistic view count.
     * </p>
     * <p>
     * There is a cron job (/console/stat/viewcnt) to flush the blog view count from cache to datastore.
     * </p>
     *
     * @param request  the specified request
     * @param response the specified response
     * @throws ServiceException service exception
     * @see Requests#searchEngineBotRequest(javax.servlet.http.HttpServletRequest)
     */
    public void incBlogViewCount(final HttpServletRequest request, final HttpServletResponse response) throws ServiceException {
        if (Requests.searchEngineBotRequest(request)) {
            return;
        }

        if (hasBeenServed(request, response)) {
            return;
        }

        final Transaction transaction = optionRepository.beginTransaction();
        JSONObject statistic;
        try {
            statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_VIEW_COUNT);
            if (null == statistic) {
                return;
            }

            LOGGER.log(Level.TRACE, "Before inc blog view count is [{0}]", statistic);

            statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) + 1);

            updateStatistic(Option.ID_C_STATISTIC_BLOG_VIEW_COUNT, statistic);

            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates blog view count failed", e);

            return;
        }

        LOGGER.log(Level.TRACE, "Inced blog view count[statistic={0}]", statistic);
    }

    /**
     * Blog statistic article count +1.
     *
     * @throws RepositoryException repository exception
     */
    public void incBlogArticleCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_ARTICLE_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) + 1);
        updateStatistic(Option.ID_C_STATISTIC_BLOG_ARTICLE_COUNT, statistic);
    }

    /**
     * Blog statistic published article count +1.
     *
     * @throws RepositoryException repository exception
     */
    public void incPublishedBlogArticleCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_PUBLISHED_ARTICLE_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) + 1);
        updateStatistic(Option.ID_C_STATISTIC_PUBLISHED_ARTICLE_COUNT, statistic);
    }

    /**
     * Blog statistic article count -1.
     *
     * @throws RepositoryException repository exception
     */
    public void decBlogArticleCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_ARTICLE_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) - 1);
        updateStatistic(Option.ID_C_STATISTIC_BLOG_ARTICLE_COUNT, statistic);
    }

    /**
     * Blog statistic published article count -1.
     *
     * @throws RepositoryException repository exception
     */
    public void decPublishedBlogArticleCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_PUBLISHED_ARTICLE_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) - 1);
        updateStatistic(Option.ID_C_STATISTIC_PUBLISHED_ARTICLE_COUNT, statistic);
    }

    /**
     * Blog statistic comment count +1.
     *
     * @throws RepositoryException repository exception
     */
    public void incBlogCommentCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }
        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) + 1);
        updateStatistic(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT, statistic);
    }

    /**
     * Blog statistic comment(published article) count +1.
     *
     * @throws RepositoryException repository exception
     */
    public void incPublishedBlogCommentCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }
        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) + 1);
        updateStatistic(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, statistic);
    }

    /**
     * Blog statistic comment count -1.
     *
     * @throws RepositoryException repository exception
     */
    public void decBlogCommentCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) - 1);
        updateStatistic(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT, statistic);
    }

    /**
     * Blog statistic comment(published article) count -1.
     *
     * @throws RepositoryException repository exception
     */
    public void decPublishedBlogCommentCount() throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) - 1);
        updateStatistic(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, statistic);
    }

    /**
     * Sets blog comment count with the specified count.
     *
     * @param count the specified count
     * @throws RepositoryException repository exception
     */
    public void setBlogCommentCount(final int count) throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, count);
        updateStatistic(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT, statistic);
    }

    /**
     * Sets blog comment(published article) count with the specified count.
     *
     * @param count the specified count
     * @throws RepositoryException repository exception
     */
    public void setPublishedBlogCommentCount(final int count) throws RepositoryException {
        final JSONObject statistic = optionRepository.get(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Option.OPTION_VALUE, count);
        updateStatistic(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, statistic);
    }

    /**
     * Refreshes online visitor count for the specified request.
     *
     * @param request the specified request
     */
    public void onlineVisitorCount(final HttpServletRequest request) {
        if (Requests.searchEngineBotRequest(request)) {
            return;
        }

        final String remoteAddr = Requests.getRemoteAddr(request);

        LOGGER.log(Level.DEBUG, "Current request [IP={0}]", remoteAddr);

        ONLINE_VISITORS.put(remoteAddr, System.currentTimeMillis());
        LOGGER.log(Level.DEBUG, "Current online visitor count [{0}]", ONLINE_VISITORS.size());
    }

    /**
     * Updates the statistic with the specified statistic.
     *
     * @param id        the specified statistic id
     * @param statistic the specified statistic
     * @throws RepositoryException repository exception
     */
    private void updateStatistic(final String id, final JSONObject statistic) throws RepositoryException {
        optionRepository.update(id, statistic);
        statisticCache.clear();
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
     * Sets the option repository with the specified option repository.
     *
     * @param optionRepository the specified option repository
     */
    public void setOptionRepository(final OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
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
