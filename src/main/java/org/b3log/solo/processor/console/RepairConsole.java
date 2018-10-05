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
package org.b3log.solo.processor.console;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
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
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Provides patches on some special issues.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.17, Sep 25, 2018
 * @since 0.3.1
 */
@RequestProcessor
@Before(adviceClass = ConsoleAuthAdvice.class)
public class RepairConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepairConsole.class);

    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

    /**
     * Bean manager.
     */
    @Inject
    private BeanManager beanManager;

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
            if (!Solos.isConfigured()) {
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
            final List<JSONObject> tags = tagRepository.getList(new Query());
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
