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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

import java.util.List;

/**
 * User console request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/DASHU">DASHU</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 0.4.0
 */
@Singleton
public class UserConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserConsole.class);

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
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Updates a user by the specified request.
     *
     * <p>
     * Request json:
     * <pre>
     * {
     *     "oId": "",
     *     "userName": "",
     *     "userRole": "",
     *     "userURL": "",
     *     "userAvatar": "",
     *     "userB3Key": ""
     * }
     * </pre>
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void updateUser(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject = context.requestJSON();
            userMgmtService.updateUser(requestJSONObject);

            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Removes a user by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void removeUser(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);
        try {
            final String userId = context.pathVar("id");
            userMgmtService.removeUser(userId);

            jsonObject.put(Keys.CODE, StatusCodes.SUCC);
            jsonObject.put(Keys.MSG, langPropsService.get("removeSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
        }
    }

    /**
     * Gets users by the specified request json object.
     * <p>
     * The request URI contains the pagination arguments. For example, the request URI is /console/users/1/10/20, means
     * the current page is 1, the page size is 10, and the window size is 20.
     * </p>
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "users": [{
     *         "oId": "",
     *         "userName": "",
     *         "roleName": "",
     *         ....
     *      }, ....]
     *     "code": 0
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getUsers(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = context.requestURI();
            final String path = requestURI.substring((Latkes.getContextPath() + "/console/users/").length());
            final JSONObject requestJSONObject = Solos.buildPaginationRequest(path);
            final JSONObject result = userQueryService.getUsers(requestJSONObject);
            result.put(Keys.CODE, StatusCodes.SUCC);
            renderer.setJSONObject(result);

            final List<JSONObject> users = (List<JSONObject>) result.opt(User.USERS);
            for (final JSONObject user : users) {
                String userName = user.optString(User.USER_NAME);
                userName = StringEscapeUtils.escapeXml(userName);
                user.put(User.USER_NAME, userName);
            }
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Gets a user by the specified request.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "user": {
     *         "oId": "",
     *         "userName": "",
     *         "userAvatar": ""
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void getUser(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final String userId = context.pathVar("id");

        final JSONObject result = userQueryService.getUser(userId);
        if (null == result) {
            final JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
            return;
        }

        renderer.setJSONObject(result);
        result.put(Keys.CODE, StatusCodes.SUCC);
    }

    /**
     * Change a user role.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "code": int,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void changeUserRole(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);
        try {
            final String userId = context.pathVar("id");
            userMgmtService.changeRole(userId);

            jsonObject.put(Keys.CODE, StatusCodes.SUCC);
            jsonObject.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            jsonObject.put(Keys.MSG, langPropsService.get("removeFailLabel"));
        }
    }
}
