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
package org.b3log.solo.service;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.util.Emotions;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.b3log.solo.model.Article.*;

/**
 * Article query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://blog.sweelia.com">ArmstrongCN</a>
 * @author <a href="http://zephyr.b3log.org">Zephyr</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.3.2.6, Oct 7, 2018
 * @since 0.3.5
 */
@Service
public class ArticleQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleQueryService.class);

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Category-Tag repository.
     */
    @Inject
    private CategoryTagRepository categoryTagRepository;

    /**
     * User service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Preference query service.
     */
    @Inject
    private PreferenceQueryService preferenceQueryService;

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
     * Archive date-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;

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
     * Searches articles with the specified keyword.
     *
     * @param keyword        the specified keyword
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return result
     */
    public JSONObject searchKeyword(final String keyword, final int currentPageNum, final int pageSize) {
        final JSONObject ret = new JSONObject();
        ret.put(Article.ARTICLES, (Object) Collections.emptyList());

        final JSONObject pagination = new JSONObject();
        ret.put(Pagination.PAGINATION, pagination);
        pagination.put(Pagination.PAGINATION_PAGE_COUNT, 0);
        pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) Collections.emptyList());

        try {
            final Query query = new Query().setFilter(
                    CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true),
                            CompositeFilterOperator.or(
                                    new PropertyFilter(Article.ARTICLE_TITLE, FilterOperator.LIKE, "%" + keyword + "%"),
                                    new PropertyFilter(Article.ARTICLE_CONTENT, FilterOperator.LIKE, "%" + keyword + "%")))).
                    addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).setCurrentPageNum(currentPageNum).setPageSize(pageSize);

            final JSONObject result = articleRepository.get(query);

            final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            final JSONObject preference = preferenceQueryService.getPreference();
            final int windowSize = preference.optInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) pageNums);

            final List<JSONObject> articles = CollectionUtils.jsonArrayToList(result.optJSONArray(Keys.RESULTS));
            ret.put(Article.ARTICLES, (Object) articles);
        } catch (final RepositoryException | ServiceException e) {
            LOGGER.log(Level.ERROR, "Searches articles error", e);
        }

        return ret;
    }

    /**
     * Gets category articles.
     *
     * @param categoryId     the specified category id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return result
     * @throws ServiceException service exception
     */
    public JSONObject getCategoryArticles(final String categoryId,
                                          final int currentPageNum, final int pageSize) throws ServiceException {
        final JSONObject ret = new JSONObject();
        ret.put(Article.ARTICLES, (Object) Collections.emptyList());

        final JSONObject pagination = new JSONObject();
        ret.put(Pagination.PAGINATION, pagination);
        pagination.put(Pagination.PAGINATION_PAGE_COUNT, 0);
        pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) Collections.emptyList());

        try {
            final JSONArray categoryTags = categoryTagRepository.getByCategoryId(
                    categoryId, 1, Integer.MAX_VALUE).optJSONArray(Keys.RESULTS);
            if (categoryTags.length() <= 0) {
                return ret;
            }

            final List<String> tagIds = new ArrayList<>();
            for (int i = 0; i < categoryTags.length(); i++) {
                tagIds.add(categoryTags.optJSONObject(i).optString(Tag.TAG + "_" + Keys.OBJECT_ID));
            }

            Query query = new Query().setFilter(
                    new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.IN, tagIds)).
                    setCurrentPageNum(currentPageNum).setPageSize(pageSize).
                    addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
            JSONObject result = tagArticleRepository.get(query);
            final JSONArray tagArticles = result.optJSONArray(Keys.RESULTS);
            if (tagArticles.length() <= 0) {
                return ret;
            }

            final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject preference = preferenceQueryService.getPreference();
            final int windowSize = preference.optInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) pageNums);

            final Set<String> articleIds = new HashSet<>();
            for (int i = 0; i < tagArticles.length(); i++) {
                articleIds.add(tagArticles.optJSONObject(i).optString(Article.ARTICLE + "_" + Keys.OBJECT_ID));
            }

            query = new Query().setFilter(
                    new PropertyFilter(Keys.OBJECT_ID, FilterOperator.IN, articleIds)).
                    setPageCount(1).addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);

            final List<JSONObject> articles = new ArrayList<>();
            final JSONArray articleArray = articleRepository.get(query).optJSONArray(Keys.RESULTS);
            for (int i = 0; i < articleArray.length(); i++) {
                final JSONObject article = articleArray.optJSONObject(i);
                if (!article.optBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    // Skips the unpublished article
                    continue;
                }

                article.put(ARTICLE_CREATE_TIME, article.optLong(ARTICLE_CREATED));
                article.put(ARTICLE_T_CREATE_DATE, new Date(article.optLong(ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(ARTICLE_UPDATED)));

                articles.add(article);
            }
            ret.put(Article.ARTICLES, (Object) articles);

            return ret;
        } catch (final RepositoryException | ServiceException e) {
            LOGGER.log(Level.ERROR, "Gets category articles error", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Can the specified user access an article specified by the given article id?
     *
     * @param articleId the given article id
     * @param user      the specified user
     * @return {@code true} if the current user can access the article, {@code false} otherwise
     * @throws Exception exception
     */
    public boolean canAccessArticle(final String articleId, final JSONObject user) throws Exception {
        if (StringUtils.isBlank(articleId)) {
            return false;
        }

        if (null == user) {
            return false;
        }

        if (Role.ADMIN_ROLE.equals(user.optString(User.USER_ROLE))) {
            return true;
        }

        final JSONObject article = articleRepository.get(articleId);
        final String currentUserId = user.getString(Keys.OBJECT_ID);

        return article.getString(Article.ARTICLE_AUTHOR_ID).equals(currentUserId);
    }

    /**
     * Gets time of the recent updated article.
     *
     * @return time of the recent updated article, returns {@code 0} if not found
     * @throws ServiceException service exception
     */
    public long getRecentArticleTime() throws ServiceException {
        try {
            final List<JSONObject> recentArticles = articleRepository.getRecentArticles(1);
            if (recentArticles.isEmpty()) {
                return 0;
            }

            final JSONObject recentArticle = recentArticles.get(0);

            return recentArticle.getLong(Article.ARTICLE_UPDATED);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            throw new ServiceException("Gets recent article time failed");
        }
    }

    /**
     * Gets the specified article's author.
     * <p>
     * The specified article has a property {@value Article#ARTICLE_AUTHOR_ID}, this method will use this property to
     * get a user from users.
     * </p>
     * <p>
     * If can't find the specified article's author (i.e. the author has been removed by administrator), returns
     * administrator.
     * </p>
     *
     * @param article the specified article
     * @return user, {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getAuthor(final JSONObject article) throws ServiceException {
        try {
            final String userId = article.getString(Article.ARTICLE_AUTHOR_ID);
            JSONObject ret = userRepository.get(userId);
            if (null == ret) {
                LOGGER.log(Level.WARN, "Gets author of article failed, assumes the administrator is the author of this article [id={0}]",
                        article.getString(Keys.OBJECT_ID));
                // This author may be deleted by admin, use admin as the author of this article
                ret = userRepository.getAdmin();
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets author of article [id={0}] failed", article.optString(Keys.OBJECT_ID));

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the sign of an article specified by the sign id.
     *
     * @param signId     the specified article id
     * @param preference the specified preference
     * @return article sign, returns the default sign (which oId is "1") if not found
     * @throws JSONException json exception
     */
    public JSONObject getSign(final String signId, final JSONObject preference) throws JSONException {
        final JSONArray signs = new JSONArray(preference.getString(Option.ID_C_SIGNS));

        JSONObject defaultSign = null;

        for (int i = 0; i < signs.length(); i++) {
            final JSONObject ret = signs.getJSONObject(i);

            if (signId.equals(ret.optString(Keys.OBJECT_ID))) {
                return ret;
            }

            if ("1".equals(ret.optString(Keys.OBJECT_ID))) {
                defaultSign = ret;
            }
        }

        LOGGER.log(Level.WARN, "Can not find the sign [id={0}], returns a default sign [id=1]", signId);

        return defaultSign;
    }

    /**
     * Determines the specified article has updated.
     *
     * @param article the specified article
     * @return {@code true} if it has updated, {@code false} otherwise
     * @throws JSONException json exception
     */
    public boolean hasUpdated(final JSONObject article) throws JSONException {
        final long updateDate = article.getLong(Article.ARTICLE_UPDATED);
        final long createDate = article.getLong(Article.ARTICLE_CREATED);

        return createDate != updateDate;
    }

    /**
     * Determines the specified article had been published.
     *
     * @param article the specified article
     * @return {@code true} if it had been published, {@code false} otherwise
     * @throws JSONException json exception
     */
    public boolean hadBeenPublished(final JSONObject article) throws JSONException {
        return article.getBoolean(Article.ARTICLE_HAD_BEEN_PUBLISHED);
    }

    /**
     * Gets all unpublished articles.
     *
     * @return articles all unpublished articles
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getUnpublishedArticles() throws RepositoryException {
        final Map<String, SortDirection> sorts = new HashMap<>();
        sorts.put(Article.ARTICLE_CREATED, SortDirection.DESCENDING);
        sorts.put(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
        final Query query = new Query().setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true));

        return articleRepository.getList(query);
    }

    /**
     * Gets the recent articles with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of json object, its size less or equal to the specified fetch size
     */
    public List<JSONObject> getRecentArticles(final int fetchSize) {
        try {
            return articleRepository.getRecentArticles(fetchSize);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets recent articles failed", e);

            return Collections.emptyList();
        }
    }

    /**
     * Gets an article by the specified article id.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return for example,      <pre>
     * {
     *     "article": {
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articlePermalink": "",
     *         "articleHadBeenPublished": boolean,
     *         "articleCreateDate": java.util.Date,
     *         "articleTags": [{
     *             "oId": "",
     *             "tagTitle": ""
     *         }, ....],
     *         "articleSignId": "",
     *         "articleViewPwd": "",
     *         "articleEditorType": "",
     *         "signs": [{
     *             "oId": "",
     *             "signHTML": ""
     *         }, ....]
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getArticle(final String articleId) throws ServiceException {
        try {
            final JSONObject ret = new JSONObject();

            final JSONObject article = articleRepository.get(articleId);

            if (null == article) {
                return null;
            }

            ret.put(ARTICLE, article);

            // Tags
            final JSONArray tags = new JSONArray();
            final List<JSONObject> tagArticleRelations = tagArticleRepository.getByArticleId(articleId);

            for (int i = 0; i < tagArticleRelations.size(); i++) {
                final JSONObject tagArticleRelation = tagArticleRelations.get(i);
                final String tagId = tagArticleRelation.getString(Tag.TAG + "_" + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);

                tags.put(tag);
            }
            article.put(ARTICLE_TAGS_REF, tags);

            // Signs
            final JSONObject preference = preferenceQueryService.getPreference();

            article.put(Sign.SIGNS, new JSONArray(preference.getString(Option.ID_C_SIGNS)));

            // Remove unused properties
            article.remove(ARTICLE_AUTHOR_ID);
            article.remove(ARTICLE_COMMENT_COUNT);
            article.remove(ARTICLE_IS_PUBLISHED);
            article.remove(ARTICLE_PUT_TOP);
            article.remove(ARTICLE_UPDATED);
            article.remove(ARTICLE_VIEW_COUNT);
            article.remove(ARTICLE_RANDOM_DOUBLE);

            LOGGER.log(Level.DEBUG, "Got an article[id={0}]", articleId);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets articles(by crate date descending) by the specified request json object.
     * <p>
     * If the property "articleIsPublished" of the specified request json object is {@code true}, the returned articles
     * all are published, {@code false} otherwise.
     * </p>
     * <p>
     * Specified the "excludes" for results properties exclusion.
     * </p>
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "paginationCurrentPageNum": 1,
     *                          "paginationPageSize": 20,
     *                          "paginationWindowSize": 10,
     *                          "articleIsPublished": boolean,
     *                          "keyword": "", // Optional search keyword
     *                          "excludes": ["", ....], // Optional
     *                          "enableArticleUpdateHint": bool // Optional
     *                          see {@link Pagination} for more details
     * @return for example,      <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "articles": [{
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleCommentCount": int,
     *         "articleCreateTime"; long,
     *         "articleViewCount": int,
     *         "articleTags": "tag1, tag2, ....",
     *         "articlePutTop": boolean,
     *         "articleSignId": "",
     *         "articleViewPwd": "",
     *         "articleEditorType": "",
     *         .... // Specified by the "excludes"
     *      }, ....]
     * }
     * </pre>, order by article update date and sticky(put top).
     * @throws ServiceException service exception
     * @see Pagination
     */
    public JSONObject getArticles(final JSONObject requestJSONObject) throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final int currentPageNum = requestJSONObject.getInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(Pagination.PAGINATION_WINDOW_SIZE);
            final boolean articleIsPublished = requestJSONObject.optBoolean(ARTICLE_IS_PUBLISHED, true);

            final Query query = new Query().setCurrentPageNum(currentPageNum).setPageSize(pageSize).
                    addSort(ARTICLE_PUT_TOP, SortDirection.DESCENDING);
            if (requestJSONObject.optBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT)) {
                query.addSort(ARTICLE_UPDATED, SortDirection.DESCENDING);
            } else {
                query.addSort(ARTICLE_CREATED, SortDirection.DESCENDING);
            }

            final String keyword = requestJSONObject.optString(Common.KEYWORD);
            if (StringUtils.isBlank(keyword)) {
                query.setFilter(new PropertyFilter(ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, articleIsPublished));
            } else {
                query.setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, articleIsPublished),
                        CompositeFilterOperator.or(
                                new PropertyFilter(ARTICLE_TITLE, FilterOperator.LIKE, "%" + keyword + "%"),
                                new PropertyFilter(ARTICLE_TAGS_REF, FilterOperator.LIKE, "%" + keyword + "%")
                        )
                ));
            }

            final JSONObject result = articleRepository.get(query);

            final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final JSONArray articles = result.getJSONArray(Keys.RESULTS);
            JSONArray excludes = requestJSONObject.optJSONArray(Keys.EXCLUDES);
            excludes = null == excludes ? new JSONArray() : excludes;

            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                final JSONObject author = getAuthor(article);
                final String authorName = author.getString(User.USER_NAME);
                article.put(Common.AUTHOR_NAME, authorName);
                article.put(ARTICLE_CREATE_TIME, article.getLong(ARTICLE_CREATED));
                article.put(ARTICLE_UPDATE_TIME, article.getLong(ARTICLE_UPDATED));

                // Remove unused properties
                for (int j = 0; j < excludes.length(); j++) {
                    article.remove(excludes.optString(j));
                }
            }

            ret.put(ARTICLES, articles);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets a list of published articles with the specified tag id, current page number and page size.
     *
     * @param tagId          the specified tag id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getArticlesByTag(final String tagId, final int currentPageNum, final int pageSize)
            throws ServiceException {
        try {
            JSONObject result = tagArticleRepository.getByTagId(tagId, currentPageNum, pageSize);
            final JSONArray tagArticleRelations = result.getJSONArray(Keys.RESULTS);

            if (0 == tagArticleRelations.length()) {
                return Collections.emptyList();
            }

            final Set<String> articleIds = new HashSet<>();

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation = tagArticleRelations.getJSONObject(i);
                final String articleId = tagArticleRelation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);

                articleIds.add(articleId);
            }

            final List<JSONObject> ret = new ArrayList<>();

            final Query query = new Query().setFilter(new PropertyFilter(Keys.OBJECT_ID, FilterOperator.IN, articleIds)).setPageCount(1).index(
                    Article.ARTICLE_PERMALINK);

            result = articleRepository.get(query);
            final JSONArray articles = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    // Skips the unpublished article
                    continue;
                }

                article.put(ARTICLE_CREATE_TIME, article.getLong(ARTICLE_CREATED));
                article.put(ARTICLE_T_CREATE_DATE, new Date(article.getLong(ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(ARTICLE_UPDATED)));

                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles by tag[id=" + tagId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets a list of published articles with the specified archive date id, current page number and page size.
     *
     * @param archiveDateId  the specified archive date id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getArticlesByArchiveDate(final String archiveDateId, final int currentPageNum, final int pageSize)
            throws ServiceException {
        try {
            JSONObject result = archiveDateArticleRepository.getByArchiveDateId(archiveDateId, currentPageNum, pageSize);
            final JSONArray relations = result.getJSONArray(Keys.RESULTS);
            if (0 == relations.length()) {
                return Collections.emptyList();
            }

            final Set<String> articleIds = new HashSet<>();
            for (int i = 0; i < relations.length(); i++) {
                final JSONObject relation = relations.getJSONObject(i);
                final String articleId = relation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);

                articleIds.add(articleId);
            }

            final List<JSONObject> ret = new ArrayList<>();

            final Query query = new Query().setFilter(new PropertyFilter(Keys.OBJECT_ID, FilterOperator.IN, articleIds)).setPageCount(1).index(
                    Article.ARTICLE_PERMALINK);
            result = articleRepository.get(query);
            final JSONArray articles = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    // Skips the unpublished article
                    continue;
                }

                article.put(ARTICLE_CREATE_TIME, article.getLong(ARTICLE_CREATED));
                article.put(ARTICLE_T_CREATE_DATE, new Date(article.getLong(ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(ARTICLE_UPDATED)));

                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles by archive date[id=" + archiveDateId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets a list of articles randomly with the specified fetch size.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param fetchSize the specified fetch size
     * @return a list of json objects, its size less or equal to the specified fetch size
     * @throws ServiceException service exception
     */
    public List<JSONObject> getArticlesRandomly(final int fetchSize) throws ServiceException {
        try {
            final List<JSONObject> ret = articleRepository.getRandomly(fetchSize);

            removeUnusedProperties(ret);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets articles randomly failed[fetchSize=" + fetchSize + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the relevant published articles of the specified article.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param article    the specified article
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getRelevantArticles(final JSONObject article, final JSONObject preference)
            throws ServiceException {
        try {
            final int displayCnt = preference.getInt(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
            final String[] tagTitles = article.getString(Article.ARTICLE_TAGS_REF).split(",");
            final int maxTagCnt = displayCnt > tagTitles.length ? tagTitles.length : displayCnt;
            final String articleId = article.getString(Keys.OBJECT_ID);

            final List<JSONObject> articles = new ArrayList<JSONObject>();

            for (int i = 0; i < maxTagCnt; i++) { // XXX: should average by tag?
                final String tagTitle = tagTitles[i];
                final JSONObject tag = tagRepository.getByTitle(tagTitle);
                final String tagId = tag.getString(Keys.OBJECT_ID);
                final JSONObject result = tagArticleRepository.getByTagId(tagId, 1, displayCnt);
                final JSONArray tagArticleRelations = result.getJSONArray(Keys.RESULTS);

                final int relationSize = displayCnt < tagArticleRelations.length() ? displayCnt : tagArticleRelations.length();

                for (int j = 0; j < relationSize; j++) {
                    final JSONObject tagArticleRelation = tagArticleRelations.getJSONObject(j);
                    final String relatedArticleId = tagArticleRelation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);

                    if (articleId.equals(relatedArticleId)) {
                        continue;
                    }

                    final JSONObject relevant = articleRepository.get(relatedArticleId);

                    if (!relevant.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                        continue;
                    }

                    boolean existed = false;

                    for (final JSONObject relevantArticle : articles) {
                        if (relevantArticle.getString(Keys.OBJECT_ID).equals(relevant.getString(Keys.OBJECT_ID))) {
                            existed = true;
                        }
                    }

                    if (!existed) {
                        articles.add(relevant);
                    }
                }
            }

            removeUnusedProperties(articles);

            if (displayCnt > articles.size()) {
                return articles;
            }

            final List<Integer> randomIntegers = CollectionUtils.getRandomIntegers(0, articles.size() - 1, displayCnt);
            final List<JSONObject> ret = new ArrayList<JSONObject>();

            for (final int index : randomIntegers) {
                ret.add(articles.get(index));
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets relevant articles failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Determines an article specified by the given article id is published.
     *
     * @param articleId the given article id
     * @return {@code true} if it is published
     * @throws ServiceException service exception
     */
    public boolean isArticlePublished(final String articleId) throws ServiceException {
        try {
            return articleRepository.isPublished(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Determines the article publish status failed[articleId=" + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the next article(by create date) by the specified article id.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return the previous article,      <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": "",
     *     "articleAbstract": ""
     * }
     * </pre> returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getNextArticle(final String articleId) throws ServiceException {
        try {
            return articleRepository.getNextArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets the next article failed[articleId=" + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the previous article(by create date) by the specified article id.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return the previous article,      <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": "",
     *     "articleAbstract": ""
     * }
     * </pre> returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getPreviousArticle(final String articleId) throws ServiceException {
        try {
            return articleRepository.getPreviousArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets the previous article failed[articleId=" + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets an article by the specified article id.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return an article, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getArticleById(final String articleId) throws ServiceException {
        try {
            return articleRepository.get(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets an article[articleId=" + articleId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets an article by the specified article permalink.
     * <p>
     * <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articlePermalink the specified article permalink
     * @return an article, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getArticleByPermalink(final String articlePermalink) throws ServiceException {
        try {
            return articleRepository.getByPermalink(articlePermalink);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets an article[articlePermalink=" + articlePermalink + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets <em>published</em> articles by the specified author id, current page number and page size.
     *
     * @param authorId       the specified author id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getArticlesByAuthorId(final String authorId, final int currentPageNum, final int pageSize)
            throws ServiceException {
        try {
            final JSONObject result = articleRepository.getByAuthorId(authorId, currentPageNum, pageSize);
            final JSONArray articles = result.getJSONArray(Keys.RESULTS);
            final List<JSONObject> ret = new ArrayList<>();

            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                article.put(ARTICLE_CREATE_TIME, article.getLong(ARTICLE_CREATED));
                article.put(ARTICLE_T_CREATE_DATE, new Date(article.optLong(ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(ARTICLE_UPDATED)));

                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles by author email failed [authorId=" + authorId +
                    ", currentPageNum=" + currentPageNum + ", pageSize=" + pageSize + "]", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets article contents with the specified article id.
     * <p>
     * Invoking this method dose not effect on article view count.
     * </p>
     *
     * @param request   the specified HTTP servlet request
     * @param articleId the specified article id
     * @return article contents, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public String getArticleContent(final HttpServletRequest request, final String articleId) throws ServiceException {
        if (StringUtils.isBlank(articleId)) {
            return null;
        }

        try {
            final JSONObject article = articleRepository.get(articleId);

            if (null == article) {
                return null;
            }

            if (Solos.needViewPwd(request, article)) {
                final String content = langPropsService.get("articleContentPwd");

                article.put(ARTICLE_CONTENT, content);
            } else if ("CodeMirror-Markdown".equals(article.optString(ARTICLE_EDITOR_TYPE))) {
                // Markdown to HTML for content and abstract
                Stopwatchs.start("Get Article Content [Markdown]");
                String content = article.optString(ARTICLE_CONTENT);
                content = Emotions.convert(content);
                content = Markdowns.toHTML(content);
                article.put(ARTICLE_CONTENT, content);
                Stopwatchs.end();
            }

            return article.getString(Article.ARTICLE_CONTENT);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets article content failed[articleId=" + articleId + "]", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Converts the content and abstract for each of the specified articles to HTML if that is saved by Markdown editor.
     *
     * @param articles the specified articles
     * @throws Exception exception
     */
    public void markdowns(final List<JSONObject> articles) throws Exception {
        for (final JSONObject article : articles) {
            markdown(article);
        }
    }

    /**
     * Converts the content and abstract for the specified article to HTML if it is saved by Markdown editor.
     *
     * @param article the specified article
     * @throws Exception exception
     */
    public void markdown(final JSONObject article) throws Exception {
        if ("CodeMirror-Markdown".equals(article.optString(ARTICLE_EDITOR_TYPE))) {
            Stopwatchs.start("Markdown Article[id=" + article.optString(Keys.OBJECT_ID) + "]");

            Stopwatchs.start("Content");
            String content = article.optString(ARTICLE_CONTENT);
            content = Emotions.convert(content);
            content = Markdowns.toHTML(content);
            article.put(ARTICLE_CONTENT, content);
            Stopwatchs.end();

            String abstractContent = article.optString(ARTICLE_ABSTRACT);

            if (StringUtils.isNotBlank(abstractContent)) {
                Stopwatchs.start("Abstract");
                abstractContent = Emotions.convert(abstractContent);
                abstractContent = Markdowns.toHTML(abstractContent);
                article.put(ARTICLE_ABSTRACT, abstractContent);
                Stopwatchs.end();
            }

            Stopwatchs.end();
        }
    }

    /**
     * Removes unused properties of each article in the specified articles.
     * <p>
     * Remains the following properties:
     * <ul>
     * <li>{@link Article#ARTICLE_TITLE article title}</li>
     * <li>{@link Article#ARTICLE_PERMALINK article permalink}</li>
     * </ul>
     * </p>
     * <p>
     * The batch version of method {@link #removeUnusedProperties(org.json.JSONObject)}.
     * </p>
     *
     * @param articles the specified articles
     * @see #removeUnusedProperties(org.json.JSONObject)
     */
    public void removeUnusedProperties(final List<JSONObject> articles) {
        for (final JSONObject article : articles) {
            removeUnusedProperties(article);
        }
    }

    /**
     * Removes unused properties of the specified article.
     * <p>
     * Remains the following properties:
     * <ul>
     * <li>{@link Article#ARTICLE_TITLE article title}</li>
     * <li>{@link Article#ARTICLE_PERMALINK article permalink}</li>
     * </ul>
     * </p>
     *
     * @param article the specified article
     * @see #removeUnusedProperties(java.util.List)
     */
    public void removeUnusedProperties(final JSONObject article) {
        article.remove(Keys.OBJECT_ID);
        article.remove(Article.ARTICLE_AUTHOR_ID);
        article.remove(Article.ARTICLE_ABSTRACT);
        article.remove(Article.ARTICLE_COMMENT_COUNT);
        article.remove(Article.ARTICLE_CONTENT);
        article.remove(Article.ARTICLE_CREATED);
        article.remove(Article.ARTICLE_TAGS_REF);
        article.remove(Article.ARTICLE_UPDATED);
        article.remove(Article.ARTICLE_VIEW_COUNT);
        article.remove(Article.ARTICLE_RANDOM_DOUBLE);
        article.remove(Article.ARTICLE_IS_PUBLISHED);
        article.remove(Article.ARTICLE_PUT_TOP);
        article.remove(Article.ARTICLE_HAD_BEEN_PUBLISHED);
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
     * Sets the preference query service with the specified preference query service.
     *
     * @param preferenceQueryService the specified preference query service
     */
    public void setPreferenceQueryService(final PreferenceQueryService preferenceQueryService) {
        this.preferenceQueryService = preferenceQueryService;
    }

    /**
     * Sets the statistic query service with the specified statistic query service.
     *
     * @param statisticQueryService the specified statistic query service
     */
    public void setStatisticQueryService(final StatisticQueryService statisticQueryService) {
        this.statisticQueryService = statisticQueryService;
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
}
