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
package org.b3log.solo.processor.console;


import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.model.User;
import org.b3log.latke.plugin.ViewLoadEventData;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.processor.renderer.ConsoleRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Users;
import org.json.JSONObject;


/**
 * Admin console render processing.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Sep 6, 2012
 * @since 0.4.1
 */
@RequestProcessor
public final class AdminConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdminConsole.class.getName());

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();

    /**
     * Shows administrator index with the specified context.
     * 
     * @param request the specified request
     * @param context the specified context
     */
    @RequestProcessing(value = "/admin-index.do", method = HTTPRequestMethod.GET)
    public void showAdminIndex(final HttpServletRequest request, final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();

        context.setRenderer(renderer);
        final String templateName = "admin-index.ftl";

        renderer.setTemplateName(templateName);

        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModel.putAll(langs);

        final JSONObject currentUser = userUtils.getCurrentUser(request);
        final String userName = currentUser.optString(User.USER_NAME);
        final String roleName = currentUser.optString(User.USER_ROLE);

        dataModel.put(User.USER_NAME, userName);
        dataModel.put(User.USER_ROLE, roleName);

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            dataModel.put(Preference.LOCALE_STRING, preference.getString(Preference.LOCALE_STRING));
            dataModel.put(Preference.BLOG_TITLE, preference.getString(Preference.BLOG_TITLE));
            dataModel.put(Preference.BLOG_SUBTITLE, preference.getString(Preference.BLOG_SUBTITLE));
            dataModel.put(Common.VERSION, SoloServletListener.VERSION);
            dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            dataModel.put(Preference.ARTICLE_LIST_DISPLAY_COUNT, preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT));
            dataModel.put(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE, preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
            dataModel.put(Preference.LOCALE_STRING, preference.getString(Preference.LOCALE_STRING));
            dataModel.put(Preference.EDITOR_TYPE, preference.getString(Preference.EDITOR_TYPE));
            dataModel.put(Skin.SKIN_DIR_NAME, preference.getString(Skin.SKIN_DIR_NAME));

            Keys.fillServer(dataModel);
            Keys.fillRuntime(dataModel);
            filler.fillMinified(dataModel);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Admin index render failed", e);
        }

        fireFreeMarkerActionEvent(templateName, dataModel);
    }

    /**
     * Shows administrator functions with the specified context.
     * 
     * @param request the specified request
     * @param context the specified context
     */
    @RequestProcessing(value = { "/admin-article.do",
            "/admin-article-list.do",
            "/admin-comment-list.do",
            "/admin-link-list.do",
            "/admin-page-list.do",
            "/admin-others.do",
            "/admin-draft-list.do",
            "/admin-user-list.do",
            "/admin-plugin-list.do",
            "/admin-main.do",
            "/admin-about.do"},
        method = HTTPRequestMethod.GET)
    public void showAdminFunctions(final HttpServletRequest request, final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();

        context.setRenderer(renderer);

        final String requestURI = request.getRequestURI();
        final String templateName = StringUtils.substringBetween(requestURI, Latkes.getContextPath() + '/', ".") + ".ftl";

        LOGGER.log(Level.FINEST, "Admin function[templateName={0}]", templateName);
        renderer.setTemplateName(templateName);

        final Locale locale = Latkes.getLocale();
        final Map<String, String> langs = langPropsService.getAll(locale);
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModel.putAll(langs);

        Keys.fillServer(dataModel);
        Keys.fillRuntime(dataModel);

        dataModel.put(Preference.LOCALE_STRING, locale.toString());

        fireFreeMarkerActionEvent(templateName, dataModel);
    }

    /**
     * Shows administrator preference function with the specified context.
     * 
     * @param request the specified request
     * @param context the specified context
     */
    @RequestProcessing(value = "/admin-preference.do", method = HTTPRequestMethod.GET)
    public void showAdminPreferenceFunction(final HttpServletRequest request, final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer();

        context.setRenderer(renderer);

        final String templateName = "admin-preference.ftl";

        renderer.setTemplateName(templateName);

        final Locale locale = Latkes.getLocale();
        final Map<String, String> langs = langPropsService.getAll(locale);
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModel.putAll(langs);
        Keys.fillServer(dataModel);
        dataModel.put(Preference.LOCALE_STRING, locale.toString());

        JSONObject preference = null;

        try {
            preference = preferenceQueryService.getPreference();
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Loads preference failed", e);
        }

        final StringBuilder timeZoneIdOptions = new StringBuilder();
        final String[] availableIDs = TimeZone.getAvailableIDs();

        for (int i = 0; i < availableIDs.length; i++) {
            final String id = availableIDs[i];
            String option;

            if (id.equals(preference.optString(Preference.TIME_ZONE_ID))) {
                option = "<option value=\"" + id + "\" selected=\"true\">" + id + "</option>";
            } else {
                option = "<option value=\"" + id + "\">" + id + "</option>";
            }

            timeZoneIdOptions.append(option);
        }

        dataModel.put("timeZoneIdOptions", timeZoneIdOptions.toString());

        fireFreeMarkerActionEvent(templateName, dataModel);
    }

    /**
     * Fires FreeMarker action event with the host template name and data model.
     * 
     * @param hostTemplateName the specified host template name
     * @param dataModel the specified data model
     */
    private void fireFreeMarkerActionEvent(final String hostTemplateName, final Map<String, Object> dataModel) {
        try {
            final ViewLoadEventData data = new ViewLoadEventData();

            data.setViewName(hostTemplateName);
            data.setDataModel(dataModel);
            eventManager.fireEventSynchronously(new Event<ViewLoadEventData>(Keys.FREEMARKER_ACTION, data));
            if (Strings.isEmptyOrNull((String) dataModel.get(Plugin.PLUGINS))) {
                // There is no plugin for this template, fill ${plugins} with blank.
                dataModel.put(Plugin.PLUGINS, "");
            }
        } catch (final EventException e) {
            LOGGER.log(Level.WARNING, "Event[FREEMARKER_ACTION] handle failed, ignores this exception for kernel health", e);
        }
    }
}
