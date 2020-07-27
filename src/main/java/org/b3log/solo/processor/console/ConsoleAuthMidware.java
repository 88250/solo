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

import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

/**
 * The common auth check middleware for admin console.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 2.9.5
 */
@Singleton
public class ConsoleAuthMidware {

    public void handle(final RequestContext context) {
        final JSONObject currentUser = Solos.getCurrentUser(context);
        if (null == currentUser) {
            context.sendError(401);
            context.abort();
            return;
        }

        final String userRole = currentUser.optString(User.USER_ROLE);
        if (Role.VISITOR_ROLE.equals(userRole)) {
            context.sendError(403);
            context.abort();
        }

        context.handle();
    }
}
