/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Option;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.service.StatisticQueryService;
import org.b3log.solo.util.Mails;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Provides patches on some special issues.
 * <p>
 * See AuthFilter filter configurations in web.xml for authentication.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.14, Mar 11, 2018
 * @since 0.3.1
 */
@RequestProcessor
public class RepairProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepairProcessor.class);

    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

    /**
     * Bean manager.
     */
    @Inject
    private LatkeBeanManager beanManager;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

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
     * Removes unused properties of each article.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/fix/normalization/articles/properties", method = HTTPRequestMethod.POST)
    public void removeUnusedArticleProperties(final HTTPRequestContext context) {
        LOGGER.log(Level.INFO, "Processes remove unused article properties");

        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        Transaction transaction = null;
        try {
            final JSONArray articles = articleRepository.get(new Query()).getJSONArray(Keys.RESULTS);
            if (articles.length() <= 0) {
                renderer.setContent("No unused article properties");
                return;
            }

            transaction = articleRepository.beginTransaction();

            final Set<String> keyNames = Repositories.getKeyNames(Article.ARTICLE);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);

                final JSONArray names = article.names();
                final Set<String> nameSet = CollectionUtils.jsonArrayToSet(names);
                if (nameSet.removeAll(keyNames)) {
                    for (final String unusedName : nameSet) {
                        article.remove(unusedName);
                    }

                    articleRepository.update(article.getString(Keys.OBJECT_ID), article);
                    LOGGER.log(Level.INFO, "Found an article[id={0}] exists unused properties[{1}]",
                            article.getString(Keys.OBJECT_ID), nameSet);
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            if (null != transaction && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, e.getMessage(), e);
            renderer.setContent("Removes unused article properties failed, error msg[" + e.getMessage() + "]");
        }
    }

    /**
     * Restores the signs of preference to default.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/fix/restore-signs.do", method = HTTPRequestMethod.GET)
    public void restoreSigns(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            final String originalSigns = preference.getString(Option.ID_C_SIGNS);

            preference.put(Option.ID_C_SIGNS, Option.DefaultPreference.DEFAULT_SIGNS);
            preferenceMgmtService.updatePreference(preference);

            renderer.setContent("Restores signs succeeded.");

            // Sends the sample signs to developer
            if (!Mails.isConfigured()) {
                return;
            }

            final Message msg = new MailService.Message();
            msg.setFrom(preference.getString(Option.ID_C_ADMIN_EMAIL));
            msg.addRecipient("d@b3log.org");
            msg.setSubject("Restore signs");
            msg.setHtmlBody(originalSigns + "<p>Admin email: " + preference.getString(Option.ID_C_ADMIN_EMAIL) + "</p>");

            MAIL_SVC.send(msg);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);

            renderer.setContent("Restores signs failed, error msg[" + e.getMessage() + "]");
        }
    }

    /**
     * Repairs tag article counter.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/fix/tag-article-counter-repair.do", method = HTTPRequestMethod.GET)
    @Transactional
    public void repairTagArticleCounter(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject result = tagRepository.get(new Query());
            final JSONArray tagArray = result.getJSONArray(Keys.RESULTS);
            final List<JSONObject> tags = CollectionUtils.jsonArrayToList(tagArray);

            for (final JSONObject tag : tags) {
                final String tagId = tag.getString(Keys.OBJECT_ID);
                final JSONObject tagArticleResult = tagArticleRepository.getByTagId(tagId, 1, Integer.MAX_VALUE);
                final JSONArray tagArticles = tagArticleResult.getJSONArray(Keys.RESULTS);
                final int tagRefCnt = tagArticles.length();
                int publishedTagRefCnt = 0;

                for (int i = 0; i < tagRefCnt; i++) {
                    final JSONObject tagArticle = tagArticles.getJSONObject(i);
                    final String articleId = tagArticle.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);
                    final JSONObject article = articleRepository.get(articleId);
                    if (null == article) {
                        tagArticleRepository.remove(tagArticle.optString(Keys.OBJECT_ID));

                        continue;
                    }

                    if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                        publishedTagRefCnt++;
                    }
                }

                tag.put(Tag.TAG_REFERENCE_COUNT, tagRefCnt);
                tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedTagRefCnt);

                tagRepository.update(tagId, tag);

                LOGGER.log(Level.INFO, "Repaired tag[title={0}, refCnt={1}, publishedTagRefCnt={2}]",
                        tag.getString(Tag.TAG_TITLE), tagRefCnt, publishedTagRefCnt);
            }

            renderer.setContent("Repair successfully!");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            renderer.setContent("Repairs failed, error msg[" + e.getMessage() + "]");
        }
    }
}
