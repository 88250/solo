/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.util;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.*;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Crypts;
import org.b3log.latke.util.Strings;
import org.b3log.solo.Server;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Solo utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.11.0.2, Apr 21, 2021
 * @since 2.8.0
 */
public final class Solos {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Solos.class);

    /**
     * Solo User-Agent.
     */
    public static final String USER_AGENT = "Solo/" + Server.VERSION + "; +https://github.com/88250/solo";

    /**
     * Cookie expiry in 30 days.
     */
    private static final int COOKIE_EXPIRY = 60 * 60 * 24 * 7;

    /**
     * Cookie name.
     */
    public static final String COOKIE_NAME;

    /**
     * Cookie secret.
     */
    public static final String COOKIE_SECRET;

    /**
     * Indicates generating a static site.
     */
    public static boolean GEN_STATIC_SITE = false;

    static {
        String cookieNameConf = Latkes.getLatkeProperty("cookieName");
        if (StringUtils.isBlank(cookieNameConf)) {
            cookieNameConf = "solo";
        }
        COOKIE_NAME = cookieNameConf;

        String cookieSecret = Latkes.getLatkeProperty("cookieSecret");
        if (StringUtils.isBlank(cookieSecret)) {
            cookieSecret = RandomStringUtils.randomAlphanumeric(8);
        }
        COOKIE_SECRET = cookieSecret;
    }

    /**
     * Gets community user info.
     *
     * @param accessToken the specified access token
     * @return community user info, for example, <pre>
     * {
     *   "userId": "",
     *   "userName": "D",
     *   "userAvatar": ""
     * }
     * </pre>, returns {@code null} if not found QQ user info
     */
    public static JSONObject getUserInfo(final String accessToken) {
        try {
            final HttpResponse res = HttpRequest.post("https://ld246.com/user/ak").
                    form("access_token", accessToken).trustAllCerts(true).followRedirects(true).
                    connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
            if (200 != res.statusCode()) {
                return null;
            }
            res.charset("UTF-8");
            final JSONObject result = new JSONObject(res.bodyText());
            if (0 != result.optInt(Keys.CODE)) {
                return null;
            }
            return result.optJSONObject(Common.DATA);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets community user info failed", e);
            return null;
        }
    }

    /**
     * Blacklist IPs.
     */
    public static final List<String> BLACKLIST_IPS = new CopyOnWriteArrayList<>();

    /**
     * Reloads blacklist IPs.
     */
    public static void reloadBlacklistIPs() {
        try {
            final HttpResponse res = HttpRequest.get("https://ld246.com/apis/blacklist/ip").
                    trustAllCerts(true).followRedirects(true).
                    connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
            if (200 != res.statusCode()) {
                return;
            }
            res.charset("UTF-8");
            final JSONObject result = new JSONObject(res.bodyText());
            if (0 != result.optInt(Keys.CODE)) {
                return;
            }

            final JSONArray ips = result.optJSONArray(Common.DATA);
            BLACKLIST_IPS.clear();
            BLACKLIST_IPS.addAll(CollectionUtils.jsonArrayToList(ips));
        } catch (final Exception e) {
            // ignored
        }
    }

    /**
     * Constructs a successful result.
     *
     * @return result
     */
    public static JSONObject newSucc() {
        return new JSONObject().put(Keys.CODE, 0).put(Keys.MSG, "");
    }

    /**
     * Checks whether Solo is running on the local server.
     *
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public static boolean isLocalServer() {
        return StringUtils.containsIgnoreCase(Latkes.getServePath(), "localhost") || Strings.isIPv4(Latkes.getServerHost()) ||
                (StringUtils.isNotBlank(Latkes.getServerPort())) && !"80".equals(Latkes.getServerPort()) && !"443".equals(Latkes.getServerPort());
    }

    /**
     * Constructs a failed result.
     *
     * @return result
     */
    public static JSONObject newFail() {
        return new JSONObject().put(Keys.CODE, -1).put(Keys.MSG, "System is abnormal, please try again later");
    }

    private static long uploadTokenCheckTime;
    private static long uploadTokenTime;
    private static String uploadToken = "";
    private static String uploadURL = "https://ld246.com/upload/client";
    private static String uploadMsg = "";

    /**
     * Gets upload token.
     *
     * @param context the specified context
     * @return upload token and URL, returns {@code null} if not found
     */
    public static JSONObject getUploadToken(final RequestContext context) {
        try {
            final JSONObject currentUser = getCurrentUser(context);
            if (null == currentUser) {
                return null;
            }

            final String userName = currentUser.optString(User.USER_NAME);
            final String userB3Key = currentUser.optString(UserExt.USER_B3_KEY);
            if (StringUtils.isBlank(userB3Key)) {
                return null;
            }

            final long now = System.currentTimeMillis();
            if (3600000 >= now - uploadTokenTime) {
                return new JSONObject().
                        put(Common.UPLOAD_TOKEN, uploadToken).
                        put(Common.UPLOAD_URL, uploadURL).
                        put(Common.UPLOAD_MSG, uploadMsg);
            }

            if (15000 >= now - uploadTokenCheckTime) {
                return new JSONObject().
                        put(Common.UPLOAD_TOKEN, uploadToken).
                        put(Common.UPLOAD_URL, uploadURL).
                        put(Common.UPLOAD_MSG, uploadMsg);
            }

            final JSONObject requestJSON = new JSONObject().put(User.USER_NAME, userName).put(UserExt.USER_B3_KEY, userB3Key);
            final HttpResponse res = HttpRequest.post("https://ld246.com/apis/upload/token").trustAllCerts(true).followRedirects(true).
                    body(requestJSON.toString()).connectionTimeout(3000).timeout(7000).header("User-Agent", Solos.USER_AGENT).send();
            uploadTokenCheckTime = now;
            if (200 != res.statusCode()) {
                return null;
            }
            res.charset("UTF-8");
            final JSONObject result = new JSONObject(res.bodyText());
            if (0 != result.optInt(Keys.CODE)) {
                uploadMsg = result.optString(Keys.MSG);
                LOGGER.log(Level.ERROR, uploadMsg);
                return null;
            }

            final JSONObject data = result.optJSONObject(Common.DATA);
            uploadTokenTime = now;
            uploadToken = data.optString("uploadToken");
            uploadURL = data.optString("uploadURL");
            uploadMsg = "";
            return new JSONObject().
                    put(Common.UPLOAD_TOKEN, uploadToken).
                    put(Common.UPLOAD_URL, uploadURL).
                    put(Common.UPLOAD_MSG, uploadMsg);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets upload token failed", e);
            return null;
        }
    }

    /**
     * Sanitizes the specified file name.
     *
     * @param unsanitized the specified file name
     * @return sanitized file name
     */
    public static String sanitizeFilename(final String unsanitized) {
        return unsanitized.
                replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5\\.)]", "").
                replaceAll("[\\?\\\\/:|<>\\*\\[\\]\\(\\)\\$%\\{\\}@~]", "").
                replaceAll("\\s", "");
    }

    /**
     * Adds noindex header for Google. https://github.com/b3log/solo/issues/12631
     * <p>
     * 使用“noindex”阻止搜索引擎将您的网页编入索引 https://support.google.com/webmasters/answer/93710?hl=zh-Hans
     * </p>
     *
     * @param context the specified context
     */
    public static void addGoogleNoIndex(final RequestContext context) {
        context.setHeader("X-Robots-Tag", "noindex");
    }

    /**
     * Gets the current logged-in user.
     *
     * @param context the specified context
     * @return the current logged-in user, returns {@code null} if not found
     */
    public static JSONObject getCurrentUser(final RequestContext context) {
        final Request request = context.getRequest();
        final Set<Cookie> cookies = request.getCookies();
        if (cookies.isEmpty()) {
            return null;
        }

        final Response response = context.getResponse();
        final BeanManager beanManager = BeanManager.getInstance();
        final UserRepository userRepository = beanManager.getReference(UserRepository.class);
        try {
            for (final Cookie cookie : cookies) {
                if (!COOKIE_NAME.equals(cookie.getName())) {
                    continue;
                }

                final String value = Crypts.decryptByAES(cookie.getValue(), COOKIE_SECRET);
                final JSONObject cookieJSONObject = new JSONObject(value);

                final String userId = cookieJSONObject.optString(Keys.OBJECT_ID);
                if (StringUtils.isBlank(userId)) {
                    break;
                }

                JSONObject user = userRepository.get(userId);
                if (null == user) {
                    break;
                }

                final String b3Key = user.optString(UserExt.USER_B3_KEY);
                final String tokenVal = cookieJSONObject.optString(Keys.TOKEN);
                final String token = StringUtils.substringBeforeLast(tokenVal, ":");
                if (StringUtils.equals(b3Key, token)) {
                    login(user, response);
                    return user;
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.TRACE, "Parses cookie failed, clears the cookie [name=" + COOKIE_NAME + "]");
            final Cookie cookie = new Cookie(COOKIE_NAME, "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        return null;
    }

    /**
     * Logins the specified user from the specified request.
     *
     * @param response the specified response
     * @param user     the specified user
     */
    public static void login(final JSONObject user, final Response response) {
        try {
            final String userId = user.optString(Keys.OBJECT_ID);
            final JSONObject cookieJSONObject = new JSONObject();
            cookieJSONObject.put(Keys.OBJECT_ID, userId);
            final String b3Key = user.optString(UserExt.USER_B3_KEY);
            final String random = RandomStringUtils.randomAlphanumeric(8);
            cookieJSONObject.put(Keys.TOKEN, b3Key + ":" + random);
            final String cookieValue = Crypts.encryptByAES(cookieJSONObject.toString(), COOKIE_SECRET);
            final Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
            cookie.setMaxAge(COOKIE_EXPIRY);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Can not write cookie", e);
        }
    }

    /**
     * Logouts the specified user.
     *
     * @param request  the specified request
     * @param response the specified response
     * @return {@code true} if succeed, otherwise returns {@code false}
     */
    public static void logout(final Request request, final Response response) {
        if (null != response) {
            final Cookie cookie = new Cookie(COOKIE_NAME, "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    /**
     * Checks whether the current request is made by a logged in user
     * (including default user and administrator lists in <i>users</i>).
     *
     * @param context the specified request context
     * @return {@code true} if the current request is made by logged in user, returns {@code false} otherwise
     */
    public static boolean isLoggedIn(final RequestContext context) {
        return null != Solos.getCurrentUser(context);
    }

    /**
     * Checks whether the current request is made by logged in administrator.
     *
     * @param context the specified request context
     * @return {@code true} if the current request is made by logged in
     * administrator, returns {@code false} otherwise
     */
    public static boolean isAdminLoggedIn(final RequestContext context) {
        final JSONObject user = getCurrentUser(context);
        if (null == user) {
            return false;
        }

        return Role.ADMIN_ROLE.equals(user.optString(User.USER_ROLE));
    }

    /**
     * Checks whether need password to view the specified article with the specified request.
     * <p>
     * Checks session, if not represents, checks article property {@link Article#ARTICLE_VIEW_PWD view password}.
     * </p>
     * <p>
     * The blogger itself dose not need view password never.
     * </p>
     *
     * @param context the specified request context
     * @param article the specified article
     * @return {@code true} if need, returns {@code false} otherwise
     */
    public static boolean needViewPwd(final RequestContext context, final JSONObject article) {
        final String articleViewPwd = article.optString(Article.ARTICLE_VIEW_PWD);

        if (StringUtils.isBlank(articleViewPwd)) {
            return false;
        }

        final Request request = context.getRequest();
        if (null == request) {
            return true;
        }

        final Session session = request.getSession();
        if (null != session) {
            JSONObject viewPwds;
            final String viewPwdsStr = session.getAttribute(Common.ARTICLES_VIEW_PWD);
            if (null == viewPwdsStr) {
                viewPwds = new JSONObject();
            } else {
                viewPwds = new JSONObject(viewPwdsStr);
            }

            if (articleViewPwd.equals(viewPwds.get(article.optString(Keys.OBJECT_ID)))) {
                return false;
            }
        }

        final JSONObject currentUser = getCurrentUser(context);

        return !(null != currentUser && !Role.VISITOR_ROLE.equals(currentUser.optString(User.USER_ROLE)));
    }

    /**
     * Checks the specified request is made from a mobile device.
     *
     * @param request the specified request
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public static boolean isMobile(final Request request) {
        final Object val = request.getAttribute(Keys.HttpRequest.IS_MOBILE_BOT);
        if (!(val instanceof Boolean)) {
            return false;
        }

        return (boolean) val;
    }

    /**
     * Checks the specified request is made from a bot.
     *
     * @param request the specified request
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public static boolean isBot(final Request request) {
        final Object val = request.getAttribute(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT);
        if (!(val instanceof Boolean)) {
            return false;
        }

        return (boolean) val;
    }

    /**
     * Gets the default user avatar URL..
     *
     * @return default user avatar URL
     */
    public static String getDefaultAvatar() {
        return Latkes.getStaticServePath() + "/images/default-user-thumbnail.png";
    }

    /**
     * Clones a JSON object from the specified source object.
     *
     * @param src the specified source object
     * @return cloned object
     */
    public static JSONObject clone(final JSONObject src) {
        return new JSONObject(src, CollectionUtils.jsonArrayToArray(src.names(), String[].class));
    }

    /**
     * Builds pagination request with the specified path.
     *
     * @param path the specified path, "/{page}/{pageSize}/{windowSize}"
     * @return pagination request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": int,
     *     "paginationPageSize": int,
     *     "paginationWindowSize": int
     * }
     * </pre>
     */
    public static JSONObject buildPaginationRequest(final String path) {
        final Integer currentPageNum = getCurrentPageNum(path);
        final Integer pageSize = getPageSize(path);
        final Integer windowSize = getWindowSize(path);

        final JSONObject ret = new JSONObject();
        ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        ret.put(Pagination.PAGINATION_PAGE_SIZE, pageSize);
        ret.put(Pagination.PAGINATION_WINDOW_SIZE, windowSize);

        return ret;
    }

    /**
     * Default page size.
     */
    private static final int DEFAULT_PAGE_SIZE = 15;

    /**
     * Default window size.
     */
    private static final int DEFAULT_WINDOW_SIZE = 20;

    /**
     * Gets the request page number from the specified path.
     *
     * @param path the specified path
     * @return page number, returns {@code 1} if the specified request URI can not convert to an number
     */
    private static int getCurrentPageNum(final String path) {
        if (StringUtils.isBlank(path) || path.equals("/")) {
            return 1;
        }
        final String currentPageNumber = path.split("/")[0];
        if (!Strings.isNumeric(currentPageNumber)) {
            return 1;
        }

        return Integer.valueOf(currentPageNumber);
    }

    /**
     * Gets the request page size from the specified path.
     *
     * @param path the specified path
     * @return page number, returns {@value #DEFAULT_PAGE_SIZE} if the specified request URI can not convert to an number
     */
    private static int getPageSize(final String path) {
        if (StringUtils.isBlank(path)) {
            return DEFAULT_PAGE_SIZE;
        }
        final String[] parts = path.split("/");
        if (1 >= parts.length) {
            return DEFAULT_PAGE_SIZE;
        }
        final String pageSize = parts[1];
        if (!Strings.isNumeric(pageSize)) {
            return DEFAULT_PAGE_SIZE;
        }

        return Integer.valueOf(pageSize);
    }

    /**
     * Gets the request window size from the specified path.
     *
     * @param path the specified path
     * @return page number, returns {@value #DEFAULT_WINDOW_SIZE} if the specified request URI can not convert to an number
     */
    private static int getWindowSize(final String path) {
        if (StringUtils.isBlank(path)) {
            return DEFAULT_WINDOW_SIZE;
        }
        final String[] parts = path.split("/");
        if (2 >= parts.length) {
            return DEFAULT_WINDOW_SIZE;
        }
        final String windowSize = parts[2];
        if (!Strings.isNumeric(windowSize)) {
            return DEFAULT_WINDOW_SIZE;
        }

        return Integer.valueOf(windowSize);
    }

    /**
     * Private constructor.
     */
    private Solos() {
    }
}
