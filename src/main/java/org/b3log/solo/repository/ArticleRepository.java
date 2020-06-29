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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.cache.ArticleCache;
import org.b3log.solo.model.Article;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.15, Jun 29, 2020
 * @since 0.3.1
 */
@Repository
public class ArticleRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArticleRepository.class);

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
    public void update(final String id, final JSONObject article, final String... propertyNames) throws RepositoryException {
        super.update(id, article, propertyNames);

        article.put(Keys.OBJECT_ID, id);
        articleCache.putArticle(article);
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<>();
        int loops = 0;
        do {
            loops++;
            double min = Math.random();
            double max = Math.random();
            if (min > max) {
                final double tmp = min;
                min = max;
                max = tmp;
            }
            final Query query = new Query().setFilter(
                    CompositeFilterOperator.and(
                            new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.GREATER_THAN_OR_EQUAL, min),
                            new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.LESS_THAN_OR_EQUAL, max),
                            new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED))).
                    setPage(1, (int) Math.ceil(fetchSize / 5)).setPageCount(1);
            if (0.05 <= min) {
                query.addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
            }

            final List<JSONObject> records = getList(query);
            for (final JSONObject record : records) {
                final String id = record.optString(Keys.OBJECT_ID);
                boolean contain = false;
                for (final JSONObject retRecord : ret) {
                    if (retRecord.optString(Keys.OBJECT_ID).equals(id)) {
                        contain = true;
                        break;
                    }
                }
                if (!contain) {
                    ret.add(record);
                }
            }
        } while (1 <= fetchSize - ret.size() && 10 >= loops);
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
                        new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED))).
                addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).addSort(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING).
                setPage(currentPageNum, pageSize);
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
        ret = getFirst(query);
        if (null == ret) {
            return null;
        }

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
                setFilter(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED)).
                addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).
                setPage(1, fetchSize).setPageCount(1);

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
        if (null == currentArticle) {
            return null;
        }

        final long currentArticleCreated = currentArticle.optLong(Article.ARTICLE_CREATED);

        final Query query = new Query().
                setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(Article.ARTICLE_CREATED, FilterOperator.LESS_THAN, currentArticleCreated),
                        new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED))).
                addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING).
                setPage(1, 1).setPageCount(1).
                select(Article.ARTICLE_TITLE, Article.ARTICLE_PERMALINK, Article.ARTICLE_ABSTRACT);
        final JSONObject article = getFirst(query);
        if (null == article) {
            return null;
        }

        final JSONObject ret = new JSONObject();
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
        if (null == currentArticle) {
            return null;
        }

        final long currentArticleCreated = currentArticle.optLong(Article.ARTICLE_CREATED);

        final Query query = new Query().
                setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(Article.ARTICLE_CREATED, FilterOperator.GREATER_THAN, currentArticleCreated),
                        new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED))).
                addSort(Article.ARTICLE_CREATED, SortDirection.ASCENDING).
                setPage(1, 1).setPageCount(1).
                select(Article.ARTICLE_TITLE, Article.ARTICLE_PERMALINK, Article.ARTICLE_ABSTRACT);
        final JSONObject article = getFirst(query);
        if (null == article) {
            return null;
        }

        final JSONObject ret = new JSONObject();
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

        return Article.ARTICLE_STATUS_C_PUBLISHED == article.optInt(Article.ARTICLE_STATUS);
    }
}
