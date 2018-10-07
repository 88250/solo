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

import freemarker.template.Template;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.model.*;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Emotions;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Comment processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author ArmstrongCN
 * @version 1.3.3.3, Oct 7, 2018
 * @since 0.3.1
 */
@RequestProcessor
public class CommentProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentProcessor.class);

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Comment management service.
     */
    @Inject
    private CommentMgmtService commentMgmtService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * Adds a comment to a page.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": "COMMENT_PAGE_SUCC"
     *     "commentDate": "", // yyyy/MM/dd HH:mm:ss
     *     "commentSharpURL": "",
     *     "commentThumbnailURL": "",
     *     "commentOriginalCommentName": "" // if exists this key, the comment is an reply
     * }
     * </pre>
     * </p>
     *
     * @param context           the specified context
     * @param requestJSONObject the specified request json object, for example,
     *                          "captcha": "",
     *                          "oId": pageId,
     *                          "commentName": "",
     *                          "commentEmail": "",
     *                          "commentURL": "",
     *                          "commentContent": "",
     *                          "commentOriginalCommentId": "" // optional, if exists this key, the comment is an reply
     */
    @RequestProcessing(value = "/add-page-comment.do", method = HTTPRequestMethod.POST)
    public void addPageComment(final HTTPRequestContext context, final JSONObject requestJSONObject) {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        requestJSONObject.put(Common.TYPE, Page.PAGE);

        fillCommenter(requestJSONObject, httpServletRequest, httpServletResponse);

        final JSONObject jsonObject = commentMgmtService.checkAddCommentRequest(requestJSONObject);
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        if (!jsonObject.optBoolean(Keys.STATUS_CODE)) {
            LOGGER.log(Level.WARN, "Can't add comment[msg={0}]", jsonObject.optString(Keys.MSG));
            return;
        }

        if (!Solos.isLoggedIn(httpServletRequest, httpServletResponse)) {
            final String captcha = requestJSONObject.optString(CaptchaProcessor.CAPTCHA);
            if (CaptchaProcessor.invalidCaptcha(captcha)) {
                jsonObject.put(Keys.STATUS_CODE, false);
                jsonObject.put(Keys.MSG, langPropsService.get("captchaErrorLabel"));

                return;
            }
        }

        try {
            final JSONObject addResult = commentMgmtService.addPageComment(requestJSONObject);

            final Map<String, Object> dataModel = new HashMap<>();
            dataModel.put(Comment.COMMENT, addResult);

            final JSONObject page = addResult.optJSONObject(Page.PAGE);
            page.put(Common.COMMENTABLE, addResult.opt(Common.COMMENTABLE));
            page.put(Common.PERMALINK, addResult.opt(Common.PERMALINK));
            dataModel.put(Article.ARTICLE, page);

            // https://github.com/b3log/solo/issues/12246
            try {
                final String skinDirName = (String) httpServletRequest.getAttribute(Keys.TEMAPLTE_DIR_NAME);
                final Template template = Skins.getSkinTemplate(httpServletRequest, "common-comment.ftl");
                final JSONObject preference = preferenceQueryService.getPreference();
                Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), skinDirName, dataModel);
                Keys.fillServer(dataModel);
                final StringWriter stringWriter = new StringWriter();
                template.process(dataModel, stringWriter);
                stringWriter.close();
                String cmtTpl = stringWriter.toString();
                cmtTpl = Emotions.convert(cmtTpl);

                addResult.put("cmtTpl", cmtTpl);
            } catch (final Exception e) {
                // 1.9.0 向后兼容
            }

            addResult.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(addResult);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Can not add comment on page", e);

            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
        }
    }

    /**
     * Adds a comment to an article.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": "COMMENT_ARTICLE_SUCC",
     *     "commentDate": "", // yyyy/MM/dd HH:mm:ss
     *     "commentSharpURL": "",
     *     "commentThumbnailURL": "",
     *     "commentOriginalCommentName": "", // if exists this key, the comment is an reply
     *     "commentContent": ""
     * }
     * </pre>
     * </p>
     *
     * @param context           the specified context, including a request json object
     * @param requestJSONObject the specified request json object, for example,
     *                          "captcha": "",
     *                          "oId": articleId,
     *                          "commentName": "",
     *                          "commentEmail": "",
     *                          "commentURL": "",
     *                          "commentContent": "",
     *                          "commentOriginalCommentId": "" // optional, if exists this key, the comment is an reply
     */
    @RequestProcessing(value = "/add-article-comment.do", method = HTTPRequestMethod.POST)
    public void addArticleComment(final HTTPRequestContext context, final JSONObject requestJSONObject) {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        requestJSONObject.put(Common.TYPE, Article.ARTICLE);

        fillCommenter(requestJSONObject, httpServletRequest, httpServletResponse);

        final JSONObject jsonObject = commentMgmtService.checkAddCommentRequest(requestJSONObject);
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        if (!jsonObject.optBoolean(Keys.STATUS_CODE)) {
            LOGGER.log(Level.WARN, "Can't add comment[msg={0}]", jsonObject.optString(Keys.MSG));
            return;
        }

        if (!Solos.isLoggedIn(httpServletRequest, httpServletResponse)) {
            final String captcha = requestJSONObject.optString(CaptchaProcessor.CAPTCHA);
            if (CaptchaProcessor.invalidCaptcha(captcha)) {
                jsonObject.put(Keys.STATUS_CODE, false);
                jsonObject.put(Keys.MSG, langPropsService.get("captchaErrorLabel"));

                return;
            }
        }

        try {
            final JSONObject addResult = commentMgmtService.addArticleComment(requestJSONObject);

            final Map<String, Object> dataModel = new HashMap<>();
            dataModel.put(Comment.COMMENT, addResult);
            final JSONObject article = addResult.optJSONObject(Article.ARTICLE);
            article.put(Common.COMMENTABLE, addResult.opt(Common.COMMENTABLE));
            article.put(Common.PERMALINK, addResult.opt(Common.PERMALINK));
            dataModel.put(Article.ARTICLE, article);

            // https://github.com/b3log/solo/issues/12246
            try {
                final String skinDirName = (String) httpServletRequest.getAttribute(Keys.TEMAPLTE_DIR_NAME);
                final Template template = Skins.getSkinTemplate(httpServletRequest, "common-comment.ftl");
                final JSONObject preference = preferenceQueryService.getPreference();
                Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), skinDirName, dataModel);
                Keys.fillServer(dataModel);
                final StringWriter stringWriter = new StringWriter();
                template.process(dataModel, stringWriter);
                stringWriter.close();
                String cmtTpl = stringWriter.toString();

                addResult.put("cmtTpl", cmtTpl);
            } catch (final Exception e) {
                // 1.9.0 向后兼容
            }

            addResult.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(addResult);
        } catch (final Exception e) {

            LOGGER.log(Level.ERROR, "Can not add comment on article", e);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
        }
    }

    /**
     * Fills commenter info if logged in.
     *
     * @param requestJSONObject the specified request json object
     * @param request           the specified HTTP servlet request
     * @param request           the specified HTTP servlet response
     */
    private void fillCommenter(final JSONObject requestJSONObject, final HttpServletRequest request, final HttpServletResponse response) {
        final JSONObject currentUser = Solos.getCurrentUser(request, response);
        if (null == currentUser) {
            return;
        }

        requestJSONObject.put(Comment.COMMENT_NAME, currentUser.optString(User.USER_NAME));
        requestJSONObject.put(Comment.COMMENT_EMAIL, currentUser.optString(User.USER_EMAIL));
        requestJSONObject.put(Comment.COMMENT_URL, currentUser.optString(User.USER_URL));
    }
}
