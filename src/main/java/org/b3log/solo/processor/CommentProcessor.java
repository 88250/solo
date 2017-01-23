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
package org.b3log.solo.processor;


import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Requests;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Page;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;


/**
 * Comment processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author ArmstrongCN
 * @version 1.2.2.12, Dec 29, 2015
 * @since 0.3.1
 */
@RequestProcessor
public class CommentProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentProcessor.class.getName());

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
     * Adds a comment to a page.
     *
     * <p> Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": "COMMENT_PAGE_SUCC"
     *     "commentDate": "", // yyyy/MM/dd HH:mm:ss
     *     "commentSharpURL": "",
     *     "commentThumbnailURL": "",
     *     "commentOriginalCommentName": "" // if exists this key, the comment is an reply
     * }
     * </pre> </p>
     *
     * @param context the specified context, including a request json object, for example,      
     * <pre>
     * {
     *     "captcha": "",
     *     "oId": pageId,
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "", // HTML
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment is an reply
     * }
     * </pre>
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @RequestProcessing(value = "/add-page-comment.do", method = HTTPRequestMethod.POST)
    public void addPageComment(final HTTPRequestContext context) throws ServletException, IOException {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        final JSONObject requestJSONObject = Requests.parseRequestJSONObject(httpServletRequest, httpServletResponse);

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

        final HttpSession session = httpServletRequest.getSession(false);

        if (null == session) {
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("captchaErrorLabel"));

            return;
        }

        final String storedCaptcha = (String) session.getAttribute(CaptchaProcessor.CAPTCHA);

        session.removeAttribute(CaptchaProcessor.CAPTCHA);

        if (!userQueryService.isLoggedIn(httpServletRequest, httpServletResponse)) {

            final String captcha = requestJSONObject.optString(CaptchaProcessor.CAPTCHA);

            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
                jsonObject.put(Keys.STATUS_CODE, false);
                jsonObject.put(Keys.MSG, langPropsService.get("captchaErrorLabel"));

                return;
            }

        }

        try {
            final JSONObject addResult = commentMgmtService.addPageComment(requestJSONObject);

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
     *
     *
     * <p> Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": "COMMENT_ARTICLE_SUCC",
     *     "commentDate": "", // yyyy/MM/dd HH:mm:ss
     *     "commentSharpURL": "",
     *     "commentThumbnailURL": "",
     *     "commentOriginalCommentName": "", // if exists this key, the comment is an reply
     *     "commentContent": "" // HTML
     * }
     * </pre>
     *
     * @param context the specified context, including a request json object, for example,      
     * <pre>
     * {
     *     "captcha": "",
     *     "oId": articleId,
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "",
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment is an reply
     * }
     * </pre>
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @RequestProcessing(value = "/add-article-comment.do", method = HTTPRequestMethod.POST)
    public void addArticleComment(final HTTPRequestContext context) throws ServletException, IOException {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        final JSONObject requestJSONObject = Requests.parseRequestJSONObject(httpServletRequest, httpServletResponse);

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

        final HttpSession session = httpServletRequest.getSession(false);

        if (null == session) {
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("captchaErrorLabel"));

            return;
        }

        final String storedCaptcha = (String) session.getAttribute(CaptchaProcessor.CAPTCHA);

        session.removeAttribute(CaptchaProcessor.CAPTCHA);

        if (!userQueryService.isLoggedIn(httpServletRequest, httpServletResponse)) {

            final String captcha = requestJSONObject.optString(CaptchaProcessor.CAPTCHA);

            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
                jsonObject.put(Keys.STATUS_CODE, false);
                jsonObject.put(Keys.MSG, langPropsService.get("captchaErrorLabel"));

                return;
            }

        }

        try {
            final JSONObject addResult = commentMgmtService.addArticleComment(requestJSONObject);

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
     * @param httpServletRequest the specified HTTP servlet request
     * @param httpServletResponse the specified HTTP servlet response
     */
    private void fillCommenter(final JSONObject requestJSONObject, 
        final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        userMgmtService.tryLogInWithCookie(httpServletRequest, httpServletResponse);
        
        final JSONObject currentUser = userQueryService.getCurrentUser(httpServletRequest);

        if (null == currentUser) {
            return;
        }

        requestJSONObject.put(Comment.COMMENT_NAME, currentUser.optString(User.USER_NAME));
        requestJSONObject.put(Comment.COMMENT_EMAIL, currentUser.optString(User.USER_EMAIL));
        requestJSONObject.put(Comment.COMMENT_URL, currentUser.optString(User.USER_URL));
    }
}
