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

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.cache.CommentCache;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Comment repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Sep 30, 2018
 * @since 0.3.1
 */
@Repository
public class CommentRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentRepository.class);

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Comment cache.
     */
    @Inject
    private CommentCache commentCache;

    /**
     * Public constructor.
     */
    public CommentRepository() {
        super(Comment.COMMENT);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        super.remove(id);

        commentCache.removeComment(id);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = commentCache.getComment(id);
        if (null != ret) {
            return ret;
        }

        ret = super.get(id);
        if (null == ret) {
            return null;
        }

        commentCache.putComment(ret);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject comment) throws RepositoryException {
        super.update(id, comment);

        comment.put(Keys.OBJECT_ID, id);
        commentCache.putComment(comment);
    }

    /**
     * Gets post comments recently with the specified fetch.
     *
     * @param fetchSize the specified fetch size
     * @return a list of comments recently, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getRecentComments(final int fetchSize) throws RepositoryException {
        final Query query = new Query().
                addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).
                setCurrentPageNum(1).setPageSize(fetchSize).setPageCount(1);
        final List<JSONObject> ret = getList(query);
        // Removes unpublished article related comments
        removeForUnpublishedArticles(ret);

        return ret;
    }

    /**
     * Gets comments with the specified on id, current page number and
     * page size.
     *
     * @param onId           the specified on id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return a list of comments, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getComments(final String onId, final int currentPageNum, final int pageSize) throws RepositoryException {
        final Query query = new Query().
                addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).
                setFilter(new PropertyFilter(Comment.COMMENT_ON_ID, FilterOperator.EQUAL, onId)).
                setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(1);

        return getList(query);
    }

    /**
     * Removes comments with the specified on id.
     *
     * @param onId the specified on id
     * @return removed count
     * @throws RepositoryException repository exception
     */
    public int removeComments(final String onId) throws RepositoryException {
        final List<JSONObject> comments = getComments(onId, 1, Integer.MAX_VALUE);
        for (final JSONObject comment : comments) {
            final String commentId = comment.optString(Keys.OBJECT_ID);
            remove(commentId);
        }

        LOGGER.log(Level.DEBUG, "Removed comments[onId={0}, removedCnt={1}]", onId, comments.size());

        return comments.size();
    }

    /**
     * Removes comments of unpublished articles for the specified comments.
     *
     * @param comments the specified comments
     * @throws RepositoryException repository exception
     */
    private void removeForUnpublishedArticles(final List<JSONObject> comments) throws RepositoryException {
        LOGGER.debug("Removing unpublished articles' comments....");
        final Iterator<JSONObject> iterator = comments.iterator();

        while (iterator.hasNext()) {
            final JSONObject comment = iterator.next();
            final String commentOnType = comment.optString(Comment.COMMENT_ON_TYPE);

            if (Article.ARTICLE.equals(commentOnType)) {
                final String articleId = comment.optString(Comment.COMMENT_ON_ID);

                if (!articleRepository.isPublished(articleId)) {
                    iterator.remove();
                }
            }
        }

        LOGGER.debug("Removed unpublished articles' comments....");
    }
}
