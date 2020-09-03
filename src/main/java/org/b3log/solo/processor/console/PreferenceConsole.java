/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.processor.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Sign;
import org.b3log.solo.service.OptionMgmtService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Preference console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/hzchendou">hzchendou</a>
 * @version 2.0.0.2, Jul 4, 2020
 * @since 0.4.0
 */
@Singleton
public class PreferenceConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PreferenceConsole.class);

    /**
     * Preference management service.
     */
    @Inject
    private PreferenceMgmtService preferenceMgmtService;

    /**
     * Option management service.
     */
    @Inject
    private OptionMgmtService optionMgmtService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Gets signs.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "signs": [{
     *         "oId": "",
     *         "signHTML": ""
     *      }, ...]
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getSigns(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        try {
            final JSONObject preference = optionQueryService.getPreference();
            final JSONArray signs = new JSONArray();
            final JSONArray allSigns = // includes the empty sign(id=0)
                    new JSONArray(preference.getString(Option.ID_C_SIGNS));
            for (int i = 1; i < allSigns.length(); i++) { // excludes the empty sign
                signs.put(allSigns.getJSONObject(i));
            }
            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            ret.put(Sign.SIGNS, signs);
            ret.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Gets preference.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "preference": {
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int,
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "skinDirName": "",
     *         "skins": "[{
     *             "skinDirName": ""
     *         }, ....]",
     *         "noticeBoard": "",
     *         "footerContent": "",
     *         "htmlHead": "",
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
     *         "feedOutputMode: "" // Optional values: "abstract"/"full"
     *         "feedOutputCnt": int,
     *         "faviconURL": "",
     *         "syncGitHub": boolean,
     *         "pullGitHub": boolean,
     *         "customVars" "", // 支持配置自定义参数 https://github.com/b3log/solo/issues/12535
     *         "showCodeBlockLn": boolean, // 支持代码块行号显示 https://github.com/88250/solo/issues/4
     *         "footnotes": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "showToC": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "autoSpace": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "fixTermTypo": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "chinesePunct": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "inlineMathAllowDigitAfterOpenMarker": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "editorMode": "", // 支持配置编辑器模式 https://github.com/88250/solo/issues/95
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getPreference(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                renderer.setJSONObject(new JSONObject().put(Keys.CODE, StatusCodes.ERR));
                return;
            }

            String footerContent = "";
            final JSONObject opt = optionQueryService.getOptionById(Option.ID_C_FOOTER_CONTENT);
            if (null != opt) {
                footerContent = opt.optString(Option.OPTION_VALUE);
            }
            preference.put(Option.ID_C_FOOTER_CONTENT, footerContent);

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            ret.put(Option.CATEGORY_C_PREFERENCE, preference);
            ret.put(Keys.CODE, StatusCodes.SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates the preference by the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "preference": {
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int,
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "noticeBoard": "",
     *         "footerContent": "",
     *         "htmlHead": "",
     *         "metaKeywords": "",
     *         "metaDescription": "",
     *         "enableArticleUpdateHint": boolean,
     *         "signs": [{
     *             "oId": "",
     *             "signHTML": ""
     *             }, ...],
     *         "allowVisitDraftViaPermalink": boolean,
     *         "articleListStyle": "",
     *         "feedOutputMode: "",
     *         "feedOutputCnt": int,
     *         "faviconURL": "",
     *         "syncGitHub": boolean,
     *         "pullGitHub": boolean,
     *         "customVars": "", // 支持配置自定义参数 https://github.com/b3log/solo/issues/12535
     *         "showCodeBlockLn": boolean, // 支持代码块行号显示 https://github.com/88250/solo/issues/4
     *         "footnotes": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "showToC": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "autoSpace": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "fixTermTypo": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "chinesePunct": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "inlineMathAllowDigitAfterOpenMarker": boolean, // Markdown 支持改进 https://github.com/88250/solo/issues/54
     *         "editorMode": "", // 支持配置编辑器模式 https://github.com/88250/solo/issues/95
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void updatePreference(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final JSONObject preference = requestJSONObject.getJSONObject(Option.CATEGORY_C_PREFERENCE);
            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            if (isInvalid(preference, ret)) {
                return;
            }

            preferenceMgmtService.updatePreference(preference);

            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Checks whether the specified preference is invalid and sets the specified response object.
     *
     * @param preference     the specified preference
     * @param responseObject the specified response object
     * @return {@code true} if the specified preference is invalid, returns {@code false} otherwise
     */
    private boolean isInvalid(final JSONObject preference, final JSONObject responseObject) {
        responseObject.put(Keys.CODE, StatusCodes.ERR);

        final StringBuilder errMsgBuilder = new StringBuilder('[' + langPropsService.get("paramSettingsLabel"));
        errMsgBuilder.append(" - ");

        String input = preference.optString(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("externalRelevantArticlesDisplayCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("relevantArticlesDisplayCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("randomArticlesDisplayCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexTagDisplayCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("pageSizeLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("windowSizeLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_FEED_OUTPUT_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("feedOutputCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
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
