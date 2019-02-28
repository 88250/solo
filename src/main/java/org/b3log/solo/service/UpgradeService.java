/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
package org.b3log.solo.service;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.cache.ArticleCache;
import org.b3log.solo.cache.CommentCache;
import org.b3log.solo.mail.MailService;
import org.b3log.solo.mail.MailServiceFactory;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.UserExt;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.upgrade.V299_300;
import org.b3log.solo.upgrade.V300_310;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

/**
 * Upgrade service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/e">Dongxu Wang</a>
 * @version 1.2.1.1, Feb 28, 2019
 * @since 1.2.0
 */
@Service
public class UpgradeService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UpgradeService.class);

    /**
     * Step for article updating.
     */
    private static final int STEP = 50;

    /**
     * Mail Service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Preference Query Service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Article cache.
     */
    @Inject
    private ArticleCache articleCache;

    /**
     * Comment cache.
     */
    @Inject
    private CommentCache commentCache;

    /**
     * Upgrades if need.
     */
    public void upgrade() {
        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                return;
            }

            final String currentVer = preference.getString(Option.ID_C_VERSION);
            if (SoloServletListener.VERSION.equals(currentVer)) {
                return;
            }

            switch (currentVer) {
                case "2.9.9":
                    V299_300.perform();
                case "3.0.0":
                    V300_310.perform();

                    break;
                default:
                    LOGGER.log(Level.ERROR, "Please upgrade to v3.0.0 first");
                    System.exit(-1);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Upgrade failed, please contact the Solo developers or reports this "
                    + "issue: https://github.com/b3log/solo/issues/new", e);
            System.exit(-1);
        }
    }

    private void alterTables() throws Exception {
        final Connection connection = Connections.getConnection();
        final Statement statement = connection.createStatement();

        final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "archiveDate` RENAME TO `" + tablePrefix + "archivedate1`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "archivedate1` RENAME TO `" + tablePrefix + "archivedate`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "archiveDate_article` RENAME TO `" + tablePrefix + "archivedate_article1`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "archivedate_article1` RENAME TO `" + tablePrefix + "archivedate_article`");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` ADD `articleAuthorId` VARCHAR(19) DEFAULT '' NOT NULL");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` ADD `articleCreated` BIGINT DEFAULT 0 NOT NULL");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "article` ADD `articleUpdated` BIGINT DEFAULT 0 NOT NULL");
        statement.executeUpdate("ALTER TABLE `" + tablePrefix + "comment` ADD `commentCreated` BIGINT DEFAULT 0 NOT NULL");
        statement.close();
        connection.commit();
        connection.close();
    }

    private void dropTables() throws Exception {
        final Connection connection = Connections.getConnection();
        final Statement statement = connection.createStatement();

        final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
        statement.execute("DROP TABLE `" + tablePrefix + "statistic`");
        statement.close();
        connection.commit();
        connection.close();
    }

    private void upgradeUsers() throws Exception {
        final JSONArray users = userRepository.get(new Query()).getJSONArray(Keys.RESULTS);

        for (int i = 0; i < users.length(); i++) {
            final JSONObject user = users.getJSONObject(i);
            final String email = user.optString(User.USER_EMAIL);
            user.put(UserExt.USER_AVATAR, Solos.getGravatarURL(email, "128"));

            userRepository.update(user.optString(Keys.OBJECT_ID), user);
            LOGGER.log(Level.INFO, "Updated user[email={0}]", email);
        }
    }

    private void upgradeArticles() throws Exception {
        final List<JSONObject> articles = articleRepository.getList(new Query().
                select(Keys.OBJECT_ID, Article.ARTICLE_T_CREATE_DATE, Article.ARTICLE_T_UPDATE_DATE, Article.ARTICLE_T_AUTHOR_EMAIL));
        if (articles.isEmpty()) {
            LOGGER.log(Level.TRACE, "No articles");

            return;
        }

        Transaction transaction = null;
        try {
            for (int i = 0; i < articles.size(); i++) {
                if (0 == i % STEP || !transaction.isActive()) {
                    transaction = userRepository.beginTransaction();
                }

                final String articleId = articles.get(i).optString(Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                String authorEmail = article.optString(Article.ARTICLE_T_AUTHOR_EMAIL);
                if (StringUtils.isBlank(authorEmail)) { // H2
                    authorEmail = article.optString(Article.ARTICLE_T_AUTHOR_EMAIL.toUpperCase());
                }
                JSONObject author = userRepository.getByEmail(authorEmail);
                if (null == author) {
                    author = userRepository.getAdmin();
                }
                article.put(Article.ARTICLE_AUTHOR_ID, author.optString(Keys.OBJECT_ID));

                Date createDate = (Date) article.opt(Article.ARTICLE_T_CREATE_DATE);
                if (null == createDate) { // H2
                    createDate = (Date) article.opt(Article.ARTICLE_T_CREATE_DATE.toUpperCase());
                }
                article.put(Article.ARTICLE_CREATED, createDate.getTime());

                Date updateDate = (Date) article.opt(Article.ARTICLE_T_UPDATE_DATE);
                if (null == updateDate) { // H2
                    updateDate = (Date) article.opt(Article.ARTICLE_T_UPDATE_DATE.toUpperCase());
                }
                article.put(Article.ARTICLE_UPDATED, updateDate.getTime());

                articleRepository.update(articleId, article);

                if (0 == i % STEP) {
                    transaction.commit();
                    LOGGER.log(Level.INFO, "Updated some articles [" + i + "]");
                }
            }

            if (transaction.isActive()) {
                transaction.commit();
            }

            LOGGER.log(Level.INFO, "Updated all articles");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    private void upgradeComments() throws Exception {
        final List<JSONObject> comments = commentRepository.getList(new Query());
        if (comments.isEmpty()) {
            LOGGER.log(Level.TRACE, "No comments");

            return;
        }

        Transaction transaction = null;
        try {
            for (int i = 0; i < comments.size(); i++) {
                if (0 == i % STEP || !transaction.isActive()) {
                    transaction = userRepository.beginTransaction();
                }

                final JSONObject comment = comments.get(i);
                final String commentId = comment.optString(Keys.OBJECT_ID);

                Date createDate = (Date) comment.opt(Comment.COMMENT_T_DATE);
                if (null == createDate) { // H2
                    createDate = (Date) comment.opt(Comment.COMMENT_T_DATE.toUpperCase());
                }
                comment.put(Comment.COMMENT_CREATED, createDate.getTime());

                commentRepository.update(commentId, comment);

                if (0 == i % STEP) {
                    transaction.commit();
                    LOGGER.log(Level.INFO, "Updated some comments [" + i + "]");
                }
            }

            if (transaction.isActive()) {
                transaction.commit();
            }

            LOGGER.log(Level.INFO, "Updated all comments");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    /**
     * Send an email to the user who upgrades Solo with a discontinuous version.
     *
     * @throws Exception exception
     */
    private void notifyUserByEmail() throws Exception {
        if (!Solos.isMailConfigured()) {
            return;
        }

        final String adminEmail = preferenceQueryService.getPreference().getString(Option.ID_C_ADMIN_EMAIL);
        final MailService.Message message = new MailService.Message();
        message.setFrom(adminEmail);
        message.addRecipient(adminEmail);
        message.setSubject(langPropsService.get("skipVersionMailSubject"));
        message.setHtmlBody(langPropsService.get("skipVersionMailBody"));

        MAIL_SVC.send(message);

        LOGGER.info("Send an email to the user who upgrades Solo with a discontinuous version.");
    }
}
