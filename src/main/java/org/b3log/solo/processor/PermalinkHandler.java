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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.HttpMethod;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.service.InitService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.PermalinkQueryService;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.Statics;
import org.json.JSONObject;

/**
 * Article permalink  handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, May 23, 2020
 * @since 3.2.0
 */
public class PermalinkHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PermalinkHandler.class);

    /**
     * Whether initialization info reported.
     */
    private static boolean initReported;

    @Override
    public void handle(final RequestContext context) {
        final BeanManager beanManager = BeanManager.getInstance();

        JSONObject article;
        try {
            final InitService initService = beanManager.getReference(InitService.class);
            if (!initService.isInited()) {
                context.handle();
                return;
            }

            final String requestURI = context.requestURI();
            final String contextPath = Latkes.getContextPath();
            final String permalink = StringUtils.substringAfter(requestURI, contextPath);
            if (PermalinkQueryService.invalidPermalinkFormat(permalink)) {
                LOGGER.log(Level.DEBUG, "Skip permalink handling request [URI={}]", permalink);
                context.handle();
                return;
            }

            // 尝试走静态化缓存
            final String html = Statics.get(context);
            if (StringUtils.isNotBlank(html)) {
                context.getResponse().setContentType("text/html; charset=utf-8");
                context.sendString(html);
                context.abort();
                return;
            }

            final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);
            article = articleRepository.getByPermalink(permalink);
            if (null == article) {
                LOGGER.log(Level.DEBUG, "Not found article with permalink [{}]", permalink);
                context.handle();
                return;
            }
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Processes article permalink handler failed", e);
            context.sendError(404);
            return;
        }

        // If requests an article and the article need view password, sends redirect to the password form
        if (Solos.needViewPwd(context, article)) {
            try {
                context.sendRedirect(Latkes.getServePath() + "/console/article-pwd?articleId=" + article.optString(Keys.OBJECT_ID));
                return;
            } catch (final Exception e) {
                context.sendError(404);
                return;
            }
        }

        final OptionQueryService optionQueryService = beanManager.getReference(OptionQueryService.class);
        final JSONObject preference = optionQueryService.getPreference();
        final boolean allowVisitDraftViaPermalink = preference.getBoolean(Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
        if (Article.ARTICLE_STATUS_C_PUBLISHED != article.optInt(Article.ARTICLE_STATUS) && !allowVisitDraftViaPermalink) {
            context.sendError(404);
            return;
        }

        dispatchToArticleProcessor(context, article);
        context.handle();
    }

    /**
     * Dispatches the specified request to the specified article processor with the specified response.
     *
     * @param context the specified request context
     * @param article the specified article
     */
    private void dispatchToArticleProcessor(final RequestContext context, final JSONObject article) {
        context.attr(Article.ARTICLE, article);
        context.attr(Keys.HttpRequest.REQUEST_URI, Latkes.getContextPath() + "/article");
        context.attr(Keys.HttpRequest.REQUEST_METHOD, HttpMethod.GET.name());
    }
}
