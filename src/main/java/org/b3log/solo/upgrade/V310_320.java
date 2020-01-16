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
package org.b3log.solo.upgrade;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Upgrade script from v3.1.0 to v3.2.0.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 3, 2019
 * @since 3.2.0
 */
public final class V310_320 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V310_320.class);

    /**
     * Performs upgrade from v3.1.0 to v3.2.0.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        final String fromVer = "3.1.0";
        final String toVer = "3.2.0";

        LOGGER.log(Level.INFO, "Upgrading from version [" + fromVer + "] to version [" + toVer + "]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
        final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);

        try {
            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";

            Connection connection = Connections.getConnection();
            Statement statement = connection.createStatement();
            // 重构文章草稿、发布状态 https://github.com/b3log/solo/issues/12669
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` ADD COLUMN `articleStatus` INT DEFAULT 0 NOT NULL");
            statement.close();
            connection.commit();
            connection.close();

            final Transaction transaction = optionRepository.beginTransaction();

            optionRepository.remove("adminEmail");
            optionRepository.remove("replyNotiTplBody");
            optionRepository.remove("replyNotiTplSubject");

            final List<JSONObject> drafts = articleRepository.getList(new Query().setFilter(new PropertyFilter("articleIsPublished", FilterOperator.EQUAL, false)));
            for (final JSONObject draft : drafts) {
                draft.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_DRAFT);
                articleRepository.update(draft.optString(Keys.OBJECT_ID), draft);
            }

            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, toVer);
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            transaction.commit();

            connection = Connections.getConnection();
            statement = connection.createStatement();
            // 移除邮件相关功能 https://github.com/b3log/solo/issues/12690
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "user` DROP COLUMN `userEmail`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "comment` DROP COLUMN `commentEmail`");
            // 重构文章草稿、发布状态 https://github.com/b3log/solo/issues/12669
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` DROP COLUMN `articleIsPublished`");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` DROP COLUMN `articleHadBeenPublished`");
            statement.close();
            connection.commit();
            connection.close();

            LOGGER.log(Level.INFO, "Upgraded from version [" + fromVer + "] to version [" + toVer + "] successfully");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);

            throw new Exception("Upgrade failed from version [" + fromVer + "] to version [" + toVer + "]");
        }
    }
}
