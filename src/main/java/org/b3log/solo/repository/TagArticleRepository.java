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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag-Article repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.0, Aug 20, 2019
 * @since 0.3.1
 */
@Repository
public class TagArticleRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(TagArticleRepository.class);

    /**
     * Public constructor.
     */
    public TagArticleRepository() {
        super(Tag.TAG + "_" + Article.ARTICLE);
    }

    /**
     * Gets most used tags with the specified number.
     *
     * @param num the specified number
     * @return a list of most used tags, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getMostUsedTags(final int num) throws RepositoryException {
        final List<JSONObject> records = select("SELECT\n" +
                "\t`tag_oId`,\n" +
                "\tcount(*) AS cnt\n" +
                "FROM `" + getName() + "`\n" +
                "GROUP BY\n" +
                "\t`tag_oId`\n" +
                "ORDER BY\n" +
                "\tcnt DESC\n" +
                "LIMIT ?", num);
        final List<JSONObject> ret = new ArrayList<>();
        final TagRepository tagRepository = BeanManager.getInstance().getReference(TagRepository.class);
        for (final JSONObject record : records) {
            final String tagId = record.optString(Tag.TAG + "_" + Keys.OBJECT_ID);
            final JSONObject tag = tagRepository.get(tagId);
            if (null != tag) {
                final int articleCount = getPublishedArticleCount(tagId);
                tag.put(Tag.TAG_T_PUBLISHED_REFERENCE_COUNT, articleCount);
            }
            ret.add(tag);
        }

        return ret;
    }

    /**
     * Gets article count of a tag specified by the given tag id.
     *
     * @param tagId the given tag id
     * @return article count, returns {@code -1} if occurred an exception
     */
    public int getArticleCount(final String tagId) {
        final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId));
        try {
            return (int) count(query);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets tag [" + tagId + "]'s article count failed", e);

            return -1;
        }
    }

    /**
     * Gets published article count of a tag specified by the given tag id.
     *
     * @param tagId the given tag id
     * @return published article count, returns {@code -1} if occurred an exception
     */
    public int getPublishedArticleCount(final String tagId) {
        try {
            final String tableNamePrefix = StringUtils.isNotBlank(Latkes.getLocalProperty("jdbc.tablePrefix"))
                    ? Latkes.getLocalProperty("jdbc.tablePrefix") + "_"
                    : "";
            final List<JSONObject> result = select("SELECT\n" +
                    "\tcount(*) AS `C`\n" +
                    "FROM\n" +
                    "\t" + tableNamePrefix + "tag_article AS t,\n" +
                    "\t" + tableNamePrefix + "article AS a\n" +
                    "WHERE\n" +
                    "\tt.article_oId = a.oId\n" +
                    "AND a.articleStatus = ?\n" +
                    "AND t.tag_oId = ?", Article.ARTICLE_STATUS_C_PUBLISHED, tagId);
            return result.get(0).optInt("C");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets tag [" + tagId + "]'s published article count failed", e);

            return -1;
        }
    }

    /**
     * Gets tag-article relations by the specified article id.
     *
     * @param articleId the specified article id
     * @return for example
     * <pre>
     * [{
     *         "oId": "",
     *         "tag_oId": "",
     *         "article_oId": articleId
     * }, ....], returns an empty list if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getByArticleId(final String articleId) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, articleId)).
                setPageCount(1);

        return getList(query);
    }

    /**
     * Gets tag-article relations by the specified tag id.
     *
     * @param tagId          the specified tag id
     * @param currentPageNum the specified current page number, MUST greater
     *                       then {@code 0}
     * @param pageSize       the specified page size(count of a page contains objects),
     *                       MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         "oId": "",
     *         "tag_oId": tagId,
     *         "article_oId": ""
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    public JSONObject getByTagId(final String tagId, final int currentPageNum, final int pageSize) throws RepositoryException {
        final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId)).
                addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID, SortDirection.DESCENDING).
                setPage(currentPageNum, pageSize);
        return get(query);
    }
}
