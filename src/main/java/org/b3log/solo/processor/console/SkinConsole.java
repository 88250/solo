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
package org.b3log.solo.processor.console;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.SkinMgmtService;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Skin console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 29, 2019
 * @since 3.5.0
 */
@RequestProcessor
@Before(ConsoleAdminAuthAdvice.class)
public class SkinConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SkinConsole.class);

    /**
     * Skin management service.
     */
    @Inject
    private SkinMgmtService skinMgmtService;

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
     * Gets skin.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "skin": {
     *         "skinDirName": "",
     *         "mobileSkinDirName": "",
     *         "skins": "[{
     *             "skinDirName": ""
     *         }, ....]"
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getSkin(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject skin = optionQueryService.getSkin();
            if (null == skin) {
                renderer.setJSONObject(new JSONObject().put(Keys.STATUS_CODE, false));

                return;
            }

            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            ret.put(Option.CATEGORY_C_SKIN, skin);
            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Updates the skin by the specified request.
     * <p>
     * Request json:
     * <pre>
     * {
     *     "skin": {
     *         "skinDirName": ""
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void updateSkin(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject requestJSONObject = context.requestJSON();
            final JSONObject skin = requestJSONObject.getJSONObject(Option.CATEGORY_C_SKIN);
            final JSONObject ret = new JSONObject();
            renderer.setJSONObject(ret);
            if (isInvalid(skin, ret)) {
                return;
            }

            skinMgmtService.updateSkin(skin);

            final HttpServletResponse response = context.getResponse();
            final Cookie cookie = new Cookie(Option.CATEGORY_C_SKIN, skin.getString(Option.ID_C_SKIN_DIR_NAME));
            cookie.setMaxAge(60 * 60); // 1 hour
            cookie.setPath("/");
            response.addCookie(cookie);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject().put(Keys.STATUS_CODE, false);
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
        responseObject.put(Keys.STATUS_CODE, false);

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

        input = preference.optString(Option.ID_C_MOST_COMMENT_ARTICLE_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexMostCommentArticleDisplayCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_MOST_VIEW_ARTICLE_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexMostViewArticleDisplayCntLabel")).append("]  ")
                    .append(langPropsService.get("nonNegativeIntegerOnlyLabel"));
            responseObject.put(Keys.MSG, errMsgBuilder.toString());
            return true;
        }

        input = preference.optString(Option.ID_C_RECENT_COMMENT_DISPLAY_CNT);
        if (!isNonNegativeInteger(input)) {
            errMsgBuilder.append(langPropsService.get("indexRecentCommentDisplayCntLabel")).append("]  ")
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
