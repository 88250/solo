/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.repository.*;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.taskqueue.TaskQueueService;
import org.b3log.latke.taskqueue.TaskQueueServiceFactory;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.repository.impl.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.9, Apr 29, 2012
 * @since 0.3.1
 */
@RequestProcessor
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
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();
    /**
     * Task queue service.
     */
    private TaskQueueService taskQueueService = TaskQueueServiceFactory.getTaskQueueService();
    /**
     * Step for article updating.
     */
    private static final int STEP = 50;

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

            if ("0.4.1".equals(version)) {
                v041ToV045();
            } else {
                final String msg = "Your B3log Solo is too old to upgrader, please contact the B3log Solo developers";
                LOGGER.warning(msg);
                renderer.setContent(msg);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Upgrade failed [" + e.getMessage() + "], please contact the B3log Solo developers or reports this "
                                + "issue directly (https://github.com/b3log/b3log-solo/issues/new) ");
        }
    }

    /**
     * Upgrades from version 041 to version 045.
     *
     * <p>
     * Model:
     *   <ul>
     *     <li>
     *       Adds a property(named {@value Article#ARTICLE_EDITOR_TYPE}) to entity {@link Article}
     *     </li>
     *     <li>
     *       Adds a property(named {@value Page#PAGE_EDITOR_TYPE}) to entity {@link Page}
     *     </li>
     *     <li>
     *       Adds a property(named {@value Preference#EDITOR_TYPE}) to entity {@link Preference}
     *     </li>
     *   </ul>
     * </p>
     * @throws Exception upgrade fails
     */
    private void v041ToV045() throws Exception {
        LOGGER.info("Upgrading from version 041 to version 045....");

        articleRepository.setCacheEnabled(false);

        Transaction transaction = null;
        try {
            upgradeArticles();

            transaction = userRepository.beginTransaction();

            // Upgrades page model
            final JSONObject result = pageRepository.get(new Query());
            final JSONArray pages = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < pages.length(); i++) {
                final JSONObject page = pages.getJSONObject(i);

                page.put(Page.PAGE_EDITOR_TYPE, "tinyMCE");

                pageRepository.update(page.getString(Keys.OBJECT_ID), page);
            }

            LOGGER.log(Level.FINEST, "Updated pages");

            // Upgrades preference model
            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);

            preference.put(Preference.COMMENTABLE, Preference.Default.DEFAULT_COMMENTABLE);
            preference.put(Preference.EDITOR_TYPE, Preference.Default.DEFAULT_EDITOR_TYPE);
            preference.put(Preference.VERSION, "0.4.5");

            preferenceRepository.update(Preference.PREFERENCE, preference);

            LOGGER.log(Level.FINEST, "Updated preference");

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade failed.", e);
            throw new Exception("Upgrade failed from version 041 to version 045");
        } finally {
            articleRepository.setCacheEnabled(true);
        }

        LOGGER.info("Upgraded from version 041 to version 045 successfully :-)");
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
}
