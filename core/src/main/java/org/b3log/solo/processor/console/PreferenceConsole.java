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


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Requests;
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
 * @version 1.0.0.5, Mar 5, 2013
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

            ret.put(Preference.REPLY_NOTIFICATION_TEMPLATE, replyNotificationTemplate);
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
            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);

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
     *         "mostViewArticleDisplayCount": int,
     *         "recentCommentDisplayCount": int,
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int,
     *         "mostCommentArticleDisplayCount": int,
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "blogTitle": "",
     *         "blogSubtitle": "",
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
     *         "feedOutputCnt": int
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
     *         "mostViewArticleDisplayCount": int,
     *         "recentCommentDisplayCount": int,
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int,
     *         "mostCommentArticleDisplayCount": int,
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "skinDirName": "",
     *         "blogHost": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "noticeBoard": "",
     *         "htmlHead": "",
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
     *         "feedOutputMode: "",
     *         "feedOutputCnt": int
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
            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);

            final JSONObject preference = requestJSONObject.getJSONObject(Preference.PREFERENCE);

            final JSONObject ret = new JSONObject();

            renderer.setJSONObject(ret);

            if (isInvalid(preference, ret)) {
                return;
            }

            preferenceMgmtService.updatePreference(preference);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();

            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Checks whether the specified preference is invalid and sets the specified response object.
     * 
     * @param preference the specified preference
     * @param responseObject the specified response object
     * @return {@code true} if the specified preference is invalid, returns {@code false} otherwise
     */
    private boolean isInvalid(final JSONObject preference, final JSONObject responseObject) {
        responseObject.put(Keys.STATUS_CODE, false);

        final StringBuilder errMsgBuilder = new StringBuilder('[' + langPropsService.get("paramSettingsLabel"));

        errMsgBuilder.append(" - ");

        String input = preference.optString(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);

        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("externalRelevantArticlesDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.RELEVANT_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("relevantArticlesDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.RANDOM_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("randomArticlesDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.MOST_COMMENT_ARTICLE_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexMostCommentArticleDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.MOST_VIEW_ARTICLE_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexMostViewArticleDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.RECENT_COMMENT_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexRecentCommentDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.MOST_USED_TAG_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexTagDisplayCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.ARTICLE_LIST_DISPLAY_COUNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("pageSizeLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("windowSizeLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Preference.FEED_OUTPUT_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("feedOutputCntLabel")).append("]  ").append(
                langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        return false;
    }

    /**
     * Checks whether the specified input is a non-negative integer.
     * 
     * @param input the specified input
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    private boolean isNonNegativeInteger(final String input) {
        try {
            return 0 <= Integer.valueOf(input);
        } catch (final Exception e) {
            return false;
        }
    }
}
