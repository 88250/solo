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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.b3log.solo.service;

import java.util.List;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Requests;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link CommentQueryService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 11, 2012
 */
@Test(suiteName = "service")
public class CommentQueryServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     * 
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = getUserQueryService();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Get Comments.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getComments() throws Exception {
        final CommentQueryService commentQueryService = getCommentQueryService();

        final JSONObject paginationRequest = Requests.buildPaginationRequest("1/10/20");
        final JSONObject result = commentQueryService.getComments(paginationRequest);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Comment.COMMENTS).length(), 1);
    }

    /**
     * Get Comment on id.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getCommentsOnId() throws Exception {
        final ArticleQueryService articleQueryService = getArticleQueryService();
        final JSONObject result = articleQueryService.getArticles(Requests.buildPaginationRequest("1/10/20"));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getJSONArray(Article.ARTICLES).length(), 1);

        final JSONObject article =
                result.getJSONArray(Article.ARTICLES).getJSONObject(0);
        final String articleId = article.getString(Keys.OBJECT_ID);

        final CommentQueryService commentQueryService = getCommentQueryService();
        final List<JSONObject> comments =
                commentQueryService.getComments(articleId);
        Assert.assertNotNull(comments);
        Assert.assertEquals(comments.size(), 1);
    }
}
