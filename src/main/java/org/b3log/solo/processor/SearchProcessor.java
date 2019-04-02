/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.renderer.TextXmlRenderer;
import org.b3log.latke.util.Paginator;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.DataModelService;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.UserQueryService;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.encoder.Encode;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Search processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.1.1.3, Mar 19, 2019
 * @since 2.4.0
 */
@RequestProcessor
public class SearchProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SearchProcessor.class);

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Shows opensearch.xml.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/opensearch.xml", method = HttpMethod.GET)
    public void showOpensearchXML(final RequestContext context) {
        final TextXmlRenderer renderer = new TextXmlRenderer();
        context.setRenderer(renderer);

        try {
            final InputStream resourceAsStream = SearchProcessor.class.getResourceAsStream("/opensearch.xml");
            String content = IOUtils.toString(resourceAsStream, "UTF-8");
            final JSONObject preference = optionQueryService.getPreference();
            content = StringUtils.replace(content, "${blogTitle}", Jsoup.clean(preference.optString(Option.ID_C_BLOG_TITLE), Whitelist.none()));
            content = StringUtils.replace(content, "${blogSubtitle}", Jsoup.clean(preference.optString(Option.ID_C_BLOG_SUBTITLE), Whitelist.none()));
            content = StringUtils.replace(content, "${servePath}", Latkes.getServePath());

            renderer.setContent(content);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shows opensearch.xml failed", e);
        }
    }

    /**
     * Searches articles.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/search", method = HttpMethod.GET)
    public void search(final RequestContext context) {
        final HttpServletRequest request = context.getRequest();
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "common-template/search.ftl");
        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.putAll(langs);

        final int pageNum = Paginator.getPage(request);
        String keyword = context.param(Common.KEYWORD);
        if (StringUtils.isBlank(keyword)) {
            keyword = "";
        }
        keyword = Encode.forHtml(keyword);

        dataModel.put(Common.KEYWORD, keyword);
        final JSONObject result = articleQueryService.searchKeyword(keyword, pageNum, 15);
        final List<JSONObject> articles = (List<JSONObject>) result.opt(Article.ARTICLES);

        try {
            final JSONObject preference = optionQueryService.getPreference();

            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
            dataModelService.setArticlesExProperties(context, articles, preference);

            dataModel.put(Article.ARTICLES, articles);
            final JSONObject pagination = result.optJSONObject(Pagination.PAGINATION);
            pagination.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
            dataModel.put(Pagination.PAGINATION, pagination);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Search articles failed");

            dataModel.put(Article.ARTICLES, Collections.emptyList());
        }
    }
}
