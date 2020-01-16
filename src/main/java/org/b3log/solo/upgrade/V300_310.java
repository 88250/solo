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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.util.Images;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Upgrade script from v3.0.0 to v3.1.0.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.1, Mar 20, 2019
 * @since 3.1.0
 */
public final class V300_310 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V300_310.class);

    /**
     * Performs upgrade from v3.0.0 to v3.1.0.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        LOGGER.log(Level.INFO, "Upgrading from version [3.0.0] to version [3.1.0]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);
        final ArticleRepository articleRepository = beanManager.getReference(ArticleRepository.class);

        try {
            Connection connection = Connections.getConnection();
            Statement statement = connection.createStatement();

            // 文章表新增首图字段
            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` ADD COLUMN `articleAbstractText` TEXT");
            statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` ADD COLUMN `articleImg1URL` VARCHAR(255) DEFAULT '' NOT NULL");
            statement.close();
            connection.commit();
            connection.close();

            final Transaction transaction = optionRepository.beginTransaction();
            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, "3.1.0");
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            // 历史文章使用随机图片填充首图字段
            final List<JSONObject> articles = articleRepository.getList(new Query());
            for (final JSONObject article : articles) {
                final String imgURL = Images.imageSize(Images.randImage(), Article.ARTICLE_THUMB_IMG_WIDTH, Article.ARTICLE_THUMB_IMG_HEIGHT);
                article.put(Article.ARTICLE_IMG1_URL, imgURL);

                final String summary = article.optString(Article.ARTICLE_ABSTRACT);
                String summaryText;
                if (StringUtils.isBlank(summary)) {
                    final String content = article.optString(Article.ARTICLE_CONTENT);
                    summaryText = Article.getAbstractText(content);
                    article.put(Article.ARTICLE_ABSTRACT, summaryText);
                } else {
                    summaryText = Article.getAbstractText(summary);
                }
                article.put(Article.ARTICLE_ABSTRACT_TEXT, summaryText);

                articleRepository.update(article.optString(Keys.OBJECT_ID), article);
            }

            transaction.commit();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed!", e);

            throw new Exception("Upgrade failed from version [3.0.0] to version [3.1.0]");
        }
    }
}
