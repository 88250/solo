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
package org.b3log.solo.processor.util;


import freemarker.template.Template;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.model.User;
import org.b3log.latke.plugin.ViewLoadEventData;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.*;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.ArchiveDateRepositoryImpl;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.StatisticQueryService;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Tags;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Filler utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.6.4, Feb 5, 2013
 * @since 0.3.1
 */
public final class Filler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Filler.class.getName());

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();

    /**
     * Comment repository.
     */
    private CommentRepository commentRepository = CommentRepositoryImpl.getInstance();

    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository = ArchiveDateRepositoryImpl.getInstance();

    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagRepositoryImpl.getInstance();

    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();

    /**
     * Tag utilities.
     */
    private Tags tagUtils = Tags.getInstance();

    /**
     * Link repository.
     */
    private LinkRepository linkRepository = LinkRepositoryImpl.getInstance();

    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();

    /**
     * Statistic query service.
     */
    private StatisticQueryService statisticQueryService = StatisticQueryService.getInstance();

    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();

    /**
     * Article query service.
     */
    private ArticleQueryService articleQueryService = ArticleQueryService.getInstance();

    /**
     * {@code true} for published.
     */
    private static final boolean PUBLISHED = true;

    /**
     * Fills articles in index.ftl.
     *
     * @param request the specified HTTP servlet request
     * @param dataModel data model
     * @param currentPageNum current page number
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillIndexArticles(final HttpServletRequest request,
        final Map<String, Object> dataModel, final int currentPageNum, final JSONObject preference)
        throws ServiceException {
        Stopwatchs.start("Fill Index Articles");

        try {
            final int pageSize = preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final JSONObject statistic = statisticQueryService.getStatistic();
            final int publishedArticleCnt = statistic.getInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT);
            final int pageCount = (int) Math.ceil((double) publishedArticleCnt / (double) pageSize);

            final Query query = new Query().setCurrentPageNum(currentPageNum).setPageSize(pageSize).setPageCount(pageCount).setFilter(
                new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, PUBLISHED));

            final Template template = Templates.getTemplate((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), "index.ftl");

            boolean isArticles1 = false;

            if (null == template) {
                LOGGER.fine("The skin dose not contain [index.ftl] template");
            } else { // See https://github.com/b3log/b3log-solo/issues/179 for more details
                if (Templates.hasExpression(template, "<#list articles1 as article>")) {
                    isArticles1 = true;
                    query.addSort(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);

                    LOGGER.finest("Query ${articles1} in index.ftl");
                } else { // <#list articles as article>
                    query.addSort(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
                    if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                        query.addSort(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING);
                    } else {
                        query.addSort(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);
                    }
                }
            }

            query.index(Article.ARTICLE_PERMALINK);

            final JSONObject result = articleRepository.get(query);
            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);

            if (0 != pageNums.size()) {
                dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
                dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
            }

            dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final List<JSONObject> articles = org.b3log.latke.util.CollectionUtils.jsonArrayToList(result.getJSONArray(Keys.RESULTS));

            final boolean hasMultipleUsers = Users.getInstance().hasMultipleUsers();

            if (hasMultipleUsers) {
                setArticlesExProperties(articles, preference);
            } else {
                if (!articles.isEmpty()) {
                    final JSONObject author = articleUtils.getAuthor(articles.get(0));

                    setArticlesExProperties(articles, author, preference);
                }
            }

            if (!isArticles1) {
                dataModel.put(Article.ARTICLES, articles);
            } else {
                dataModel.put(Article.ARTICLES + "1", articles);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills index articles failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills index articles failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills links.
     *
     * @param dataModel data model
     * @throws ServiceException service exception
     */
    public void fillLinks(final Map<String, Object> dataModel) throws ServiceException {
        Stopwatchs.start("Fill Links");
        try {
            final Map<String, SortDirection> sorts = new HashMap<String, SortDirection>();

            sorts.put(Link.LINK_ORDER, SortDirection.ASCENDING);
            final Query query = new Query().addSort(Link.LINK_ORDER, SortDirection.ASCENDING).setPageCount(1);
            final JSONObject linkResult = linkRepository.get(query);
            final List<JSONObject> links = org.b3log.latke.util.CollectionUtils.jsonArrayToList(linkResult.getJSONArray(Keys.RESULTS));

            dataModel.put(Link.LINKS, links);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills links failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills links failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
        Stopwatchs.end();
    }

    /**
     * Fills most used tags.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillMostUsedTags(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Most Used Tags");

        try {
            LOGGER.finer("Filling most used tags....");
            final int mostUsedTagDisplayCnt = preference.getInt(Preference.MOST_USED_TAG_DISPLAY_CNT);

            final List<JSONObject> tags = tagRepository.getMostUsedTags(mostUsedTagDisplayCnt);

            tagUtils.removeForUnpublishedArticles(tags);

            dataModel.put(Common.MOST_USED_TAGS, tags);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills most used tags failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills most used tags failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills archive dates.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillArchiveDates(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Archive Dates");

        try {
            LOGGER.finer("Filling archive dates....");
            final List<JSONObject> archiveDates = archiveDateRepository.getArchiveDates();
            final List<JSONObject> archiveDates2 = new ArrayList<JSONObject>();

            dataModel.put(ArchiveDate.ARCHIVE_DATES, archiveDates2);

            if (archiveDates.isEmpty()) {
                return;
            }

            archiveDates2.add(archiveDates.get(0));

            // XXX: Workaround, remove the duplicated archive dates
            for (final JSONObject archiveDate : archiveDates) {
                final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
                final String dateString = DateFormatUtils.format(time, "yyyy/MM");

                final JSONObject last = archiveDates2.get(archiveDates2.size() - 1);
                final String lastDateString = DateFormatUtils.format(last.getLong(ArchiveDate.ARCHIVE_TIME), "yyyy/MM");

                if (!dateString.equals(lastDateString)) {
                    archiveDates2.add(archiveDate);
                } else {
                    LOGGER.log(Level.WARNING, "Found a duplicated archive date [{0}]", dateString);
                }
            }

            final String localeString = preference.getString(Preference.LOCALE_STRING);
            final String language = Locales.getLanguage(localeString);

            for (final JSONObject archiveDate : archiveDates2) {
                final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
                final String dateString = DateFormatUtils.format(time, "yyyy/MM");
                final String[] dateStrings = dateString.split("/");
                final String year = dateStrings[0];
                final String month = dateStrings[1];

                archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);

                archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
                if ("en".equals(language)) {
                    final String monthName = Dates.EN_MONTHS.get(month);

                    archiveDate.put(Common.MONTH_NAME, monthName);
                }
            }

            dataModel.put(ArchiveDate.ARCHIVE_DATES, archiveDates2);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills archive dates failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills archive dates failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills most view count articles.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillMostViewCountArticles(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Most View Articles");
        try {
            LOGGER.finer("Filling the most view count articles....");
            final int mostCommentArticleDisplayCnt = preference.getInt(Preference.MOST_VIEW_ARTICLE_DISPLAY_CNT);
            final List<JSONObject> mostViewCountArticles = articleRepository.getMostViewCountArticles(mostCommentArticleDisplayCnt);

            dataModel.put(Common.MOST_VIEW_COUNT_ARTICLES, mostViewCountArticles);

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Fills most view count articles failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills most comments articles.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillMostCommentArticles(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Most CMMTs Articles");

        try {
            LOGGER.finer("Filling most comment articles....");
            final int mostCommentArticleDisplayCnt = preference.getInt(Preference.MOST_COMMENT_ARTICLE_DISPLAY_CNT);
            final List<JSONObject> mostCommentArticles = articleRepository.getMostCommentArticles(mostCommentArticleDisplayCnt);

            dataModel.put(Common.MOST_COMMENT_ARTICLES, mostCommentArticles);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Fills most comment articles failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills post articles recently.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillRecentArticles(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Recent Articles");

        try {
            final int recentArticleDisplayCnt = preference.getInt(Preference.RECENT_ARTICLE_DISPLAY_CNT);

            final List<JSONObject> recentArticles = articleRepository.getRecentArticles(recentArticleDisplayCnt);

            dataModel.put(Common.RECENT_ARTICLES, recentArticles);

        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills recent articles failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills recent articles failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills post comments recently.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillRecentComments(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Recent Comments");
        try {
            LOGGER.finer("Filling recent comments....");
            final int recentCommentDisplayCnt = preference.getInt(Preference.RECENT_COMMENT_DISPLAY_CNT);

            final List<JSONObject> recentComments = commentRepository.getRecentComments(recentCommentDisplayCnt);

            for (final JSONObject comment : recentComments) {
                final String content = comment.getString(Comment.COMMENT_CONTENT).replaceAll(SoloServletListener.ENTER_ESC, "&nbsp;");

                comment.put(Comment.COMMENT_CONTENT, content);
                comment.put(Comment.COMMENT_NAME, StringEscapeUtils.escapeHtml(comment.getString(Comment.COMMENT_NAME)));
                comment.put(Comment.COMMENT_URL, StringEscapeUtils.escapeHtml(comment.getString(Comment.COMMENT_URL)));

                comment.remove(Comment.COMMENT_EMAIL); // Erases email for security reason
            }

            dataModel.put(Common.RECENT_COMMENTS, recentComments);

        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills recent comments failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills recent comments failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills footer.ftl.
     *
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillBlogFooter(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Footer");
        try {
            LOGGER.finer("Filling footer....");
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);

            dataModel.put(Preference.BLOG_TITLE, blogTitle);
            final String blogHost = preference.getString(Preference.BLOG_HOST);

            dataModel.put(Preference.BLOG_HOST, blogHost);

            dataModel.put(Common.VERSION, SoloServletListener.VERSION);
            dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

            dataModel.put(Keys.Server.STATIC_SERVER, Latkes.getStaticServer());
            dataModel.put(Keys.Server.SERVER, Latkes.getServer());

            // Activates plugins
            try {
                final ViewLoadEventData data = new ViewLoadEventData();

                data.setViewName("footer.ftl");
                data.setDataModel(dataModel);
                EventManager.getInstance().fireEventSynchronously(new Event<ViewLoadEventData>(Keys.FREEMARKER_ACTION, data));
                if (Strings.isEmptyOrNull((String) dataModel.get(Plugin.PLUGINS))) {
                    // There is no plugin for this template, fill ${plugins} with blank.
                    dataModel.put(Plugin.PLUGINS, "");
                }
            } catch (final EventException e) {
                LOGGER.log(Level.WARNING, "Event[FREEMARKER_ACTION] handle failed, ignores this exception for kernel health", e);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills blog footer failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills header.ftl.
     *
     * @param request the specified HTTP servlet request
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillBlogHeader(final HttpServletRequest request, final Map<String, Object> dataModel, final JSONObject preference)
        throws ServiceException {
        Stopwatchs.start("Fill Header");
        try {
            LOGGER.fine("Filling header....");
            dataModel.put(Preference.ARTICLE_LIST_DISPLAY_COUNT, preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT));
            dataModel.put(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE, preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
            dataModel.put(Preference.LOCALE_STRING, preference.getString(Preference.LOCALE_STRING));
            dataModel.put(Preference.BLOG_TITLE, preference.getString(Preference.BLOG_TITLE));
            dataModel.put(Preference.BLOG_SUBTITLE, preference.getString(Preference.BLOG_SUBTITLE));
            dataModel.put(Preference.HTML_HEAD, preference.getString(Preference.HTML_HEAD));
            dataModel.put(Preference.META_KEYWORDS, preference.getString(Preference.META_KEYWORDS));
            dataModel.put(Preference.META_DESCRIPTION, preference.getString(Preference.META_DESCRIPTION));
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

            final String noticeBoard = preference.getString(Preference.NOTICE_BOARD);

            dataModel.put(Preference.NOTICE_BOARD, noticeBoard);

            final Query query = new Query().setPageCount(1);
            final JSONObject result = userRepository.get(query);
            final JSONArray users = result.getJSONArray(Keys.RESULTS);
            final List<JSONObject> userList = CollectionUtils.jsonArrayToList(users);

            dataModel.put(User.USERS, userList);
            for (final JSONObject user : userList) {
                user.remove(User.USER_EMAIL);
                user.remove(User.USER_PASSWORD);
            }

            final String skinDirName = (String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME);

            dataModel.put(Skin.SKIN_DIR_NAME, skinDirName);

            Keys.fillServer(dataModel);
            Keys.fillRuntime(dataModel);
            fillMinified(dataModel);
            fillPageNavigations(dataModel);
            fillStatistic(dataModel);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills blog header failed", e);
            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills blog header failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills minified directory and file postfix for static JavaScript, CSS.
     *
     * @param dataModel the specified data model
     */
    public void fillMinified(final Map<String, Object> dataModel) {
        switch (Latkes.getRuntimeMode()) {
        case DEVELOPMENT:
            dataModel.put(Common.MINI_POSTFIX, "");
            break;

        case PRODUCTION:
            dataModel.put(Common.MINI_POSTFIX, Common.MINI_POSTFIX_VALUE);
            break;

        default:
            throw new AssertionError();
        }
    }

    /**
     * Fills side.ftl.
     *
     * @param request the specified HTTP servlet request
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillSide(final HttpServletRequest request, final Map<String, Object> dataModel, final JSONObject preference)
        throws ServiceException {
        Stopwatchs.start("Fill Side");
        try {
            LOGGER.fine("Filling side....");

            Template template = Templates.getTemplate((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), "side.ftl");

            if (null == template) {
                LOGGER.fine("The skin dose not contain [side.ftl] template");

                template = Templates.getTemplate((String) request.getAttribute(Keys.TEMAPLTE_DIR_NAME), "index.ftl");
                if (null == template) {
                    LOGGER.fine("The skin dose not contain [index.ftl] template");

                    return;
                }

                if (Templates.hasExpression(template, "<#list archiveDates as archiveDate>")) {
                    fillArchiveDates(dataModel, preference);
                }

                return;
            }

            // TODO:       fillRecentArticles(dataModel, preference);
            if (Templates.hasExpression(template, "<#list links as link>")) {
                fillLinks(dataModel);
            }

            if (Templates.hasExpression(template, "<#list recentComments as comment>")) {
                fillRecentComments(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list mostUsedTags as tag>")) {
                fillMostUsedTags(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list mostCommentArticles as article>")) {
                fillMostCommentArticles(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list mostViewCountArticles as article>")) {
                fillMostViewCountArticles(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list archiveDates as archiveDate>")) {
                fillArchiveDates(dataModel, preference);
            }
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Fills side failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills the specified template.
     *
     * @param template the specified template
     * @param dataModel data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillUserTemplate(final Template template, final Map<String, Object> dataModel, final JSONObject preference)
        throws ServiceException {
        Stopwatchs.start("Fill User Template[name=" + template.getName() + "]");
        try {
            LOGGER.log(Level.FINE, "Filling user template[name{0}]", template.getName());

            if (Templates.hasExpression(template, "<#list links as link>")) {
                fillLinks(dataModel);
            }

            if (Templates.hasExpression(template, "<#list recentComments as comment>")) {
                fillRecentComments(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list mostUsedTags as tag>")) {
                fillMostUsedTags(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list mostCommentArticles as article>")) {
                fillMostCommentArticles(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list mostViewCountArticles as article>")) {
                fillMostViewCountArticles(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list archiveDates as archiveDate>")) {
                fillArchiveDates(dataModel, preference);
            }

            final String noticeBoard = preference.getString(Preference.NOTICE_BOARD);

            dataModel.put(Preference.NOTICE_BOARD, noticeBoard);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Fills user template failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills page navigations.
     *
     * @param dataModel data model
     * @throws ServiceException service exception
     */
    private void fillPageNavigations(final Map<String, Object> dataModel) throws ServiceException {
        Stopwatchs.start("Fill Navigations");
        try {
            LOGGER.finer("Filling page navigations....");
            final List<JSONObject> pages = pageRepository.getPages();

            for (final JSONObject page : pages) {
                if ("page".equals(page.optString(Page.PAGE_TYPE))) {
                    final String permalink = page.optString(Page.PAGE_PERMALINK);

                    page.put(Page.PAGE_PERMALINK, Latkes.getServePath() + permalink);
                }
            }

            dataModel.put(Common.PAGE_NAVIGATIONS, pages);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Fills page navigations failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills statistic.
     *
     * @param dataModel data model
     * @throws ServiceException service exception
     */
    private void fillStatistic(final Map<String, Object> dataModel) throws ServiceException {
        Stopwatchs.start("Fill Statistic");
        try {
            LOGGER.finer("Filling statistic....");
            final JSONObject statistic = statisticQueryService.getStatistic();

            dataModel.put(Statistic.STATISTIC, statistic);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Fills statistic failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Sets some extra properties into the specified article with the specified
     * author and preference, performs content and abstract editor processing.
     *
     * <p> Article ext properties:
     * <pre>
     * {
     *     ....,
     *     "authorName": "",
     *     "authorId": "",
     *     "hasUpdated": boolean
     * }
     * </pre> </p>
     *
     * @param article the specified article
     * @param author the specified author
     * @param preference the specified preference
     * @throws ServiceException service exception
     * @see #setArticlesExProperties(java.util.List, org.json.JSONObject)
     */
    private void setArticleExProperties(final JSONObject article, final JSONObject author, final JSONObject preference)
        throws ServiceException {
        try {
            final String authorName = author.getString(User.USER_NAME);

            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);

            article.put(Common.AUTHOR_ID, authorId);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleUtils.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            processArticleAbstract(preference, article);

            articleQueryService.markdown(article);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sets article extra properties failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Sets some extra properties into the specified article with the specified
     * preference, performs content and abstract editor processing.
     *
     * <p> Article ext properties:
     * <pre>
     * {
     *     ....,
     *     "authorName": "",
     *     "authorId": "",
     *     "hasUpdated": boolean
     * }
     * </pre> </p>
     *
     * @param article the specified article
     * @param preference the specified preference
     * @throws ServiceException service exception
     * @see #setArticlesExProperties(java.util.List, org.json.JSONObject)
     */
    private void setArticleExProperties(final JSONObject article, final JSONObject preference) throws ServiceException {
        try {
            final JSONObject author = articleUtils.getAuthor(article);
            final String authorName = author.getString(User.USER_NAME);

            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);

            article.put(Common.AUTHOR_ID, authorId);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleUtils.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            processArticleAbstract(preference, article);

            articleQueryService.markdown(article);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sets article extra properties failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Sets some extra properties into the specified article with the specified
     * author and preference.
     *
     * <p> The batch version of method
     * {@linkplain #setArticleExProperties(org.json.JSONObject, org.json.JSONObject)}.
     * </p>
     *
     * <p> Article ext properties:
     * <pre>
     * {
     *     ....,
     *     "authorName": "",
     *     "authorId": "",
     *     "hasUpdated": boolean
     * }
     * </pre> </p>
     *
     * @param articles the specified articles
     * @param author the specified author
     * @param preference the specified preference
     * @throws ServiceException service exception
     * @see #setArticleExProperties(org.json.JSONObject, org.json.JSONObject)
     */
    public void setArticlesExProperties(final List<JSONObject> articles, final JSONObject author, final JSONObject preference)
        throws ServiceException {
        for (final JSONObject article : articles) {
            setArticleExProperties(article, author, preference);
        }
    }

    /**
     * Sets some extra properties into the specified article with the specified
     * preference.
     *
     * <p> The batch version of method
     * {@linkplain #setArticleExProperties(org.json.JSONObject, org.json.JSONObject)}.
     * </p>
     *
     * <p> Article ext properties:
     * <pre>
     * {
     *     ....,
     *     "authorName": "",
     *     "authorId": "",
     *     "hasUpdated": boolean
     * }
     * </pre> </p>
     *
     * @param articles the specified articles
     * @param preference the specified preference
     * @throws ServiceException service exception
     * @see #setArticleExProperties(org.json.JSONObject, org.json.JSONObject)
     */
    public void setArticlesExProperties(final List<JSONObject> articles, final JSONObject preference)
        throws ServiceException {
        for (final JSONObject article : articles) {
            setArticleExProperties(article, preference);
        }
    }

    /**
     * Processes the abstract of the specified article with the specified preference.
     *
     * <p> 
     *   <ul> 
     *     <li>If the abstract is {@code null}, sets it with ""</li> 
     *     <li>If user configured preference "titleOnly", sets the abstract with ""</li>
     *     <li>If user configured preference "titleAndContent", sets the abstract with the content of the article</li> 
     *   </ul> 
     * </p>
     *
     * @param preference the specified preference
     * @param article the specified article
     */
    private void processArticleAbstract(final JSONObject preference, final JSONObject article) {
        final String articleAbstract = article.optString(Article.ARTICLE_ABSTRACT, null);

        if (null == articleAbstract) {
            article.put(Article.ARTICLE_ABSTRACT, "");
        }

        final String articleListStyle = preference.optString(Preference.ARTICLE_LIST_STYLE);

        if ("titleOnly".equals(articleListStyle)) {
            article.put(Article.ARTICLE_ABSTRACT, "");
        } else if ("titleAndContent".equals(articleListStyle)) {
            article.put(Article.ARTICLE_ABSTRACT, article.optString(Article.ARTICLE_CONTENT));
        }
    }

    /**
     * Gets the {@link Filler} singleton.
     *
     * @return the singleton
     */
    public static Filler getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Filler() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final Filler SINGLETON = new Filler();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
