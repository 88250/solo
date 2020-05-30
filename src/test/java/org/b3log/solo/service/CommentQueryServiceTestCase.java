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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.b3log.solo.service;

import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link CommentQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 11, 2012
 */
@Test(suiteName = "service")
public class CommentQueryServiceTestCase extends AbstractTestCase {

    /**
     * Get Comments.
     *
     * @throws Exception exception
     */
    public void getComments() throws Exception {
        final CommentQueryService commentQueryService = getCommentQueryService();

        final JSONObject paginationRequest = Solos.buildPaginationRequest("1/10/20");
        final JSONObject result = commentQueryService.getComments(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Comment.COMMENTS).length(), 1);
    }

    /**
     * Get Comment on id.
     *
     * @throws Exception exception
     */
    public void getCommentsOnId() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject result = articleQueryService.getArticles(Solos.buildPaginationRequest("1/10/20"));
        Assert.assertNotNull(result);
        Assert.assertEquals(((List<JSONObject>) result.opt(Article.ARTICLES)).size(), 1);

        final JSONObject article = ((List<JSONObject>) result.opt(Article.ARTICLES)).get(0);
        final String articleId = article.getString(Keys.OBJECT_ID);

        final CommentQueryService commentQueryService = getCommentQueryService();
        final List<JSONObject> comments = commentQueryService.getComments(articleId);
        Assert.assertNotNull(comments);
        Assert.assertEquals(comments.size(), 1);
    }
}
