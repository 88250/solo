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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Sessions;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.util.Thumbnails;
import org.json.JSONObject;

/**
 * User management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @version 1.1.0.6, Oct 17, 2015
 * @since 0.4.0
 */
@Service
public class UserMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserMgmtService.class.getName());

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Length of hashed password.
     */
    private static final int HASHED_PASSWORD_LENGTH = 32;

    /**
     * Tries to login with cookie.
     *
     * @param request the specified request
     * @param response the specified response
     */
    public void tryLogInWithCookie(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();

        if (null == cookies || 0 == cookies.length) {
            return;
        }

        try {
            for (int i = 0; i < cookies.length; i++) {
                final Cookie cookie = cookies[i];

                if (!"b3log-latke".equals(cookie.getName())) {
                    continue;
                }

                final JSONObject cookieJSONObject = new JSONObject(cookie.getValue());

                final String userEmail = cookieJSONObject.optString(User.USER_EMAIL);

                if (Strings.isEmptyOrNull(userEmail)) {
                    break;
                }

                final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
                final UserQueryService userQueryService = beanManager.getReference(UserQueryService.class);

                final JSONObject user = userQueryService.getUserByEmail(userEmail.toLowerCase().trim());

                if (null == user) {
                    break;
                }

                final String userPassword = user.optString(User.USER_PASSWORD);
                final String hashPassword = cookieJSONObject.optString(User.USER_PASSWORD);

                if (userPassword.equals(hashPassword)) {
                    Sessions.login(request, response, user);
                    LOGGER.log(Level.DEBUG, "Logged in with cookie[email={0}]", userEmail);
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Parses cookie failed, clears the cookie[name=b3log-latke]", e);

            final Cookie cookie = new Cookie("b3log-latke", null);

            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
        }
    }

    /**
     * Updates a user by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,      <pre>
     * {
     *     "oId": "",
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassword": "", // Unhashed
     *     "userRole": "", // optional
     *     "userURL": "", // optional
     * }
     * </pre>
     *
     * @throws ServiceException service exception
     */
    public void updateUser(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = userRepository.beginTransaction();

        try {
            final String oldUserId = requestJSONObject.optString(Keys.OBJECT_ID);
            final JSONObject oldUser = userRepository.get(oldUserId);

            if (null == oldUser) {
                throw new ServiceException(langPropsService.get("updateFailLabel"));
            }

            final String userNewEmail = requestJSONObject.optString(User.USER_EMAIL).toLowerCase().trim();
            // Check email is whether duplicated
            final JSONObject mayBeAnother = userRepository.getByEmail(userNewEmail);

            if (null != mayBeAnother && !mayBeAnother.optString(Keys.OBJECT_ID).equals(oldUserId)) {
                // Exists someone else has the save email as requested
                throw new ServiceException(langPropsService.get("duplicatedEmailLabel"));
            }

            // Update
            final String userName = requestJSONObject.optString(User.USER_NAME);
            final String userPassword = requestJSONObject.optString(User.USER_PASSWORD);

            oldUser.put(User.USER_EMAIL, userNewEmail);
            oldUser.put(User.USER_NAME, userName);

            final boolean mybeHashed = HASHED_PASSWORD_LENGTH == userPassword.length();
            final String newHashedPassword = MD5.hash(userPassword);
            final String oldHashedPassword = oldUser.optString(User.USER_PASSWORD);

            if (!"demo.b3log.org".equals(Latkes.getServerHost())) { // Skips the Solo Online Demo (http://demo.b3log.org)
                if (!mybeHashed || (!oldHashedPassword.equals(userPassword) && !oldHashedPassword.equals(newHashedPassword))) {
                    oldUser.put(User.USER_PASSWORD, newHashedPassword);
                }
            }

            final String userRole = requestJSONObject.optString(User.USER_ROLE);
            if (!Strings.isEmptyOrNull(userRole)) {
                oldUser.put(User.USER_ROLE, userRole);
            }

            final String userURL = requestJSONObject.optString(User.USER_URL);
            if (!Strings.isEmptyOrNull(userURL)) {
                oldUser.put(User.USER_URL, userURL);
            }
            
            final String userAvatar = requestJSONObject.optString(UserExt.USER_AVATAR);
            if (!StringUtils.equals(userAvatar, oldUser.optString(UserExt.USER_AVATAR))) {
                oldUser.put(UserExt.USER_AVATAR, userAvatar);
            }

            userRepository.update(oldUserId, oldUser);
            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates a user failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Swithches the user role between "defaultRole" and "visitorRole" by the specified user id.
     *
     * @param userId the specified user id
     * @throws ServiceException exception
     * @see User
     */
    public void changeRole(final String userId) throws ServiceException {
        final Transaction transaction = userRepository.beginTransaction();

        try {
            final JSONObject oldUser = userRepository.get(userId);

            if (null == oldUser) {
                throw new ServiceException(langPropsService.get("updateFailLabel"));
            }

            final String role = oldUser.optString(User.USER_ROLE);

            if (Role.VISITOR_ROLE.equals(role)) {
                oldUser.put(User.USER_ROLE, Role.DEFAULT_ROLE);
            } else if (Role.DEFAULT_ROLE.equals(role)) {
                oldUser.put(User.USER_ROLE, Role.VISITOR_ROLE);
            }

            userRepository.update(userId, oldUser);
            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates a user failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Adds a user with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,      <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassword": "", // Unhashed
     *     "userURL": "", // optional, uses 'servePath' instead if not specified
     *     "userRole": "", // optional, uses {@value Role#DEFAULT_ROLE} instead if not specified
     *     "userAvatar": "" // optional, users generated gravatar url instead if not specified
     * }
     * </pre>,see {@link User} for more details
     *
     * @return generated user id
     * @throws ServiceException service exception
     */
    public String addUser(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = userRepository.beginTransaction();

        try {
            final JSONObject user = new JSONObject();
            final String userEmail = requestJSONObject.optString(User.USER_EMAIL).trim().toLowerCase();
            final JSONObject duplicatedUser = userRepository.getByEmail(userEmail);

            if (null != duplicatedUser) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new ServiceException(langPropsService.get("duplicatedEmailLabel"));
            }

            user.put(User.USER_EMAIL, userEmail);

            final String userName = requestJSONObject.optString(User.USER_NAME);
            user.put(User.USER_NAME, userName);

            final String userPassword = requestJSONObject.optString(User.USER_PASSWORD);
            user.put(User.USER_PASSWORD, MD5.hash(userPassword));

            String userURL = requestJSONObject.optString(User.USER_URL);
            if (Strings.isEmptyOrNull(userURL)) {
                userURL = Latkes.getServePath();
            }

            if (!Strings.isURL(userURL)) {
                throw new ServiceException(langPropsService.get("urlInvalidLabel"));
            }

            user.put(User.USER_URL, userURL);

            final String roleName = requestJSONObject.optString(User.USER_ROLE, Role.DEFAULT_ROLE);
            user.put(User.USER_ROLE, roleName);

            user.put(UserExt.USER_ARTICLE_COUNT, 0);
            user.put(UserExt.USER_PUBLISHED_ARTICLE_COUNT, 0);

            String userAvatar = requestJSONObject.optString(UserExt.USER_AVATAR);
            if (Strings.isEmptyOrNull(userAvatar)) {
                userAvatar = Thumbnails.getGravatarURL(userEmail, "128");
            }
            user.put(UserExt.USER_AVATAR, userAvatar);

            userRepository.add(user);

            transaction.commit();

            return user.optString(Keys.OBJECT_ID);
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Adds a user failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Removes a user specified by the given user id.
     *
     * @param userId the given user id
     * @throws ServiceException service exception
     */
    public void removeUser(final String userId) throws ServiceException {
        final Transaction transaction = userRepository.beginTransaction();

        try {
            userRepository.remove(userId);

            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Removes a user[id=" + userId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Sets the user repository with the specified user repository.
     *
     * @param userRepository the specified user repository
     */
    public void setUserRepository(final UserRepository userRepository) {
        this.userRepository = userRepository;
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
