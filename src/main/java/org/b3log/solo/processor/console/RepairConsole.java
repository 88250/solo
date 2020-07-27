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
package org.b3log.solo.processor.console;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.TextHtmlRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.*;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.service.OptionQueryService;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.service.StatisticQueryService;
import org.json.JSONObject;

import java.util.List;

/**
 * Provides patches on some special issues.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 0.3.1
 */
@Singleton
public class RepairConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(RepairConsole.class);

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Preference management service.
     */
    @Inject
    private PreferenceMgmtService preferenceMgmtService;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * ArchiveDate-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Restores the signs of preference to default.
     *
     * @param context the specified context
     */
    public void restoreSigns(final RequestContext context) {
        final TextHtmlRenderer renderer = new TextHtmlRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = optionQueryService.getPreference();
            preference.put(Option.ID_C_SIGNS, Option.DefaultPreference.DEFAULT_SIGNS);
            preferenceMgmtService.updatePreference(preference);

            renderer.setContent("Restore signs succeeded.");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            renderer.setContent("Restores signs failed, error msg [" + e.getMessage() + "]");
        }
    }

    /**
     * Cleans duplicated archive date-articles.
     *
     * @param context the specified context
     */
    public void cleanArchiveDateArticles(final RequestContext context) {
        final TextHtmlRenderer renderer = new TextHtmlRenderer();
        context.setRenderer(renderer);

        final Transaction transaction = archiveDateArticleRepository.beginTransaction();
        try {
            // 清理存档-文章关联表中的冗余数据
            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            final List<JSONObject> archiveDateArticles = archiveDateArticleRepository.select("SELECT\n" +
                    "\t*\n" +
                    "FROM\n" +
                    "\t" + tablePrefix + "archivedate_article\n" +
                    "WHERE\n" +
                    "\tarticle_oId IN (\n" +
                    "\t\tSELECT\n" +
                    "\t\t\tarticle_oId\n" +
                    "\t\tFROM\n" +
                    "\t\t\t" + tablePrefix + "archivedate_article\n" +
                    "\t\tGROUP BY\n" +
                    "\t\t\tarticle_oId\n" +
                    "\t\tHAVING\n" +
                    "\t\t\tcount(*) > 1\n" +
                    "\t) ORDER BY archiveDate_oId, article_oId DESC");
            for (int i = 0; i < archiveDateArticles.size(); i++) {
                final JSONObject archiveDateArticle = archiveDateArticles.get(i);
                final String archiveDateId = archiveDateArticle.optString("archiveDate_oId");
                final String articleId = archiveDateArticle.optString("article_oId");
                archiveDateArticleRepository.remove(new Query().setFilter(CompositeFilterOperator.and(
                        new PropertyFilter("archiveDate_oId", FilterOperator.EQUAL, archiveDateId),
                        new PropertyFilter("article_oId", FilterOperator.EQUAL, articleId),
                        new PropertyFilter("oId", FilterOperator.NOT_EQUAL, archiveDateArticle.optString("oId")))));
                while (i < archiveDateArticles.size() - 1) {
                    if (!archiveDateId.equalsIgnoreCase(archiveDateArticles.get(i + 1).optString("archiveDate_oId"))
                            || !articleId.equalsIgnoreCase(archiveDateArticles.get(i + 1).optString("article_oId"))) {
                        break;
                    }
                    i++;
                }
            }

            transaction.commit();

            renderer.setContent("Cleaned duplicated archive date articles");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, e.getMessage(), e);
            renderer.setContent("Clean duplicated archive date articles failed [" + e.getMessage() + "]");
        }
    }
}
