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
package org.b3log.solo.repository;

import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.CommentRepository;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * {@link ArticleRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 16, 2018
 */
@Test(suiteName = "repository")
public class CommentRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Adds successfully.
     *
     * @throws Exception exception
     */
    @Test
    public void add() throws Exception {
        final CommentRepository commentRepository = getCommentRepository();

        final JSONObject comment = new JSONObject();

        comment.put(Comment.COMMENT_CONTENT, "comment1 content");
        comment.put(Comment.COMMENT_CREATED, new Date().getTime());
        comment.put(Comment.COMMENT_EMAIL, "test@gmail.com");
        comment.put(Comment.COMMENT_NAME, "comment1 name");
        comment.put(Comment.COMMENT_ON_ID, "comment1 on id");
        comment.put(Comment.COMMENT_ON_TYPE, "comment1 on type");
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
        comment.put(Comment.COMMENT_SHARP_URL, "comment1 sharp url");
        comment.put(Comment.COMMENT_URL, "comment1 url");
        comment.put(Comment.COMMENT_THUMBNAIL_URL, "comment1 thumbnail url");

        final Transaction transaction = commentRepository.beginTransaction();
        commentRepository.add(comment);
        transaction.commit();

        final List<JSONObject> comments = commentRepository.getComments("comment1 on id", 1, Integer.MAX_VALUE);
        Assert.assertNotNull(comments);
        Assert.assertEquals(comments.size(), 1);

        Assert.assertEquals(commentRepository.getComments("not found", 1, Integer.MAX_VALUE).size(), 0);

        Assert.assertEquals(commentRepository.getRecentComments(3).size(), 1);
    }
}
