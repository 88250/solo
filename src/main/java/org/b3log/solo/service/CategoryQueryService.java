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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Paginator;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Category query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/lzh984294471">lzh984294471</a>
 * @version 1.1.0.0, Mar 29, 2020
 * @since 2.0.0
 */
@Service
public class CategoryQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CategoryQueryService.class);

    /**
     * Category repository.
     */
    @Inject
    private CategoryRepository categoryRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Category tag repository.
     */
    @Inject
    private CategoryTagRepository categoryTagRepository;

    /**
     * Gets published article count of a category specified by the given category id.
     *
     * @param categoryId the given category id
     * @return article count, returns {@code -1} if occurred an exception
     */
    public int getPublishedArticleCount(final String categoryId) {
        try {
            final List<JSONObject> categoryTags = (List<JSONObject>) categoryTagRepository.getByCategoryId(categoryId, 1, Integer.MAX_VALUE).opt(Keys.RESULTS);
            if (categoryTags.isEmpty()) {
                return 0;
            }

            final List<String> tagIds = new ArrayList<>();
            for (JSONObject categoryTag : categoryTags) {
                tagIds.add(categoryTag.optString(Tag.TAG + "_" + Keys.OBJECT_ID));
            }

            final StringBuilder queryCount = new StringBuilder("SELECT count(DISTINCT(article.oId)) as `C` FROM ");
            final StringBuilder queryStr = new StringBuilder(articleRepository.getName() + " AS article,").
                    append(tagArticleRepository.getName() + " AS tag_article").
                    append(" WHERE article.oId=tag_article.article_oId ").
                    append(" AND article.").append(Article.ARTICLE_STATUS).append("=?").
                    append(" AND ").append("tag_article.tag_oId").append(" IN (");
            for (int i = 0; i < tagIds.size(); i++) {
                queryStr.append(" ").append(tagIds.get(i));
                if (i < (tagIds.size() - 1)) {
                    queryStr.append(",");
                }
            }
            queryStr.append(") ORDER BY `C` DESC");
            final List<JSONObject> tagArticlesCountResult = articleRepository.
                    select(queryCount.append(queryStr.toString()).toString(), Article.ARTICLE_STATUS_C_PUBLISHED);

            return tagArticlesCountResult == null ? 0 : tagArticlesCountResult.get(0).optInt("C");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets article count failed", e);

            return -1;
        }
    }

    /**
     * Gets most tag category.
     *
     * @param fetchSize the specified fetch size
     * @return categories, returns an empty list if not found
     */
    public List<JSONObject> getMostTagCategory(final int fetchSize) {
        final Query query = new Query().addSort(Category.CATEGORY_ORDER, SortDirection.ASCENDING).
                addSort(Category.CATEGORY_TAG_CNT, SortDirection.DESCENDING).
                addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).
                setPageSize(fetchSize).setPageCount(1);
        try {
            final List<JSONObject> ret = categoryRepository.getList(query);
            for (final JSONObject category : ret) {
                final List<JSONObject> tags = getTags(category.optString(Keys.OBJECT_ID));

                category.put(Category.CATEGORY_T_TAGS, (Object) tags);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets most tag category error", e);

            return Collections.emptyList();
        }
    }

    /**
     * Gets a category's tags.
     *
     * @param categoryId the specified category id
     * @return tags, returns an empty list if not found
     */
    public List<JSONObject> getTags(final String categoryId) {
        final List<JSONObject> ret = new ArrayList<>();

        final Query query = new Query().
                setFilter(new PropertyFilter(Category.CATEGORY + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, categoryId));
        try {
            final List<JSONObject> relations = categoryTagRepository.getList(query);
            for (final JSONObject relation : relations) {
                final String tagId = relation.optString(Tag.TAG + "_" + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);
                if (null == tag) { // 修复修改分类时空指针错误 https://github.com/b3log/solo/pull/12876
                    continue;
                }
                ret.add(tag);
            }
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets category [id=" + categoryId + "] tags error", e);
        }

        return ret;
    }

    /**
     * Gets a category by the specified category URI.
     *
     * @param categoryURI the specified category URI
     * @return category, returns {@code null} if not null
     * @throws ServiceException service exception
     */
    public JSONObject getByURI(final String categoryURI) throws ServiceException {
        try {
            final JSONObject ret = categoryRepository.getByURI(categoryURI);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets category [URI=" + categoryURI + "] failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets a category by the specified category title.
     *
     * @param categoryTitle the specified category title
     * @return category, returns {@code null} if not null
     * @throws ServiceException service exception
     */
    public JSONObject getByTitle(final String categoryTitle) throws ServiceException {
        try {
            final JSONObject ret = categoryRepository.getByTitle(categoryTitle);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets category [title=" + categoryTitle + "] failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets categories by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "categoryTitle": "", // optional
     *                          "paginationCurrentPageNum": 1,
     *                          "paginationPageSize": 20,
     *                          "paginationWindowSize": 10
     *                          see {@link Pagination} for more details
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "categories": [{
     *         "oId": "",
     *         "categoryTitle": "",
     *         "categoryDescription": "",
     *         ....
     *      }, ....]
     * }
     * </pre>
     * @throws ServiceException service exception
     * @see Pagination
     */
    public JSONObject getCategoris(final JSONObject requestJSONObject) throws ServiceException {
        final JSONObject ret = new JSONObject();

        final int currentPageNum = requestJSONObject.optInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
        final int pageSize = requestJSONObject.optInt(Pagination.PAGINATION_PAGE_SIZE);
        final int windowSize = requestJSONObject.optInt(Pagination.PAGINATION_WINDOW_SIZE);
        final Query query = new Query().setPage(currentPageNum, pageSize).
                addSort(Category.CATEGORY_ORDER, SortDirection.ASCENDING).
                addSort(Category.CATEGORY_TAG_CNT, SortDirection.DESCENDING).
                addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
        if (requestJSONObject.has(Category.CATEGORY_TITLE)) {
            query.setFilter(new PropertyFilter(Category.CATEGORY_TITLE, FilterOperator.EQUAL,
                    requestJSONObject.optString(Category.CATEGORY_TITLE)));
        }

        JSONObject result;
        try {
            result = categoryRepository.get(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets categories failed", e);
            throw new ServiceException(e);
        }

        final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
        final JSONObject pagination = new JSONObject();
        ret.put(Pagination.PAGINATION, pagination);
        final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
        pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        ret.put(Category.CATEGORIES, result.opt(Keys.RESULTS));
        return ret;
    }

    /**
     * Gets a category by the specified id.
     *
     * @param categoryId the specified id
     * @return a category, return {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getCategory(final String categoryId) throws ServiceException {
        try {
            final JSONObject ret = categoryRepository.get(categoryId);
            if (null == ret) {
                return null;
            }

            final List<JSONObject> tags = getTags(categoryId);
            ret.put(Category.CATEGORY_T_TAGS, (Object) tags);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets a category [categoryId=" + categoryId + "] failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Whether a tag specified by the given tag title in a category specified by the given category id.
     *
     * @param tagTitle   the given tag title
     * @param categoryId the given category id
     * @return {@code true} if the tag in the category, returns {@code false} otherwise
     */
    public boolean containTag(final String tagTitle, final String categoryId) {
        try {
            final JSONObject category = categoryRepository.get(categoryId);
            if (null == category) {
                return true;
            }

            final JSONObject tag = tagRepository.getByTitle(tagTitle);
            if (null == tag) {
                return true;
            }

            final Query query = new Query().setFilter(
                    CompositeFilterOperator.and(
                            new PropertyFilter(Category.CATEGORY + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, categoryId),
                            new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tag.optString(Keys.OBJECT_ID))));

            return categoryTagRepository.count(query) > 0;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Check category tag [tagTitle=" + tagTitle + ", categoryId=" + categoryId + "] failed", e);

            return true;
        }
    }
}
