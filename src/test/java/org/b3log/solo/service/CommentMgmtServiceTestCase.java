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
package org.b3log.solo.service;

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link CommentMgmtService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Apr 18, 2019
 */
@Test(suiteName = "service")
public class CommentMgmtServiceTestCase extends AbstractTestCase {

    /**
     * Add Article Comment.
     *
     * @throws Exception exception
     */
    public void addArticleComment() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();

        final List<JSONObject> articles = articleQueryService.getRecentArticles(10);

        Assert.assertEquals(articles.size(), 1);

        final CommentQueryService commentQueryService = getCommentQueryService();
        JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        JSONObject result = commentQueryService.getComments(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Comment.COMMENTS).length(), 1);

        final CommentMgmtService commentMgmtService = getCommentMgmtService();
        final JSONObject requestJSONObject = new JSONObject();

        final String articleId = articles.get(0).getString(Keys.OBJECT_ID);
        requestJSONObject.put(Keys.OBJECT_ID, articleId);
        requestJSONObject.put(Comment.COMMENT_NAME, "Solo");
        requestJSONObject.put(Comment.COMMENT_URL, "comment URL");
        requestJSONObject.put(Comment.COMMENT_CONTENT, "comment content");

        final JSONObject addResult = commentMgmtService.addArticleComment(requestJSONObject);
        Assert.assertNotNull(addResult);
        Assert.assertNotNull(addResult.getString(Keys.OBJECT_ID));
        Assert.assertNotNull(addResult.getString(Comment.COMMENT_T_DATE));
        Assert.assertNotNull(addResult.getString(Comment.COMMENT_THUMBNAIL_URL));
        Assert.assertNotNull(addResult.getString(Comment.COMMENT_SHARP_URL));

        result = commentQueryService.getComments(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Comment.COMMENTS).length(), 2);
    }

    /**
     * Adds a page.
     *
     * @throws Exception exception
     */
    private void addPage() throws Exception {
        final PageMgmtService pageMgmtService = getPageMgmtService();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject page = new JSONObject();
        requestJSONObject.put(Page.PAGE, page);

        page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + "/p1");
        page.put(Page.PAGE_TITLE, "page1 title");
        page.put(Page.PAGE_OPEN_TARGET, "_self");

        final String pageId = pageMgmtService.addPage(requestJSONObject);

        Assert.assertNotNull(pageId);
    }
}
