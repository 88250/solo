/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.b3log.latke.Keys;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.CompositeFilterOperator;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.3.10, Jun 13, 2015
 * @since 0.3.1
 */
@Repository
public class ArticleRepositoryImpl extends AbstractRepository implements ArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleRepositoryImpl.class.getName());

    /**
     * Random range.
     */
    private static final double RANDOM_RANGE = 0.1D;

    /**
     * Public constructor.
     */
    public ArticleRepositoryImpl() {
        super(Article.ARTICLE);
    }

    @Override
    public JSONObject getByAuthorEmail(final String authorEmail, final int currentPageNum, final int pageSize)
            throws RepositoryException {
        final Query query = new Query().setFilter(CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_AUTHOR_EMAIL, FilterOperator.EQUAL, authorEmail), new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING).addSort(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING).setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(
                1);

        return get(query);
    }

    @Override
    public JSONObject getByPermalink(final String permalink) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Article.ARTICLE_PERMALINK, FilterOperator.EQUAL, permalink)).setPageCount(
                1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public List<JSONObject> getRecentArticles(final int fetchSize) throws RepositoryException {
        final Query query = new Query();

        query.setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true));
        query.addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING);
        query.setCurrentPageNum(1);
        query.setPageSize(fetchSize);
        query.setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(array);
    }

    @Override
    public List<JSONObject> getMostCommentArticles(final int num) throws RepositoryException {
        final Query query = new Query().addSort(Article.ARTICLE_COMMENT_COUNT, SortDirection.DESCENDING).addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING).setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true)).setCurrentPageNum(1).setPageSize(num).setPageCount(
                1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(array);
    }

    @Override
    public List<JSONObject> getMostViewCountArticles(final int num) throws RepositoryException {
        final Query query = new Query();

        query.addSort(Article.ARTICLE_VIEW_COUNT, SortDirection.DESCENDING).addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING);
        query.setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true));
        query.setCurrentPageNum(1);
        query.setPageSize(num);
        query.setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(array);
    }

    @Override
    public JSONObject getPreviousArticle(final String articleId) throws RepositoryException {
        final JSONObject currentArticle = get(articleId);
        final Date currentArticleCreateDate = (Date) currentArticle.opt(Article.ARTICLE_CREATE_DATE);

        final Query query = new Query().setFilter(CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_CREATE_DATE,
                FilterOperator.LESS_THAN, currentArticleCreateDate), new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                addSort(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING).setCurrentPageNum(1).setPageSize(1).setPageCount(1).
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

    @Override
    public JSONObject getNextArticle(final String articleId) throws RepositoryException {
        final JSONObject currentArticle = get(articleId);
        final Date currentArticleCreateDate = (Date) currentArticle.opt(Article.ARTICLE_CREATE_DATE);

        final Query query = new Query().setFilter(CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_CREATE_DATE,
                FilterOperator.GREATER_THAN, currentArticleCreateDate),
                new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true))).
                addSort(Article.ARTICLE_CREATE_DATE, SortDirection.ASCENDING).setCurrentPageNum(1).setPageSize(1).setPageCount(1).
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

    @Override
    public boolean isPublished(final String articleId) throws RepositoryException {
        final JSONObject article = get(articleId);

        if (null == article) {
            return false;
        }

        return article.optBoolean(Article.ARTICLE_IS_PUBLISHED);
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();

        if (0 == count()) {
            return ret;
        }

        final double mid = Math.random() + RANDOM_RANGE;

        LOGGER.log(Level.TRACE, "Random mid[{0}]", mid);

        Query query = new Query();

        query.setFilter(
                CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.GREATER_THAN_OR_EQUAL, mid),
                        new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.LESS_THAN_OR_EQUAL, mid),
                        new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true)));
        query.setCurrentPageNum(1);
        query.setPageSize(fetchSize);
        query.setPageCount(1);

        final JSONObject result1 = get(query);
        final JSONArray array1 = result1.optJSONArray(Keys.RESULTS);

        final List<JSONObject> list1 = CollectionUtils.<JSONObject>jsonArrayToList(array1);

        ret.addAll(list1);

        final int reminingSize = fetchSize - array1.length();

        if (0 != reminingSize) { // Query for remains
            query = new Query();
            query.setFilter(
                    CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.GREATER_THAN_OR_EQUAL, 0D),
                            new PropertyFilter(Article.ARTICLE_RANDOM_DOUBLE, FilterOperator.LESS_THAN_OR_EQUAL, mid),
                            new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true)));
            query.setCurrentPageNum(1);
            query.setPageSize(reminingSize);
            query.setPageCount(1);

            final JSONObject result2 = get(query);
            final JSONArray array2 = result2.optJSONArray(Keys.RESULTS);

            final List<JSONObject> list2 = CollectionUtils.<JSONObject>jsonArrayToList(array2);

            ret.addAll(list2);
        }

        return ret;
    }
}
