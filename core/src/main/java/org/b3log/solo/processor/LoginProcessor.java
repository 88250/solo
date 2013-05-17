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
package org.b3log.solo.processor;


import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Sessions;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.processor.renderer.ConsoleRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Randoms;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Login/logout processor.
 *
 * <p>Initializes administrator</p>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:dongxu.wang@acm.org">Dongxu Wang</a>
 * @version 1.1.1.4, Mar 11, 2013
 * @since 0.3.1
 */
@RequestProcessor
public final class LoginProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LoginProcessor.class.getName());

    /**
     * User query service.
     */
    private static UserQueryService userQueryService = UserQueryService.getInstance();

    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();

    /**
     * Mail service.
     */
    private MailService mailService = MailServiceFactory.getMailService();

    /**
     * User management service.
     */
    private UserMgmtService userMgmtService = UserMgmtService.getInstance();

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Shows login page.
     *
     * @param context the specified context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/login", method = HTTPRequestMethod.GET)
    public void showLogin(final HTTPRequestContext context) throws Exception {
        final HttpServletRequest request = context.getRequest();

        String destinationURL = request.getParameter(Common.GOTO);

        if (Strings.isEmptyOrNull(destinationURL)) {
            destinationURL = Latkes.getServePath() + Common.ADMIN_INDEX_URI;
        }

        final HttpServletResponse response = context.getResponse();

        LoginProcessor.tryLogInWithCookie(request, response);

        if (null != userService.getCurrentUser(request)) { // User has already logged in
            response.sendRedirect(destinationURL);

            return;
        }
        renderPage(context, "login.ftl", destinationURL);
    }

    /**
     * Logins.
     *
     * <p> Renders the response with a json object, for example,
     * <pre>
     * {
     *     "isLoggedIn": boolean,
     *     "msg": "" // optional, exists if isLoggedIn equals to false
     * }
     * </pre> </p>
     *
     * @param context the specified context
     */
    @RequestProcessing(value = { "/login"}, method = HTTPRequestMethod.POST)
    public void login(final HTTPRequestContext context) {
        final HttpServletRequest request = context.getRequest();

        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();

        renderer.setJSONObject(jsonObject);

        try {
            jsonObject.put(Common.IS_LOGGED_IN, false);
            final String loginFailLabel = langPropsService.get("loginFailLabel");

            jsonObject.put(Keys.MSG, loginFailLabel);

            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, context.getResponse());
            final String userEmail = requestJSONObject.getString(User.USER_EMAIL);
            final String userPwd = requestJSONObject.getString(User.USER_PASSWORD);

            if (Strings.isEmptyOrNull(userEmail) || Strings.isEmptyOrNull(userPwd)) {
                return;
            }

            LOGGER.log(Level.INFO, "Login[email={0}]", userEmail);

            final JSONObject user = userQueryService.getUserByEmail(userEmail);

            if (null == user) {
                LOGGER.log(Level.WARNING, "Not found user[email={0}]", userEmail);
                return;
            }

            if (MD5.hash(userPwd).equals(user.getString(User.USER_PASSWORD))) {
                Sessions.login(request, context.getResponse(), user);

                LOGGER.log(Level.INFO, "Logged in[email={0}]", userEmail);

                jsonObject.put(Common.IS_LOGGED_IN, true);
                jsonObject.put("to", Latkes.getServePath() + Common.ADMIN_INDEX_URI);
                jsonObject.remove(Keys.MSG);

                return;
            }

            LOGGER.log(Level.WARNING, "Wrong password[{0}]", userPwd);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Logout.
     *
     * @param context the specified context
     * @throws IOException io exception
     */
    @RequestProcessing(value = { "/logout"}, method = HTTPRequestMethod.GET)
    public void logout(final HTTPRequestContext context) throws IOException {
        final HttpServletRequest httpServletRequest = context.getRequest();

        Sessions.logout(httpServletRequest, context.getResponse());

        String destinationURL = httpServletRequest.getParameter(Common.GOTO);

        if (Strings.isEmptyOrNull(destinationURL)) {
            destinationURL = "/";
        }

        context.getResponse().sendRedirect(destinationURL);
    }

    /**
     * Shows forgotten password page.
     *
     * @param context the specified context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/forgot", method = HTTPRequestMethod.GET)
    public void showForgot(final HTTPRequestContext context) throws Exception {
        final HttpServletRequest request = context.getRequest();

        String destinationURL = request.getParameter(Common.GOTO);

        if (Strings.isEmptyOrNull(destinationURL)) {
            destinationURL = Latkes.getServePath() + Common.ADMIN_INDEX_URI;
        }

        renderPage(context, "reset-pwd.ftl", destinationURL);
    }

    /**
     * reset forgotten password.
     *
     * <p> Renders the response with a json object, for example,
     * <pre>
     * {
     *     "isLoggedIn": boolean,
     *     "msg": "" // optional, exists if isLoggedIn equals to false
     * }
     * </pre> </p>
     *
     * @param context the specified context
     */
    @RequestProcessing(value = { "/forgot"}, method = HTTPRequestMethod.POST)
    public void forgot(final HTTPRequestContext context) {
        final HttpServletRequest request = context.getRequest();

        final JSONRenderer renderer = new JSONRenderer();

        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();

        renderer.setJSONObject(jsonObject);

        try {
            jsonObject.put("succeed", false);
            jsonObject.put(Keys.MSG, langPropsService.get("resetPwdSuccessMsg"));

            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, context.getResponse());
            final String userEmail = requestJSONObject.getString(User.USER_EMAIL);

            if (Strings.isEmptyOrNull(userEmail)) {
                LOGGER.log(Level.WARNING, "Why user's email is empty");
                return;
            }

            LOGGER.log(Level.INFO, "Login[email={0}]", userEmail);

            final JSONObject user = userQueryService.getUserByEmail(userEmail);

            if (null == user) {
                LOGGER.log(Level.WARNING, "Not found user[email={0}]", userEmail);
                jsonObject.put(Keys.MSG, langPropsService.get("userEmailNotFoundMsg"));
                return;
            }

            if (isPwdExpired()) {
                LOGGER.log(Level.WARNING, "User[email={0}]'s random password has been expired", userEmail);
                jsonObject.put(Keys.MSG, langPropsService.get("userEmailNotFoundMsg"));
                return;
            }

            sendRandomPwd(user, userEmail, jsonObject);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Tries to login with cookie.
     *
     * @param request the specified request
     * @param response the specified response
     */
    public static void tryLogInWithCookie(final HttpServletRequest request, final HttpServletResponse response) {
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

                final JSONObject user = userQueryService.getUserByEmail(userEmail.toLowerCase().trim());

                if (null == user) {
                    break;
                }

                final String userPassword = user.optString(User.USER_PASSWORD);
                final String hashPassword = cookieJSONObject.optString(User.USER_PASSWORD);

                if (userPassword.equals(hashPassword)) {
                    Sessions.login(request, response, user);
                    LOGGER.log(Level.INFO, "Logged in with cookie[email={0}]", userEmail);
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Parses cookie failed, clears the cookie[name=b3log-latke]", e);

            final Cookie cookie = new Cookie("b3log-latke", null);

            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
        }
    }

    /**
     * Whether user is going to update an expired password out of 24 hours.
     *
     * @return whether the password has been expired TODO implement it
     */
    private boolean isPwdExpired() {
        return false;
    }

    /**
     * Send the random password to the given address and update the ever one.
     *
     * @param user the user relative to the given email below
     * @param userEmail the given email
     * @param jsonObject return code and message object
     * @throws JSONException the JSONException
     * @throws ServiceException the ServiceException
     * @throws IOException the IOException
     */
    private void sendRandomPwd(final JSONObject user, final String userEmail, final JSONObject jsonObject) throws JSONException, ServiceException, IOException {
        final JSONObject preference = preferenceQueryService.getPreference();
        final String randomPwd = new Randoms().nextString();
        final String blogTitle = preference.getString(Preference.BLOG_TITLE);
        final String adminEmail = preference.getString(Preference.ADMIN_EMAIL);
        final String mailSubject = langPropsService.get("resetPwdMailSubject");
        final String mailBody = langPropsService.get("resetPwdMailBody") + randomPwd;
        final MailService.Message message = new MailService.Message();

        // FIXME whether we should put the ever-hashed password here, rather during updating?
        user.put(User.USER_PASSWORD, randomPwd);
        userMgmtService.updateUser(user);

        message.setFrom(adminEmail);
        message.addRecipient(userEmail);
        message.setSubject(mailSubject);
        message.setHtmlBody(mailBody);

        mailService.send(message);

        jsonObject.put("succeed", true);
        jsonObject.put("to", Latkes.getServePath() + "/login");
        jsonObject.put(Keys.MSG, langPropsService.get("resetPwdSuccessMsg"));

        LOGGER.log(Level.FINER, "Sending a mail[mailSubject={0}, mailBody=[{1}] to [{2}]", new Object[] {mailSubject, mailBody, userEmail});
    }

    /**
     * Render a page template with the destination URL.
     *
     * @param context the context
     * @param pageTemplate the page template
     * @param destinationURL the destination URL
     * @throws JSONException the JSONException
     * @throws ServiceException the ServiceException
     */
    private void renderPage(final HTTPRequestContext context, final String pageTemplate, final String destinationURL) throws JSONException, ServiceException {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();

        renderer.setTemplateName(pageTemplate);
        context.setRenderer(renderer);

        final Map<String, Object> dataModel = renderer.getDataModel();
        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
        final JSONObject preference = preferenceQueryService.getPreference();

        dataModel.putAll(langs);
        dataModel.put(Common.GOTO, destinationURL);
        dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        dataModel.put(Common.VERSION, SoloServletListener.VERSION);
        dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
        dataModel.put(Preference.BLOG_TITLE, preference.getString(Preference.BLOG_TITLE));

        Keys.fillServer(dataModel);
        Keys.fillRuntime(dataModel);
        filler.fillMinified(dataModel);
    }
}
