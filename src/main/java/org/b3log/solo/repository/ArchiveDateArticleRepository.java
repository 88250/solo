/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Archive date-Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Sep 11, 2019
 * @since 0.3.1
 */
@Repository
public class ArchiveDateArticleRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArchiveDateArticleRepository.class);

    /**
     * Public constructor.
     */
    public ArchiveDateArticleRepository() {
        super((ArchiveDate.ARCHIVE_DATE + "_" + Article.ARTICLE).toLowerCase());
    }

    /**
     * Gets published article count of an archive date specified by the given archive data id.
     *
     * @param archiveDateId the given archive date id
     * @return published article count, returns {@code -1} if occurred an exception
     */
    public int getPublishedArticleCount(final String archiveDateId) {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);
            final ArchiveDateArticleRepository archiveDateArticleRepository = beanManager.getReference(ArchiveDateArticleRepository.class);

            final StringBuilder queryCount = new StringBuilder("SELECT count(DISTINCT(article.oId)) as C FROM ");
            final StringBuilder queryStr = new StringBuilder(articleRepository.getName() + " AS article,").
                    append(archiveDateArticleRepository.getName() + " AS archive_article").
                    append(" WHERE article.oId=archive_article.article_oId ").
                    append(" AND article.articleStatus=").append(Article.ARTICLE_STATUS_C_PUBLISHED).
                    append(" AND ").append("archive_article.archiveDate_oId=").append(archiveDateId);
            final List<JSONObject> articlesCountResult = select(queryCount.append(queryStr.toString()).toString());
            return articlesCountResult == null ? 0 : articlesCountResult.get(0).optInt("C");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets archivedate [" + archiveDateId + "]'s published article count failed", e);

            return -1;
        }
    }

    /**
     * Gets archive date-article relations by the specified archive date id.
     *
     * @param archiveDateId  the specified archive date id
     * @param currentPageNum the specified current page number, MUST greater then {@code 0}
     * @param pageSize       the specified page size(count of a page contains objects), MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "",
     *         "archiveDate_oId": "",
     *         "article_oId": ""
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    public JSONObject getByArchiveDateId(final String archiveDateId, final int currentPageNum, final int pageSize) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, archiveDateId)).
                addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID, SortDirection.DESCENDING).
                setPage(currentPageNum, pageSize).setPageCount(1);

        return get(query);
    }

    /**
     * Gets an archive date-article relations by the specified article id.
     *
     * @param articleId the specified article id
     * @return for example
     * <pre>
     * {
     *     "archiveDate_oId": "",
     *     "article_oId": articleId
     * }, returns {@code null} if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    public JSONObject getByArticleId(final String articleId) throws RepositoryException {
        final Query query = new Query().
                setFilter(new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId));
        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }
}
