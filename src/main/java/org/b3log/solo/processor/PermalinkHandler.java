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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.servlet.DispatcherServlet;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.handler.Handler;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.service.InitService;
import org.b3log.solo.service.PermalinkQueryService;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * Article/Page permalink  handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 1, 2019
 * @since 3.2.0
 */
public class PermalinkHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PermalinkHandler.class);

    /**
     * Whether initialization info reported.
     */
    private static boolean initReported;

    @Override
    public void handle(final RequestContext context) {
        JSONObject article;
        JSONObject page = null;
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final InitService initService = beanManager.getReference(InitService.class);
            if (!initService.isInited()) {
                context.handle();

                return;
            }

            final String requestURI = context.requestURI();
            final String contextPath = Latkes.getContextPath();
            final String permalink = StringUtils.substringAfter(requestURI, contextPath);
            if (PermalinkQueryService.invalidPermalinkFormat(permalink)) {
                LOGGER.log(Level.DEBUG, "Skip permalink handling request [URI={0}]", permalink);
                context.handle();

                return;
            }

            final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);
            article = articleRepository.getByPermalink(permalink);
            if (null == article) {
                final PageRepository pageRepository = beanManager.getReference(PageRepository.class);
                page = pageRepository.getByPermalink(permalink);
            }

            if (null == page && null == article) {
                LOGGER.log(Level.DEBUG, "Not found article/page with permalink [{0}]", permalink);
                context.handle();

                return;
            }
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Processes article permalink handler failed", e);
            context.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        // If requests an article and the article need view password, sends redirect to the password form
        if (null != article && Solos.needViewPwd(context, article)) {
            try {
                context.sendRedirect(Latkes.getServePath() + "/console/article-pwd?articleId=" + article.optString(Keys.OBJECT_ID));

                return;
            } catch (final Exception e) {
                context.sendError(HttpServletResponse.SC_NOT_FOUND);

                return;
            }
        }

        dispatchToArticleOrPageProcessor(context, article, page);
        context.handle();
    }

    /**
     * Dispatches the specified request to the specified article or page processor with the specified response.
     *
     * @param context the specified request context
     * @param article the specified article
     * @param page    the specified page
     * @see DispatcherServlet#result(RequestContext)
     */
    private void dispatchToArticleOrPageProcessor(final RequestContext context, final JSONObject article, final JSONObject page) {
        if (null != article) {
            context.attr(Article.ARTICLE, article);
            context.attr(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/article");
        } else {
            context.attr(Page.PAGE, page);
            context.attr(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/page");
        }
        context.attr(Keys.HttpRequest.REQUEST_METHOD, HttpMethod.GET.name());
    }
}
