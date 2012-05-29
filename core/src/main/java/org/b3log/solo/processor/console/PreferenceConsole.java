/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Sign;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.QueryResults;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Preference console request processing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Mar 28, 2012
 * @since 0.4.0
 */
@RequestProcessor
public final class PreferenceConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceConsole.class.getName());
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();
    /**
     * Preference management service.
     */
    private PreferenceMgmtService preferenceMgmtService = PreferenceMgmtService.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Preference URI prefix.
     */
    private static final String PREFERENCE_URI_PREFIX = "/console/preference/";

    /**
     * Gets reply template.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "replyNotificationTemplate": {
     *         "subject": "",
     *         "body": ""
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/console/reply/notification/template", method = HTTPRequestMethod.GET)
    public void getReplyNotificationTemplate(final HttpServletRequest request,
                                             final HttpServletResponse response,
                                             final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject replyNotificationTemplate = preferenceQueryService.getReplyNotificationTemplate();

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);

            ret.put(Preference.REPLY_NOTIFICATION_TEMPLATE,
                    replyNotificationTemplate);
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates reply template.
     * 
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "replyNotificationTemplate": {
     *         "subject": "",
     *         "body": ""
     *     }
     * }
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/console/reply/notification/template", method = HTTPRequestMethod.PUT)
    public void updateReplyNotificationTemplate(final HttpServletRequest request,
                                                final HttpServletResponse response,
                                                final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject requestJSONObject = AbstractAction.parseRequestJSONObject(request, response);

            final JSONObject replyNotificationTemplate = requestJSONObject.getJSONObject(Preference.REPLY_NOTIFICATION_TEMPLATE);

            preferenceMgmtService.updateReplyNotificationTemplate(replyNotificationTemplate);

            final JSONObject ret = new JSONObject();

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));

            renderer.setJSONObject(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Gets signs.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "signs": [{
     *         "oId": "",
     *         "signHTML": ""
     *      }, ...]
     * }
     * </pre>
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/console/signs/", method = HTTPRequestMethod.GET)
    public void getSigns(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request, response)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            final JSONArray signs = new JSONArray();

            final JSONArray allSigns = // includes the empty sign(id=0)
                            new JSONArray(preference.getString(Preference.SIGNS));
            for (int i = 1; i < allSigns.length(); i++) { // excludes the empty sign
                signs.put(allSigns.getJSONObject(i));
            }

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);

            ret.put(Sign.SIGNS, signs);
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Gets preference.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "preference": {
     *         "recentArticleDisplayCount": int,
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int,
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "mostCommentArticleDisplayCount": int,
     *         "blogHost": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "skinName": "",
     *         "skinDirName": "",
     *         "skins": "[{
     *             "skinName": "",
     *             "skinDirName": ""
     *         }, ....]",
     *         "noticeBoard": "",
     *         "htmlHead": "",
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "adminEmail": "",
     *         "metaKeywords": "",
     *         "metaDescription": "",
     *         "enableArticleUpdateHint": boolean,
     *         "signs": "[{
     *             "oId": "",
     *             "signHTML": ""
     *         }, ...]",
     *         "allowVisitDraftViaPermalink": boolean,
     *         "version": "",
     *         "articleListStyle": "", // Optional values: "titleOnly"/"titleAndContent"/"titleAndAbstract"
     *         "commentable": boolean,
     *         "feedOutputMode: "" // Optional values: "abstract"/"full"
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = PREFERENCE_URI_PREFIX, method = HTTPRequestMethod.GET)
    public void getPreference(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceQueryService.getPreference();

            if (null == preference) {
                renderer.setJSONObject(QueryResults.defaultResult());

                return;
            }

            final JSONObject ret = new JSONObject();

            renderer.setJSONObject(ret);
            ret.put(Preference.PREFERENCE, preference);
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates the preference by the specified request.
     *
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "preference": {
     *         "recentArticleDisplayCount": int,
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "mostCommentArticleDisplayCount": int,
     *         "skinDirName": "",
     *         "blogHost": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "noticeBoard": "",
     *         "htmlHead": "",
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "metaKeywords": "",
     *         "metaDescription": "",
     *         "enableArticleUpdateHint": boolean,
     *         "signs": [{
     *             "oId": "",
     *             "signHTML": ""
     *         }, ...],
     *         "allowVisitDraftViaPermalink": boolean,
     *         "articleListStyle": "",
     *         "commentable": boolean,
     *         "feedOutputMode: ""
     *     }
     * }, see {@link org.b3log.solo.model.Preference} for more details
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = PREFERENCE_URI_PREFIX, method = HTTPRequestMethod.PUT)
    public void updatePreference(final HttpServletRequest request, final HttpServletResponse response, final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject requestJSONObject = AbstractAction.parseRequestJSONObject(request, response);

            final JSONObject preference = requestJSONObject.getJSONObject(Preference.PREFERENCE);

            preferenceMgmtService.updatePreference(preference);

            final JSONObject ret = new JSONObject();

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));

            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
