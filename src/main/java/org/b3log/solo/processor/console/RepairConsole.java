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
