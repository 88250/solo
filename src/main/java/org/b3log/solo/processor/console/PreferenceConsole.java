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
package org.b3log.solo.processor.console;

import static org.b3log.solo.model.Option.CATEGORY_C_ALIYUN;
import static org.b3log.solo.model.Option.CATEGORY_C_CLOU_STORGE;
import static org.b3log.solo.model.Option.CATEGORY_C_QINIU;
import static org.b3log.solo.model.Option.ID_C_ALIYUN_ACCESS_KEY;
import static org.b3log.solo.model.Option.ID_C_ALIYUN_BUCKET;
import static org.b3log.solo.model.Option.ID_C_ALIYUN_DOMAIN;
import static org.b3log.solo.model.Option.ID_C_ALIYUN_SECRET_KEY;
import static org.b3log.solo.model.Option.ID_C_CLOUD_STORGE_KEY;
import static org.b3log.solo.model.Option.ID_C_QINIU_ACCESS_KEY;
import static org.b3log.solo.model.Option.ID_C_QINIU_BUCKET;
import static org.b3log.solo.model.Option.ID_C_QINIU_DOMAIN;
import static org.b3log.solo.model.Option.ID_C_QINIU_SECRET_KEY;
import static org.b3log.solo.util.TernaryExpressionUtils.expressionSelectOptions;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Sign;
import org.b3log.solo.model.Skin;
import org.b3log.solo.service.OptionMgmtService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Preference console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.15, Dec 3, 2018
 * @since 0.4.0
 */
