/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
package org.b3log.solo.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Crypts;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Solo utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.5.0.1, Oct 16, 2018
 * @since 2.8.0
 */
public final class Solos {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Solos.class);

    /**
     * Mail configuration (mail.properties).
     */
    private static final ResourceBundle mailConf = ResourceBundle.getBundle("mail");

    /**
     * B3log Rhythm address.
     */
    public static final String B3LOG_RHYTHM_SERVE_PATH;

    /**
     * B3log Symphony address.
     */
    public static final String B3LOG_SYMPHONY_SERVE_PATH;

    /**
     * Favicon API.
     */
    public static final String FAVICON_API;

    /**
     * Gravatar address.
     */
    public static final String GRAVATAR;

    /**
     * Local file upload dir path.
     */
    public static final String UPLOAD_DIR_PATH;

    /**
     * Mobile skin.
     */
    public static final String MOBILE_SKIN;

    /**
     * Solo User-Agent.
     */
    public static final String USER_AGENT = "Solo/" + SoloServletListener.VERSION + "; +https://github.com/b3log/solo";

    /**
     * Cookie expiry in 30 days.
     */
    private static final int COOKIE_EXPIRY = 60 * 60 * 24 * 30;

    /**
     * Cookie name.
     */
    public static final String COOKIE_NAME;

    /**
     * Cookie secret.
     */
    public static final String COOKIE_SECRET;

    /**
     * Cookie HTTP only.
     */
    public static final boolean COOKIE_HTTP_ONLY;

    static {
        ResourceBundle solo;
        try {
            solo = ResourceBundle.getBundle("solo");
        } catch (final MissingResourceException e) {
            solo = ResourceBundle.getBundle("b3log"); // 2.8.0 向后兼容
        }

        B3LOG_RHYTHM_SERVE_PATH = solo.getString("rhythm.servePath");
        B3LOG_SYMPHONY_SERVE_PATH = solo.getString("symphony.servePath");
        FAVICON_API = solo.getString("faviconAPI");
        GRAVATAR = solo.getString("gravatar");
        String dir = solo.getString("uploadDir");
        if (StringUtils.isNotBlank(dir) && !StringUtils.endsWith(dir, "/")) {
            dir += "/";
        }
        UPLOAD_DIR_PATH = dir;

        String mobileSkin = Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME;
        try {
            mobileSkin = solo.getString("mobile.skin");
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Loads [mobile.skin] in solo.props failed [" + e.getMessage() + "], using [" + mobileSkin + "] as the default mobile skin");
        }
        MOBILE_SKIN = mobileSkin;
    }

    static {
        String cookieNameConf = Latkes.getLatkeProperty("cookieName");
        if (StringUtils.isBlank(cookieNameConf)) {
            cookieNameConf = "b3log-latke";
        }
        COOKIE_NAME = cookieNameConf;

        String cookieSecret = Latkes.getLatkeProperty("cookieSecret");
        if (StringUtils.isBlank(cookieSecret)) {
            cookieSecret = "Beyond";
        }
        COOKIE_SECRET = cookieSecret;

        COOKIE_HTTP_ONLY = Boolean.valueOf(Latkes.getLocalProperty("cookieHttpOnly"));
    }

    /**
     * Gets the current logged-in user.
     *
     * @param request  the specified request
     * @param response the specified response
     * @return the current logged-in user, returns {@code null} if not found
     */
    public static JSONObject getCurrentUser(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (null == cookies || 0 == cookies.length) {
            return null;
        }

        final BeanManager beanManager = BeanManager.getInstance();
        final UserRepository userRepository = beanManager.getReference(UserRepository.class);
        try {
            for (int i = 0; i < cookies.length; i++) {
                final Cookie cookie = cookies[i];
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

                final String userPassword = user.optString(User.USER_PASSWORD);
                final String token = cookieJSONObject.optString(Keys.TOKEN);
                final String hashPassword = StringUtils.substringBeforeLast(token, ":");
                if (userPassword.equals(hashPassword)) {
                    login(user, response);

                    return user;
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.TRACE, "Parses cookie failed, clears the cookie [name=" + COOKIE_NAME + "]");

            final Cookie cookie = new Cookie(COOKIE_NAME, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return null;
    }

    /**
     * Logins the specified user from the specified request.
     *
     * @param response the specified response
     * @param user     the specified user, for example,
     *                 {
     *                 "userEmail": "",
     *                 "userPassword": ""
     *                 }
     */
    public static void login(final JSONObject user, final HttpServletResponse response) {
        try {
            final String userId = user.optString(Keys.OBJECT_ID);
            final JSONObject cookieJSONObject = new JSONObject();
            cookieJSONObject.put(Keys.OBJECT_ID, userId);
            cookieJSONObject.put(User.USER_PASSWORD, user.optString(User.USER_PASSWORD));

            final String random = RandomStringUtils.randomAlphanumeric(16);
            cookieJSONObject.put(Keys.TOKEN, user.optString(User.USER_PASSWORD) + ":" + random);

            final String cookieValue = Crypts.encryptByAES(cookieJSONObject.toString(), COOKIE_SECRET);
            final Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_EXPIRY);
            cookie.setHttpOnly(COOKIE_HTTP_ONLY);
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
    public static void logout(final HttpServletRequest request, final HttpServletResponse response) {
        if (null != response) {
            final Cookie cookie = new Cookie(COOKIE_NAME, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
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
        return null != Solos.getCurrentUser(context.getRequest(), context.getResponse());
    }

    /**
     * Checks whether the current request is made by logged in administrator.
     *
     * @param context the specified request context
     * @return {@code true} if the current request is made by logged in
     * administrator, returns {@code false} otherwise
     */
    public static boolean isAdminLoggedIn(final RequestContext context) {
        final JSONObject user = getCurrentUser(context.getRequest(), context.getResponse());
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
     * @param request  the specified request
     * @param response the specified response
     * @param article  the specified article
     * @return {@code true} if need, returns {@code false} otherwise
     */
    public static boolean needViewPwd(final HttpServletRequest request, final HttpServletResponse response, final JSONObject article) {
        final String articleViewPwd = article.optString(Article.ARTICLE_VIEW_PWD);

        if (StringUtils.isBlank(articleViewPwd)) {
            return false;
        }

        if (null == request) {
            return true;
        }

        final HttpSession session = request.getSession();
        if (null != session) {
            Map<String, String> viewPwds = (Map<String, String>) session.getAttribute(Common.ARTICLES_VIEW_PWD);
            if (null == viewPwds) {
                viewPwds = new HashMap<>();
            }

            if (articleViewPwd.equals(viewPwds.get(article.optString(Keys.OBJECT_ID)))) {
                return false;
            }
        }

        final JSONObject currentUser = getCurrentUser(request, response);

        return !(null != currentUser && !Role.VISITOR_ROLE.equals(currentUser.optString(User.USER_ROLE)));
    }

    /**
     * Whether user configures the mail.properties.
     *
     * @return {@code true} if user configured, returns {@code false} otherwise
     */
    public static boolean isMailConfigured() {
        try {
            return StringUtils.isNotBlank(mailConf.getString("mail.user")) &&
                    StringUtils.isNotBlank(mailConf.getString("mail.password")) &&
                    StringUtils.isNotBlank(mailConf.getString("mail.smtp.host")) &&
                    StringUtils.isNotBlank(mailConf.getString("mail.smtp.port"));
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Checks the specified request is made from a mobile device.
     *
     * @param request the specified request
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public static boolean isMobile(final HttpServletRequest request) {
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
    public static boolean isBot(final HttpServletRequest request) {
        final Object val = request.getAttribute(Keys.HttpRequest.IS_SEARCH_ENGINE_BOT);
        if (!(val instanceof Boolean)) {
            return false;
        }

        return (boolean) val;
    }

    /**
     * Gets the Gravatar URL for the specified email with the specified size.
     *
     * @param email the specified email
     * @param size  the specified size
     * @return the Gravatar URL
     */
    public static String getGravatarURL(final String email, final String size) {
        return GRAVATAR + DigestUtils.md5Hex(email) + "?s=" + size;
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
