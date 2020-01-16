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
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.service.ArticleMgmtService;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Upgrade script from v3.5.0 to v3.6.0.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Apr 18, 2019
 * @since 3.6.0
 */
public final class V350_360 {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(V350_360.class);

    /**
     * Performs upgrade from v3.5.0 to v3.6.0.
     *
     * @throws Exception upgrade fails
     */
    public static void perform() throws Exception {
        final String fromVer = "3.5.0";
        final String toVer = "3.6.0";

        LOGGER.log(Level.INFO, "Upgrading from version [" + fromVer + "] to version [" + toVer + "]....");

        final BeanManager beanManager = BeanManager.getInstance();
        final OptionRepository optionRepository = beanManager.getReference(OptionRepository.class);

        try {
            convertPagesToArticles();

            final Transaction transaction = optionRepository.beginTransaction();

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

    /**
     * 去掉自定义页面 https://github.com/b3log/solo/issues/12764
     */
    private static void convertPagesToArticles() throws Exception {
        final BeanManager beanManager = BeanManager.getInstance();
        final PageRepository pageRepository = beanManager.getReference(PageRepository.class);
        final CommentRepository commentRepository = beanManager.getReference(CommentRepository.class);
        final ArticleMgmtService articleMgmtService = beanManager.getReference(ArticleMgmtService.class);
        final UserRepository userRepository = beanManager.getReference(UserRepository.class);
        final JSONObject admin = userRepository.getAdmin();

        final List<JSONObject> pages = pageRepository.getList(new Query().
                setFilter(new PropertyFilter("pageType", FilterOperator.EQUAL, Page.PAGE)));
        for (final JSONObject page : pages) {
            final String pageId = page.optString(Keys.OBJECT_ID);
            Transaction transaction = pageRepository.beginTransaction();
            pageRepository.remove(pageId);
            transaction.commit();

            final String title = page.optString(Page.PAGE_TITLE);
            final String permalink = page.optString(Page.PAGE_PERMALINK);
            final String content = page.optString("pageContent");
            final int commentCnt = page.optInt("pageCommentCount");
            final boolean commentable = page.optBoolean("pageCommentable");

            final JSONObject article = new JSONObject();
            article.put(Article.ARTICLE_AUTHOR_ID, admin.optString(Keys.OBJECT_ID));
            article.put(Article.ARTICLE_TITLE, title);
            article.put(Article.ARTICLE_ABSTRACT, Article.getAbstractText(content));
            article.put(Article.ARTICLE_COMMENT_COUNT, commentCnt);
            if ("/my-github-repos".equals(permalink)) {
                article.put(Article.ARTICLE_TAGS_REF, "开源,GitHub");
            }
            article.put(Article.ARTICLE_PERMALINK, permalink);
            article.put(Article.ARTICLE_COMMENTABLE, commentable);
            article.put(Article.ARTICLE_CONTENT, content);
            article.put(Article.ARTICLE_VIEW_PWD, "");
            article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
            article.put(Common.POST_TO_COMMUNITY, false);

            final JSONObject addArticleReq = new JSONObject();
            addArticleReq.put(Article.ARTICLE, article);
            final String articleId = articleMgmtService.addArticle(addArticleReq);
            final List<JSONObject> comments = commentRepository.getList(new Query().setFilter(new PropertyFilter(Comment.COMMENT_ON_ID, FilterOperator.EQUAL, pageId)));

            transaction = pageRepository.beginTransaction();
            for (final JSONObject comment : comments) {
                comment.put(Comment.COMMENT_ON_ID, articleId);
                final String commentId = comment.optString(Keys.OBJECT_ID);
                commentRepository.update(commentId, comment);
            }

            transaction.commit();
        }

        JdbcRepository.dispose();

        final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
        final Connection connection = Connections.getConnection();
        final Statement statement = connection.createStatement();
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "page` DROP COLUMN `pageType`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "page` DROP COLUMN `pageContent`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "page` DROP COLUMN `pageCommentCount`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "page` DROP COLUMN `pageCommentable`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "comment` DROP COLUMN `commentOnType`");
        statement.close();
        connection.commit();
        connection.close();
    }
}
