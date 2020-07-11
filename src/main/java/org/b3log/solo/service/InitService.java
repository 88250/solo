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

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories.CreateTableResult;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Ids;
import org.b3log.solo.Server;
import org.b3log.solo.model.*;
import org.b3log.solo.model.Option.DefaultPreference;
import org.b3log.solo.repository.*;
import org.b3log.solo.util.Images;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.text.ParseException;
import java.util.List;

/**
 * Solo initialization service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.5.2.48, Jul 11, 2020
 * @since 0.4.0
 */
@Service
public class InitService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(InitService.class);

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;

    /**
     * Archive date-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Plugin manager.
     */
    @Inject
    private PluginManager pluginManager;

    /**
     * Flag of init status.
     */
    public static boolean inited;

    /**
     * Flag of printed init prompt.
     */
    private static boolean printedInitMsg;

    /**
     * Determines Solo had been initialized.
     *
     * @return {@code true} if it had been initialized, {@code false} otherwise
     */
    public boolean isInited() {
        if (inited) {
            return true;
        }

        try {
            inited = null != optionRepository.get(Option.ID_C_VERSION);
            if (!inited && !printedInitMsg) {
                LOGGER.log(Level.WARN, "Solo has not been initialized, please open your browser to init Solo");
                printedInitMsg = true;
            }
            return inited;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Check init failed", e);
            System.exit(-1);
            return false;
        }
    }

    /**
     * Initializes database tables.
     */
    public void initTables() {
        try (final Connection chk = Connections.getConnection()) {
            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            final boolean userTableExist = JdbcRepositories.existTable(tablePrefix + User.USER);
            final boolean optionTableExist = JdbcRepositories.existTable(tablePrefix + Option.OPTION);
            if (userTableExist && optionTableExist) {
                return;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Check tables failed, please make sure database existed and database configuration [jdbc.*] in local.props is correct [msg=" + e.getMessage() + "]");
            System.exit(-1);
        }

        LOGGER.info("It's your first time setup Solo, initialize tables in database [" + Latkes.getRuntimeDatabase() + "]");

        if (Latkes.RuntimeDatabase.H2 == Latkes.getRuntimeDatabase()) {
            String dataDir = Latkes.getLocalProperty("jdbc.URL");
            dataDir = dataDir.replace("~", System.getProperty("user.home"));
            LOGGER.log(Level.INFO, "Your DATA will be stored in directory [" + dataDir + "], "
                    + "please pay more attention on it!");
        }

        final List<CreateTableResult> createTableResults = JdbcRepositories.initAllTables();
        for (final CreateTableResult createTableResult : createTableResults) {
            LOGGER.log(Level.DEBUG, "Creates table result [tableName={}, isSuccess={}]",
                    createTableResult.getName(), createTableResult.isSuccess());
        }
    }

    /**
     * Initializes Solo.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "userName": "",
     *                          "userAvatar": "", // optional
     *                          "userB3Key": "", // optional
     *                          "userGitHubId": "" // optional
     *                          }
     */
    public void init(final JSONObject requestJSONObject) {
        if (isInited()) {
            return;
        }

        final Transaction transaction = userRepository.beginTransaction();
        try {
            initStatistic();
            initOptions(requestJSONObject);
            initAdmin(requestJSONObject);
            helloWorld();

            transaction.commit();
        } catch (final Throwable e) {
            LOGGER.log(Level.ERROR, "Initializes Solo failed", e);
            System.exit(-1);
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        pluginManager.load();
    }

    /**
     * Publishes the first article "Hello World".
     *
     * @throws Exception exception
     */
    private void helloWorld() throws Exception {
        final JSONObject article = new JSONObject();

        article.put(Article.ARTICLE_TITLE, langPropsService.get("helloWorld.title"));
        final String content = "![](" + Images.imageSize(Images.randImage(), Article.ARTICLE_THUMB_IMG_WIDTH, Article.ARTICLE_THUMB_IMG_HEIGHT) + ") \n\n" +
                langPropsService.get("helloWorld.content");

        article.put(Article.ARTICLE_ABSTRACT_TEXT, Article.getAbstractText(content));
        article.put(Article.ARTICLE_ABSTRACT, content);
        article.put(Article.ARTICLE_CONTENT, content);
        article.put(Article.ARTICLE_TAGS_REF, "Solo");
        article.put(Article.ARTICLE_PERMALINK, "/hello-solo");
        article.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        final JSONObject admin = userRepository.getAdmin();
        final long now = System.currentTimeMillis();
        article.put(Article.ARTICLE_CREATED, now);
        article.put(Article.ARTICLE_UPDATED, now);
        article.put(Article.ARTICLE_PUT_TOP, false);
        article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());
        article.put(Article.ARTICLE_AUTHOR_ID, admin.optString(Keys.OBJECT_ID));
        article.put(Article.ARTICLE_VIEW_PWD, "");
        final String articleImg1URL = Article.getArticleImg1URL(article);
        article.put(Article.ARTICLE_IMG1_URL, articleImg1URL);
        addHelloWorldArticle(article);
    }

    /**
     * Adds the specified "Hello World" article.
     *
     * @param article the specified "Hello World" article
     * @return generated article id
     * @throws RepositoryException repository exception
     */
    private String addHelloWorldArticle(final JSONObject article) throws RepositoryException {
        final String ret = Ids.genTimeMillisId();

        try {
            article.put(Keys.OBJECT_ID, ret);

            final String tagsString = article.optString(Article.ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tag(tagTitles, article);
            addTagArticleRelation(tags, article);
            archiveDate(article);
            articleRepository.add(article);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Adds an article failed", e);
            throw new RepositoryException(e);
        }
        return ret;
    }

    /**
     * Archive the create date with the specified article.
     *
     * @param article the specified article, for example,
     *                {
     *                ....,
     *                "oId": "",
     *                "articleCreateDate": java.util.Date,
     *                ....
     *                }
     * @throws RepositoryException repository exception
     */
    public void archiveDate(final JSONObject article) throws RepositoryException {
        final long created = article.optLong(Article.ARTICLE_CREATED);
        final String createDateString = DateFormatUtils.format(created, "yyyy/MM");
        final JSONObject archiveDate = new JSONObject();

        try {
            archiveDate.put(ArchiveDate.ARCHIVE_TIME, DateUtils.parseDate(createDateString, new String[]{"yyyy/MM"}).getTime());
            archiveDateRepository.add(archiveDate);
        } catch (final ParseException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        final JSONObject archiveDateArticleRelation = new JSONObject();
        archiveDateArticleRelation.put(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID, archiveDate.optString(Keys.OBJECT_ID));
        archiveDateArticleRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID, article.optString(Keys.OBJECT_ID));
        archiveDateArticleRepository.add(archiveDateArticleRelation);
    }

    /**
     * Adds relation of the specified tags and article.
     *
     * @param tags    the specified tags
     * @param article the specified article
     * @throws RepositoryException repository exception
     */
    private void addTagArticleRelation(final JSONArray tags, final JSONObject article) throws RepositoryException {
        for (int i = 0; i < tags.length(); i++) {
            final JSONObject tag = tags.optJSONObject(i);
            final JSONObject tagArticleRelation = new JSONObject();
            tagArticleRelation.put(Tag.TAG + "_" + Keys.OBJECT_ID, tag.optString(Keys.OBJECT_ID));
            tagArticleRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID, article.optString(Keys.OBJECT_ID));
            tagArticleRepository.add(tagArticleRelation);
        }
    }

    /**
     * Tags the specified article with the specified tag titles.
     *
     * @param tagTitles the specified tag titles
     * @param article   the specified article
     * @return an array of tags
     * @throws RepositoryException repository exception
     */
    private JSONArray tag(final String[] tagTitles, final JSONObject article) throws RepositoryException {
        final JSONArray ret = new JSONArray();

        for (String tagTitle1 : tagTitles) {
            final String tagTitle = tagTitle1.trim();
            final JSONObject tag = new JSONObject();
            LOGGER.log(Level.TRACE, "Found a new tag[title={}] in article[title={}]", tagTitle, article.optString(Article.ARTICLE_TITLE));
            tag.put(Tag.TAG_TITLE, tagTitle);
            final String tagId = tagRepository.add(tag);
            tag.put(Keys.OBJECT_ID, tagId);
            ret.put(tag);
        }
        return ret;
    }

    /**
     * Initializes administrator with the specified request json object, and then logins it.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "userName": "",
     *                          "userAvatar": "", // optional
     *                          "userB3Key": "", // optional
     *                          "userGitHubId": "" // optional
     *                          }
     * @throws Exception exception
     */
    private void initAdmin(final JSONObject requestJSONObject) throws Exception {
        LOGGER.debug("Initializing admin....");
        final JSONObject admin = new JSONObject();
        admin.put(User.USER_NAME, requestJSONObject.getString(User.USER_NAME));
        admin.put(User.USER_URL, Latkes.getServePath());
        admin.put(User.USER_ROLE, Role.ADMIN_ROLE);
        admin.put(UserExt.USER_AVATAR, requestJSONObject.optString(UserExt.USER_AVATAR));
        admin.put(UserExt.USER_B3_KEY, requestJSONObject.optString(UserExt.USER_B3_KEY));
        admin.put(UserExt.USER_GITHUB_ID, requestJSONObject.optString(UserExt.USER_GITHUB_ID));
        userRepository.add(admin);

        LOGGER.debug("Initialized admin");
    }

    /**
     * Initializes statistic.
     *
     * @throws RepositoryException repository exception
     * @throws JSONException       json exception
     */
    private void initStatistic() throws RepositoryException, JSONException {
        LOGGER.debug("Initializing statistic....");
        final JSONObject statisticBlogViewCountOpt = new JSONObject();
        statisticBlogViewCountOpt.put(Keys.OBJECT_ID, Option.ID_C_STATISTIC_BLOG_VIEW_COUNT);
        statisticBlogViewCountOpt.put(Option.OPTION_VALUE, "0");
        statisticBlogViewCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_STATISTIC);
        optionRepository.add(statisticBlogViewCountOpt);
        LOGGER.debug("Initialized statistic");
    }

    /**
     * Initializes options.
     *
     * @param requestJSONObject the specified json object
     * @throws Exception exception
     */
    private void initOptions(final JSONObject requestJSONObject) throws Exception {
        LOGGER.debug("Initializing preference....");

        final JSONObject speechOpt = new JSONObject();
        speechOpt.put(Keys.OBJECT_ID, Option.ID_C_SPEECH);
        speechOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        speechOpt.put(Option.OPTION_VALUE, "true");
        optionRepository.add(speechOpt);

        final JSONObject paragraphBeginningSpaceOpt = new JSONObject();
        paragraphBeginningSpaceOpt.put(Keys.OBJECT_ID, Option.ID_C_PARAGRAPH_BEGINNING_SPACE);
        paragraphBeginningSpaceOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        paragraphBeginningSpaceOpt.put(Option.OPTION_VALUE, "false");
        optionRepository.add(paragraphBeginningSpaceOpt);

        final JSONObject editorModeOpt = new JSONObject();
        editorModeOpt.put(Keys.OBJECT_ID, Option.ID_C_EDITOR_MODE);
        editorModeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        editorModeOpt.put(Option.OPTION_VALUE, "ir");
        optionRepository.add(editorModeOpt);

        final JSONObject IMADAOMOpt = new JSONObject();
        IMADAOMOpt.put(Keys.OBJECT_ID, Option.ID_C_IMADAOM);
        IMADAOMOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        IMADAOMOpt.put(Option.OPTION_VALUE, "false");
        optionRepository.add(IMADAOMOpt);

        final JSONObject chinesePunctOpt = new JSONObject();
        chinesePunctOpt.put(Keys.OBJECT_ID, Option.ID_C_CHINESE_PUNCT);
        chinesePunctOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        chinesePunctOpt.put(Option.OPTION_VALUE, "true");
        optionRepository.add(chinesePunctOpt);

        final JSONObject fixTermTypoOpt = new JSONObject();
        fixTermTypoOpt.put(Keys.OBJECT_ID, Option.ID_C_FIX_TERM_TYPO);
        fixTermTypoOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        fixTermTypoOpt.put(Option.OPTION_VALUE, "true");
        optionRepository.add(fixTermTypoOpt);

        final JSONObject autoSpaceOpt = new JSONObject();
        autoSpaceOpt.put(Keys.OBJECT_ID, Option.ID_C_AUTO_SPACE);
        autoSpaceOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        autoSpaceOpt.put(Option.OPTION_VALUE, "true");
        optionRepository.add(autoSpaceOpt);

        final JSONObject showToCOpt = new JSONObject();
        showToCOpt.put(Keys.OBJECT_ID, Option.ID_C_SHOW_TOC);
        showToCOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        showToCOpt.put(Option.OPTION_VALUE, "false");
        optionRepository.add(showToCOpt);

        final JSONObject footnotesOpt = new JSONObject();
        footnotesOpt.put(Keys.OBJECT_ID, Option.ID_C_FOOTNOTES);
        footnotesOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        footnotesOpt.put(Option.OPTION_VALUE, "true");
        optionRepository.add(footnotesOpt);

        final JSONObject showCodeBlockLnOpt = new JSONObject();
        showCodeBlockLnOpt.put(Keys.OBJECT_ID, Option.ID_C_SHOW_CODE_BLOCK_LN);
        showCodeBlockLnOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        showCodeBlockLnOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_SHOW_CODE_BLOCK_LN);
        optionRepository.add(showCodeBlockLnOpt);

        final JSONObject hljsThemeOpt = new JSONObject();
        hljsThemeOpt.put(Keys.OBJECT_ID, Option.ID_C_HLJS_THEME);
        hljsThemeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        hljsThemeOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_HLJS_THEME);
        optionRepository.add(hljsThemeOpt);

        final JSONObject syncGitHubOpt = new JSONObject();
        syncGitHubOpt.put(Keys.OBJECT_ID, Option.ID_C_SYNC_GITHUB);
        syncGitHubOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        syncGitHubOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_SYNC_GITHUB);
        optionRepository.add(syncGitHubOpt);

        final JSONObject githubPATOpt = new JSONObject();
        githubPATOpt.put(Keys.OBJECT_ID, Option.ID_C_GITHUB_PAT);
        githubPATOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        githubPATOpt.put(Option.OPTION_VALUE, "");
        optionRepository.add(githubPATOpt);

        final JSONObject pullGitHubOpt = new JSONObject();
        pullGitHubOpt.put(Keys.OBJECT_ID, Option.ID_C_PULL_GITHUB);
        pullGitHubOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        pullGitHubOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_PULL_GITHUB);
        optionRepository.add(pullGitHubOpt);

        final JSONObject faviconURLOpt = new JSONObject();
        faviconURLOpt.put(Keys.OBJECT_ID, Option.ID_C_FAVICON_URL);
        faviconURLOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        faviconURLOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_FAVICON_URL);
        optionRepository.add(faviconURLOpt);

        final JSONObject customVarsOpt = new JSONObject();
        customVarsOpt.put(Keys.OBJECT_ID, Option.ID_C_CUSTOM_VARS);
        customVarsOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        customVarsOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_CUSTOM_VARS);
        optionRepository.add(customVarsOpt);

        final JSONObject noticeBoardOpt = new JSONObject();
        noticeBoardOpt.put(Keys.OBJECT_ID, Option.ID_C_NOTICE_BOARD);
        noticeBoardOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        noticeBoardOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_NOTICE_BOARD);
        optionRepository.add(noticeBoardOpt);

        final JSONObject metaDescriptionOpt = new JSONObject();
        metaDescriptionOpt.put(Keys.OBJECT_ID, Option.ID_C_META_DESCRIPTION);
        metaDescriptionOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        metaDescriptionOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_META_DESCRIPTION);
        optionRepository.add(metaDescriptionOpt);

        final JSONObject metaKeywordsOpt = new JSONObject();
        metaKeywordsOpt.put(Keys.OBJECT_ID, Option.ID_C_META_KEYWORDS);
        metaKeywordsOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        metaKeywordsOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_META_KEYWORDS);
        optionRepository.add(metaKeywordsOpt);

        final JSONObject htmlHeadOpt = new JSONObject();
        htmlHeadOpt.put(Keys.OBJECT_ID, Option.ID_C_HTML_HEAD);
        htmlHeadOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        htmlHeadOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_HTML_HEAD);
        optionRepository.add(htmlHeadOpt);

        final JSONObject relevantArticlesDisplayCountOpt = new JSONObject();
        relevantArticlesDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
        relevantArticlesDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        relevantArticlesDisplayCountOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT);
        optionRepository.add(relevantArticlesDisplayCountOpt);

        final JSONObject randomArticlesDisplayCountOpt = new JSONObject();
        randomArticlesDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
        randomArticlesDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        randomArticlesDisplayCountOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT);
        optionRepository.add(randomArticlesDisplayCountOpt);

        final JSONObject externalRelevantArticlesDisplayCountOpt = new JSONObject();
        externalRelevantArticlesDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT);
        externalRelevantArticlesDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        externalRelevantArticlesDisplayCountOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT);
        optionRepository.add(externalRelevantArticlesDisplayCountOpt);

        final JSONObject articleListDisplayCountOpt = new JSONObject();
        articleListDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
        articleListDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        articleListDisplayCountOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_ARTICLE_LIST_DISPLAY_COUNT);
        optionRepository.add(articleListDisplayCountOpt);

        final JSONObject articleListPaginationWindowSizeOpt = new JSONObject();
        articleListPaginationWindowSizeOpt.put(Keys.OBJECT_ID, Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        articleListPaginationWindowSizeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        articleListPaginationWindowSizeOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        optionRepository.add(articleListPaginationWindowSizeOpt);

        final JSONObject mostUsedTagDisplayCountOpt = new JSONObject();
        mostUsedTagDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
        mostUsedTagDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        mostUsedTagDisplayCountOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_MOST_USED_TAG_DISPLAY_COUNT);
        optionRepository.add(mostUsedTagDisplayCountOpt);

        final JSONObject recentArticleDisplayCountOpt = new JSONObject();
        recentArticleDisplayCountOpt.put(Keys.OBJECT_ID, Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT);
        recentArticleDisplayCountOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        recentArticleDisplayCountOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT);
        optionRepository.add(recentArticleDisplayCountOpt);

        final JSONObject blogTitleOpt = new JSONObject();
        blogTitleOpt.put(Keys.OBJECT_ID, Option.ID_C_BLOG_TITLE);
        blogTitleOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        blogTitleOpt.put(Option.OPTION_VALUE, requestJSONObject.optString(User.USER_NAME) + " 的个人博客");
        optionRepository.add(blogTitleOpt);

        final JSONObject blogSubtitleOpt = new JSONObject();
        blogSubtitleOpt.put(Keys.OBJECT_ID, Option.ID_C_BLOG_SUBTITLE);
        blogSubtitleOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        blogSubtitleOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_BLOG_SUBTITLE);
        optionRepository.add(blogSubtitleOpt);

        final JSONObject localeStringOpt = new JSONObject();
        localeStringOpt.put(Keys.OBJECT_ID, Option.ID_C_LOCALE_STRING);
        localeStringOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        localeStringOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_LANGUAGE);
        optionRepository.add(localeStringOpt);

        final JSONObject enableArticleUpdateHintOpt = new JSONObject();
        enableArticleUpdateHintOpt.put(Keys.OBJECT_ID, Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT);
        enableArticleUpdateHintOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        enableArticleUpdateHintOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_ENABLE_ARTICLE_UPDATE_HINT);
        optionRepository.add(enableArticleUpdateHintOpt);

        final JSONObject signsOpt = new JSONObject();
        signsOpt.put(Keys.OBJECT_ID, Option.ID_C_SIGNS);
        signsOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        signsOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_SIGNS);
        optionRepository.add(signsOpt);

        final JSONObject timeZoneIdOpt = new JSONObject();
        timeZoneIdOpt.put(Keys.OBJECT_ID, Option.ID_C_TIME_ZONE_ID);
        timeZoneIdOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        timeZoneIdOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_TIME_ZONE);
        optionRepository.add(timeZoneIdOpt);

        final JSONObject allowVisitDraftViaPermalinkOpt = new JSONObject();
        allowVisitDraftViaPermalinkOpt.put(Keys.OBJECT_ID, Option.ID_C_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
        allowVisitDraftViaPermalinkOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        allowVisitDraftViaPermalinkOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
        optionRepository.add(allowVisitDraftViaPermalinkOpt);

        final JSONObject versionOpt = new JSONObject();
        versionOpt.put(Keys.OBJECT_ID, Option.ID_C_VERSION);
        versionOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        versionOpt.put(Option.OPTION_VALUE, Server.VERSION);
        optionRepository.add(versionOpt);

        final JSONObject articleListStyleOpt = new JSONObject();
        articleListStyleOpt.put(Keys.OBJECT_ID, Option.ID_C_ARTICLE_LIST_STYLE);
        articleListStyleOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        articleListStyleOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_ARTICLE_LIST_STYLE);
        optionRepository.add(articleListStyleOpt);

        final JSONObject feedOutputModeOpt = new JSONObject();
        feedOutputModeOpt.put(Keys.OBJECT_ID, Option.ID_C_FEED_OUTPUT_MODE);
        feedOutputModeOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        feedOutputModeOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_FEED_OUTPUT_MODE);
        optionRepository.add(feedOutputModeOpt);

        final JSONObject feedOutputCntOpt = new JSONObject();
        feedOutputCntOpt.put(Keys.OBJECT_ID, Option.ID_C_FEED_OUTPUT_CNT);
        feedOutputCntOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        feedOutputCntOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_FEED_OUTPUT_CNT);
        optionRepository.add(feedOutputCntOpt);

        final JSONObject footerContentOpt = new JSONObject();
        footerContentOpt.put(Keys.OBJECT_ID, Option.ID_C_FOOTER_CONTENT);
        footerContentOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_PREFERENCE);
        footerContentOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_FOOTER_CONTENT);
        optionRepository.add(footerContentOpt);

        final JSONObject skinDirNameOpt = new JSONObject();
        skinDirNameOpt.put(Keys.OBJECT_ID, Option.ID_C_SKIN_DIR_NAME);
        skinDirNameOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_SKIN);
        skinDirNameOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        optionRepository.add(skinDirNameOpt);

        final JSONObject mobileSkinDirNameOpt = new JSONObject();
        mobileSkinDirNameOpt.put(Keys.OBJECT_ID, Option.ID_C_MOBILE_SKIN_DIR_NAME);
        mobileSkinDirNameOpt.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_SKIN);
        mobileSkinDirNameOpt.put(Option.OPTION_VALUE, DefaultPreference.DEFAULT_MOBILE_SKIN_DIR_NAME);
        optionRepository.add(mobileSkinDirNameOpt);

        LOGGER.debug("Initialized preference");
    }
}
