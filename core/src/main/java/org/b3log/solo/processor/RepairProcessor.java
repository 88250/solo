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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.repository.*;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleRepositoryImpl;
import org.b3log.solo.repository.impl.ArchiveDateRepositoryImpl;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.repository.impl.PluginRepositoryImpl;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.repository.impl.TagArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.service.PreferenceMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.StatisticMgmtService;
import org.b3log.solo.service.StatisticQueryService;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Provides patches on some special issues.
 * 
 * <p>See AuthFilter filter configurations in web.xml for authentication.</p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, Dec 25, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class RepairProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepairProcessor.class.getName());

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagRepositoryImpl.getInstance();

    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository = TagArticleRepositoryImpl.getInstance();

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Statistic query service.
     */
    private StatisticQueryService statisticQueryService = StatisticQueryService.getInstance();

    /**
     * Statistic management service.
     */
    private StatisticMgmtService statisticMgmtService = StatisticMgmtService.getInstance();

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
                final Set<String> nameSet = CollectionUtils.<String>jsonArrayToSet(names);

                if (nameSet.removeAll(keyNames)) {
                    for (final String unusedName : nameSet) {
                        article.remove(unusedName);
                    }

                    articleRepository.update(article.getString(Keys.OBJECT_ID), article);
                    LOGGER.log(Level.INFO, "Found an article[id={0}] exists unused properties[{1}]",
                        new Object[] {article.getString(Keys.OBJECT_ID), nameSet});
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            if (null != transaction && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Removes unused article properties failed, error msg[" + e.getMessage() + "]");
        }
    }

    /**
     * Restores the statistics.
     * 
     * <p>
     *   <ul>
     *     <li>Uses the value of {@link Statistic#STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT}
     *     for {@link Statistic#STATISTIC_BLOG_COMMENT_COUNT}</li>
     *     <li>Uses the value of {@link Statistic#STATISTIC_PUBLISHED_ARTICLE_COUNT}
     *     for {@link Statistic#STATISTIC_BLOG_ARTICLE_COUNT}</li>
     *   </ul>
     * </p>
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/fix/restore-stat.do", method = HTTPRequestMethod.GET)
    public void restoreStat(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();

        context.setRenderer(renderer);

        try {
            PageCaches.removeAll(); // Clears all first

            final JSONObject statistic = statisticQueryService.getStatistic();

            if (statistic.has(Statistic.STATISTIC_BLOG_COMMENT_COUNT) && statistic.has(Statistic.STATISTIC_BLOG_ARTICLE_COUNT)) {
                LOGGER.info("No need for repairing statistic");
                renderer.setContent("No need for repairing statistic.");

                return;
            }

            if (!statistic.has(Statistic.STATISTIC_BLOG_COMMENT_COUNT)) {
                statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT));
            }

            if (!statistic.has(Statistic.STATISTIC_BLOG_ARTICLE_COUNT)) {
                statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, statistic.getInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT));
            }

            statisticMgmtService.updateStatistic(statistic);

            renderer.setContent("Restores statistic succeeded.");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Restores statistics failed, error msg[" + e.getMessage() + "]");
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
            final String originalSigns = preference.getString(Preference.SIGNS);

            preference.put(Preference.SIGNS, Preference.Default.DEFAULT_SIGNS);

            PreferenceMgmtService.getInstance().updatePreference(preference);

            // Sends the sample signs to developer
            final Message msg = new MailService.Message();

            msg.setFrom(preference.getString(Preference.ADMIN_EMAIL));
            msg.addRecipient("DL88250@gmail.com");
            msg.setSubject("Restore signs");
            msg.setHtmlBody(originalSigns + "<p>Admin email: " + preference.getString(Preference.ADMIN_EMAIL) + "</p>");

            MAIL_SVC.send(msg);
            renderer.setContent("Restores signs succeeded.");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Restores signs failed, error msg[" + e.getMessage() + "]");
        }
    }

    /**
     * Repairs tag article counter.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/fix/tag-article-counter-repair.do", method = HTTPRequestMethod.GET)
    public void repairTagArticleCounter(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();

        context.setRenderer(renderer);

        final Transaction transaction = tagRepository.beginTransaction();

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
                    final boolean isPublished = article.getBoolean(Article.ARTICLE_IS_PUBLISHED);

                    if (isPublished) {
                        publishedTagRefCnt++;
                    }
                }

                tag.put(Tag.TAG_REFERENCE_COUNT, tagRefCnt);
                tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedTagRefCnt);

                tagRepository.update(tagId, tag);

                LOGGER.log(Level.INFO, "Repaired tag[title={0}, refCnt={1}, publishedTagRefCnt={2}]",
                    new Object[] {tag.getString(Tag.TAG_TITLE), tagRefCnt, publishedTagRefCnt});
            }

            transaction.commit();

            renderer.setContent("Repair sucessfully!");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Repairs failed, error msg[" + e.getMessage() + "]");
        }
    }

    /**
     * Shows remove all data page.
     * 
     * @param context the specified context
     * @param request the specified HTTP servlet request 
     */
    @RequestProcessing(value = "/rm-all-data.do", method = HTTPRequestMethod.GET)
    public void removeAllDataGET(final HTTPRequestContext context, final HttpServletRequest request) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();

        context.setRenderer(renderer);

        try {
            final StringBuilder htmlBuilder = new StringBuilder();

            htmlBuilder.append("<html><head><title>WARNING!</title>");
            htmlBuilder.append("<script type='text/javascript'");
            htmlBuilder.append("src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js'");
            htmlBuilder.append("></script></head><body>");
            htmlBuilder.append("<button id='ok' onclick='removeData()'>");
            htmlBuilder.append("Continue to delete ALL DATA</button></body>");
            htmlBuilder.append("<script type='text/javascript'>");
            htmlBuilder.append("function removeData() {");
            htmlBuilder.append("$.ajax({type: 'POST',url:'").append(Latkes.getContextPath()).append("/rm-all-data.do',");
            htmlBuilder.append("dataType: 'text/html',success: function(result){");
            htmlBuilder.append("$('html').html(result);}});}</script></html>");

            renderer.setContent(htmlBuilder.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Removes all data.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = { "/rm-all-data.do"}, method = HTTPRequestMethod.POST)
    public void removeAllDataPOST(final HTTPRequestContext context) {
        LOGGER.info("Removing all data....");

        PageCaches.removeAll();

        boolean succeed = false;

        try {
            remove(ArchiveDateArticleRepositoryImpl.getInstance());
            remove(ArchiveDateRepositoryImpl.getInstance());
            remove(ArticleRepositoryImpl.getInstance());
            remove(CommentRepositoryImpl.getInstance());
            remove(LinkRepositoryImpl.getInstance());
            remove(PageRepositoryImpl.getInstance());
            remove(PreferenceRepositoryImpl.getInstance());
            remove(StatisticRepositoryImpl.getInstance());
            remove(TagArticleRepositoryImpl.getInstance());
            remove(TagRepositoryImpl.getInstance());
            remove(UserRepositoryImpl.getInstance());
            remove(PluginRepositoryImpl.getInstance());

            succeed = true;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            LOGGER.log(Level.WARNING, "Removed partial data only");
        }

        final StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<html><head><title>Result</title></head><body>");

        try {
            final TextHTMLRenderer renderer = new TextHTMLRenderer();

            context.setRenderer(renderer);
            if (succeed) {
                htmlBuilder.append("Removed all data!");
            } else {
                htmlBuilder.append("Refresh this page and run this remover again.");
            }
            htmlBuilder.append("</body></html>");

            renderer.setContent(htmlBuilder.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        LOGGER.info("Removed all data....");
    }

    /**
     * Removes data in the specified repository.
     *
     * @param repository the specified repository
     * @throws ExecutionException execution exception
     * @throws InterruptedException interrupted exception
     */
    private void remove(final Repository repository) throws ExecutionException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        final long step = 20000;

        final Transaction transaction = repository.beginTransaction();

        try {
            final JSONObject result = repository.get(new Query());
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);

                repository.remove(object.getString(Keys.OBJECT_ID));

                if (System.currentTimeMillis() >= startTime + step) {
                    break;
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Removes all data in repository[name=" + repository.getName() + "] failed", e);
        }
    }
}
