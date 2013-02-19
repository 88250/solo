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
package org.b3log.solo.processor.console.common;


import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;
import org.b3log.latke.servlet.advice.RequestReturnAdviceException;
import org.b3log.solo.util.Users;


/**
 *  the common auth check before advice for admin console.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Jan 17, 2013
 */
public class ProcessAuthAdvice extends BeforeRequestProcessAdvice {

    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    @Override
    public void doAdvice(final HTTPRequestContext context, final Map<String, Object> args) throws RequestProcessAdviceException {

        if (!userUtils.isLoggedIn(context.getRequest(), context.getResponse())) {
            try {
                context.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            throw new RequestReturnAdviceException(null);
        }

    }

}
