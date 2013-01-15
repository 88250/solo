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
package org.b3log.solo.processor;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.util.Users;
import org.json.JSONObject;


/**
 * Cache processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.2, Aug 9, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class CacheProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CacheProcessor.class.getName());

    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Clears cache with the specified context.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws IOException io exception 
     */
    @RequestProcessing(value = "/clear-cache.do", method = HTTPRequestMethod.POST)
    public void clearCache(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        LoginProcessor.tryLogInWithCookie(request, response);

        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);
            final String all = requestJSONObject.optString("all");

            if (Strings.isEmptyOrNull(all)) { // Just clears single page cache
                final String uri = requestJSONObject.optString(Common.URI);

                clearPageCache(uri);
            } else { // Clears all page caches
                clearAllPageCache();
            }

            context.setRenderer(new DoNothingRenderer());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Clears a page cache specified by the given URI.
     *
     * @param uri the specified URI
     */
    private void clearPageCache(final String uri) {
        final String pageCacheKey = PageCaches.getPageCacheKey(uri, null);

        LOGGER.log(Level.INFO, "Clears page cache[pageCacheKey={0}]", pageCacheKey);

        PageCaches.remove(pageCacheKey);
    }

    /**
     * Clears all page cache.
     */
    private void clearAllPageCache() {
        PageCaches.removeAll();
    }
}
