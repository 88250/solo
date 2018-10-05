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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

/**
 * User management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @author <a href="https://github.com/nanolikeyou">nanolikeyou</a>
 * @version 1.1.0.14, Oct 5, 2018
 * @since 0.4.0
 */
@Service
public class UserMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserMgmtService.class);

    /**
     * Length of hashed password.
     */
    private static final int HASHED_PASSWORD_LENGTH = 32;

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
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Option management service.
     */
    @Inject
    private OptionMgmtService optionMgmtService;

    /**
     * Updates a user by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "oId": "",
     *                          "userName": "",
     *                          "userEmail": "",
     *                          "userPassword": "", // Unhashed
     *                          "userRole": "", // optional
     *                          "userURL": "", // optional
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

            oldUser.put(User.USER_EMAIL, userNewEmail);

            // Update
            final String userName = requestJSONObject.optString(User.USER_NAME);
            if (UserExt.invalidUserName(userName)) {
                throw new ServiceException(langPropsService.get("userNameInvalidLabel"));
            }
            oldUser.put(User.USER_NAME, userName);

            final String userPassword = requestJSONObject.optString(User.USER_PASSWORD);
            final boolean maybeHashed = HASHED_PASSWORD_LENGTH == userPassword.length();
            final String newHashedPassword = DigestUtils.md5Hex(userPassword);
            final String oldHashedPassword = oldUser.optString(User.USER_PASSWORD);

            if (!"demo.b3log.org".equals(Latkes.getServerHost())) { // Skips the Solo Online Demo (http://demo.b3log.org)
                if (!maybeHashed || (!oldHashedPassword.equals(userPassword) && !oldHashedPassword.equals(newHashedPassword))) {
                    oldUser.put(User.USER_PASSWORD, newHashedPassword);
                }
            }

            final String userRole = requestJSONObject.optString(User.USER_ROLE);
            if (StringUtils.isNotBlank(userRole)) {
                oldUser.put(User.USER_ROLE, userRole);
            }

            final String userURL = requestJSONObject.optString(User.USER_URL);
            if (StringUtils.isNotBlank(userURL)) {
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
     * Switches the user role between "defaultRole" and "visitorRole" by the specified user id.
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
     * @param requestJSONObject the specified request json object, for example,
     *                          "userName": "",
     *                          "userEmail": "",
     *                          "userPassword": "", // Unhashed
     *                          "userURL": "", // optional, uses 'servePath' instead if not specified
     *                          "userRole": "", // optional, uses {@value Role#DEFAULT_ROLE} instead if not specified
     *                          "userAvatar": "" // optional, users generated gravatar url instead if not specified
     *                          ,see {@link User} for more details
     * @return generated user id
     * @throws ServiceException service exception
     */
    public synchronized String addUser(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = userRepository.beginTransaction();

        try {
            final JSONObject user = new JSONObject();
            final String userEmail = requestJSONObject.optString(User.USER_EMAIL).trim().toLowerCase();
            if (!Strings.isEmail(userEmail)) {
                throw new ServiceException(langPropsService.get("mailInvalidLabel"));
            }

            JSONObject duplicatedUser = userRepository.getByEmail(userEmail);
            if (null != duplicatedUser) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new ServiceException(langPropsService.get("duplicatedEmailLabel"));
            }

            user.put(User.USER_EMAIL, userEmail);

            final String userName = requestJSONObject.optString(User.USER_NAME);
            if (UserExt.invalidUserName(userName)) {
                throw new ServiceException(langPropsService.get("userNameInvalidLabel"));
            }
            duplicatedUser = userRepository.getByUserName(userName);
            if (null != duplicatedUser) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new ServiceException(langPropsService.get("duplicatedUserNameLabel"));
            }
            user.put(User.USER_NAME, userName);

            final String userPassword = requestJSONObject.optString(User.USER_PASSWORD);
            user.put(User.USER_PASSWORD, DigestUtils.md5Hex(userPassword));

            String userURL = requestJSONObject.optString(User.USER_URL);
            if (StringUtils.isBlank(userURL)) {
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
            if (StringUtils.isBlank(userAvatar)) {
                userAvatar = Solos.getGravatarURL(userEmail, "128");
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

            LOGGER.log(Level.ERROR, "Removes a user [id=" + userId + "] failed", e);
            throw new ServiceException(e);
        }

        final JSONObject oauthGitHubOpt = optionQueryService.getOptionById(Option.ID_C_OAUTH_GITHUB);
        if (null != oauthGitHubOpt) {
            String value = oauthGitHubOpt.optString(Option.OPTION_VALUE);
            try {
                final Set<String> githubs = CollectionUtils.jsonArrayToSet(new JSONArray(value));
                final String oAuthPair = Option.getOAuthPair(githubs, userId);
                if (StringUtils.isNotBlank(oAuthPair)) {
                    githubs.remove(oAuthPair);
                    value = new JSONArray(githubs).toString();
                    oauthGitHubOpt.put(Option.OPTION_VALUE, value);
                    optionMgmtService.addOrUpdateOption(oauthGitHubOpt);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Remove oauth GitHub data for user [id=" + userId + "] failed", e);
            }
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
