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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Session;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.http.renderer.TextHtmlRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Dates;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.Server;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.*;
import org.b3log.solo.processor.console.ConsoleRenderer;
import org.b3log.solo.service.*;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Solos;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.*;

/**
 * Article processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/ZephyrJung">Zephyr</a>
 * @version 2.0.0.3, Jul 8, 2020
 * @since 0.3.1
 */
@Singleton
public class ArticleProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ArticleProcessor.class);

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * DataModelService.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Archive date query service.
     */
    @Inject
    private ArchiveDateQueryService archiveDateQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Article management service.
     */
    @Inject
    private ArticleMgmtService articleMgmtService;

    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;

    /**
     * Markdowns.
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "html": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified request context
     */
    public void markdown2HTML(final RequestContext context) {
        final JSONObject result = Solos.newSucc();
        context.renderJSON(result);

        final String markdownText = context.requestJSON().optString("markdownText");
        if (StringUtils.isBlank(markdownText)) {
            result.put(Common.DATA, "");
            return;
        }

        if (!Solos.isLoggedIn(context)) {
            result.put(Keys.CODE, -1);
            result.put(Keys.MSG, langPropsService.get("getFailLabel"));
            return;
        }

        try {
            final String html = Markdowns.toHTML(markdownText);
            result.put(Common.DATA, html);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            result.put(Keys.CODE, -1);
            result.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Shows the article view password form.
     *
     * @param context the specified context
     */
    public void showArticlePwdForm(final RequestContext context) {
        final String articleId = context.param("articleId");
        if (StringUtils.isBlank(articleId)) {
            context.sendError(404);
            return;
        }

        final JSONObject article = articleQueryService.getArticleById(articleId);
        if (null == article) {
            context.sendError(404);
            return;
        }

        final AbstractFreeMarkerRenderer renderer = new ConsoleRenderer(context, "article-pwd.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put("articleId", articleId);
        dataModel.put("articlePermalink", article.optString(Article.ARTICLE_PERMALINK));
        dataModel.put("articleTitle", article.optString(Article.ARTICLE_TITLE));
        dataModel.put("articleAbstract", article.optString(Article.ARTICLE_ABSTRACT));
        final String msg = context.param(Keys.MSG);

        if (StringUtils.isNotBlank(msg)) {
            dataModel.put(Keys.MSG, langPropsService.get("passwordNotMatchLabel"));
        }

        final Map<String, String> langs = langPropsService.getAll(Latkes.getLocale());
        dataModel.putAll(langs);

        final JSONObject preference = optionQueryService.getPreference();
        dataModel.put(Option.ID_C_BLOG_TITLE, preference.getString(Option.ID_C_BLOG_TITLE));
        dataModel.put(Common.VERSION, Server.VERSION);
        dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
        dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        Keys.fillRuntime(dataModel);
        dataModelService.fillFaviconURL(dataModel, preference);
        dataModelService.fillUsite(dataModel);
    }

    /**
     * Processes the article view password form submits.
     *
     * @param context the specified context
     */
    public void onArticlePwdForm(final RequestContext context) {
        try {
            final Request request = context.getRequest();
            final String articleId = context.param("articleId");
            final String pwdTyped = context.param("pwdTyped");

            final JSONObject article = articleQueryService.getArticleById(articleId);
            if (article.getString(Article.ARTICLE_VIEW_PWD).equals(pwdTyped)) {
                final Session session = request.getSession();
                if (null != session) {
                    JSONObject viewPwds;
                    final String viewPwdsStr = session.getAttribute(Common.ARTICLES_VIEW_PWD);
                    if (null == viewPwdsStr) {
                        viewPwds = new JSONObject();
                    } else {
                        viewPwds = new JSONObject(viewPwdsStr);
                    }

                    viewPwds.put(articleId, pwdTyped);
                    session.setAttribute(Common.ARTICLES_VIEW_PWD, viewPwds.toString());
                }

                context.sendRedirect(Latkes.getServePath() + article.getString(Article.ARTICLE_PERMALINK));
                return;
            }

            context.sendRedirect(Latkes.getServePath() + "/console/article-pwd?articleId=" + article.optString(Keys.OBJECT_ID) + "&msg=1");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Processes article view password form submits failed", e);
            context.sendError(404);
        }
    }

    /**
     * Gets random articles with the specified context.
     *
     * @param context the specified context
     */
    public void getRandomArticles(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();

        final JSONObject preference = optionQueryService.getPreference();
        final int displayCnt = preference.getInt(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
        if (0 == displayCnt) {
            jsonObject.put(Common.RANDOM_ARTICLES, new ArrayList<JSONObject>());
            final JsonRenderer renderer = new JsonRenderer();
            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);
            return;
        }

        Stopwatchs.start("Get Random Articles");
        final List<JSONObject> randomArticles = getRandomArticles(preference);
        jsonObject.put(Common.RANDOM_ARTICLES, randomArticles);
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
        Stopwatchs.end();
    }

    /**
     * Gets relevant articles with the specified context.
     *
     * @param context the specified context
     */
    public void getRelevantArticles(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();

        final JSONObject preference = optionQueryService.getPreference();
        final int displayCnt = preference.getInt(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT);
        if (0 == displayCnt) {
            jsonObject.put(Common.RANDOM_ARTICLES, new ArrayList<JSONObject>());

            final JsonRenderer renderer = new JsonRenderer();
            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);
            return;
        }

        final Request request = context.getRequest();
        Stopwatchs.start("Get Relevant Articles");
        final String articleId = context.pathVar("id");
        if (StringUtils.isBlank(articleId)) {
            context.sendError(404);
            return;
        }

        final JSONObject article = articleQueryService.getArticleById(articleId);
        if (null == article) {
            context.sendError(404);
            return;
        }

        final List<JSONObject> relevantArticles = articleQueryService.getRelevantArticles(article, preference);
        jsonObject.put(Common.RELEVANT_ARTICLES, relevantArticles);

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        Stopwatchs.end();
    }

    /**
     * Gets article content with the specified context.
     *
     * @param context the specified context
     */
    public void getArticleContent(final RequestContext context) {
        final String articleId = context.param("id");
        if (StringUtils.isBlank(articleId)) {
            return;
        }

        final TextHtmlRenderer renderer = new TextHtmlRenderer();
        context.setRenderer(renderer);

        String content;
        try {
            content = articleQueryService.getArticleContent(context, articleId);
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, "Can not get article content", e);
            return;
        }

        if (null == content) {
            return;
        }

        renderer.setContent(content);
    }

    /**
     * Gets articles paged with the specified context.
     *
     * @param context the specified context
     */
    public void getArticlesByPage(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();
        final Request request = context.getRequest();
        final int currentPageNum = Paginator.getPage(request);

        Stopwatchs.start("Get Articles Paged [pageNum=" + currentPageNum + ']');
        try {
            jsonObject.put(Keys.CODE, StatusCodes.SUCC);

            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(currentPageNum).append('/').append(pageSize).append('/').append(windowSize);

            final JSONObject requestJSONObject = Solos.buildPaginationRequest(pathBuilder.toString());
            requestJSONObject.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
            requestJSONObject.put(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT, preference.optBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT));
            final JSONObject result = articleQueryService.getArticles(requestJSONObject);
            final List<JSONObject> articles = (List<JSONObject>) result.opt(Article.ARTICLES);
            dataModelService.setArticlesExProperties(context, articles, preference);

            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            LOGGER.log(Level.ERROR, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Gets tag articles paged with the specified context.
     *
     * @param context the specified context
     */
    public void getTagArticlesByPage(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();

        final Request request = context.getRequest();
        final String tagTitle = context.pathVar("tagTitle");
        final int currentPageNum = Paginator.getPage(request);
        Stopwatchs.start("Get Tag-Articles Paged [tagTitle=" + tagTitle + ", pageNum=" + currentPageNum + ']');
        try {
            jsonObject.put(Keys.CODE, StatusCodes.SUCC);

            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

            final JSONObject tagQueryResult = tagQueryService.getTagByTitle(tagTitle);
            if (null == tagQueryResult) {
                throw new Exception("Can not found tag [title=" + tagTitle + "]");
            }

            final JSONObject tag = tagQueryResult.getJSONObject(Tag.TAG);
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final JSONObject tagArticleResult = articleQueryService.getArticlesByTag(tagId, currentPageNum, pageSize);
            if (null == tagArticleResult) {
                throw new Exception("Can not found tag [title=" + tagTitle + "]'s articles");
            }

            final List<JSONObject> articles = (List<JSONObject>) tagArticleResult.opt(Keys.RESULTS);
            final int pageCount = tagArticleResult.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            dataModelService.setArticlesExProperties(context, articles, preference);

            final JSONObject result = new JSONObject();
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            result.put(Pagination.PAGINATION, pagination);
            result.put(Article.ARTICLES, articles);
            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            LOGGER.log(Level.ERROR, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Gets tag articles paged with the specified context.
     *
     * @param context the specified context
     */
    public void getArchivesArticlesByPage(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();

        final Request request = context.getRequest();
        final String archiveDateString = context.pathVar("yyyy") + "/" + context.pathVar("MM");
        final int currentPageNum = Paginator.getPage(request);

        Stopwatchs.start("Get Archive-Articles Paged [archive=" + archiveDateString + ", pageNum=" + currentPageNum + ']');
        try {
            jsonObject.put(Keys.CODE, StatusCodes.SUCC);

            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

            final JSONObject archiveQueryResult = archiveDateQueryService.getByArchiveDateString(archiveDateString);
            if (null == archiveQueryResult) {
                throw new Exception("Can not found archive [archiveDate=" + archiveDateString + "]");
            }

            final JSONObject archiveDate = archiveQueryResult.getJSONObject(ArchiveDate.ARCHIVE_DATE);
            final String archiveDateId = archiveDate.getString(Keys.OBJECT_ID);

            final int articleCount = archiveDateQueryService.getArchiveDatePublishedArticleCount(archiveDateId);
            final int pageCount = (int) Math.ceil((double) articleCount / (double) pageSize);

            final List<JSONObject> articles = articleQueryService.getArticlesByArchiveDate(archiveDateId, currentPageNum, pageSize);
            dataModelService.setArticlesExProperties(context, articles, preference);

            final JSONObject result = new JSONObject();
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            result.put(Pagination.PAGINATION, pagination);
            result.put(Article.ARTICLES, articles);
            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            LOGGER.log(Level.ERROR, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Gets author articles paged with the specified context.
     *
     * @param context the specified context
     */
    public void getAuthorsArticlesByPage(final RequestContext context) {
        final JSONObject jsonObject = new JSONObject();

        final Request request = context.getRequest();
        final String authorId = context.pathVar("author");
        final int currentPageNum = Paginator.getPage(request);

        Stopwatchs.start("Get Author-Articles Paged [authorId=" + authorId + ", pageNum=" + currentPageNum + ']');
        try {
            jsonObject.put(Keys.CODE, StatusCodes.SUCC);

            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

            final JSONObject authorRet = userQueryService.getUser(authorId);
            if (null == authorRet) {
                context.sendError(404);
                return;
            }

            final JSONObject articlesResult = articleQueryService.getArticlesByAuthorId(authorId, currentPageNum, pageSize);
            final List<JSONObject> articles = (List<JSONObject>) articlesResult.opt(Keys.RESULTS);
            dataModelService.setArticlesExProperties(context, articles, preference);
            final int pageCount = articlesResult.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject result = new JSONObject();
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            result.put(Pagination.PAGINATION, pagination);
            result.put(Article.ARTICLES, articles);
            jsonObject.put(Keys.RESULTS, result);
        } catch (final Exception e) {
            jsonObject.put(Keys.CODE, StatusCodes.ERR);
            LOGGER.log(Level.ERROR, "Gets article paged failed", e);
        } finally {
            Stopwatchs.end();
        }

        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);
    }

    /**
     * Shows author articles with the specified context.
     *
     * @param context the specified context
     */
    public void showAuthorArticles(final RequestContext context) {
        final Request request = context.getRequest();
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "author-articles.ftl");

        try {
            final String authorId = context.pathVar("author");
            final int currentPageNum = Paginator.getPage(request);
            LOGGER.log(Level.DEBUG, "Request author articles [authorId={}, currentPageNum={}]", authorId, currentPageNum);

            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                context.sendError(404);
                return;
            }

            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final JSONObject result = userQueryService.getUser(authorId);
            if (null == result) {
                context.sendError(404);
                return;
            }

            final JSONObject articlesResult = articleQueryService.getArticlesByAuthorId(authorId, currentPageNum, pageSize);
            if (null == articlesResult) {
                context.sendError(404);
                return;
            }

            final List<JSONObject> articles = (List<JSONObject>) articlesResult.opt(Keys.RESULTS);
            dataModelService.setArticlesExProperties(context, articles, preference);
            final int pageCount = articlesResult.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

            final Map<String, Object> dataModel = renderer.getDataModel();
            final JSONObject author = result.getJSONObject(User.USER);
            prepareShowAuthorArticles(pageNums, dataModel, pageCount, currentPageNum, articles, author);
            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) context.attr(Keys.TEMPLATE_DIR_NAME), dataModel);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            context.sendError(404);
        }
    }

    /**
     * Shows archive articles with the specified context.
     *
     * @param context the specified context
     */
    public void showArchiveArticles(final RequestContext context) {
        final Request request = context.getRequest();
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "archive-articles.ftl");

        try {
            final int currentPageNum = Paginator.getPage(request);
            final String archiveDateString = context.pathVar("yyyy") + "/" + context.pathVar("MM");
            LOGGER.log(Level.DEBUG, "Request archive date [string={}, currentPageNum={}]", archiveDateString, currentPageNum);
            final JSONObject result = archiveDateQueryService.getByArchiveDateString(archiveDateString);
            if (null == result) {
                LOGGER.log(Level.DEBUG, "Can not find articles for the specified archive date[string={}]", archiveDateString);
                context.sendError(404);
                return;
            }

            final JSONObject archiveDate = result.getJSONObject(ArchiveDate.ARCHIVE_DATE);
            final String archiveDateId = archiveDate.getString(Keys.OBJECT_ID);

            final JSONObject preference = optionQueryService.getPreference();
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);

            final int articleCount = archiveDateQueryService.getArchiveDatePublishedArticleCount(archiveDateId);
            final int pageCount = (int) Math.ceil((double) articleCount / (double) pageSize);

            final List<JSONObject> articles = articleQueryService.getArticlesByArchiveDate(archiveDateId, currentPageNum, pageSize);
            if (articles.isEmpty()) {
                context.sendError(404);
                return;
            }

            dataModelService.setArticlesExProperties(context, articles, preference);

            final Map<String, Object> dataModel = renderer.getDataModel();
            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) context.attr(Keys.TEMPLATE_DIR_NAME), dataModel);
            prepareShowArchiveArticles(preference, dataModel, articles, currentPageNum, pageCount, archiveDateString, archiveDate);
            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            context.sendError(404);
        }
    }

    /**
     * Shows an article with the specified context.
     *
     * @param context the specified context
     */
    public void showArticle(final RequestContext context) {
        // See PermalinkHandler#dispatchToArticleProcessor()
        final JSONObject article = (JSONObject) context.attr(Article.ARTICLE);
        if (null == article) {
            context.sendError(404);
            return;
        }

        final String articleId = article.optString(Keys.OBJECT_ID);
        LOGGER.log(Level.DEBUG, "Article [id={}]", articleId);

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "article.ftl");

        try {
            LOGGER.log(Level.TRACE, "Article [title={}]", article.getString(Article.ARTICLE_TITLE));
            articleQueryService.markdown(article);

            article.put(Article.ARTICLE_T_CREATE_DATE, new Date(article.optLong(Article.ARTICLE_CREATED)));
            article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(Article.ARTICLE_UPDATED)));
            // For <meta name="description" content="${article.articleAbstract}"/>
            final String metaDescription = Jsoup.parse(article.optString(Article.ARTICLE_ABSTRACT)).text();
            article.put(Article.ARTICLE_ABSTRACT, metaDescription);
            final JSONObject preference = optionQueryService.getPreference();
            if (preference.getBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleQueryService.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            final JSONObject author = articleQueryService.getAuthor(article);
            final String authorName = author.getString(User.USER_NAME);
            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);
            article.put(Common.AUTHOR_ID, authorId);
            article.put(Common.AUTHOR_ROLE, author.getString(User.USER_ROLE));
            final String userAvatar = author.optString(UserExt.USER_AVATAR);
            article.put(Common.AUTHOR_THUMBNAIL_URL, userAvatar);
            dataModelService.fillCategory(article);
            final Map<String, Object> dataModel = renderer.getDataModel();

            prepareShowArticle(preference, dataModel, article);

            dataModelService.fillCommon(context, dataModel, preference);
            dataModelService.fillFaviconURL(dataModel, preference);
            dataModelService.fillUsite(dataModel);
            Skins.fillLangs(preference.optString(Option.ID_C_LOCALE_STRING), (String) context.attr(Keys.TEMPLATE_DIR_NAME), dataModel);

            // Fire [Before Render Article] event
            final JSONObject eventData = new JSONObject();
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventSynchronously(new Event<>(EventTypes.BEFORE_RENDER_ARTICLE, eventData));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            context.sendError(404);
        }
    }

    /**
     * Gets the random articles.
     *
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     */
    private List<JSONObject> getRandomArticles(final JSONObject preference) {
        try {
            final int displayCnt = preference.getInt(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT);
            final List<JSONObject> ret = articleQueryService.getArticlesRandomly(displayCnt);
            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Prepares the specified data model for rendering author articles.
     *
     * @param pageNums       the specified page numbers
     * @param dataModel      the specified data model
     * @param pageCount      the specified page count
     * @param currentPageNum the specified current page number
     * @param articles       the specified articles
     * @param author         the specified author
     */
    private void prepareShowAuthorArticles(final List<Integer> pageNums,
                                           final Map<String, Object> dataModel,
                                           final int pageCount,
                                           final int currentPageNum,
                                           final List<JSONObject> articles,
                                           final JSONObject author) {
        if (0 != pageNums.size()) {
            dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
            dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
        }
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);
        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
        }

        dataModel.put(Article.ARTICLES, articles);
        final String authorId = author.optString(Keys.OBJECT_ID);
        dataModel.put(Common.PATH, "/authors/" + authorId);
        dataModel.put(Keys.OBJECT_ID, authorId);
        dataModel.put(Common.AUTHOR_NAME, author.optString(User.USER_NAME));
        final String userAvatar = author.optString(UserExt.USER_AVATAR);
        dataModel.put(Common.AUTHOR_THUMBNAIL_URL, userAvatar);
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
    }

    /**
     * Prepares the specified data model for rendering archive articles.
     *
     * @param preference        the specified preference
     * @param dataModel         the specified data model
     * @param articles          the specified articles
     * @param currentPageNum    the specified current page number
     * @param pageCount         the specified page count
     * @param archiveDateString the specified archive data string
     * @param archiveDate       the specified archive date
     * @return page title for caching
     */
    private String prepareShowArchiveArticles(final JSONObject preference,
                                              final Map<String, Object> dataModel,
                                              final List<JSONObject> articles,
                                              final int currentPageNum,
                                              final int pageCount,
                                              final String archiveDateString,
                                              final JSONObject archiveDate) {
        final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
        final int windowSize = preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

        final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

        dataModel.put(Article.ARTICLES, articles);
        final String previousPageNum = Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);

        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM, "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum + 1);
        }
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
        dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        dataModel.put(Common.PATH, "/archives/" + archiveDateString);
        dataModel.put(Keys.OBJECT_ID, archiveDate.getString(Keys.OBJECT_ID));

        final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
        final String dateString = DateFormatUtils.format(time, "yyyy/MM");
        final String[] dateStrings = dateString.split("/");
        final String year = dateStrings[0];
        final String month = dateStrings[1];

        archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);
        final String language = Locales.getLanguage(preference.getString(Option.ID_C_LOCALE_STRING));
        String ret;

        if ("en".equals(language)) {
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, Dates.EN_MONTHS.get(month));
            ret = Dates.EN_MONTHS.get(month) + " " + year;
        } else {
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
            ret = year + " " + dataModel.get("yearLabel") + " " + month + " " + dataModel.get("monthLabel");
        }
        dataModel.put(ArchiveDate.ARCHIVE_DATE, archiveDate);
        return ret;
    }

    /**
     * Prepares the specified data model for rendering article.
     *
     * @param preference the specified preference
     * @param dataModel  the specified data model
     * @param article    the specified article
     * @throws Exception exception
     */
    private void prepareShowArticle(final JSONObject preference, final Map<String, Object> dataModel, final JSONObject article) throws Exception {
        article.put(Common.PERMALINK, article.getString(Article.ARTICLE_PERMALINK));
        dataModel.put(Article.ARTICLE, article);
        final String articleId = article.getString(Keys.OBJECT_ID);

        Stopwatchs.start("Get Article Sign");
        LOGGER.debug("Getting article sign....");
        final JSONObject sign = articleQueryService.getSign(article.getString(Article.ARTICLE_SIGN_ID), preference);
        final String articleTitle = article.optString(Article.ARTICLE_TITLE);
        final String author = article.optString(Common.AUTHOR_NAME);
        final String url = Latkes.getServePath() + article.optString(Article.ARTICLE_PERMALINK);
        String signHtml = sign.optString(Sign.SIGN_HTML);
        // 签名档内置模板变量 https://github.com/b3log/solo/issues/12758
        signHtml = StringUtils.replace(signHtml, "{title}", articleTitle);
        signHtml = StringUtils.replace(signHtml, "{author}", author);
        signHtml = StringUtils.replace(signHtml, "{url}", url);
        signHtml = StringUtils.replace(signHtml, "{blog}", Latkes.getServePath());
        sign.put(Sign.SIGN_HTML, signHtml);
        article.put(Common.ARTICLE_SIGN, sign);
        LOGGER.debug("Got article sign");
        Stopwatchs.end();

        Stopwatchs.start("Get Next Article");
        LOGGER.debug("Getting the next article....");
        final JSONObject nextArticle = articleQueryService.getNextArticle(articleId);

        if (null != nextArticle) {
            dataModel.put(Common.NEXT_ARTICLE_PERMALINK, nextArticle.getString(Article.ARTICLE_PERMALINK));
            dataModel.put(Common.NEXT_ARTICLE_TITLE, nextArticle.getString(Article.ARTICLE_TITLE));
            dataModel.put(Common.NEXT_ARTICLE_ABSTRACT, nextArticle.getString(Article.ARTICLE_ABSTRACT));
            LOGGER.debug("Got the next article");
        }
        Stopwatchs.end();

        Stopwatchs.start("Get Previous Article");
        LOGGER.debug("Getting the previous article....");
        final JSONObject previousArticle = articleQueryService.getPreviousArticle(articleId);
        if (null != previousArticle) {
            dataModel.put(Common.PREVIOUS_ARTICLE_PERMALINK, previousArticle.getString(Article.ARTICLE_PERMALINK));
            dataModel.put(Common.PREVIOUS_ARTICLE_TITLE, previousArticle.getString(Article.ARTICLE_TITLE));
            dataModel.put(Common.PREVIOUS_ARTICLE_ABSTRACT, previousArticle.getString(Article.ARTICLE_ABSTRACT));
            LOGGER.debug("Got the previous article");
        }
        Stopwatchs.end();

        dataModel.put(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT, preference.getInt(Option.ID_C_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT));
        dataModel.put(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT, preference.getInt(Option.ID_C_RANDOM_ARTICLES_DISPLAY_CNT));
        dataModel.put(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT, preference.getInt(Option.ID_C_RELEVANT_ARTICLES_DISPLAY_CNT));
    }
}
