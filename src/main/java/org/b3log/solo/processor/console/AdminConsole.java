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
package org.b3log.solo.processor.console;

import jodd.io.ZipUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.model.User;
import org.b3log.latke.plugin.ViewLoadEventData;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Execs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.ExportService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Admin console render processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.7.0.16, Sep 17, 2019
 * @since 0.4.1
 */
@Singleton
@Before(ConsoleAuthAdvice.class)
public class AdminConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdminConsole.class);

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
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Export service.
     */
    @Inject
    private ExportService exportService;

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;

    /**
     * Shows administrator index with the specified context.
     *
     * @param context the specified context
     */
    public void showAdminIndex(final RequestContext context) {
        final String templateName = "admin-index.ftl";
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer(context, templateName);
        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.putAll(langs);
        final JSONObject currentUser = Solos.getCurrentUser(context.getRequest(), context.getResponse());
        final String userName = currentUser.optString(User.USER_NAME);
        dataModel.put(User.USER_NAME, userName);
        final String roleName = currentUser.optString(User.USER_ROLE);
        dataModel.put(User.USER_ROLE, roleName);
        final String userAvatar = currentUser.optString(UserExt.USER_AVATAR);
        dataModel.put(Common.GRAVATAR, userAvatar);

        try {
            final JSONObject preference = optionQueryService.getPreference();
            dataModel.put(Option.ID_C_LOCALE_STRING, preference.getString(Option.ID_C_LOCALE_STRING));
            dataModel.put(Option.ID_C_BLOG_TITLE, preference.getString(Option.ID_C_BLOG_TITLE));
            dataModel.put(Option.ID_C_BLOG_SUBTITLE, preference.getString(Option.ID_C_BLOG_SUBTITLE));
            dataModel.put(Common.VERSION, SoloServletListener.VERSION);
            dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            dataModel.put(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT, preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT));
            dataModel.put(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE, preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
            final JSONObject skin = optionQueryService.getSkin();
            dataModel.put(Option.CATEGORY_C_SKIN, skin.optString(Option.ID_C_SKIN_DIR_NAME));
            Keys.fillRuntime(dataModel);
            dataModelService.fillMinified(dataModel);
            dataModel.put(Common.LUTE_AVAILABLE, Markdowns.LUTE_AVAILABLE);
            // 内置 HTTPS+CDN 文件存储 https://github.com/b3log/solo/issues/12556
            dataModel.put(Common.UPLOAD_TOKEN, "");
            dataModel.put(Common.UPLOAD_URL, "");
            dataModel.put(Common.UPLOAD_MSG, langPropsService.get("getUploadTokenErrLabel"));
            final JSONObject upload = Solos.getUploadToken(context);
            if (null != upload) {
                dataModel.put(Common.UPLOAD_TOKEN, upload.optString(Common.UPLOAD_TOKEN));
                dataModel.put(Common.UPLOAD_URL, upload.optString(Common.UPLOAD_URL));
                dataModel.put(Common.UPLOAD_MSG, upload.optString(Common.UPLOAD_MSG));
            }
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
            dataModelService.fillCommon(context, dataModel, preference);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Admin index render failed", e);
        }

        fireFreeMarkerActionEvent(templateName, dataModel);
    }

    /**
     * Shows administrator functions with the specified context.
     *
     * @param context the specified context
     */
    public void showAdminFunctions(final RequestContext context) {
        final String requestURI = context.requestURI();
        final String templateName = StringUtils.substringBetween(requestURI, Latkes.getContextPath() + '/', ".") + ".ftl";
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer(context, templateName);

        final Locale locale = Latkes.getLocale();
        final Map<String, String> langs = langPropsService.getAll(locale);
        final Map<String, Object> dataModel = renderer.getDataModel();

        // 使用 MySQL 时不启用 SQL 导出功能 https://github.com/b3log/solo/issues/12806
        dataModel.put("supportExport", Latkes.RuntimeDatabase.H2 == Latkes.getRuntimeDatabase());
        dataModel.putAll(langs);
        Keys.fillRuntime(dataModel);
        dataModel.put(Option.ID_C_LOCALE_STRING, locale.toString());

        fireFreeMarkerActionEvent(templateName, dataModel);
    }

    /**
     * Shows administrator preference function with the specified context.
     *
     * @param context the specified context
     */
    public void showAdminPreferenceFunction(final RequestContext context) {
        final String templateName = "admin-preference.ftl";
        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer(context, templateName);

        final Locale locale = Latkes.getLocale();
        final Map<String, String> langs = langPropsService.getAll(locale);
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.putAll(langs);
        dataModel.put(Option.ID_C_LOCALE_STRING, locale.toString());

        final JSONObject preference = optionQueryService.getPreference();
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
     * Exports data as SQL zip file.
     *
     * @param context the specified request context
     */
    public void exportSQL(final RequestContext context) {
        final HttpServletResponse response = context.getResponse();

        if (!Solos.isAdminLoggedIn(context)) {
            context.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        try {
            Thread.sleep(550); // 前端会发两次请求，文件名又是按秒生成，所以两次请求需要错开至少 1 秒避免文件名冲突
        } catch (final Exception e) {
            // ignored
        }

        final Latkes.RuntimeDatabase runtimeDatabase = Latkes.getRuntimeDatabase();
        if (Latkes.RuntimeDatabase.H2 != runtimeDatabase && Latkes.RuntimeDatabase.MYSQL != runtimeDatabase) {
            context.renderJSON().renderMsg("Just support MySQL/H2 export now");

            return;
        }

        final String dbUser = Latkes.getLocalProperty("jdbc.username");
        final String dbPwd = Latkes.getLocalProperty("jdbc.password");
        final String dbURL = Latkes.getLocalProperty("jdbc.URL");
        String sql; // exported SQL script

        if (Latkes.RuntimeDatabase.MYSQL == runtimeDatabase) {
            String db = StringUtils.substringAfterLast(dbURL, "/");
            db = StringUtils.substringBefore(db, "?");

            try {
                if (StringUtils.isNotBlank(dbPwd)) {
                    sql = Execs.exec("mysqldump -u" + dbUser + " -p" + dbPwd + " --databases " + db, 60 * 1000 * 5);
                } else {
                    sql = Execs.exec("mysqldump -u" + dbUser + " --databases " + db, 60 * 1000 * 5);
                }
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Export failed", e);
                context.renderJSON().renderMsg("Export failed, please check log");

                return;
            }
        } else if (Latkes.RuntimeDatabase.H2 == runtimeDatabase) {
            try (final Connection connection = Connections.getConnection();
                 final Statement statement = connection.createStatement()) {
                final StringBuilder sqlBuilder = new StringBuilder();
                final ResultSet resultSet = statement.executeQuery("SCRIPT");
                while (resultSet.next()) {
                    final String stmt = resultSet.getString(1);
                    sqlBuilder.append(stmt).append(Strings.LINE_SEPARATOR);
                }
                resultSet.close();

                sql = sqlBuilder.toString();
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Export failed", e);
                context.renderJSON().renderMsg("Export failed, please check log");

                return;
            }
        } else {
            context.renderJSON().renderMsg("Just support MySQL/H2 export now");

            return;
        }

        if (StringUtils.isBlank(sql)) {
            LOGGER.log(Level.ERROR, "Export failed, executing export script returns empty");
            context.renderJSON().renderMsg("Export failed, please check log");

            return;
        }

        final String tmpDir = System.getProperty("java.io.tmpdir");
        final String date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String localFilePath = tmpDir + File.separator + "solo-" + date + ".sql";
        LOGGER.trace(localFilePath);
        final File localFile = new File(localFilePath);

        try {
            final byte[] data = sql.getBytes("UTF-8");
            try (final OutputStream output = new FileOutputStream(localFile)) {
                IOUtils.write(data, output);
            }

            final File zipFile = ZipUtil.zip(localFile);
            byte[] zipData;
            try (final FileInputStream inputStream = new FileInputStream(zipFile)) {
                zipData = IOUtils.toByteArray(inputStream);
            }

            response.setContentType("application/zip");
            final String fileName = "solo-sql-" + date + ".zip";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            final ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(zipData);
            outputStream.flush();
            outputStream.close();

            // 导出 SQL 包后清理临时文件 https://github.com/b3log/solo/issues/12770
            localFile.delete();
            zipFile.delete();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Export failed", e);
            context.renderJSON().renderMsg("Export failed, please check log");

            return;
        }
    }

    /**
     * Exports data as JSON zip file.
     *
     * @param context the specified request context
     */
    public void exportJSON(final RequestContext context) {
        final HttpServletResponse response = context.getResponse();
        if (!Solos.isAdminLoggedIn(context)) {
            context.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        try {
            Thread.sleep(550);
        } catch (final Exception e) {
            // ignored
        }

        final String tmpDir = System.getProperty("java.io.tmpdir");
        final String date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String localFilePath = tmpDir + File.separator + "solo-" + date + ".json";
        LOGGER.trace(localFilePath);
        final File localFile = new File(localFilePath);

        try {
            final JSONObject json = exportService.getJSONs();
            final byte[] data = json.toString(4).getBytes("UTF-8");

            try (final OutputStream output = new FileOutputStream(localFile)) {
                IOUtils.write(data, output);
            }

            try (final FileInputStream inputStream = new FileInputStream(ZipUtil.zip(localFile));
                 final ServletOutputStream outputStream = response.getOutputStream()) {
                final byte[] zipData = IOUtils.toByteArray(inputStream);
                response.setContentType("application/zip");
                final String fileName = "solo-json-" + date + ".zip";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                outputStream.write(zipData);
                outputStream.flush();
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Export failed", e);
            context.renderJSON().renderMsg("Export failed, please check log");

            return;
        }
    }

    /**
     * Exports data as Hexo markdown zip file.
     *
     * @param context the specified request context
     */
    public void exportHexo(final RequestContext context) {
        final HttpServletResponse response = context.getResponse();
        if (!Solos.isAdminLoggedIn(context)) {
            context.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        try {
            Thread.sleep(550);
        } catch (final Exception e) {
            // ignored
        }

        final String tmpDir = System.getProperty("java.io.tmpdir");
        final String date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String localFilePath = tmpDir + File.separator + "solo-hexo-" + date;
        LOGGER.trace(localFilePath);
        final File localFile = new File(localFilePath);

        final File postDir = new File(localFilePath + File.separator + "posts");
        final File passwordDir = new File(localFilePath + File.separator + "passwords");
        final File draftDir = new File(localFilePath + File.separator + "drafts");

        try {
            if (!postDir.mkdirs()) {
                throw new Exception("Create dir [" + postDir.getPath() + "] failed");
            }
            if (!passwordDir.mkdirs()) {
                throw new Exception("Create dir [" + passwordDir.getPath() + "] failed");
            }
            if (!draftDir.mkdirs()) {
                throw new Exception("Create dir [" + draftDir.getPath() + "] failed");
            }

            final JSONObject result = exportService.exportHexoMDs();
            final List<JSONObject> posts = (List<JSONObject>) result.opt("posts");
            exportService.exportHexoMd(posts, postDir.getPath());
            final List<JSONObject> passwords = (List<JSONObject>) result.opt("passwords");
            exportService.exportHexoMd(passwords, passwordDir.getPath());
            final List<JSONObject> drafts = (List<JSONObject>) result.opt("drafts");
            exportService.exportHexoMd(drafts, draftDir.getPath());

            final File zipFile = ZipUtil.zip(localFile);
            byte[] zipData;
            try (final FileInputStream inputStream = new FileInputStream(zipFile)) {
                zipData = IOUtils.toByteArray(inputStream);
                response.setContentType("application/zip");
                final String fileName = "solo-hexo-" + date + ".zip";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            }

            try (final ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(zipData);
                outputStream.flush();
            }
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
     * @param dataModel        the specified data model
     */
    private void fireFreeMarkerActionEvent(final String hostTemplateName, final Map<String, Object> dataModel) {
        final ViewLoadEventData data = new ViewLoadEventData();

        data.setViewName(hostTemplateName);
        data.setDataModel(dataModel);
        eventManager.fireEventSynchronously(new Event<>(Keys.FREEMARKER_ACTION, data));
        if (StringUtils.isBlank((String) dataModel.get(Plugin.PLUGINS))) {
            // There is no plugin for this template, fill ${plugins} with blank.
            dataModel.put(Plugin.PLUGINS, "");
        }
    }
}
