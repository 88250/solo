/*
 * Copyright (c) 2010-2015, b3log.org
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
package org.b3log.solo.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import javax.inject.Inject;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.util.Thumbnails;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Upgrade service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:dongxu.wang@acm.org">Dongxu Wang</a>
 * @version 1.1.0.3, Nov 23, 2015
 * @since 1.2.0
 */
@Service
public class UpgradeService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UpgradeService.class.getName());

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

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
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;

    /**
     * Step for article updating.
     */
    private static final int STEP = 50;

    /**
     * Preference Query Service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

    /**
     * Mail Service.
     */
    private static final MailService MAIL_SVC = MailServiceFactory.getMailService();

    /**
     * Whether the email has been sent.
     */
    private static boolean sent = false;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Old version.
     */
    private static final String FROM_VER = "1.1.0";

    /**
     * New version.
     */
    private static final String TO_VER = SoloServletListener.VERSION;

    /**
     * Upgrades if need.
     */
    public void upgrade() {
        try {
            final JSONObject preference = preferenceRepository.get(Option.CATEGORY_C_PREFERENCE);
            if (null == preference) {
                return;
            }

            final String currentVer = preference.getString(Option.ID_C_VERSION);

            if (SoloServletListener.VERSION.equals(currentVer)) {
                return;
            }

            if (FROM_VER.equals(currentVer)) {
                perform();

                return;
            }

            LOGGER.log(Level.WARN, "Attempt to skip more than one version to upgrade. Expected: {0}; Actually: {1}", FROM_VER, currentVer);

            if (!sent) {
                notifyUserByEmail();

                sent = true;
            }

        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            LOGGER.log(Level.ERROR,
                    "Upgrade failed [" + e.getMessage() + "], please contact the Solo developers or reports this "
                    + "issue directly (<a href='https://github.com/b3log/solo/issues/new'>"
                    + "https://github.com/b3log/solo/issues/new</a>) ");
        }
    }

    /**
     * Performs upgrade.
     *
     * @throws Exception upgrade fails
     */
    private void perform() throws Exception {
        LOGGER.log(Level.INFO, "Upgrading from version [{0}] to version [{1}]....", FROM_VER, TO_VER);

        Transaction transaction = null;

        try {
            final Connection connection = Connections.getConnection();
            final Statement statement = connection.createStatement();

            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            statement.execute("ALTER TABLE `" + tablePrefix + "user` ADD COLUMN `userAvatar` varchar(255)");
            statement.close();
            connection.commit();
            connection.close();

            transaction = userRepository.beginTransaction();

            upgradeUsers();

            // Upgrades preference model
            final JSONObject preference = preferenceRepository.get(Option.CATEGORY_C_PREFERENCE);

            final String adminEmail = preference.optString(Option.ID_C_ADMIN_EMAIL);
            final JSONObject adminEmailOpt = new JSONObject();
            adminEmailOpt.put(Keys.OBJECT_ID, Option.ID_C_ADMIN_EMAIL);
            adminEmailOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            adminEmailOpt.put(Option.OPTION_VALUE, adminEmail);
            optionRepository.add(adminEmailOpt);

            final boolean allowVisitDraftViaPermalink = preference.optBoolean(Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
            final JSONObject allowVisitDraftViaPermalinkOpt = new JSONObject();
            allowVisitDraftViaPermalinkOpt.put(Keys.OBJECT_ID, Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
            allowVisitDraftViaPermalinkOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            allowVisitDraftViaPermalinkOpt.put(Option.OPTION_VALUE, Boolean.toString(allowVisitDraftViaPermalink));
            optionRepository.add(allowVisitDraftViaPermalinkOpt);

            final boolean commentable = preference.optBoolean(Option.ID_C_COMMENTABLE);
            final JSONObject commentableOpt = new JSONObject();
            commentableOpt.put(Keys.OBJECT_ID, Option.ID_C_COMMENTABLE);
            commentableOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            commentableOpt.put(Option.OPTION_VALUE, Boolean.toString(commentable));
            optionRepository.add(commentableOpt);

            final String feedOutputMode = preference.optString(Option.ID_C_FEED_OUTPUT_MODE);
            final JSONObject feedOutputModeOpt = new JSONObject();
            feedOutputModeOpt.put(Keys.OBJECT_ID, Option.ID_C_FEED_OUTPUT_MODE);
            feedOutputModeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            feedOutputModeOpt.put(Option.OPTION_VALUE, feedOutputMode);
            optionRepository.add(feedOutputModeOpt);

            final int feedOutputCnt = preference.optInt(Option.ID_C_FEED_OUTPUT_CNT);
            final JSONObject feedOutputCntOpt = new JSONObject();
            feedOutputCntOpt.put(Keys.OBJECT_ID, Option.ID_C_FEED_OUTPUT_CNT);
            feedOutputCntOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            feedOutputCntOpt.put(Option.OPTION_VALUE, feedOutputCnt);
            optionRepository.add(feedOutputCntOpt);

            final int articleListDisplayCount = preference.optInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            final JSONObject articleListDisplayCountOpt = new JSONObject();
            articleListDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            articleListDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            articleListDisplayCountOpt.put(Option.OPTION_VALUE, articleListDisplayCount);
            optionRepository.add(articleListDisplayCountOpt);

            final int relevantArticlesDisplayCount = preference.optInt(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
            final JSONObject relevantArticlesDisplayCountOpt = new JSONObject();
            relevantArticlesDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
            relevantArticlesDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            relevantArticlesDisplayCountOpt.put(Option.OPTION_VALUE, relevantArticlesDisplayCount);
            optionRepository.add(relevantArticlesDisplayCountOpt);

            final int articleListPaginationWindowSize = preference.optInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            final JSONObject articleListPaginationWindowSizeOpt = new JSONObject();
            articleListPaginationWindowSizeOpt.put(Keys.OBJECT_ID, Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            articleListPaginationWindowSizeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            articleListPaginationWindowSizeOpt.put(Option.OPTION_VALUE, articleListPaginationWindowSize);
            optionRepository.add(articleListPaginationWindowSizeOpt);

            final String articleListStyle = preference.optString(Option.ID_C_ARTICLE_LIST_STYLE);
            final JSONObject articleListStyleOpt = new JSONObject();
            articleListStyleOpt.put(Keys.OBJECT_ID, Option.ID_C_ARTICLE_LIST_STYLE);
            articleListStyleOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            articleListStyleOpt.put(Option.OPTION_VALUE, articleListStyle);
            optionRepository.add(articleListStyleOpt);

            final String blogSubtitle = preference.optString(Option.ID_C_BLOG_SUBTITLE);
            final JSONObject blogSubtitleOpt = new JSONObject();
            blogSubtitleOpt.put(Keys.OBJECT_ID, Option.ID_C_BLOG_SUBTITLE);
            blogSubtitleOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            blogSubtitleOpt.put(Option.OPTION_VALUE, blogSubtitle);
            optionRepository.add(blogSubtitleOpt);

            final String blogTitle = preference.optString(Option.ID_C_BLOG_TITLE);
            final JSONObject blogTitleOpt = new JSONObject();
            blogTitleOpt.put(Keys.OBJECT_ID, Option.ID_C_BLOG_TITLE);
            blogTitleOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            blogTitleOpt.put(Option.OPTION_VALUE, blogTitle);
            optionRepository.add(blogTitleOpt);

            final boolean enableArticleUpdateHint = preference.optBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT);
            final JSONObject enableArticleUpdateHintOpt = new JSONObject();
            enableArticleUpdateHintOpt.put(Keys.OBJECT_ID, Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT);
            enableArticleUpdateHintOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            enableArticleUpdateHintOpt.put(Option.OPTION_VALUE, Boolean.toString(enableArticleUpdateHint));
            optionRepository.add(enableArticleUpdateHintOpt);

            final int externalRelevantArticlesDisplayCount = preference.optInt(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);
            final JSONObject externalRelevantArticlesDisplayCountOpt = new JSONObject();
            externalRelevantArticlesDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);
            externalRelevantArticlesDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            externalRelevantArticlesDisplayCountOpt.put(Option.OPTION_VALUE, externalRelevantArticlesDisplayCount);
            optionRepository.add(externalRelevantArticlesDisplayCountOpt);

            final String htmlHead = preference.optString(Option.ID_C_HTML_HEAD);
            final JSONObject htmlHeadOpt = new JSONObject();
            htmlHeadOpt.put(Keys.OBJECT_ID, Option.ID_C_HTML_HEAD);
            htmlHeadOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            htmlHeadOpt.put(Option.OPTION_VALUE, htmlHead);
            optionRepository.add(htmlHeadOpt);

            final String keyOfSolo = preference.optString(Option.ID_C_KEY_OF_SOLO);
            final JSONObject keyOfSoloOpt = new JSONObject();
            keyOfSoloOpt.put(Keys.OBJECT_ID, Option.ID_C_KEY_OF_SOLO);
            keyOfSoloOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            keyOfSoloOpt.put(Option.OPTION_VALUE, keyOfSolo);
            optionRepository.add(keyOfSoloOpt);

            final String localeString = preference.optString(Option.ID_C_LOCALE_STRING);
            final JSONObject localeStringOpt = new JSONObject();
            localeStringOpt.put(Keys.OBJECT_ID, Option.ID_C_LOCALE_STRING);
            localeStringOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            localeStringOpt.put(Option.OPTION_VALUE, localeString);
            optionRepository.add(localeStringOpt);

            final String metaDescription = preference.optString(Option.ID_C_META_DESCRIPTION);
            final JSONObject metaDescriptionOpt = new JSONObject();
            metaDescriptionOpt.put(Keys.OBJECT_ID, Option.ID_C_META_DESCRIPTION);
            metaDescriptionOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            metaDescriptionOpt.put(Option.OPTION_VALUE, metaDescription);
            optionRepository.add(metaDescriptionOpt);

            final String metaKeywords = preference.optString(Option.ID_C_META_KEYWORDS);
            final JSONObject metaKeywordsOpt = new JSONObject();
            metaKeywordsOpt.put(Keys.OBJECT_ID, Option.ID_C_META_KEYWORDS);
            metaKeywordsOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            metaKeywordsOpt.put(Option.OPTION_VALUE, metaKeywords);
            optionRepository.add(metaKeywordsOpt);

            final int mostCommentArticleDisplayCount = preference.optInt(Option.ID_C_MOST_COMMENT_ARTICLE_DISPLAY_CNT);
            final JSONObject mostCommentArticleDisplayCountOpt = new JSONObject();
            mostCommentArticleDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_MOST_COMMENT_ARTICLE_DISPLAY_CNT);
            mostCommentArticleDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            mostCommentArticleDisplayCountOpt.put(Option.OPTION_VALUE, mostCommentArticleDisplayCount);
            optionRepository.add(mostCommentArticleDisplayCountOpt);

            final int mostUsedTagDisplayCount = preference.optInt(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
            final JSONObject mostUsedTagDisplayCountOpt = new JSONObject();
            mostUsedTagDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
            mostUsedTagDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            mostUsedTagDisplayCountOpt.put(Option.OPTION_VALUE, mostUsedTagDisplayCount);
            optionRepository.add(mostUsedTagDisplayCountOpt);

            final int mostViewArticleDisplayCount = preference.optInt(Option.ID_C_MOST_VIEW_ARTICLE_DISPLAY_CNT);
            final JSONObject mostViewArticleDisplayCountOpt = new JSONObject();
            mostViewArticleDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_MOST_VIEW_ARTICLE_DISPLAY_CNT);
            mostViewArticleDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            mostViewArticleDisplayCountOpt.put(Option.OPTION_VALUE, mostViewArticleDisplayCount);
            optionRepository.add(mostViewArticleDisplayCountOpt);

            final String noticeBoard = preference.optString(Option.ID_C_NOTICE_BOARD);
            final JSONObject noticeBoardOpt = new JSONObject();
            noticeBoardOpt.put(Keys.OBJECT_ID, Option.ID_C_NOTICE_BOARD);
            noticeBoardOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            noticeBoardOpt.put(Option.OPTION_VALUE, noticeBoard);
            optionRepository.add(noticeBoardOpt);

            final int randomArticlesDisplayCount = preference.optInt(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
            final JSONObject randomArticlesDisplayCountOpt = new JSONObject();
            randomArticlesDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
            randomArticlesDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            randomArticlesDisplayCountOpt.put(Option.OPTION_VALUE, randomArticlesDisplayCount);
            optionRepository.add(randomArticlesDisplayCountOpt);

            final int recentCommentDisplayCount = preference.optInt(Option.ID_C_RECENT_COMMENT_DISPLAY_CNT);
            final JSONObject recentCommentDisplayCountOpt = new JSONObject();
            recentCommentDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RECENT_COMMENT_DISPLAY_CNT);
            recentCommentDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            recentCommentDisplayCountOpt.put(Option.OPTION_VALUE, recentCommentDisplayCount);
            optionRepository.add(recentCommentDisplayCountOpt);

            final int recentArticleDisplayCount = preference.optInt(Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT);
            final JSONObject recentArticleDisplayCountOpt = new JSONObject();
            recentArticleDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT);
            recentArticleDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            recentArticleDisplayCountOpt.put(Option.OPTION_VALUE, recentArticleDisplayCount);
            optionRepository.add(recentArticleDisplayCountOpt);

            final String signs = preference.optString(Option.ID_C_SIGNS);
            final JSONObject signsOpt = new JSONObject();
            signsOpt.put(Keys.OBJECT_ID, Option.ID_C_SIGNS);
            signsOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            signsOpt.put(Option.OPTION_VALUE, signs);
            optionRepository.add(signsOpt);

            final String skinDirName = preference.optString(Skin.SKIN_DIR_NAME);
            final JSONObject skinDirNameOpt = new JSONObject();
            skinDirNameOpt.put(Keys.OBJECT_ID, Option.ID_C_SKIN_DIR_NAME);
            skinDirNameOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            skinDirNameOpt.put(Option.OPTION_VALUE, skinDirName);
            optionRepository.add(skinDirNameOpt);

            final String skinName = preference.optString(Skin.SKIN_NAME);
            final JSONObject skinNameOpt = new JSONObject();
            skinNameOpt.put(Keys.OBJECT_ID, Option.ID_C_SKIN_NAME);
            skinNameOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            skinNameOpt.put(Option.OPTION_VALUE, skinName);
            optionRepository.add(skinNameOpt);

            final String skins = preference.optString(Skin.SKINS);
            final JSONObject skinsOpt = new JSONObject();
            skinsOpt.put(Keys.OBJECT_ID, Option.ID_C_SKINS);
            skinsOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            skinsOpt.put(Option.OPTION_VALUE, skins);
            optionRepository.add(skinsOpt);

            final String timeZoneId = preference.optString(Option.ID_C_TIME_ZONE_ID);
            final JSONObject timeZoneIdOpt = new JSONObject();
            timeZoneIdOpt.put(Keys.OBJECT_ID, Option.ID_C_TIME_ZONE_ID);
            timeZoneIdOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            timeZoneIdOpt.put(Option.OPTION_VALUE, timeZoneId);
            optionRepository.add(timeZoneIdOpt);

            final String editorType = preference.optString(Option.ID_C_EDITOR_TYPE);
            final JSONObject editorTypeOpt = new JSONObject();
            editorTypeOpt.put(Keys.OBJECT_ID, Option.ID_C_EDITOR_TYPE);
            editorTypeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            editorTypeOpt.put(Option.OPTION_VALUE, editorType);
            optionRepository.add(editorTypeOpt);

            final JSONObject footerContentOpt = new JSONObject();
            footerContentOpt.put(Keys.OBJECT_ID, Option.ID_C_FOOTER_CONTENT);
            footerContentOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            footerContentOpt.put(Option.OPTION_VALUE, Option.DefaultPreference.DEFAULT_FOOTER_CONTENT);
            optionRepository.add(footerContentOpt);

            final JSONObject replyNotificationTemplate = preferenceRepository.get("replyNotificationTemplate");

            final String body = replyNotificationTemplate.optString("body");
            final JSONObject bodyOpt = new JSONObject();
            bodyOpt.put(Keys.OBJECT_ID, Option.ID_C_REPLY_NOTI_TPL_BODY);
            bodyOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            bodyOpt.put(Option.OPTION_VALUE, body);
            optionRepository.add(bodyOpt);

            final String subject = replyNotificationTemplate.optString("subject");
            final JSONObject subjectOpt = new JSONObject();
            subjectOpt.put(Keys.OBJECT_ID, Option.ID_C_REPLY_NOTI_TPL_SUBJECT);
            subjectOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            subjectOpt.put(Option.OPTION_VALUE, subject);
            optionRepository.add(subjectOpt);

            final JSONObject versionOpt = new JSONObject();
            versionOpt.put(Keys.OBJECT_ID, Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            versionOpt.put(Option.OPTION_VALUE, TO_VER);
            optionRepository.add(versionOpt);

            final JSONObject allowRegisterOpt = new JSONObject();
            allowRegisterOpt.put(Keys.OBJECT_ID, Option.ID_C_ALLOW_REGISTER);
            allowRegisterOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            allowRegisterOpt.put(Option.OPTION_VALUE, Option.DefaultPreference.DEFAULT_ALLOW_REGISTER);
            optionRepository.add(allowRegisterOpt);

            preference.put(Option.ID_C_VERSION, TO_VER);
            preferenceRepository.update(Option.CATEGORY_C_PREFERENCE, preference);

            transaction.commit();

            LOGGER.log(Level.INFO, "Updated preference");
        } catch (final Exception e) {
            if (null != transaction && transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Upgrade failed!", e);
            throw new Exception("Upgrade failed from version [" + FROM_VER + "] to version [" + TO_VER + ']');
        }

        LOGGER.log(Level.INFO, "Upgraded from version [{0}] to version [{1}] successfully :-)", FROM_VER, TO_VER);
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
            final String email = user.optString(User.USER_EMAIL);

            user.put(UserExt.USER_AVATAR, Thumbnails.getGravatarURL(email, "128"));

            userRepository.update(user.optString(Keys.OBJECT_ID), user);

            LOGGER.log(Level.INFO, "Updated user[email={0}]", email);
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
            LOGGER.log(Level.TRACE, "No articles");
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
                    LOGGER.log(Level.TRACE, "Updated some articles");
                }
            }

            if (transaction.isActive()) {
                transaction.commit();
            }

            LOGGER.log(Level.TRACE, "Updated all articles");
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
     * @throws ServiceException ServiceException
     * @throws JSONException JSONException
     * @throws IOException IOException
     */
    private void notifyUserByEmail() throws ServiceException, JSONException, IOException {
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
