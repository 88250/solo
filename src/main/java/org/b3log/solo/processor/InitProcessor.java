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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.processor.console.ConsoleRenderer;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.InitService;
import org.b3log.solo.service.UserQueryService;
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
 * @version 1.2.0.16, Dec 3, 2018
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
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Shows initialization page.
     *
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/init", method = HttpMethod.GET)
    public void showInit(final RequestContext context) {
        if (initService.isInited()) {
            context.sendRedirect("/");

            return;
        }

        final HttpServletRequest request = context.getRequest();
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
     * <p>
     * Request json:
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassword": "",
     *     "userAvatar": "" // optional
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/init", method = HttpMethod.POST)
    public void initSolo(final RequestContext context) {
        if (initService.isInited()) {
            context.sendRedirect("/");

            return;
        }

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject().put(Keys.STATUS_CODE, false);
        renderer.setJSONObject(ret);

        try {
            final JSONObject requestJSONObject = context.requestJSON();
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

            final HttpServletRequest request = context.getRequest();
            final Locale locale = Locales.getLocale(request);
            requestJSONObject.put(Keys.LOCALE, locale.toString());

            initService.init(requestJSONObject);

            final JSONObject admin = userQueryService.getAdmin();
            final HttpServletResponse response = context.getResponse();
            Solos.login(admin, response);

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            ret.put(Keys.MSG, e.getMessage());
        }
    }
}
