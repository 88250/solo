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
package org.b3log.solo.processor.console;

import com.qiniu.util.Auth;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jodd.io.ZipUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeDatabase;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
import org.b3log.latke.util.Execs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Skin;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.processor.renderer.ConsoleRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Thumbnails;
import org.json.JSONObject;

/**
 * Admin console render processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.2.10, Nov 15, 2016
 * @since 0.4.1
 */
@RequestProcessor
public class AdminConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdminConsole.class.getName());

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Filler.
     */
    @Inject
    private Filler filler;

    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;

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

        final JSONObject currentUser = userQueryService.getCurrentUser(request);
        final String userName = currentUser.optString(User.USER_NAME);

        dataModel.put(User.USER_NAME, userName);

        final String roleName = currentUser.optString(User.USER_ROLE);

        dataModel.put(User.USER_ROLE, roleName);

        final String email = currentUser.optString(User.USER_EMAIL);

        final String userAvatar = currentUser.optString(UserExt.USER_AVATAR);
        if (!Strings.isEmptyOrNull(userAvatar)) {
            dataModel.put(Common.GRAVATAR, userAvatar);
        } else {
            final String gravatar = Thumbnails.getGravatarURL(email, "128");
            dataModel.put(Common.GRAVATAR, gravatar);
        }

        try {
            final JSONObject qiniu = optionQueryService.getOptions(Option.CATEGORY_C_QINIU);

            dataModel.put(Option.ID_C_QINIU_DOMAIN, "");
            dataModel.put("qiniuUploadToken", "");

            if (null != qiniu && StringUtils.isNotBlank(qiniu.optString(Option.ID_C_QINIU_ACCESS_KEY))
                    && StringUtils.isNotBlank(qiniu.optString(Option.ID_C_QINIU_SECRET_KEY))
                    && StringUtils.isNotBlank(qiniu.optString(Option.ID_C_QINIU_BUCKET))
                    && StringUtils.isNotBlank(qiniu.optString(Option.ID_C_QINIU_DOMAIN))) {
                try {
                    final Auth auth = Auth.create(qiniu.optString(Option.ID_C_QINIU_ACCESS_KEY),
                            qiniu.optString(Option.ID_C_QINIU_SECRET_KEY));

                    final String uploadToken = auth.uploadToken(qiniu.optString(Option.ID_C_QINIU_BUCKET),
                            null, 3600 * 6, null);
                    dataModel.put("qiniuUploadToken", uploadToken);
                    dataModel.put(Option.ID_C_QINIU_DOMAIN, qiniu.optString(Option.ID_C_QINIU_DOMAIN));
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Qiniu settings error", e);
                }
            }

            final JSONObject preference = preferenceQueryService.getPreference();

            dataModel.put(Option.ID_C_LOCALE_STRING, preference.getString(Option.ID_C_LOCALE_STRING));
            dataModel.put(Option.ID_C_BLOG_TITLE, preference.getString(Option.ID_C_BLOG_TITLE));
            dataModel.put(Option.ID_C_BLOG_SUBTITLE, preference.getString(Option.ID_C_BLOG_SUBTITLE));
            dataModel.put(Common.VERSION, SoloServletListener.VERSION);
            dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            dataModel.put(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT, preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT));
            dataModel.put(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE, preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
            dataModel.put(Option.ID_C_LOCALE_STRING, preference.getString(Option.ID_C_LOCALE_STRING));
            dataModel.put(Option.ID_C_EDITOR_TYPE, preference.getString(Option.ID_C_EDITOR_TYPE));
            dataModel.put(Skin.SKIN_DIR_NAME, preference.getString(Skin.SKIN_DIR_NAME));

            Keys.fillRuntime(dataModel);
            filler.fillMinified(dataModel);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Admin index render failed", e);
        }

        fireFreeMarkerActionEvent(templateName, dataModel);
    }

    /**
     * Shows administrator functions with the specified context.
     *
     * @param request the specified request
     * @param context the specified context
     */
    @RequestProcessing(value = {"/admin-article.do",
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

        LOGGER.log(Level.TRACE, "Admin function[templateName={0}]", templateName);
        renderer.setTemplateName(templateName);

        final Locale locale = Latkes.getLocale();
        final Map<String, String> langs = langPropsService.getAll(locale);
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModel.put("isMySQL", RuntimeDatabase.MYSQL == Latkes.getRuntimeDatabase());

        dataModel.putAll(langs);

        Keys.fillRuntime(dataModel);

        dataModel.put(Option.ID_C_LOCALE_STRING, locale.toString());

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
        dataModel.put(Option.ID_C_LOCALE_STRING, locale.toString());

        JSONObject preference = null;

        try {
            preference = preferenceQueryService.getPreference();
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, "Loads preference failed", e);
        }

        final StringBuilder timeZoneIdOptions = new StringBuilder();
        final String[] availableIDs = TimeZone.getAvailableIDs();

        for (int i = 0; i < availableIDs.length; i++) {
            final String id = availableIDs[i];
            String option;

            if (id.equals(preference.optString(Option.ID_C_TIME_ZONE_ID))) {
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
     * Exports data as SQL file.
     *
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @param context the specified HTTP request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/console/export/sql", method = HTTPRequestMethod.GET)
    public void exportSQL(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
            throws Exception {
        if (!userQueryService.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        if (!Latkes.runsWithJDBCDatabase() || RuntimeDatabase.MYSQL != Latkes.getRuntimeDatabase()) {
            context.renderJSON().renderMsg("Just support MySQL export now");

            return;
        }

        final String dbUser = Latkes.getLocalProperty("jdbc.username");
        final String dbPwd = Latkes.getLocalProperty("jdbc.password");
        final String dbURL = Latkes.getLocalProperty("jdbc.URL");
        String db = StringUtils.substringAfterLast(dbURL, "/");
        db = StringUtils.substringBefore(db, "?");

        String sql;
        try {
            if (StringUtils.isNotBlank(dbPwd)) {
                sql = Execs.exec("mysqldump -u" + dbUser + " -p" + dbPwd + " --databases " + db);
            } else {
                sql = Execs.exec("mysqldump -u" + dbUser + " --databases " + db);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Export failed", e);
            context.renderJSON().renderMsg("Export failed, please check log");

            return;
        }

        final String tmpDir = System.getProperty("java.io.tmpdir");
        String localFilePath = tmpDir + "/b3_solo_" + UUID.randomUUID().toString() + ".sql";
        LOGGER.info(localFilePath);
        final File localFile = new File(localFilePath);

        try {
            final byte[] data = sql.getBytes("UTF-8");

            OutputStream output = new FileOutputStream(localFile);
            IOUtils.write(data, output);
            IOUtils.closeQuietly(output);

            final File zipFile = ZipUtil.zip(localFile);

            final FileInputStream inputStream = new FileInputStream(zipFile);
            final byte[] zipData = IOUtils.toByteArray(inputStream);
            IOUtils.closeQuietly(inputStream);

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"solo.sql.zip\"");

            final ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(zipData);
            outputStream.flush();
            outputStream.close();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Export failed", e);
            context.renderJSON().renderMsg("Export failed, please check log");

            return;
        }
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
            LOGGER.log(Level.WARN, "Event[FREEMARKER_ACTION] handle failed, ignores this exception for kernel health", e);
        }
    }
}
