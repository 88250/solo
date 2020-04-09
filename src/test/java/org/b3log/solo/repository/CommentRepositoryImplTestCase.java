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
package org.b3log.solo.repository;

import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Comment;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * {@link ArticleRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Apr 19, 2019
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
        comment.put(Comment.COMMENT_NAME, "comment1 name");
        comment.put(Comment.COMMENT_ON_ID, "comment1 on id");
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
    }
}
