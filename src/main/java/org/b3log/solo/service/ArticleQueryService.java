/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Inject;
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
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Article query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://hacpai.com/member/armstrong">ArmstrongCN</a>
 * @author <a href="https://hacpai.com/member/ZephyrJung">Zephyr</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.3.6.0, Jan 15, 2020
 * @since 0.3.5
 */
@Service
public class ArticleQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArticleQueryService.class);

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
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

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
                    CompositeFilterOperator.and(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED),
                            CompositeFilterOperator.or(
                                    new PropertyFilter(Article.ARTICLE_TITLE, FilterOperator.LIKE, "%" + keyword + "%"),
                                    new PropertyFilter(Article.ARTICLE_TAGS_REF, FilterOperator.LIKE, "%" + keyword + "%"),
                                    new PropertyFilter(Article.ARTICLE_CONTENT, FilterOperator.LIKE, "%" + keyword + "%")))).
                    addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING).setPage(currentPageNum, pageSize);

            final JSONObject result = articleRepository.get(query);

            final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            final JSONObject preference = optionQueryService.getPreference();
            final int windowSize = preference.optInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) pageNums);

            final List<JSONObject> articles = CollectionUtils.jsonArrayToList(result.optJSONArray(Keys.RESULTS));
            ret.put(Article.ARTICLES, (Object) articles);
        } catch (final RepositoryException e) {
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
    public JSONObject getCategoryArticles(final String categoryId, final int currentPageNum, final int pageSize) throws ServiceException {
        final JSONObject ret = new JSONObject();
        ret.put(Keys.RESULTS, (Object) Collections.emptyList());

        final JSONObject pagination = new JSONObject();
        ret.put(Pagination.PAGINATION, pagination);
        pagination.put(Pagination.PAGINATION_PAGE_COUNT, 0);
        pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) Collections.emptyList());

        try {
            final JSONArray categoryTags = categoryTagRepository.getByCategoryId(categoryId, 1, Integer.MAX_VALUE).optJSONArray(Keys.RESULTS);
            if (categoryTags.length() <= 0) {
                return ret;
            }

            final List<String> tagIds = new ArrayList<>();
            for (int i = 0; i < categoryTags.length(); i++) {
                tagIds.add(categoryTags.optJSONObject(i).optString(Tag.TAG + "_" + Keys.OBJECT_ID));
            }

            final StringBuilder queryCount = new StringBuilder("SELECT count(DISTINCT(article.oId)) as `C` FROM ");
            final StringBuilder queryList = new StringBuilder("SELECT DISTINCT(article.oId) as `C` FROM ");
            final StringBuilder queryStr = new StringBuilder(articleRepository.getName() + " AS article,").
                    append(tagArticleRepository.getName() + " AS tag_article").
                    append(" WHERE article.oId=tag_article.article_oId ").
                    append(" AND article.").append(Article.ARTICLE_STATUS).append("=?").
                    append(" AND ").append("tag_article.tag_oId").append(" IN (");
            for (int i = 0; i < tagIds.size(); i++) {
                queryStr.append(" ").append(tagIds.get(i));
                if (i < (tagIds.size() - 1)) {
                    queryStr.append(",");
                }
            }
            queryStr.append(") ORDER BY `C` DESC");

            final List<JSONObject> tagArticlesCountResult = articleRepository.
                    select(queryCount.append(queryStr.toString()).toString(), Article.ARTICLE_STATUS_C_PUBLISHED);
            queryStr.append(" LIMIT ").append((currentPageNum - 1) * pageSize).append(",").append(pageSize);
            final List<JSONObject> tagArticles = articleRepository.
                    select(queryList.append(queryStr.toString()).toString(), Article.ARTICLE_STATUS_C_PUBLISHED);
            if (tagArticles.size() <= 0) {
                return ret;
            }

            final int tagArticlesCount = tagArticlesCountResult == null ? 0 : tagArticlesCountResult.get(0).optInt("C");
            final int pageCount = (int) Math.ceil(tagArticlesCount / (double) pageSize);
            final JSONObject preference = optionQueryService.getPreference();
            final int windowSize = preference.optInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, (Object) pageNums);
            pagination.put(Pagination.PAGINATION_RECORD_COUNT, tagArticlesCount);

            final Set<String> articleIds = new HashSet<>();
            for (int i = 0; i < tagArticles.size(); i++) {
                articleIds.add(tagArticles.get(i).optString("C"));
            }
            final Query query = new Query().setFilter(new PropertyFilter(Keys.OBJECT_ID, FilterOperator.IN, articleIds)).
                    setPageCount(1).addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
            final List<JSONObject> articles = new ArrayList<>();
            final JSONArray articleArray = articleRepository.get(query).optJSONArray(Keys.RESULTS);
            for (int i = 0; i < articleArray.length(); i++) {
                final JSONObject article = articleArray.optJSONObject(i);
                article.put(Article.ARTICLE_CREATE_TIME, article.optLong(Article.ARTICLE_CREATED));
                article.put(Article.ARTICLE_T_CREATE_DATE, new Date(article.optLong(Article.ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(Article.ARTICLE_UPDATED)));
                articles.add(article);
            }
            ret.put(Keys.RESULTS, (Object) articles);

            return ret;
        } catch (final RepositoryException e) {
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
     * @throws ServiceException service exception
     */
    public boolean canAccessArticle(final String articleId, final JSONObject user) throws ServiceException {
        if (StringUtils.isBlank(articleId)) {
            return false;
        }

        if (null == user) {
            return false;
        }

        if (Role.ADMIN_ROLE.equals(user.optString(User.USER_ROLE))) {
            return true;
        }

        try {
            final JSONObject article = articleRepository.get(articleId);
            final String currentUserId = user.getString(Keys.OBJECT_ID);

            return article.getString(Article.ARTICLE_AUTHOR_ID).equals(currentUserId);
        } catch (final Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Gets time of the recent updated article.
     *
     * @return time of the recent updated article, returns {@code 0} if not found
     */
    public long getRecentArticleTime() {
        try {
            final List<JSONObject> recentArticles = articleRepository.getRecentArticles(1);
            if (recentArticles.isEmpty()) {
                return 0;
            }

            final JSONObject recentArticle = recentArticles.get(0);

            return recentArticle.getLong(Article.ARTICLE_UPDATED);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets recent article time failed", e);

            return 0;
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
                LOGGER.log(Level.WARN, "Gets author of article failed, assumes the administrator is the author of this article [id={}]",
                        article.getString(Keys.OBJECT_ID));
                // This author may be deleted by admin, use admin as the author of this article
                ret = userRepository.getAdmin();
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets author of article [id={}] failed", article.optString(Keys.OBJECT_ID));

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

        LOGGER.log(Level.WARN, "Can not find the sign [id={}], returns a default sign [id=1]", signId);

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
     *         "articleCreateDate": java.util.Date,
     *         "articleTags": [{
     *             "oId": "",
     *             "tagTitle": ""
     *         }, ....],
     *         "articleSignId": "",
     *         "articleViewPwd": "",
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

            ret.put(Article.ARTICLE, article);

            // Tags
            final JSONArray tags = new JSONArray();
            final List<JSONObject> tagArticleRelations = tagArticleRepository.getByArticleId(articleId);
            for (final JSONObject tagArticleRelation : tagArticleRelations) {
                final String tagId = tagArticleRelation.getString(Tag.TAG + "_" + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);

                tags.put(tag);
            }
            article.put(Article.ARTICLE_TAGS_REF, tags);

            // Signs
            final JSONObject preference = optionQueryService.getPreference();
            article.put(Sign.SIGNS, new JSONArray(preference.getString(Option.ID_C_SIGNS)));
            // Remove unused properties
            article.remove(Article.ARTICLE_AUTHOR_ID);
            article.remove(Article.ARTICLE_COMMENT_COUNT);
            article.remove(Article.ARTICLE_PUT_TOP);
            article.remove(Article.ARTICLE_UPDATED);
            article.remove(Article.ARTICLE_VIEW_COUNT);
            article.remove(Article.ARTICLE_RANDOM_DOUBLE);

            LOGGER.log(Level.DEBUG, "Got an article [id={}]", articleId);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets articles(by crate date descending) by the specified request json object.
     * <p>
     * Specified the "excludes" for results properties exclusion.
     * </p>
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "paginationCurrentPageNum": 1,
     *                          "paginationPageSize": 20,
     *                          "paginationWindowSize": 10,
     *                          "articleStatus": int,
     *                          "keyword": "", // Optional search keyword
     *                          "excludes": ["", ....], // Optional
     *                          "enableArticleUpdateHint": bool // Optional
     * @return for example,
     * <pre>
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
     *         .... // Specified by the "excludes"
     *      }, ....]
     * }
     * </pre>, order by article update date and sticky(put top).
     * @see Pagination
     */
    public JSONObject getArticles(final JSONObject requestJSONObject) {
        final JSONObject ret = new JSONObject();

        try {
            final int currentPageNum = requestJSONObject.getInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(Pagination.PAGINATION_WINDOW_SIZE);
            final int articleStatus = requestJSONObject.optInt(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);

            final Query query = new Query().setPage(currentPageNum, pageSize).
                    addSort(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
            if (requestJSONObject.optBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT)) {
                query.addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING);
            } else {
                query.addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING);
            }
            final String keyword = requestJSONObject.optString(Common.KEYWORD);
            if (StringUtils.isBlank(keyword)) {
                if (Solos.GEN_STATIC_SITE) {
                    // 生成静态站点时不包括加密文章
                    query.setFilter(CompositeFilterOperator.and(
                            new PropertyFilter(Article.ARTICLE_VIEW_PWD, FilterOperator.EQUAL, ""),
                            new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, articleStatus)));
                } else {
                    query.setFilter(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, articleStatus));
                }
            } else {
                query.setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, articleStatus),
                        CompositeFilterOperator.or(
                                new PropertyFilter(Article.ARTICLE_TITLE, FilterOperator.LIKE, "%" + keyword + "%"),
                                new PropertyFilter(Article.ARTICLE_TAGS_REF, FilterOperator.LIKE, "%" + keyword + "%"),
                                new PropertyFilter(Article.ARTICLE_CONTENT, FilterOperator.LIKE, "%" + keyword + "%"))));
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
                article.put(Article.ARTICLE_CREATE_TIME, article.getLong(Article.ARTICLE_CREATED));
                article.put(Article.ARTICLE_UPDATE_TIME, article.getLong(Article.ARTICLE_UPDATED));

                // Remove unused properties
                for (int j = 0; j < excludes.length(); j++) {
                    article.remove(excludes.optString(j));
                }
            }

            ret.put(Article.ARTICLES, articles);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles failed", e);

            return null;
        }
    }

    /**
     * Gets a list of published articles with the specified tag id, current page number and page size.
     *
     * @param tagId          the specified tag id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return result, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getArticlesByTag(final String tagId, final int currentPageNum, final int pageSize) throws ServiceException {
        try {
            JSONObject result = tagArticleRepository.getByTagId(tagId, currentPageNum, pageSize);
            if (null == result) {
                return null;
            }
            final JSONArray tagArticleRelations = result.getJSONArray(Keys.RESULTS);
            if (0 == tagArticleRelations.length()) {
                return null;
            }
            final JSONObject pagination = result.optJSONObject(Pagination.PAGINATION);

            final Set<String> articleIds = new HashSet<>();
            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation = tagArticleRelations.getJSONObject(i);
                final String articleId = tagArticleRelation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);
                articleIds.add(articleId);
            }

            final List<JSONObject> retArticles = new ArrayList<>();

            final Query query = new Query().setFilter(CompositeFilterOperator.and(
                    new PropertyFilter(Keys.OBJECT_ID, FilterOperator.IN, articleIds),
                    new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED))).
                    setPageCount(1).
                    addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
            final List<JSONObject> articles = articleRepository.getList(query);
            for (final JSONObject article : articles) {
                article.put(Article.ARTICLE_CREATE_TIME, article.getLong(Article.ARTICLE_CREATED));
                article.put(Article.ARTICLE_T_CREATE_DATE, new Date(article.getLong(Article.ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(Article.ARTICLE_UPDATED)));
                retArticles.add(article);
            }
            final JSONObject ret = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            ret.put(Keys.RESULTS, (Object) retArticles);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles by tag [id=" + tagId + "] failed", e);
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
    public List<JSONObject> getArticlesByArchiveDate(final String archiveDateId, final int currentPageNum, final int pageSize) throws ServiceException {
        try {
            final String tablePrefix = Latkes.getLocalProperty("jdbc.tablePrefix") + "_";
            final List<JSONObject> ret = new ArrayList<>();
            final String query = "SELECT\n" +
                    "\t*\n" +
                    "FROM\n" +
                    "\t" + tablePrefix + "article AS a,\n" +
                    "\t" + tablePrefix + "archivedate_article aa\n" +
                    "WHERE\n" +
                    "\taa.archiveDate_oId = ?\n" +
                    "AND a.oId = aa.article_oId\n" +
                    "AND a.articleStatus = 0\n" +
                    "ORDER BY\n" +
                    "\ta.oId DESC\n" +
                    "LIMIT ?,\n" +
                    " ?";
            final List<JSONObject> articles = articleRepository.select(query, archiveDateId, (currentPageNum - 1) * pageSize, pageSize);
            for (final JSONObject article : articles) {
                article.put(Article.ARTICLE_CREATE_TIME, article.getLong(Article.ARTICLE_CREATED));
                article.put(Article.ARTICLE_T_CREATE_DATE, new Date(article.getLong(Article.ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(Article.ARTICLE_UPDATED)));
                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles by archive date [id=" + archiveDateId + "] failed", e);
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
     */
    public List<JSONObject> getRelevantArticles(final JSONObject article, final JSONObject preference) {
        try {
            final int displayCnt = preference.getInt(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
            final String[] tagTitles = article.getString(Article.ARTICLE_TAGS_REF).split(",");
            final int maxTagCnt = displayCnt > tagTitles.length ? tagTitles.length : displayCnt;
            final String articleId = article.getString(Keys.OBJECT_ID);

            final List<JSONObject> articles = new ArrayList<>();

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

                    if (Article.ARTICLE_STATUS_C_PUBLISHED != relevant.optInt(Article.ARTICLE_STATUS)) {
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
            final List<JSONObject> ret = new ArrayList<>();
            for (final int index : randomIntegers) {
                ret.add(articles.get(index));
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets relevant articles failed", e);

            return Collections.emptyList();
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
     */
    public JSONObject getArticleById(final String articleId) {
        try {
            return articleRepository.get(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Gets an article [id=" + articleId + "] failed", e);

            return null;
        }
    }

    /**
     * Gets <em>published</em> articles by the specified author id, current page number and page size.
     *
     * @param authorId       the specified author id
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return result
     * @throws ServiceException service exception
     */
    public JSONObject getArticlesByAuthorId(final String authorId, final int currentPageNum, final int pageSize) throws ServiceException {
        try {
            final JSONObject ret = articleRepository.getByAuthorId(authorId, currentPageNum, pageSize);
            final JSONArray articles = ret.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                article.put(Article.ARTICLE_CREATE_TIME, article.getLong(Article.ARTICLE_CREATED));
                article.put(Article.ARTICLE_T_CREATE_DATE, new Date(article.optLong(Article.ARTICLE_CREATED)));
                article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(Article.ARTICLE_UPDATED)));
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets articles by author id failed [authorId=" + authorId + ", currentPageNum=" + currentPageNum + ", pageSize=" + pageSize + "]", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets article contents with the specified article id.
     * <p>
     * Invoking this method dose not effect on article view count.
     * </p>
     *
     * @param context   the specified HTTP request context
     * @param articleId the specified article id
     * @return article contents, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public String getArticleContent(final RequestContext context, final String articleId) throws ServiceException {
        if (StringUtils.isBlank(articleId)) {
            return null;
        }

        try {
            final JSONObject article = articleRepository.get(articleId);
            if (null == article) {
                return null;
            }

            if (null != context && Solos.needViewPwd(context, article)) {
                final String content = langPropsService.get("articleContentPwd");
                article.put(Article.ARTICLE_CONTENT, content);
            } else {
                // Markdown to HTML for content and abstract
                Stopwatchs.start("Get Article Content [Markdown]");
                String content = article.optString(Article.ARTICLE_CONTENT);
                content = Markdowns.toHTML(content);
                article.put(Article.ARTICLE_CONTENT, content);
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
     */
    public void markdowns(final List<JSONObject> articles) {
        for (final JSONObject article : articles) {
            markdown(article);
        }
    }

    /**
     * Converts the content and abstract for the specified article to HTML if it is saved by Markdown editor.
     *
     * @param article the specified article
     */
    public void markdown(final JSONObject article) {
        Stopwatchs.start("Markdown Article [id=" + article.optString(Keys.OBJECT_ID) + "]");

        String content = article.optString(Article.ARTICLE_CONTENT);
        content = Markdowns.toHTML(content);
        article.put(Article.ARTICLE_CONTENT, content);

        String abstractContent = article.optString(Article.ARTICLE_ABSTRACT);
        if (StringUtils.isNotBlank(abstractContent)) {
            Stopwatchs.start("Abstract");
            abstractContent = Markdowns.toHTML(abstractContent);
            article.put(Article.ARTICLE_ABSTRACT, abstractContent);
            Stopwatchs.end();
        }

        Stopwatchs.end();
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
    private void removeUnusedProperties(final List<JSONObject> articles) {
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
    private void removeUnusedProperties(final JSONObject article) {
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
        article.remove(Article.ARTICLE_PUT_TOP);
        article.remove(Article.ARTICLE_VIEW_PWD);
        article.remove(Article.ARTICLE_SIGN_ID);
        article.remove(Article.ARTICLE_COMMENTABLE);
    }
}
