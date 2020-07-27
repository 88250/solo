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
package org.b3log.solo.upgrade;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.*;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

import java.util.List;

/**
 * Upgrade script from v3.6.6 to v3.6.7.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.0, Nov 11, 2019
 * @since 3.6.7
 */
public final class V366_367 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V366_367.class);

    /**
     * Performs upgrade from v3.6.6 to v3.6.7.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        final String fromVer = "3.6.6";
        final String toVer = "3.6.7";

        LOGGER.log(Level.INFO, "Upgrading from version [" + fromVer + "] to version [" + toVer + "]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
        final ArchiveDateArticleRepository archiveDateArticleRepository = beanManager.getReference(ArchiveDateArticleRepository.class);
        try {
            final Transaction transaction = optionRepository.beginTransaction();

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

            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, toVer);
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            transaction.commit();

            LOGGER.log(Level.INFO, "Upgraded from version [" + fromVer + "] to version [" + toVer + "] successfully");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);
            throw new Exception("Upgrade failed from version [" + fromVer + "] to version [" + toVer + "]");
        }
    }
}
