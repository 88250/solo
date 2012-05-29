/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.solo.dev;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeMode;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Article;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;

/**
 * Generates some dummy articles for development testing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, May 21, 2012
 * @since 0.4.0
 */
@RequestProcessor
public final class ArticleGenerator {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleGenerator.class.getName());

    /**
     * Generates some dummy articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response
     * @throws IOException io exception 
     */
    @RequestProcessing(value = "/dev/articles/gen/*", method = HTTPRequestMethod.GET)
    public void genArticles(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        if (RuntimeMode.DEVELOPMENT != Latkes.getRuntimeMode()) {
            LOGGER.log(Level.WARNING, "Article generation just for development mode, " + "current runtime mode is [{0}]",
                       Latkes.getRuntimeMode());
            response.sendRedirect("/");

            return;
        }

        Stopwatchs.start("Gen Articles");

        final String requestURI = request.getRequestURI();
        final int num = Integer.valueOf(requestURI.substring((Latkes.getContextPath() + "/dev/articles/gen/").length()));

        try {
            final ArticleMgmtService articleMgmtService = ArticleMgmtService.getInstance();
            final UserQueryService userQueryService = UserQueryService.getInstance();
            final JSONObject admin = userQueryService.getAdmin();
            final String authorEmail = admin.optString(User.USER_EMAIL);

            for (int i = 0; i < num; i++) {
                final JSONObject article = new JSONObject();

                // XXX: http://en.wikipedia.org/wiki/Markov_chain
                article.put(Article.ARTICLE_TITLE, "article title" + i);
                article.put(Article.ARTICLE_ABSTRACT, "article" + i + " abstract");
                article.put(Article.ARTICLE_TAGS_REF, "tag1,tag2");
                article.put(Article.ARTICLE_AUTHOR_EMAIL, authorEmail);
                article.put(Article.ARTICLE_COMMENT_COUNT, 0);
                article.put(Article.ARTICLE_VIEW_COUNT, 0);
                article.put(Article.ARTICLE_CONTENT, "article content");
                article.put(Article.ARTICLE_PERMALINK, "article" + i + " permalink");
                article.put(Article.ARTICLE_HAD_BEEN_PUBLISHED, true);
                article.put(Article.ARTICLE_IS_PUBLISHED, true);
                article.put(Article.ARTICLE_PUT_TOP, false);
                article.put(Article.ARTICLE_CREATE_DATE, new Date());
                article.put(Article.ARTICLE_UPDATE_DATE, new Date());
                article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());
                article.put(Article.ARTICLE_COMMENTABLE, true);
                article.put(Article.ARTICLE_VIEW_PWD, "");
                article.put(Article.ARTICLE_SIGN_ID, "1");

                articleMgmtService.addArticle(new JSONObject().put(Article.ARTICLE, article));
            }

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        Stopwatchs.end();

        response.sendRedirect("/");
    }
}
