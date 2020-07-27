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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.json.JSONObject;

import java.util.List;

/**
 * Archive date-Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Oct 14, 2019
 * @since 0.3.1
 */
@Repository
public class ArchiveDateArticleRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArchiveDateArticleRepository.class);

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
        final Query query = new Query().setFilter(new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId));
        return getFirst(query);
    }
}
