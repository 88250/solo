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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Page;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.PageMgmtService;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CommentProcessorTestCase} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Apr 19, 2019
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class CommentProcessorTestCase extends AbstractTestCase {

    /**
     * addArticleComment.
     *
     * @throws Exception exception
     */
    public void addArticleComment() throws Exception {
        final MockRequest request = mockRequest();
        request.setRequestURI("/article/comments");
        request.setMethod("POST");
        request.setAttribute(Keys.TEMAPLTE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);

        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("oId", addArticle());
        requestJSON.put("commentName", "88250");
        requestJSON.put("commentEmail", "d@hacpai.com");
        requestJSON.put("commentURL", "https://hacpai.com");
        requestJSON.put("commentContent", "测试评论");
        request.setJSON(requestJSON);

        mockAdminLogin(request);

        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "\"sc\":true"));
    }

    private String addPage() throws ServiceException {
        final PageMgmtService pageMgmtService = getPageMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSONObject.put(Page.PAGE, page);

        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p1");
        page.put(Page.PAGE_TITLE, "page1 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");

        return pageMgmtService.addPage(requestJSONObject);
    }

    private String addArticle() throws Exception {
        final ArticleMgmtService articleMgmtService = getArticleMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        final JSONObject admin = getUserQueryService().getAdmin();
        final String userId = admin.optString(Keys.OBJECT_ID);

        article.put(Article.ARTICLE_AUTHOR_ID, userId);
        article.put(Article.ARTICLE_TITLE, "article1 title");
        article.put(Article.ARTICLE_ABSTRACT, "article1 abstract");
        article.put(Article.ARTICLE_CONTENT, "article1 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article1 permalink");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_COMMENTABLE, true);
        article.put(Article.ARTICLE_VIEW_PWD, "");

        return articleMgmtService.addArticle(requestJSONObject);
    }
}
