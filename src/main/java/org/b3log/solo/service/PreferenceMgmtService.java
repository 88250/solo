/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Statics;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;

/**
 * Preference management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.4.0.10, Jul 5, 2020
 * @since 0.4.0
 */
@Service
public class PreferenceMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PreferenceMgmtService.class);

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Updates the preference with the specified preference.
     *
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void updatePreference(final JSONObject preference) throws ServiceException {
        final Iterator<String> keys = preference.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (preference.isNull(key)) {
                throw new ServiceException("A value is null of preference [key=" + key + "]");
            }
        }

        final Transaction transaction = optionRepository.beginTransaction();
        try {
            preference.put(Option.ID_C_SIGNS, preference.get(Option.ID_C_SIGNS).toString());

            final JSONObject oldPreference = optionQueryService.getPreference();

            final String version = oldPreference.optString(Option.ID_C_VERSION);
            preference.put(Option.ID_C_VERSION, version);

            final String localeString = preference.getString(Option.ID_C_LOCALE_STRING);
            Latkes.setLocale(new Locale(Locales.getLanguage(localeString), Locales.getCountry(localeString)));

            final JSONObject allowVisitDraftViaPermalinkOpt = optionRepository.get(Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
            allowVisitDraftViaPermalinkOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK));
            optionRepository.update(Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK, allowVisitDraftViaPermalinkOpt);

            final JSONObject articleListDisplayCountOpt = optionRepository.get(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            articleListDisplayCountOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT));
            optionRepository.update(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT, articleListDisplayCountOpt);

            final JSONObject articleListPaginationWindowSizeOpt = optionRepository.get(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            articleListPaginationWindowSizeOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
            optionRepository.update(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE, articleListPaginationWindowSizeOpt);

            final JSONObject articleListStyleOpt = optionRepository.get(Option.ID_C_ARTICLE_LIST_STYLE);
            articleListStyleOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_ARTICLE_LIST_STYLE));
            optionRepository.update(Option.ID_C_ARTICLE_LIST_STYLE, articleListStyleOpt);

            final JSONObject blogSubtitleOpt = optionRepository.get(Option.ID_C_BLOG_SUBTITLE);
            blogSubtitleOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_BLOG_SUBTITLE));
            optionRepository.update(Option.ID_C_BLOG_SUBTITLE, blogSubtitleOpt);

            final JSONObject blogTitleOpt = optionRepository.get(Option.ID_C_BLOG_TITLE);
            blogTitleOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_BLOG_TITLE));
            optionRepository.update(Option.ID_C_BLOG_TITLE, blogTitleOpt);

            final JSONObject enableArticleUpdateHintOpt = optionRepository.get(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT);
            enableArticleUpdateHintOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT));
            optionRepository.update(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT, enableArticleUpdateHintOpt);

            final JSONObject externalRelevantArticlesDisplayCountOpt = optionRepository.get(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);
            externalRelevantArticlesDisplayCountOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT));
            optionRepository.update(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT, externalRelevantArticlesDisplayCountOpt);

            final JSONObject feedOutputCntOpt = optionRepository.get(Option.ID_C_FEED_OUTPUT_CNT);
            feedOutputCntOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_FEED_OUTPUT_CNT));
            optionRepository.update(Option.ID_C_FEED_OUTPUT_CNT, feedOutputCntOpt);

            final JSONObject feedOutputModeOpt = optionRepository.get(Option.ID_C_FEED_OUTPUT_MODE);
            feedOutputModeOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_FEED_OUTPUT_MODE));
            optionRepository.update(Option.ID_C_FEED_OUTPUT_MODE, feedOutputModeOpt);

            final JSONObject footerContentOpt = optionRepository.get(Option.ID_C_FOOTER_CONTENT);
            footerContentOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_FOOTER_CONTENT));
            optionRepository.update(Option.ID_C_FOOTER_CONTENT, footerContentOpt);

            final JSONObject htmlHeadOpt = optionRepository.get(Option.ID_C_HTML_HEAD);
            htmlHeadOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_HTML_HEAD));
            optionRepository.update(Option.ID_C_HTML_HEAD, htmlHeadOpt);

            final JSONObject localeStringOpt = optionRepository.get(Option.ID_C_LOCALE_STRING);
            localeStringOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_LOCALE_STRING));
            optionRepository.update(Option.ID_C_LOCALE_STRING, localeStringOpt);

            final JSONObject metaDescriptionOpt = optionRepository.get(Option.ID_C_META_DESCRIPTION);
            metaDescriptionOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_META_DESCRIPTION));
            optionRepository.update(Option.ID_C_META_DESCRIPTION, metaDescriptionOpt);

            final JSONObject metaKeywordsOpt = optionRepository.get(Option.ID_C_META_KEYWORDS);
            metaKeywordsOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_META_KEYWORDS));
            optionRepository.update(Option.ID_C_META_KEYWORDS, metaKeywordsOpt);

            final JSONObject mostUsedTagDisplayCountOpt = optionRepository.get(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
            mostUsedTagDisplayCountOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT));
            optionRepository.update(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT, mostUsedTagDisplayCountOpt);

            final JSONObject noticeBoardOpt = optionRepository.get(Option.ID_C_NOTICE_BOARD);
            noticeBoardOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_NOTICE_BOARD));
            optionRepository.update(Option.ID_C_NOTICE_BOARD, noticeBoardOpt);

            final JSONObject randomArticlesDisplayCountOpt = optionRepository.get(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
            randomArticlesDisplayCountOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT));
            optionRepository.update(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT, randomArticlesDisplayCountOpt);

            final JSONObject recentArticleDisplayCountOpt = optionRepository.get(Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT);
            recentArticleDisplayCountOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT));
            optionRepository.update(Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT, recentArticleDisplayCountOpt);

            final JSONObject relevantArticlesDisplayCountOpt = optionRepository.get(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
            relevantArticlesDisplayCountOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT));
            optionRepository.update(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT, relevantArticlesDisplayCountOpt);

            final JSONObject signsOpt = optionRepository.get(Option.ID_C_SIGNS);
            signsOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_SIGNS));
            optionRepository.update(Option.ID_C_SIGNS, signsOpt);

            final JSONObject timeZoneIdOpt = optionRepository.get(Option.ID_C_TIME_ZONE_ID);
            timeZoneIdOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_TIME_ZONE_ID));
            optionRepository.update(Option.ID_C_TIME_ZONE_ID, timeZoneIdOpt);

            final JSONObject versionOpt = optionRepository.get(Option.ID_C_VERSION);
            versionOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_VERSION));
            optionRepository.update(Option.ID_C_VERSION, versionOpt);

            final JSONObject faviconURLOpt = optionRepository.get(Option.ID_C_FAVICON_URL);
            faviconURLOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_FAVICON_URL));
            optionRepository.update(Option.ID_C_FAVICON_URL, faviconURLOpt);

            final JSONObject syncGitHubOpt = optionRepository.get(Option.ID_C_SYNC_GITHUB);
            syncGitHubOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_SYNC_GITHUB));
            optionRepository.update(Option.ID_C_SYNC_GITHUB, syncGitHubOpt);

            final String githubPATVal = preference.optString(Option.ID_C_GITHUB_PAT);
            emptyPreferenceOptSave(Option.ID_C_GITHUB_PAT, githubPATVal);

            final JSONObject pullGitHubOpt = optionRepository.get(Option.ID_C_PULL_GITHUB);
            pullGitHubOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_PULL_GITHUB));
            optionRepository.update(Option.ID_C_PULL_GITHUB, pullGitHubOpt);

            final JSONObject hljsThemeOpt = optionRepository.get(Option.ID_C_HLJS_THEME);
            hljsThemeOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_HLJS_THEME));
            optionRepository.update(Option.ID_C_HLJS_THEME, hljsThemeOpt);

            final JSONObject customVarsOpt = optionRepository.get(Option.ID_C_CUSTOM_VARS);
            customVarsOpt.put(Option.OPTION_VALUE, preference.optString(Option.ID_C_CUSTOM_VARS));
            optionRepository.update(Option.ID_C_CUSTOM_VARS, customVarsOpt);

            final String editorModeVal = preference.optString(Option.ID_C_EDITOR_MODE);
            emptyPreferenceOptSave(Option.ID_C_EDITOR_MODE, editorModeVal);

            final String showCodeBlockLnVal = preference.optString(Option.ID_C_SHOW_CODE_BLOCK_LN);
            emptyPreferenceOptSave(Option.ID_C_SHOW_CODE_BLOCK_LN, showCodeBlockLnVal);
            final String footnotesVal = preference.optString(Option.ID_C_FOOTNOTES);
            emptyPreferenceOptSave(Option.ID_C_FOOTNOTES, footnotesVal);
            final String showToCVal = preference.optString(Option.ID_C_SHOW_TOC);
            emptyPreferenceOptSave(Option.ID_C_SHOW_TOC, showToCVal);
            final String autoSpaceVal = preference.optString(Option.ID_C_AUTO_SPACE);
            emptyPreferenceOptSave(Option.ID_C_AUTO_SPACE, autoSpaceVal);
            final String fixTermTypoVal = preference.optString(Option.ID_C_FIX_TERM_TYPO);
            emptyPreferenceOptSave(Option.ID_C_FIX_TERM_TYPO, fixTermTypoVal);
            final String chinesePunctVal = preference.optString(Option.ID_C_CHINESE_PUNCT);
            emptyPreferenceOptSave(Option.ID_C_CHINESE_PUNCT, chinesePunctVal);
            final String IMADAOMVal = preference.optString(Option.ID_C_IMADAOM);
            emptyPreferenceOptSave(Option.ID_C_IMADAOM, IMADAOMVal);
            final String paragraphBeginningSpaceVal = preference.optString(Option.ID_C_PARAGRAPH_BEGINNING_SPACE);
            emptyPreferenceOptSave(Option.ID_C_PARAGRAPH_BEGINNING_SPACE, paragraphBeginningSpaceVal);
            final String speechVal = preference.optString(Option.ID_C_SPEECH);
            emptyPreferenceOptSave(Option.ID_C_SPEECH, speechVal);
            Markdowns.loadMarkdownOption(preference);

            transaction.commit();

            Markdowns.clearCache();
            Statics.clear();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Updates preference failed", e);
            throw new ServiceException(langPropsService.get("updateFailLabel"));
        }

        LOGGER.log(Level.DEBUG, "Updates preference successfully");
    }

    private void emptyPreferenceOptSave(final String optID, final String val) throws Exception {
        // 该方法用于向后兼容，如果数据库中不存在该配置项则创建再保存

        JSONObject opt = optionRepository.get(optID);
        if (null == opt) {
            opt = new JSONObject();
            opt.put(Keys.OBJECT_ID, optID);
            opt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
            opt.put(Option.OPTION_VALUE, val);
            optionRepository.add(opt);
        } else {
            opt.put(Option.OPTION_VALUE, val);
            optionRepository.update(optID, opt);
        }
    }
}
