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
package org.b3log.solo.service;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories.CreateTableResult;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.model.Preference.Default;
import org.b3log.solo.repository.*;
import org.b3log.solo.util.Comments;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Thumbnails;
import org.b3log.solo.util.TimeZones;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * B3log Solo initialization service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.7, Jun 28, 2013
 * @since 0.4.0
 */
@Service
public class InitService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InitService.class.getName());

    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;

    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;

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
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;

    /**
     * Maximum count of initialization.
     */
    private static final int MAX_RETRIES_CNT = 3;

    /**
     * Initialized time zone id.
     */
    private static final String INIT_TIME_ZONE_ID = "Asia/Shanghai";

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
     * Determines Solo had been initialized.
     *
     * @return {@code true} if it had been initialized, {@code false} otherwise
     */
    // XXX: to find a better way (isInited)?
    public boolean isInited() {
        try {
            final JSONObject admin = userRepository.getAdmin();

            return null != admin;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.WARN, "B3log Solo has not been initialized");
            return false;
        }
    }

    /**
     * Initializes B3log Solo.
     * 
     * <p>
     * Initializes the followings in sequence:
     *   <ol>
     *     <li>Statistic.</li>
     *     <li>Preference.</li>
     *     <li>Administrator.</li>
     *   </ol>
     * </p>
     * 
     * <p>
     *   We will try to initialize B3log Solo 3 times at most.
     * </p>
     * 
     * <p>
     *   Posts "Hello World!" article and its comment while B3log Solo initialized.
     * </p>
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassword": "", // Unhashed
     * }
     * </pre>
     * @throws ServiceException service exception
     */
    public void init(final JSONObject requestJSONObject) throws ServiceException {
        if (isInited()) {
            return;
        }

        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();

        if (RuntimeEnv.LOCAL == runtimeEnv || RuntimeEnv.BAE == runtimeEnv) {
            LOGGER.log(Level.INFO, "B3log Solo is running on [" + runtimeEnv + "] environment, database [{0}], creates all tables",
                Latkes.getRuntimeDatabase());
            final List<CreateTableResult> createTableResults = JdbcRepositories.initAllTables();

            for (final CreateTableResult createTableResult : createTableResults) {
                LOGGER.log(Level.INFO, "Create table result[tableName={0}, isSuccess={1}]",
                    new Object[] {createTableResult.getName(), createTableResult.isSuccess()});
            }
        }

        int retries = MAX_RETRIES_CNT;

        while (true) {
            final Transaction transaction = userRepository.beginTransaction();

            try {
                final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

                if (null == statistic) {
                    initStatistic();
                    initPreference(requestJSONObject);
                    initReplyNotificationTemplate();
                    initAdmin(requestJSONObject);
                }

                transaction.commit();
                break;
            } catch (final Exception e) {
                if (0 == retries) {
                    LOGGER.log(Level.ERROR, "Initialize B3log Solo error", e);
                    throw new ServiceException("Initailize B3log Solo error: " + e.getMessage());
                }

                // Allow retry to occur
                --retries;
                LOGGER.log(Level.WARN, "Retrying to init B3log Solo[retries={0}]", retries);
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }

        final Transaction transaction = userRepository.beginTransaction();

        try {
            helloWorld();
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Hello World error?!", e);
        }

        pluginManager.load();
    }

    /**
     * Publishes the first article "Hello World" and the first comment with the specified locale.
     *
     * @throws Exception exception
     */
    private void helloWorld() throws Exception {
        final JSONObject article = new JSONObject();

        article.put(Article.ARTICLE_TITLE, langPropsService.get("helloWorld.title"));
        final String content = langPropsService.get("helloWorld.content");

        article.put(Article.ARTICLE_ABSTRACT, content);
        article.put(Article.ARTICLE_CONTENT, content);
        article.put(Article.ARTICLE_TAGS_REF, "B3log");
        article.put(Article.ARTICLE_PERMALINK, "/b3log-hello-wolrd.html");
        article.put(Article.ARTICLE_IS_PUBLISHED, true);
        article.put(Article.ARTICLE_HAD_BEEN_PUBLISHED, true);
        article.put(Article.ARTICLE_SIGN_ID, "1");
        article.put(Article.ARTICLE_COMMENT_COUNT, 1);
        article.put(Article.ARTICLE_VIEW_COUNT, 0);
        final Date date = new Date();

        article.put(Article.ARTICLE_CREATE_DATE, date);
        article.put(Article.ARTICLE_UPDATE_DATE, date);
        article.put(Article.ARTICLE_PUT_TOP, false);
        article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());
        article.put(Article.ARTICLE_AUTHOR_EMAIL, preferenceRepository.get(Preference.PREFERENCE).optString(Preference.ADMIN_EMAIL));
        article.put(Article.ARTICLE_COMMENTABLE, true);
        article.put(Article.ARTICLE_VIEW_PWD, "");
        article.put(Article.ARTICLE_EDITOR_TYPE, Default.DEFAULT_EDITOR_TYPE);

        final String articleId = addHelloWorldArticle(article);

        final JSONObject comment = new JSONObject();

        comment.put(Keys.OBJECT_ID, articleId);
        comment.put(Comment.COMMENT_NAME, "88250");
        comment.put(Comment.COMMENT_EMAIL, "dl88250@gmail.com");
        comment.put(Comment.COMMENT_URL, "http://88250.b3log.org");
        comment.put(Comment.COMMENT_CONTENT, StringEscapeUtils.escapeHtml(langPropsService.get("helloWorld.comment.content")));
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
        comment.put(Comment.COMMENT_THUMBNAIL_URL, Thumbnails.GRAVATAR + "59a5e8209c780307dbe9c9ba728073f5??s=60&r=G");
        comment.put(Comment.COMMENT_DATE, date);
        comment.put(Comment.COMMENT_ON_ID, articleId);
        comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);
        final String commentId = Ids.genTimeMillisId();

        comment.put(Keys.OBJECT_ID, commentId);
        final String commentSharpURL = Comments.getCommentSharpURLForArticle(article, commentId);

        comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

        commentRepository.add(comment);

        LOGGER.info("Hello World!");
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

            // Step 1: Add tags
            final String tagsString = article.optString(Article.ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tag(tagTitles, article);

            // Step 2: Add tag-article relations
            addTagArticleRelation(tags, article);
            // Step 3: Inc blog article and comment count statictis
            final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);

            statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, 1);
            statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, 1);
            statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, 1);
            statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, 1);
            statisticRepository.update(Statistic.STATISTIC, statistic);
            // Step 4: Add archive date-article relations
            archiveDate(article);
            // Step 5: Add article
            articleRepository.add(article);
            // Step 6: Update admin user for article statistic
            final JSONObject admin = userRepository.getAdmin();

            admin.put(UserExt.USER_ARTICLE_COUNT, 1);
            admin.put(UserExt.USER_PUBLISHED_ARTICLE_COUNT, 1);
            userRepository.update(admin.optString(Keys.OBJECT_ID), admin);
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
     * <pre>
     * {
     *     ....,
     *     "oId": "",
     *     "articleCreateDate": java.util.Date,
     *     ....
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    public void archiveDate(final JSONObject article) throws RepositoryException {
        final Date createDate = (Date) article.opt(Article.ARTICLE_CREATE_DATE);
        final String createDateString = DateFormatUtils.format(createDate, "yyyy/MM");
        final JSONObject archiveDate = new JSONObject();

        try {
            archiveDate.put(ArchiveDate.ARCHIVE_TIME, DateUtils.parseDate(createDateString, new String[] {"yyyy/MM"}).getTime());
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT, 1);
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT, 1);

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
     * @param tags the specified tags
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
     * @param article the specified article
     * @return an array of tags
     * @throws RepositoryException repository exception
     */
    private JSONArray tag(final String[] tagTitles, final JSONObject article) throws RepositoryException {
        final JSONArray ret = new JSONArray();

        for (int i = 0; i < tagTitles.length; i++) {
            final String tagTitle = tagTitles[i].trim();
            final JSONObject tag = new JSONObject();

            LOGGER.log(Level.TRACE, "Found a new tag[title={0}] in article[title={1}]",
                new Object[] {tagTitle, article.optString(Article.ARTICLE_TITLE)});
            tag.put(Tag.TAG_TITLE, tagTitle);
            tag.put(Tag.TAG_REFERENCE_COUNT, 1);
            tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, 1);

            final String tagId = tagRepository.add(tag);

            tag.put(Keys.OBJECT_ID, tagId);

            ret.put(tag);
        }

        return ret;
    }

    /**
     * Initializes administrator with the specified request json object, and 
     * then logins it.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassowrd": "" // Unhashed
     * }
     * </pre>
     * @throws Exception exception
     */
    private void initAdmin(final JSONObject requestJSONObject) throws Exception {
        LOGGER.info("Initializing admin....");
        final JSONObject admin = new JSONObject();

        admin.put(User.USER_NAME, requestJSONObject.getString(User.USER_NAME));
        admin.put(User.USER_EMAIL, requestJSONObject.getString(User.USER_EMAIL));
        admin.put(User.USER_URL, Latkes.getServePath());
        admin.put(User.USER_ROLE, Role.ADMIN_ROLE);
        admin.put(User.USER_PASSWORD, MD5.hash(requestJSONObject.getString(User.USER_PASSWORD)));
        admin.put(UserExt.USER_ARTICLE_COUNT, 0);
        admin.put(UserExt.USER_PUBLISHED_ARTICLE_COUNT, 0);

        userRepository.add(admin);

        LOGGER.info("Initialized admin");
    }

    /**
     * Initializes statistic.
     *
     * @return statistic
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private JSONObject initStatistic() throws RepositoryException, JSONException {
        LOGGER.info("Initializing statistic....");
        final JSONObject ret = new JSONObject();

        ret.put(Keys.OBJECT_ID, Statistic.STATISTIC);
        ret.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, 0);
        ret.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, 0);
        ret.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, 0);
        ret.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, 0);
        ret.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, 0);
        statisticRepository.add(ret);

        LOGGER.info("Initialized statistic");

        return ret;
    }

    /**
     * Initializes reply notification template.
     * 
     * @throws Exception exception
     */
    private void initReplyNotificationTemplate() throws Exception {
        LOGGER.info("Initializing reply notification template");

        final JSONObject replyNotificationTemplate = new JSONObject(Preference.Default.DEFAULT_REPLY_NOTIFICATION_TEMPLATE);

        replyNotificationTemplate.put(Keys.OBJECT_ID, Preference.REPLY_NOTIFICATION_TEMPLATE);

        preferenceRepository.add(replyNotificationTemplate);

        LOGGER.info("Initialized reply notification template");
    }

    /**
     * Initializes preference.
     *
     * @param requestJSONObject the specified json object
     * @return preference
     * @throws Exception exception
     */
    private JSONObject initPreference(final JSONObject requestJSONObject) throws Exception {
        LOGGER.info("Initializing preference....");

        final JSONObject ret = new JSONObject();

        ret.put(NOTICE_BOARD, Default.DEFAULT_NOTICE_BOARD);
        ret.put(META_DESCRIPTION, Default.DEFAULT_META_DESCRIPTION);
        ret.put(META_KEYWORDS, Default.DEFAULT_META_KEYWORDS);
        ret.put(HTML_HEAD, Default.DEFAULT_HTML_HEAD);
        ret.put(Preference.RELEVANT_ARTICLES_DISPLAY_CNT, Default.DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT);
        ret.put(Preference.RANDOM_ARTICLES_DISPLAY_CNT, Default.DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT);
        ret.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT, Default.DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT);
        ret.put(Preference.MOST_VIEW_ARTICLE_DISPLAY_CNT, Default.DEFAULT_MOST_VIEW_ARTICLES_DISPLAY_COUNT);
        ret.put(ARTICLE_LIST_DISPLAY_COUNT, Default.DEFAULT_ARTICLE_LIST_DISPLAY_COUNT);
        ret.put(ARTICLE_LIST_PAGINATION_WINDOW_SIZE, Default.DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        ret.put(MOST_USED_TAG_DISPLAY_CNT, Default.DEFAULT_MOST_USED_TAG_DISPLAY_COUNT);
        ret.put(MOST_COMMENT_ARTICLE_DISPLAY_CNT, Default.DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT);
        ret.put(RECENT_ARTICLE_DISPLAY_CNT, Default.DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT);
        ret.put(RECENT_COMMENT_DISPLAY_CNT, Default.DEFAULT_RECENT_COMMENT_DISPLAY_COUNT);
        ret.put(BLOG_TITLE, Default.DEFAULT_BLOG_TITLE);
        ret.put(BLOG_SUBTITLE, Default.DEFAULT_BLOG_SUBTITLE);
        ret.put(ADMIN_EMAIL, requestJSONObject.getString(User.USER_EMAIL));
        ret.put(LOCALE_STRING, Default.DEFAULT_LANGUAGE);
        ret.put(ENABLE_ARTICLE_UPDATE_HINT, Default.DEFAULT_ENABLE_ARTICLE_UPDATE_HINT);
        ret.put(SIGNS, Default.DEFAULT_SIGNS);
        ret.put(TIME_ZONE_ID, Default.DEFAULT_TIME_ZONE);
        ret.put(ALLOW_VISIT_DRAFT_VIA_PERMALINK, Default.DEFAULT_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
        ret.put(COMMENTABLE, Default.DEFAULT_COMMENTABLE);
        ret.put(VERSION, SoloServletListener.VERSION);
        ret.put(ARTICLE_LIST_STYLE, Default.DEFAULT_ARTICLE_LIST_STYLE);
        ret.put(KEY_OF_SOLO, Default.DEFAULT_KEY_OF_SOLO);
        ret.put(FEED_OUTPUT_MODE, Default.DEFAULT_FEED_OUTPUT_MODE);
        ret.put(FEED_OUTPUT_CNT, Default.DEFAULT_FEED_OUTPUT_CNT);
        ret.put(EDITOR_TYPE, Default.DEFAULT_EDITOR_TYPE);

        final String skinDirName = Default.DEFAULT_SKIN_DIR_NAME;

        ret.put(Skin.SKIN_DIR_NAME, skinDirName);

        final String skinName = Skins.getSkinName(skinDirName);

        ret.put(Skin.SKIN_NAME, skinName);

        final Set<String> skinDirNames = Skins.getSkinDirNames();
        final JSONArray skinArray = new JSONArray();

        for (final String dirName : skinDirNames) {
            final JSONObject skin = new JSONObject();

            skinArray.put(skin);

            final String name = Skins.getSkinName(dirName);

            skin.put(Skin.SKIN_NAME, name);
            skin.put(Skin.SKIN_DIR_NAME, dirName);
        }

        ret.put(Skin.SKINS, skinArray.toString());

        try {
            final String webRootPath = SoloServletListener.getWebRoot();
            final String skinPath = webRootPath + Skin.SKINS + "/" + skinDirName;

            Templates.MAIN_CFG.setDirectoryForTemplateLoading(new File(skinPath));
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Loads skins error!", e);
            throw new IllegalStateException(e);
        }

        TimeZones.setTimeZone(INIT_TIME_ZONE_ID);

        ret.put(Keys.OBJECT_ID, PREFERENCE);
        preferenceRepository.add(ret);

        LOGGER.info("Initialized preference");

        return ret;
    }

    /**
     * Sets archive date article repository with the specified archive date article repository.
     * 
     * @param archiveDateArticleRepository the specified archive date article repository
     */
    public void setArchiveDateArticleRepository(final ArchiveDateArticleRepository archiveDateArticleRepository) {
        this.archiveDateArticleRepository = archiveDateArticleRepository;
    }

    /**
     * Sets archive date repository with the specified archive date repository.
     * 
     * @param archiveDateRepository the specified archive date repository
     */
    public void setArchiveDateRepository(final ArchiveDateRepository archiveDateRepository) {
        this.archiveDateRepository = archiveDateRepository;
    }

    /**
     * Sets the article repository with the specified article repository.
     * 
     * @param articleRepository the specified article repository
     */
    public void setArticleRepository(final ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Sets the user repository with the specified user repository.
     * 
     * @param userRepository the specified user repository
     */
    public void setUserRepository(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sets the preference repository with the specified preference repository.
     * 
     * @param preferenceRepository the specified preference repository
     */
    public void setPreferenceRepository(final PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    /**
     * Sets the statistic repository with the specified statistic repository.
     * 
     * @param statisticRepository the specified statistic repository
     */
    public void setStatisticRepository(final StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    /**
     * Sets the tag repository with the specified tag repository.
     * 
     * @param tagRepository the specified tag repository
     */
    public void setTagRepository(final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Sets the tag article repository with the specified tag article repository.
     * 
     * @param tagArticleRepository the specified tag article repository
     */
    public void setTagArticleRepository(final TagArticleRepository tagArticleRepository) {
        this.tagArticleRepository = tagArticleRepository;
    }

    /**
     * Sets the comment repository with the specified comment repository.
     * 
     * @param commentRepository the specified comment repository
     */
    public void setCommentRepository(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Sets the language service with the specified language service.
     * 
     * @param langPropsService the specified language service
     */
    public void setLangPropsService(final LangPropsService langPropsService) {
        this.langPropsService = langPropsService;
    }
    
    /**
     * Sets the plugin manager with the specified plugin manager.
     * 
     * @param pluginManager the specified plugin manager
     */
    public void setPluginManager(final PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}
