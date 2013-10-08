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
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;


/**
 * Cache processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.2, Aug 9, 2012
 * @since 0.3.1
 */
@RequestProcessor
public class CacheProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CacheProcessor.class.getName());

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
     * Page caches.
     */
    @Inject
    private PageCaches pageCaches;

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
        userMgmtService.tryLogInWithCookie(request, response);

        if (!userQueryService.isAdminLoggedIn(request)) {
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
            LOGGER.log(Level.ERROR, e.getMessage(), e);
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
        pageCaches.removeAll();
    }
}
