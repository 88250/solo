/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.Cookie;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.URLs;
import org.b3log.solo.cache.StatisticCache;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Statistic management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.5, Jan 8, 2020
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
    private static final Logger LOGGER = LogManager.getLogger(StatisticMgmtService.class);

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
                LOGGER.log(Level.TRACE, "Removed online visitor[ip={}]", onlineVisitor.getKey());
            }
        }

        LOGGER.log(Level.DEBUG, "Current online visitor count [{}]", ONLINE_VISITORS.size());
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
     * @param context  the specified request context
     * @param response the specified response
     * @return {@code true} if the specified request has been served, returns {@code false} otherwise
     */
    public static boolean hasBeenServed(final RequestContext context, final Response response) {
        final Request request = context.getRequest();
        final Set<Cookie> cookies = request.getCookies();
        if (cookies.isEmpty()) {
            return false;
        }

        boolean needToCreate = true;
        boolean needToAppend = true;
        JSONArray cookieJSONArray = null;

        try {
            for (final Cookie cookie : cookies) {
                if (!"visited".equals(cookie.getName())) {
                    continue;
                }

                final String value = URLs.decode(cookie.getValue());
                cookieJSONArray = new JSONArray(value);
                if (0 == cookieJSONArray.length()) {
                    return false;
                }

                needToCreate = false;

                for (int j = 0; j < cookieJSONArray.length(); j++) {
                    final String visitedURL = cookieJSONArray.optString(j);
                    if (request.getRequestURI().equals(visitedURL)) {
                        return true;
                    }
                }
            }

            if (needToCreate) {
                final StringBuilder builder = new StringBuilder("[").append("\"").append(request.getRequestURI()).append("\"]");
                final Cookie c = new Cookie("visited", URLs.encode(builder.toString()));
                c.setMaxAge(COOKIE_EXPIRY);
                c.setPath("/");
                response.addCookie(c);
            } else if (needToAppend) {
                cookieJSONArray.put(request.getRequestURI());

                final Cookie c = new Cookie("visited", URLs.encode(cookieJSONArray.toString()));
                c.setMaxAge(COOKIE_EXPIRY);
                c.setPath("/");
                response.addCookie(c);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Parses cookie failed, clears the cookie[name=visited]");

            final Cookie c = new Cookie("visited", "");
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
     * @param context  the specified request context
     * @param response the specified response
     * @throws ServiceException service exception
     */
    public void incBlogViewCount(final RequestContext context, final Response response) throws ServiceException {
        // v3.7.0 后开始使用社区浏览计数服务 https://github.com/Vanessa219/uvstat

//        if (Solos.isBot(context.getRequest())) {
//            return;
//        }
//
//        if (hasBeenServed(context, response)) {
//            return;
//        }
//
//        final Transaction transaction = optionRepository.beginTransaction();
//        JSONObject statistic;
//        try {
//            statistic = optionRepository.get(Option.ID_C_STATISTIC_BLOG_VIEW_COUNT);
//            if (null == statistic) {
//                return;
//            }
//
//            LOGGER.log(Level.TRACE, "Before inc blog view count is [{}]", statistic);
//
//            statistic.put(Option.OPTION_VALUE, statistic.optInt(Option.OPTION_VALUE) + 1);
//
//            updateStatistic(Option.ID_C_STATISTIC_BLOG_VIEW_COUNT, statistic);
//
//            transaction.commit();
//        } catch (final RepositoryException e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//
//            LOGGER.log(Level.ERROR, "Updates blog view count failed", e);
//
//            return;
//        }
//
//        LOGGER.log(Level.TRACE, "Inced blog view count[statistic={}]", statistic);
    }

    /**
     * Refreshes online visitor count for the specified request.
     *
     * @param request the specified request
     */
    public void onlineVisitorCount(final Request request) {
        if (Solos.isBot(request)) {
            return;
        }

        final String remoteAddr = Requests.getRemoteAddr(request);
        LOGGER.log(Level.DEBUG, "Current request [IP={}]", remoteAddr);
        ONLINE_VISITORS.put(remoteAddr, System.currentTimeMillis());
        LOGGER.log(Level.DEBUG, "Current online visitor count [{}]", ONLINE_VISITORS.size());
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
}
