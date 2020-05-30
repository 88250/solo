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

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Category;
import org.json.JSONObject;

import java.util.List;

/**
 * Category repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.3, Sep 11, 2019
 * @since 2.0.0
 */
@Repository
public class CategoryRepository extends AbstractRepository {

    /**
     * Public constructor.
     */
    public CategoryRepository() {
        super(Category.CATEGORY);
    }

    /**
     * Gets a category by the specified category title.
     *
     * @param categoryTitle the specified category title
     * @return a category, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByTitle(final String categoryTitle) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Category.CATEGORY_TITLE, FilterOperator.EQUAL, categoryTitle)).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets a category by the specified category URI.
     *
     * @param categoryURI the specified category URI
     * @return a category, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByURI(final String categoryURI) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Category.CATEGORY_URI, FilterOperator.EQUAL, categoryURI)).setPageCount(1);
        return getFirst(query);
    }

    /**
     * Gets the maximum order.
     *
     * @return order number, returns {@code -1} if not found
     * @throws RepositoryException repository exception
     */
    public int getMaxOrder() throws RepositoryException {
        final Query query = new Query().addSort(Category.CATEGORY_ORDER, SortDirection.DESCENDING);
        final JSONObject result = getFirst(query);
        if (null == result) {
            return -1;
        }
        return result.optInt(Category.CATEGORY_ORDER);
    }

    /**
     * Gets the upper category of the category specified by the given id.
     *
     * @param id the given id
     * @return upper category, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getUpper(final String id) throws RepositoryException {
        final JSONObject category = get(id);
        if (null == category) {
            return null;
        }

        final Query query = new Query().setFilter(new PropertyFilter(Category.CATEGORY_ORDER, FilterOperator.LESS_THAN, category.optInt(Category.CATEGORY_ORDER))).
                addSort(Category.CATEGORY_ORDER, SortDirection.DESCENDING).setPage(1, 1);
        return getFirst(query);
    }

    /**
     * Gets the under category of the category specified by the given id.
     *
     * @param id the given id
     * @return under category, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getUnder(final String id) throws RepositoryException {
        final JSONObject category = get(id);
        if (null == category) {
            return null;
        }

        final Query query = new Query().setFilter(new PropertyFilter(Category.CATEGORY_ORDER, FilterOperator.GREATER_THAN, category.optInt(Category.CATEGORY_ORDER))).
                addSort(Category.CATEGORY_ORDER, SortDirection.ASCENDING).setPage(1, 1);
        return getFirst(query);
    }

    /**
     * Gets a category by the specified order.
     *
     * @param order the specified order
     * @return category, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    public JSONObject getByOrder(final int order) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Category.CATEGORY_ORDER, FilterOperator.EQUAL, order));
        return getFirst(query);
    }

    /**
     * Gets most used categories (contains the most tags) with the specified number.
     *
     * @param num the specified number
     * @return a list of most used categories, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getMostUsedCategories(final int num) throws RepositoryException {
        final Query query = new Query().addSort(Category.CATEGORY_ORDER, SortDirection.ASCENDING).
                setPage(1, num).setPageCount(1);
        final List<JSONObject> ret = getList(query);

        final BeanManager beanManager = BeanManager.getInstance();
        final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);
        final TagArticleRepository tagArticleRepository = beanManager.getReference(TagArticleRepository.class);
        final CategoryTagRepository categoryTagRepository = beanManager.getReference(CategoryTagRepository.class);

        for (final JSONObject category : ret) {
            final String categoryId = category.optString(Keys.OBJECT_ID);
            final StringBuilder queryCount = new StringBuilder("SELECT count(DISTINCT(b3_solo_article.oId)) as C FROM ");
            final StringBuilder queryStr = new StringBuilder(articleRepository.getName() + " AS b3_solo_article,").
                    append(tagArticleRepository.getName() + " AS b3_solo_tag_article").
                    append(" WHERE b3_solo_article.oId=b3_solo_tag_article.article_oId ").
                    append(" AND b3_solo_article.articleStatus=").append(Article.ARTICLE_STATUS_C_PUBLISHED).
                    append(" AND ").append("b3_solo_tag_article.tag_oId").append(" IN (").
                    append("SELECT tag_oId FROM ").append(categoryTagRepository.getName() + " AS b3_solo_category_tag WHERE b3_solo_category_tag.category_oId = ").
                    append(categoryId).append(")");
            final List<JSONObject> articlesCountResult = select(queryCount.append(queryStr.toString()).toString());
            final int articleCount = articlesCountResult == null ? 0 : articlesCountResult.get(0).optInt("C");
            category.put(Category.CATEGORY_T_PUBLISHED_ARTICLE_COUNT, articleCount);
        }

        return ret;
    }
}
