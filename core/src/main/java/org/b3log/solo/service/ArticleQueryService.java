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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import static org.b3log.solo.model.Article.*;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Sign;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleRepositoryImpl;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Article query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @author <a href="http://blog.sweelia.com">ArmstrongCN</a>
 * @version 1.0.1.2, Jan 30, 2013
 * @since 0.3.5
 */
public final class ArticleQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleQueryService.class.getName());

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagRepositoryImpl.getInstance();

    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository = TagArticleRepositoryImpl.getInstance();

    /**
     * Archive date-Article repository.
     */
    private ArchiveDateArticleRepository archiveDateArticleRepository = ArchiveDateArticleRepositoryImpl.getInstance();

    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();

    /**
     * Gets the recent articles with the specified fetch size.
     * 
     * @param fetchSize the specified fetch size
     * @return a list of json object, its size less or equal to the specified
     * fetch size
     * @throws ServiceException service exception 
     */
    public List<JSONObject> getRecentArticles(final int fetchSize) throws ServiceException {
        try {
            return articleRepository.getRecentArticles(fetchSize);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets recent articles failed", e);

            return Collections.emptyList();
        }
    }

    /**
     * Gets an article by the specified article id.
     * 
     * <p>
     *   <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return for example,
     * <pre>
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

            article.put(Sign.SIGNS, new JSONArray(preference.getString(Preference.SIGNS)));

            // Remove unused properties
            article.remove(ARTICLE_AUTHOR_EMAIL);
            article.remove(ARTICLE_COMMENT_COUNT);
            article.remove(ARTICLE_IS_PUBLISHED);
            article.remove(ARTICLE_PUT_TOP);
            article.remove(ARTICLE_UPDATE_DATE);
            article.remove(ARTICLE_VIEW_COUNT);
            article.remove(ARTICLE_RANDOM_DOUBLE);

            LOGGER.log(Level.FINER, "Got an article[id={0}]", articleId);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets articles(by crate date descending) by the specified request json
     * object.
     *
     * <p>
     * If the property "articleIsPublished" of the specified request json object is {@code true}, the returned articles all are published, 
     * {@code false} otherwise.
     * </p>
     * 
     * <p>
     * Specified the "excludes" for results properties exclusion.
     * </p>
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10,
     *     "articleIsPublished": boolean,
     *     "excludes": ["", ....] // Optional
     * }, see {@link Pagination} for more details
     * </pre>
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

            final Query query = new Query().setCurrentPageNum(currentPageNum).setPageSize(pageSize).addSort(ARTICLE_PUT_TOP, SortDirection.DESCENDING).addSort(ARTICLE_CREATE_DATE, SortDirection.DESCENDING).setFilter(
                new PropertyFilter(ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, articleIsPublished));

            int articleCount = statistics.getBlogArticleCount();

            if (!articleIsPublished) {
                articleCount -= statistics.getPublishedBlogArticleCount();
            } else {
                articleCount = statistics.getPublishedBlogArticleCount();
            }
            
            final int pageCount = (int) Math.ceil((double) articleCount / (double) pageSize);

            query.setPageCount(pageCount);

            final JSONObject result = articleRepository.get(query);

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
                final JSONObject author = articleUtils.getAuthor(article);
                final String authorName = author.getString(User.USER_NAME);

                article.put(Common.AUTHOR_NAME, authorName);

                article.put(ARTICLE_CREATE_TIME, ((Date) article.get(ARTICLE_CREATE_DATE)).getTime());
                article.put(ARTICLE_UPDATE_TIME, ((Date) article.get(ARTICLE_UPDATE_DATE)).getTime());

                // Markdown to HTML for content and abstract
                markdown(article);

                // Remove unused properties
                for (int j = 0; j < excludes.length(); j++) {
                    article.remove(excludes.optString(j));
                }
            }

            ret.put(ARTICLES, articles);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets articles failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets a list of published articles with the specified tag id, current page number and page size.
     * 
     * @param tagId the specified tag id
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
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

            final Set<String> articleIds = new HashSet<String>();

            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation = tagArticleRelations.getJSONObject(i);
                final String articleId = tagArticleRelation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);

                articleIds.add(articleId);
            }

            final List<JSONObject> ret = new ArrayList<JSONObject>();

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
                
                article.put(ARTICLE_CREATE_TIME, ((Date) article.get(ARTICLE_CREATE_DATE)).getTime());

                // Markdown to HTML for content and abstract
                markdown(article);

                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets articles by tag[id=" + tagId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets a list of published articles with the specified archive date id, current page number and page size.
     * 
     * @param archiveDateId the specified archive date id
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
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

            final Set<String> articleIds = new HashSet<String>();

            for (int i = 0; i < relations.length(); i++) {
                final JSONObject relation = relations.getJSONObject(i);
                final String articleId = relation.getString(Article.ARTICLE + "_" + Keys.OBJECT_ID);

                articleIds.add(articleId);
            }

            final List<JSONObject> ret = new ArrayList<JSONObject>();

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
                
                article.put(ARTICLE_CREATE_TIME, ((Date) article.get(ARTICLE_CREATE_DATE)).getTime());

                // Markdown to HTML for content and abstract
                markdown(article);

                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets articles by archive date[id=" + archiveDateId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets a list of articles randomly with the specified fetch size.
     * 
     * <p>
     *   <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param fetchSize the specified fetch size
     * @return a list of json objects, its size less or equal to the specified
     * fetch size
     * @throws ServiceException service exception 
     */
    public List<JSONObject> getArticlesRandomly(final int fetchSize) throws ServiceException {
        try {
            final List<JSONObject> ret = articleRepository.getRandomly(fetchSize);

            removeUnusedProperties(ret);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets articles randomly failed[fetchSize=" + fetchSize + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the relevant published articles of the specified article.
     * 
     * <p>
     *   <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param article the specified article
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getRelevantArticles(final JSONObject article, final JSONObject preference)
        throws ServiceException {
        try {
            final int displayCnt = preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT);
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

            Collections.sort(articles, Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
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
            LOGGER.log(Level.SEVERE, "Gets relevant articles failed", e);

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
            LOGGER.log(Level.SEVERE, "Determines the article publish status failed[articleId=" + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the next article(by create date) by the specified article id.
     * 
     * <p>
     *   <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     * @throws ServiceException service exception 
     */
    public JSONObject getNextArticle(final String articleId) throws ServiceException {
        try {
            return articleRepository.getNextArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets the next article failed[articleId=" + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the previous article(by create date) by the specified article id.
     * 
     * <p>
     *   <b>Note</b>: The article content and abstract is raw (no editor type processing).
     * </p>
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     * @throws ServiceException service exception 
     */
    public JSONObject getPreviousArticle(final String articleId) throws ServiceException {
        try {
            return articleRepository.getPreviousArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets the previous article failed[articleId=" + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets an article by the specified article id.
     * 
     * <p>
     *   <b>Note</b>: The article content and abstract is raw (no editor type processing).
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
            LOGGER.log(Level.SEVERE, "Gets an article[articleId=" + articleId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets <em>published</em> articles by the specified author email, current page number and page size.
     * 
     * @param authorEmail the specified author email
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception 
     */
    public List<JSONObject> getArticlesByAuthorEmail(final String authorEmail, final int currentPageNum, final int pageSize)
        throws ServiceException {
        try {
            final JSONObject result = articleRepository.getByAuthorEmail(authorEmail, currentPageNum, pageSize);
            final JSONArray articles = result.getJSONArray(Keys.RESULTS);
            final List<JSONObject> ret = new ArrayList<JSONObject>();

            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);

                article.put(ARTICLE_CREATE_TIME, ((Date) article.get(ARTICLE_CREATE_DATE)).getTime());
                
                // Markdown to HTML for content and abstract
                markdown(article);

                ret.add(article);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE,
                "Gets articles by author email failed[authorEmail=" + authorEmail + ", currentPageNum=" + currentPageNum + ", pageSize="
                + pageSize + "]",
                e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets article contents with the specified article id.
     * 
     * <p>
     * Invoking this method dose not effect on article view count.
     * </p>
     * 
     * @param articleId the specified article id
     * @return article contents, returns {@code null} if not found
     * @throws ServiceException service exception 
     */
    public String getArticleContent(final String articleId) throws ServiceException {
        if (Strings.isEmptyOrNull(articleId)) {
            return null;
        }

        try {
            final JSONObject article = articleRepository.get(articleId);

            if (null == article) {
                return null;
            }

            // Markdown to HTML for content and abstract
            if ("CodeMirror-Markdown".equals(article.optString(ARTICLE_EDITOR_TYPE))) {
                Stopwatchs.start("Get Article Content [Markdown]");
                final String content = article.optString(ARTICLE_CONTENT);

                article.put(ARTICLE_CONTENT, Markdowns.toHTML(content));
                Stopwatchs.end();
            }

            return article.getString(Article.ARTICLE_CONTENT);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets article content failed[articleId=" + articleId + "]", e);

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
            final String content = article.optString(ARTICLE_CONTENT);

            article.put(ARTICLE_CONTENT, Markdowns.toHTML(content));
            Stopwatchs.end();

            final String abstractContent = article.optString(ARTICLE_ABSTRACT);

            if (!Strings.isEmptyOrNull(abstractContent)) {
                Stopwatchs.start("Abstract");
                article.put(ARTICLE_ABSTRACT, Markdowns.toHTML(abstractContent));
                Stopwatchs.end();
            }

            Stopwatchs.end();
        }
    }

    /**
     * Removes unused properties of each article in the specified articles.
     * 
     * <p>
     * Remains the following properties:
     * <ul>
     *   <li>{@link Article#ARTICLE_TITLE article title}</li>
     *   <li>{@link Article#ARTICLE_PERMALINK article permalink}</li>
     * </ul>
     * </p>
     * 
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
     * 
     * <p>
     * Remains the following properties:
     * <ul>
     *   <li>{@link Article#ARTICLE_TITLE article title}</li>
     *   <li>{@link Article#ARTICLE_PERMALINK article permalink}</li>
     * </ul>
     * </p>
     * 
     * @param article the specified article
     * @see #removeUnusedProperties(java.util.List) 
     */
    public void removeUnusedProperties(final JSONObject article) {
        article.remove(Keys.OBJECT_ID);
        article.remove(Article.ARTICLE_AUTHOR_EMAIL);
        article.remove(Article.ARTICLE_ABSTRACT);
        article.remove(Article.ARTICLE_COMMENT_COUNT);
        article.remove(Article.ARTICLE_CONTENT);
        article.remove(Article.ARTICLE_CREATE_DATE);
        article.remove(Article.ARTICLE_TAGS_REF);
        article.remove(Article.ARTICLE_UPDATE_DATE);
        article.remove(Article.ARTICLE_VIEW_COUNT);
        article.remove(Article.ARTICLE_RANDOM_DOUBLE);
        article.remove(Article.ARTICLE_IS_PUBLISHED);
        article.remove(Article.ARTICLE_PUT_TOP);
        article.remove(Article.ARTICLE_HAD_BEEN_PUBLISHED);
    }

    /**
     * Gets the {@link ArticleQueryService} singleton.
     *
     * @return the singleton
     */
    public static ArticleQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private ArticleQueryService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 3, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final ArticleQueryService SINGLETON = new ArticleQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
