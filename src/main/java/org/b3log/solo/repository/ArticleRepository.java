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
import org.b3log.solo.cache.ArticleCache;
import org.b3log.solo.model.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.11, Sep 30, 2018
 * @since 0.3.1
 */
@Repository
public class ArticleRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleRepository.class);

    /**
     * Random range.
     */
    private static final double RANDOM_RANGE = 0.1D;

    /**
     * Article cache.
     */
    @Inject
    private ArticleCache articleCache;

    /**
     * Public constructor.
     */
    public ArticleRepository() {
        super(Article.ARTICLE);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        super.remove(id);

        articleCache.removeArticle(id);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = articleCache.getArticle(id);
        if (null != ret) {
            return ret;
        }

        ret = super.get(id);
        if (null == ret) {
            return null;
        }

        articleCache.putArticle(ret);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject article) throws RepositoryException {
        super.update(id, article);

        article.put(Keys.OBJECT_ID, id);
        articleCache.putArticle(article);
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList();

        if (0 == count()) {
            return ret;
        }

        final double mid = Math.random() + RANDOM_RANGE;

        LOGGER.log(Level.TRACE, "Random mid[{0}]", mid);

        Query query = new Query().setFilter(CompositeFilterOperator.and(
                new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.GREATER_THAN_OR_EQUAL, mid),
                new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.LESS_THAN_OR_EQUAL, mid),
                new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                setCurrentPageNum(1).setPageSize(fetchSize).setPageCount(1);

        final List<JSONObject> list1 = getList(query);
        ret.addAll(list1);

        final int reminingSize = fetchSize - list1.size();
        if (0 != reminingSize) { // Query for remains
            query = new Query();
            query.setFilter(
                    CompositeFilterOperator.and(
                            new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.GREATER_THAN_OR_EQUAL, 0D),
                            new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.LESS_THAN_OR_EQUAL, mid),
                            new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                    setCurrentPageNum(1).setPageSize(reminingSize).setPageCount(1);

            final List<JSONObject> list2 = getList(query);

            ret.addAll(list2);
        }

        return ret;
    }

    /**
     * Gets published articles by the specified author id, current page number and page size.
     *
     * @param authorId       the specified author id
     * @param currentPageNum the specified current page number, MUST greater then {@code 0}
     * @param pageSize       the specified page size(count of a page contains objects), MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         // article keys....
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    public JSONObject getByAuthorId(final String authorId, final int currentPageNum, final int pageSize) throws RepositoryException {
        final Query query = new Query().
                setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(Article.ARTICLE_AUTHOR_ID, FilterOperator.EQUAL, authorId),
                        new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).addSort(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING).
                setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(1);

        return get(query);
    }

    /**
     * Gets an article by the specified permalink.
     *
     * @param permalink the specified permalink
     * @return an article, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByPermalink(final String permalink) throws RepositoryException {
        JSONObject ret = articleCache.getArticleByPermalink(permalink);
        if (null != ret) {
            return ret;
        }

        final Query query = new Query().
                setFilter(new PropertyFilter(Article.ARTICLE_PERMALINK, FilterOperator.EQUAL, permalink)).
                setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (0 == array.length()) {
            return null;
        }

        ret = array.optJSONObject(0);
        articleCache.putArticle(ret);

        return ret;
    }

    /**
     * Gets post articles recently with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of articles recently, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getRecentArticles(final int fetchSize) throws RepositoryException {
        final Query query = new Query().
                setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true)).
                addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).
                setCurrentPageNum(1).setPageSize(fetchSize).setPageCount(1);

        return getList(query);
    }

    /**
     * Gets most commented and published articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most comment articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getMostCommentArticles(final int num) throws RepositoryException {
        final Query query = new Query().
                addSort(Article.ARTICLE_COMMENT_COUNT, SortDirection.DESCENDING).
                addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).
                setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true)).
                setCurrentPageNum(1).setPageSize(num).setPageCount(1);

        return getList(query);
    }

    /**
     * Gets most view count and published articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most view count articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getMostViewCountArticles(final int num) throws RepositoryException {
        final Query query = new Query().
                addSort(Article.ARTICLE_VIEW_COUNT, SortDirection.DESCENDING).
                addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).
                setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true)).
                setCurrentPageNum(1).setPageSize(num).setPageCount(1);

        return getList(query);
    }

    /**
     * Gets the previous article(by create date) by the specified article id.
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": "",
     *     "articleAbstract: ""
     * }
     * </pre>
     * returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getPreviousArticle(final String articleId) throws RepositoryException {
        final JSONObject currentArticle = get(articleId);
        final long currentArticleCreated = currentArticle.optLong(Article.ARTICLE_CREATED);

        final Query query = new Query().
                setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(Article.ARTICLE_CREATED, FilterOperator.LESS_THAN, currentArticleCreated),
                        new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING).
                setCurrentPageNum(1).setPageSize(1).setPageCount(1).
                addProjection(Article.ARTICLE_TITLE, String.class).
                addProjection(Article.ARTICLE_PERMALINK, String.class).
                addProjection(Article.ARTICLE_ABSTRACT, String.class);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (1 != array.length()) {
            return null;
        }

        final JSONObject ret = new JSONObject();
        final JSONObject article = array.optJSONObject(0);

        try {
            ret.put(Article.ARTICLE_TITLE, article.getString(Article.ARTICLE_TITLE));
            ret.put(Article.ARTICLE_PERMALINK, article.getString(Article.ARTICLE_PERMALINK));
            ret.put(Article.ARTICLE_ABSTRACT, article.getString((Article.ARTICLE_ABSTRACT)));
        } catch (final JSONException e) {
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * Gets the next article(by create date, oId) by the specified article id.
     *
     * @param articleId the specified article id
     * @return the next article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": "",
     *     "articleAbstract: ""
     * }
     * </pre>
     * returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getNextArticle(final String articleId) throws RepositoryException {
        final JSONObject currentArticle = get(articleId);
        final long currentArticleCreated = currentArticle.optLong(Article.ARTICLE_CREATED);

        final Query query = new Query().
                setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(Article.ARTICLE_CREATED, FilterOperator.GREATER_THAN, currentArticleCreated),
                        new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                addSort(Article.ARTICLE_CREATED, SortDirection.ASCENDING).
                setCurrentPageNum(1).setPageSize(1).setPageCount(1).
                addProjection(Article.ARTICLE_TITLE, String.class).
                addProjection(Article.ARTICLE_PERMALINK, String.class).
                addProjection(Article.ARTICLE_ABSTRACT, String.class);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (1 != array.length()) {
            return null;
        }

        final JSONObject ret = new JSONObject();
        final JSONObject article = array.optJSONObject(0);

        try {
            ret.put(Article.ARTICLE_TITLE, article.getString(Article.ARTICLE_TITLE));
            ret.put(Article.ARTICLE_PERMALINK, article.getString(Article.ARTICLE_PERMALINK));
            ret.put(Article.ARTICLE_ABSTRACT, article.getString((Article.ARTICLE_ABSTRACT)));
        } catch (final JSONException e) {
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * Determines an article specified by the given article id is published.
     *
     * @param articleId the given article id
     * @return {@code true} if it is published, {@code false} otherwise
     * @throws RepositoryException repository exception
     */
    public boolean isPublished(final String articleId) throws RepositoryException {
        final JSONObject article = get(articleId);
        if (null == article) {
            return false;
        }

        return article.optBoolean(Article.ARTICLE_IS_PUBLISHED);
    }
}
