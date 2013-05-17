/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.processor;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.jdbc.JdbcFactory;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.repository.impl.*;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Upgrader.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="mailto:dongxu.wang@acm.org">Dongxu Wang</a>
 * @version 1.1.1.9, Apr 26, 2013
 * @since 0.3.1
 */
@RequestProcessor
// TODO:
// 1. Removes preference.blogHost from repository
public final class UpgradeProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UpgradeProcessor.class.getName());

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();

    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();

    /**
     * Step for article updating.
     */
    private static final int STEP = 50;

    /**
     * Preference Query Service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Mail Service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

    /**
     * Whether the email has been sent.
     */
    private boolean sent = false;

    /**
     * Language service.
     */
    private static LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Checks upgrade.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/upgrade/checker.do", method = HTTPRequestMethod.GET)
    public void upgrade(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();

        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);

            if (null == preference) {
                LOGGER.log(Level.INFO, "Not init yet");
                renderer.setContent("Not init yet");

                return;
            }

            renderer.setContent("Upgrade successfully ;-)");

            final String version = preference.getString(Preference.VERSION);

            if (SoloServletListener.VERSION.equals(version)) {
                return;
            }

            if ("0.5.6".equals(version)) {
                v056ToV060();
            } else {
                LOGGER.log(Level.WARNING, "Attempt to skip more than one version to upgrade. Expected: 0.5.6; Actually: {0}", version);
                if (!sent) {
                    notifyUserByEmail();
                    sent = true;
                }
                renderer.setContent(langPropsService.get("skipVersionAlert"));
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent(
                "Upgrade failed [" + e.getMessage() + "], please contact the B3log Solo developers or reports this "
                + "issue directly (<a href='https://github.com/b3log/b3log-solo/issues/new'>"
                + "https://github.com/b3log/b3log-solo/issues/new</a>) ");
        }
    }

    /**
     * Upgrades from version 056 to version 060.
     *
     * @throws Exception upgrade fails
     */
    private void v056ToV060() throws Exception {
        LOGGER.info("Upgrading from version 056 to version 060....");

        articleRepository.setCacheEnabled(false);

        Transaction transaction = null;

        try {
            transaction = userRepository.beginTransaction();
            
            final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

            if (RuntimeEnv.LOCAL == runtimeEnv || RuntimeEnv.BAE == runtimeEnv) {
                final Connection connection = Connections.getConnection();
                final Statement statement = connection.createStatement();

                final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix");

                String tableName = Strings.isEmptyOrNull(tablePrefix) ? "preference" : tablePrefix + "_preference";

                statement.execute("ALTER TABLE " + tableName + " ADD feedOutputCnt int");

                tableName = Strings.isEmptyOrNull(tablePrefix) ? "user" : tablePrefix + "_user";
                statement.execute("ALTER TABLE " + tableName + " ADD userURL varchar(255)");

                connection.commit();

                tableName = Strings.isEmptyOrNull(tablePrefix) ? "option" : tablePrefix + "_option";
                final Map<String, List<FieldDefinition>> map = JdbcRepositories.getRepositoriesMap();

                try {
                    JdbcFactory.createJdbcFactory().createTable(tableName, map.get(tableName));
                } catch (final SQLException e) {
                    LOGGER.log(Level.SEVERE, "createTable[" + tableName + "] error", e);
                }
            }
            
            // Upgrades preference model
            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);

            preference.put(Preference.VERSION, "0.6.0");
            preference.put(Preference.FEED_OUTPUT_CNT, Preference.Default.DEFAULT_FEED_OUTPUT_CNT);
            preferenceRepository.update(Preference.PREFERENCE, preference);

            upgradeUsers();

            transaction.commit();

            LOGGER.log(Level.FINEST, "Updated preference");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade failed.", e);
            throw new Exception("Upgrade failed from version 056 to version 060");
        } finally {
            articleRepository.setCacheEnabled(true);
        }

        LOGGER.info("Upgraded from version 056 to version 060 successfully :-)");
    }

    /**
     * Upgrades users.
     * 
     * <p>
     * Password hashing.
     * </p>
     * 
     * @throws Exception exception
     */
    private void upgradeUsers() throws Exception {
        final JSONArray users = userRepository.get(new Query()).getJSONArray(Keys.RESULTS);

        for (int i = 0; i < users.length(); i++) {
            final JSONObject user = users.getJSONObject(i);

            user.put(User.USER_URL, Latkes.getServePath());

            userRepository.update(user.optString(Keys.OBJECT_ID), user);

            LOGGER.log(Level.INFO, "Hashed user[name={0}] password.", user.optString(User.USER_NAME));
        }
    }

    /**
     * Upgrades articles.
     *
     * @throws Exception exception
     */
    private void upgradeArticles() throws Exception {
        LOGGER.log(Level.INFO, "Adds a property [articleEditorType] to each of articles");

        final JSONArray articles = articleRepository.get(new Query()).getJSONArray(Keys.RESULTS);

        if (articles.length() <= 0) {
            LOGGER.log(Level.FINEST, "No articles");
            return;
        }

        Transaction transaction = null;

        try {
            for (int i = 0; i < articles.length(); i++) {
                if (0 == i % STEP || !transaction.isActive()) {
                    transaction = userRepository.beginTransaction();
                }

                final JSONObject article = articles.getJSONObject(i);

                final String articleId = article.optString(Keys.OBJECT_ID);

                LOGGER.log(Level.INFO, "Found an article[id={0}]", articleId);
                article.put(Article.ARTICLE_EDITOR_TYPE, "tinyMCE");

                articleRepository.update(article.getString(Keys.OBJECT_ID), article);

                if (0 == i % STEP) {
                    transaction.commit();
                    LOGGER.log(Level.FINEST, "Updated some articles");
                }
            }

            if (transaction.isActive()) {
                transaction.commit();
            }

            LOGGER.log(Level.FINEST, "Updated all articles");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    /**
     * Send an email to the user who upgrades B3log Solo with a discontinuous version.
     * 
     * @throws ServiceException ServiceException
     * @throws JSONException JSONException
     * @throws IOException IOException
     */
    private void notifyUserByEmail() throws ServiceException, JSONException, IOException {
        final String adminEmail = preferenceQueryService.getPreference().getString(Preference.ADMIN_EMAIL);
        final MailService.Message message = new MailService.Message();

        message.setFrom(adminEmail);
        message.addRecipient(adminEmail);
        message.setSubject(langPropsService.get("skipVersionMailSubject"));
        message.setHtmlBody(langPropsService.get("skipVersionMailBody"));
        MAIL_SVC.send(message);
        LOGGER.info("Send an email to the user who upgrades B3log Solo with a discontinuous version.");
    }
}
