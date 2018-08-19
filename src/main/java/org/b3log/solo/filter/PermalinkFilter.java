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
package org.b3log.solo.filter;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.servlet.DispatcherServlet;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.renderer.HTTP500Renderer;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PermalinkQueryService;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Article/Page permalink filter.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.7, Jan 8, 2013
 * @see org.b3log.solo.processor.ArticleProcessor#showArticle(org.b3log.latke.servlet.HTTPRequestContext,
 * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 * @see org.b3log.solo.processor.PageProcessor#showPage(org.b3log.latke.servlet.HTTPRequestContext)
 * @since 0.3.1
 */
public final class PermalinkFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PermalinkFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Tries to dispatch request to article processor.
     *
     * @param request  the specified request
     * @param response the specified response
     * @param chain    filter chain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        final String requestURI = httpServletRequest.getRequestURI();

        LOGGER.log(Level.DEBUG, "Request URI[{0}]", requestURI);

        final String contextPath = Latkes.getContextPath();
        final String permalink = StringUtils.substringAfter(requestURI, contextPath);

        if (PermalinkQueryService.invalidPermalinkFormat(permalink)) {
            LOGGER.log(Level.DEBUG, "Skip filter request[URI={0}]", permalink);
            chain.doFilter(request, response);

            return;
        }

        JSONObject article;
        JSONObject page = null;

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();

        try {
            final ArticleRepository articleRepository = beanManager.getReference(ArticleRepositoryImpl.class);

            article = articleRepository.getByPermalink(permalink);
            if (null == article) {
                final PageRepository pageRepository = beanManager.getReference(PageRepositoryImpl.class);

                page = pageRepository.getByPermalink(permalink);
            }

            if (null == page && null == article) {
                LOGGER.log(Level.DEBUG, "Not found article/page with permalink[{0}]", permalink);
                chain.doFilter(request, response);

                return;
            }
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Processes article permalink filter failed", e);
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        // If requests an article and the article need view passowrd, sends redirect to the password form
        final ArticleQueryService articleQueryService = beanManager.getReference(ArticleQueryService.class);

        if (null != article && articleQueryService.needViewPwd(httpServletRequest, article)) {
            try {
                httpServletResponse.sendRedirect(
                        Latkes.getServePath() + "/console/article-pwd?articleId=" + article.optString(Keys.OBJECT_ID));
                return;
            } catch (final Exception e) {
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        dispatchToArticleOrPageProcessor(request, response, article, page);
    }

    /**
     * Dispatches the specified request to the specified article or page
     * processor with the specified response.
     *
     * @param request  the specified request
     * @param response the specified response
     * @param article  the specified article
     * @param page     the specified page
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     * @see DispatcherServlet#result(HTTPRequestContext)
     */
    private void dispatchToArticleOrPageProcessor(final ServletRequest request, final ServletResponse response,
                                                  final JSONObject article, final JSONObject page)
            throws ServletException, IOException {
        final HTTPRequestContext context = new HTTPRequestContext();

        context.setRequest((HttpServletRequest) request);
        context.setResponse((HttpServletResponse) response);

        if (null != article) {
            request.setAttribute(Article.ARTICLE, article);
            request.setAttribute(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/article");
        } else {
            request.setAttribute(Page.PAGE, page);
            request.setAttribute(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/page");
        }

        request.setAttribute(Keys.HttpRequest.REQUEST_METHOD, HTTPRequestMethod.GET.name());

        final HttpControl httpControl = new HttpControl(DispatcherServlet.SYS_HANDLER.iterator(), context);
        try {
            httpControl.nextHandler();
        } catch (final Exception e) {
            context.setRenderer(new HTTP500Renderer(e));
        }

        DispatcherServlet.result(context);
    }

    @Override
    public void destroy() {
    }
}
