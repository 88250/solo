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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Sessions;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.processor.console.ConsoleRenderer;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.InitService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Solo initialization service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.14, Sep 21, 2018
 * @since 0.4.0
 */
@RequestProcessor
public class InitProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InitProcessor.class);

    /**
     * Initialization service.
     */
    @Inject
    private InitService initService;

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Shows initialization page.
     *
     * @param context  the specified http request context
     * @param request  the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = "/init", method = HTTPRequestMethod.GET)
    public void showInit(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        if (initService.isInited()) {
            response.sendRedirect("/");

            return;
        }

        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();
        renderer.setTemplateName("init.ftl");
        context.setRenderer(renderer);
        final Map<String, Object> dataModel = renderer.getDataModel();
        final Map<String, String> langs = langPropsService.getAll(Locales.getLocale(request));
        dataModel.putAll(langs);
        dataModel.put(Common.VERSION, SoloServletListener.VERSION);
        dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
        dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        Keys.fillRuntime(dataModel);
        dataModelService.fillMinified(dataModel);
    }

    /**
     * Initializes Solo.
     *
     * @param context           the specified http request context
     * @param request           the specified http servlet request
     * @param response          the specified http servlet response
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "userName": "",
     *                          "userEmail": "",
     *                          "userPassword": "",
     *                          "userAvatar": "" // optional
     *                          }
     * @throws Exception exception
     */
    @RequestProcessing(value = "/init", method = HTTPRequestMethod.POST)
    public void initSolo(final HTTPRequestContext context, final HttpServletRequest request,
                         final HttpServletResponse response, final JSONObject requestJSONObject) throws Exception {
        if (initService.isInited()) {
            response.sendRedirect("/");

            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject().put(Keys.STATUS_CODE, false);
        renderer.setJSONObject(ret);

        try {
            final String userName = requestJSONObject.optString(User.USER_NAME);
            final String userEmail = requestJSONObject.optString(User.USER_EMAIL);
            final String userPassword = requestJSONObject.optString(User.USER_PASSWORD);
            if (StringUtils.isBlank(userName) || StringUtils.isBlank(userEmail) || StringUtils.isBlank(userPassword)
                    || !Strings.isEmail(userEmail)) {
                ret.put(Keys.MSG, "Init failed, please check your input");

                return;
            }

            if (UserExt.invalidUserName(userName)) {
                ret.put(Keys.MSG, "Init failed, please check your username (length [1, 20], content {a-z, A-Z, 0-9}, do not contain 'admin' for security reason]");

                return;
            }

            final Locale locale = Locales.getLocale(request);
            requestJSONObject.put(Keys.LOCALE, locale.toString());

            initService.init(requestJSONObject);

            // If initialized, login the admin
            final JSONObject admin = new JSONObject();
            admin.put(User.USER_NAME, userName);
            admin.put(User.USER_EMAIL, userEmail);
            admin.put(User.USER_ROLE, Role.ADMIN_ROLE);
            admin.put(User.USER_PASSWORD, userPassword);
            String avatar = requestJSONObject.optString(UserExt.USER_AVATAR);
            if (StringUtils.isBlank(avatar)) {
                avatar = Solos.getGravatarURL(userEmail, "128");
            }
            admin.put(UserExt.USER_AVATAR, avatar);

            Sessions.login(request, response, admin);

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            ret.put(Keys.MSG, e.getMessage());
        }
    }
}