@RequestProcessor
@Before(ConsoleAdminAuthAdvice.class)
public class PreferenceConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceConsole.class);

    /**
     * Preference URI prefix.
     */
    private static final String PREFERENCE_URI_PREFIX = "/console/preference/";

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

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
     * Gets reply template.
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
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/console/reply/notification/template", method = HttpMethod.GET)
    public void getReplyNotificationTemplate(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject replyNotificationTemplate = preferenceQueryService.getReplyNotificationTemplate();

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            ret.put("replyNotificationTemplate", replyNotificationTemplate);
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates reply template.
     *
     * <p>
     * Request json:
     * <pre>
     * {
     *     "replyNotificationTemplate": {
     *         "subject": "",
     *         "body": ""
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/console/reply/notification/template", method = HttpMethod.PUT)
    public void updateReplyNotificationTemplate(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final JSONObject replyNotificationTemplate = requestJSONObject.getJSONObject("replyNotificationTemplate");
            preferenceMgmtService.updateReplyNotificationTemplate(replyNotificationTemplate);

            final JSONObject ret = new JSONObject();
            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            renderer.setJSONObject(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Gets signs.
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
     * @param context the specified http request context
     */
    @RequestProcessing(value = "/console/signs/", method = HttpMethod.GET)
    public void getSigns(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            final JSONArray signs = new JSONArray();
            final JSONArray allSigns
                    = // includes the empty sign(id=0)
                    new JSONArray(preference.getString(Option.ID_C_SIGNS));

            for (int i = 1; i < allSigns.length(); i++) { // excludes the empty sign
                signs.put(allSigns.getJSONObject(i));
            }

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            ret.put(Sign.SIGNS, signs);
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
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
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "skinName": "",
     *         "skinDirName": "",
     *         "skins": "[{
     *             "skinName": "",
     *             "skinDirName": ""
     *         }, ....]",
     *         "noticeBoard": "",
     *         "footerContent": "",
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
     *         "allowRegister": boolean,
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
     * @param context the specified http request context
     */
    @RequestProcessing(value = PREFERENCE_URI_PREFIX, method = HttpMethod.GET)
    public void getPreference(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                renderer.setJSONObject(new JSONObject().put(Keys.STATUS_CODE, false));

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
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates the preference by the specified request.
     *
     * <p>
     * Request json:
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
     *         "allowRegister": boolean,
     *         "articleListStyle": "",
     *         "editorType": "",
     *         "commentable": boolean,
     *         "feedOutputMode: "",
     *         "feedOutputCnt": int
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     */
    @RequestProcessing(value = PREFERENCE_URI_PREFIX, method = HttpMethod.PUT)
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

            final HttpServletResponse response = context.getResponse();
            final Cookie cookie = new Cookie(Skin.SKIN, preference.getString(Skin.SKIN_DIR_NAME));
            cookie.setMaxAge(60 * 60); // 1 hour
            cookie.setPath("/");
            response.addCookie(cookie);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Gets Qiniu preference.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "qiniuAccessKey": "",
     *     "qiniuSecretKey": "",
     *     "qiniuDomain": "",
     *     "qiniuBucket": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     */
    @RequestProcessing(value = PREFERENCE_URI_PREFIX + "oss", method = HttpMethod.GET)
    public void getOssPreference(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            //1.获取服务器厂商信息
            final JSONObject ossServer = optionQueryService.getOptions(CATEGORY_C_CLOU_STORGE);
            if (ossServer == null) {
                renderer.setJSONObject(new JSONObject().put(Keys.STATUS_CODE, false));
                return;
            }
            String ossServerVal = ossServer.getString(ID_C_CLOUD_STORGE_KEY);
            final JSONObject oss = optionQueryService.getOptions(ossServerVal);
            if (null == oss) {
                renderer.setJSONObject(new JSONObject().put(Keys.STATUS_CODE, false));
                return;
            }

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            ret.put("oss", convertOssOpts(ossServerVal, oss));
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates the Qiniu preference by the specified request.
     *
     * <p>
     * Request json:
     * <pre>
     * {
     *     "qiniuAccessKey": "",
     *     "qiniuSecretKey": "",
     *     "qiniuDomain": "",
     *     "qiniuBucket": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     */
    @RequestProcessing(value = PREFERENCE_URI_PREFIX + "oss", method = HttpMethod.PUT)
    public void updateOss(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);
        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final String ossServer = requestJSONObject.optString(ID_C_CLOUD_STORGE_KEY).trim();
            final String accessKey = requestJSONObject.optString("ossAccessKey").trim();
            final String secretKey = requestJSONObject.optString("ossSecretKey").trim();
            String domain = requestJSONObject.optString("ossDomain").trim();
            domain = StringUtils.lowerCase(domain);
            final String bucket = requestJSONObject.optString("ossBucket").trim();
            if (StringUtils.isNotBlank(domain) && !StringUtils.endsWith(domain, "/")) {
                domain += "/";
            }
            if (StringUtils.isNotBlank(domain) && !StringUtils.startsWithAny(domain, new String[] {"http", "https"})) {
                domain = "http://" + domain;
            }
            //让七牛占点便宜，主要是为了之前用户兼容
            boolean isAliyunOp = StringUtils.endsWithIgnoreCase(ossServer, CATEGORY_C_ALIYUN);

            //存放使用的云服务厂商信息
            final JSONObject ossServerKeyOpt = new JSONObject();
            ossServerKeyOpt.put(Keys.OBJECT_ID, ID_C_CLOUD_STORGE_KEY);
            ossServerKeyOpt.put(Option.OPTION_CATEGORY, CATEGORY_C_CLOU_STORGE);
            ossServerKeyOpt.put(Option.OPTION_VALUE, ossServer);

            final JSONObject accessKeyOpt = new JSONObject();
            accessKeyOpt.put(Keys.OBJECT_ID,
                    expressionSelectOptions(isAliyunOp, ID_C_ALIYUN_ACCESS_KEY, ID_C_QINIU_ACCESS_KEY));
            accessKeyOpt.put(Option.OPTION_CATEGORY, ossServer);
            accessKeyOpt.put(Option.OPTION_VALUE, accessKey);
            final JSONObject secretKeyOpt = new JSONObject();
            secretKeyOpt.put(Keys.OBJECT_ID,
                    expressionSelectOptions(isAliyunOp, ID_C_ALIYUN_SECRET_KEY, ID_C_QINIU_SECRET_KEY));
            secretKeyOpt.put(Option.OPTION_CATEGORY, ossServer);
            secretKeyOpt.put(Option.OPTION_VALUE, secretKey);
            final JSONObject domainOpt = new JSONObject();
            domainOpt.put(Keys.OBJECT_ID, expressionSelectOptions(isAliyunOp, ID_C_ALIYUN_DOMAIN, ID_C_QINIU_DOMAIN));
            domainOpt.put(Option.OPTION_CATEGORY, ossServer);
            domainOpt.put(Option.OPTION_VALUE, domain);
            final JSONObject bucketOpt = new JSONObject();
            bucketOpt.put(Keys.OBJECT_ID, expressionSelectOptions(isAliyunOp, ID_C_ALIYUN_BUCKET, ID_C_QINIU_BUCKET));
            bucketOpt.put(Option.OPTION_CATEGORY, ossServer);
            bucketOpt.put(Option.OPTION_VALUE, bucket);

            optionMgmtService.addOrUpdateOption(ossServerKeyOpt);
            optionMgmtService.addOrUpdateOption(accessKeyOpt);
            optionMgmtService.addOrUpdateOption(secretKeyOpt);
            optionMgmtService.addOrUpdateOption(domainOpt);
            optionMgmtService.addOrUpdateOption(bucketOpt);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            if (isQiniuTestDomain(domain)) {
                ret.put(Keys.MSG, langPropsService.get("donotUseQiniuTestDoaminLabel"));
            }
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
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
        responseObject.put(Keys.STATUS_CODE, false);

        final StringBuilder errMsgBuilder = new StringBuilder('[' + langPropsService.get("paramSettingsLabel"));
        errMsgBuilder.append(" - ");

        String input = preference.optString(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("externalRelevantArticlesDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("relevantArticlesDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("randomArticlesDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_MOST_COMMENT_ARTICLE_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexMostCommentArticleDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_MOST_VIEW_ARTICLE_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexMostViewArticleDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_RECENT_COMMENT_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexRecentCommentDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexTagDisplayCntLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("pageSizeLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("windowSizeLabel")).append("]  ").append(
                    langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_FEED_OUTPUT_CNT);
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

    /**
     * Checks whether the specified domain is a qiniu test domain.
     *
     * @param domain the specified domain
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    private boolean isQiniuTestDomain(final String domain) {
        return Arrays.asList("clouddn.com", "qiniucdn.com", "qiniudn.com", "qnssl.com", "qbox.me").stream().
                anyMatch(testDomain -> StringUtils.containsIgnoreCase(domain, testDomain));
    }

    /**
     * 转换成通用OSS格式
     *
     * @param ossServer
     * @param oss
     * @return
     */
    private JSONObject convertOssOpts(String ossServer, JSONObject oss) {
        JSONObject ossJson = new JSONObject();
        ossJson.put("ossServer", ossServer);
        boolean isAliyunOp = StringUtils.endsWithIgnoreCase(ossServer, CATEGORY_C_ALIYUN);
        boolean isQiniuOp = StringUtils.endsWithIgnoreCase(ossServer, CATEGORY_C_QINIU);
        if (isAliyunOp) {
            ossJson.put("ossAccessKey", oss.getString(ID_C_ALIYUN_ACCESS_KEY));
            ossJson.put("ossSecretKey", oss.getString(ID_C_ALIYUN_SECRET_KEY));
            ossJson.put("ossDomain", oss.getString(ID_C_ALIYUN_DOMAIN));
            ossJson.put("ossBucket", oss.getString(ID_C_ALIYUN_BUCKET));
        } else if (isQiniuOp) {
            ossJson.put("ossAccessKey", oss.getString(ID_C_QINIU_ACCESS_KEY));
            ossJson.put("ossSecretKey", oss.getString(ID_C_QINIU_SECRET_KEY));
            ossJson.put("ossDomain", oss.getString(ID_C_QINIU_DOMAIN));
            ossJson.put("ossBucket", oss.getString(ID_C_QINIU_BUCKET));
        }
        return ossJson;
    }
}
