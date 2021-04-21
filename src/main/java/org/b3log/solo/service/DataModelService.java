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

import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.plugin.ViewLoadEventData;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.*;
import org.b3log.solo.Server;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.util.Markdowns;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Solos;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.*;

import static org.b3log.solo.model.Article.ARTICLE_CONTENT;

/**
 * Data model service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.7.1.3, Apr 21, 2021
 * @since 0.3.1
 */
@Service
public class DataModelService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(DataModelService.class);

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;

    /**
     * Category repository.
     */
    @Inject
    private CategoryRepository categoryRepository;

    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;

    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Category-Tag repository.
     */
    @Inject
    private CategoryTagRepository categoryTagRepository;

    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;

    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Option query service..
     */
    @Inject
    private OptionQueryService optionQueryService;

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
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * Fills articles in index.ftl.
     *
     * @param context        the specified HTTP request context
     * @param dataModel      data model
     * @param currentPageNum current page number
     * @param preference     the specified preference
     * @throws ServiceException service exception
     */
    public void fillIndexArticles(final RequestContext context, final Map<String, Object> dataModel, final int currentPageNum, final JSONObject preference)
            throws ServiceException {
        Stopwatchs.start("Fill Index Articles");

        try {
            final int pageSize = preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final Query query = new Query().setPage(currentPageNum, pageSize).
                    setFilter(new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED));

            final Template template = Skins.getSkinTemplate(context, "index.ftl");
            boolean isArticles1 = false;
            if (null == template) {
                LOGGER.debug("The skin dose not contain [index.ftl] template");
            } else // See https://github.com/b3log/solo/issues/179 for more details
                if (Templates.hasExpression(template, "<#list articles1 as article>")) {
                    isArticles1 = true;
                    query.addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING);
                    LOGGER.trace("Query ${articles1} in index.ftl");
                } else { // <#list articles as article>
                    query.addSort(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
                    if (preference.getBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT)) {
                        query.addSort(Article.ARTICLE_UPDATED, SortDirection.DESCENDING);
                    } else {
                        query.addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING);
                    }
                }

            final JSONObject articlesResult = articleRepository.get(query);
            final List<JSONObject> articles = (List<JSONObject>) articlesResult.opt(Keys.RESULTS);
            final int pageCount = articlesResult.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
            setArticlesExProperties(context, articles, preference);

            final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
            if (0 != pageNums.size()) {
                dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
                dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
            }
            dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            if (!isArticles1) {
                dataModel.put(Article.ARTICLES, articles);
            } else {
                dataModel.put(Article.ARTICLES + "1", articles);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills index articles failed", e);

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
            final Map<String, SortDirection> sorts = new HashMap<>();

            sorts.put(Link.LINK_ORDER, SortDirection.ASCENDING);
            final Query query = new Query().addSort(Link.LINK_ORDER, SortDirection.ASCENDING).setPageCount(1);
            final List<JSONObject> links = linkRepository.getList(query);

            dataModel.put(Link.LINKS, links);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Fills links failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
        Stopwatchs.end();
    }

    /**
     * Fills tags.
     *
     * @param dataModel data model
     * @throws ServiceException service exception
     */
    public void fillTags(final Map<String, Object> dataModel) throws ServiceException {
        Stopwatchs.start("Fill Tags");
        try {
            final List<JSONObject> tags = tagQueryService.getTagsOfPublishedArticles();
            dataModel.put(Tag.TAGS, tags);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills tags failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }

        Stopwatchs.end();
    }

    /**
     * Fills categories.
     *
     * @param dataModel data model
     * @throws ServiceException service exception
     */
    public void fillCategories(final Map<String, Object> dataModel) throws ServiceException {
        Stopwatchs.start("Fill Categories");

        try {
            LOGGER.debug("Filling categories....");
            final List<JSONObject> categories = categoryRepository.getMostUsedCategories(Integer.MAX_VALUE);
            dataModel.put(Category.CATEGORIES, categories);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Fills categories failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills most used categories.
     *
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillMostUsedCategories(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Most Used Categories");

        try {
            LOGGER.debug("Filling most used categories....");
            final int mostUsedCategoryDisplayCnt = Integer.MAX_VALUE; // XXX: preference instead
            final List<JSONObject> categories = categoryRepository.getMostUsedCategories(mostUsedCategoryDisplayCnt);
            dataModel.put(Common.MOST_USED_CATEGORIES, categories);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Fills most used categories failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills most used tags.
     *
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillMostUsedTags(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Most Used Tags");

        try {
            LOGGER.debug("Filling most used tags....");
            final int mostUsedTagDisplayCnt = preference.getInt(Option.ID_C_MOST_USED_TAG_DISPLAY_CNT);
            final List<JSONObject> tags = tagArticleRepository.getMostUsedTags(mostUsedTagDisplayCnt);
            dataModel.put(Common.MOST_USED_TAGS, tags);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills most used tags failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills archive dates.
     *
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillArchiveDates(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Archive Dates");

        try {
            LOGGER.debug("Filling archive dates....");
            final List<JSONObject> archiveDates = archiveDateRepository.getArchiveDates();
            final List<JSONObject> archiveDates2 = new ArrayList<>();
            dataModel.put(ArchiveDate.ARCHIVE_DATES, archiveDates2);
            if (archiveDates.isEmpty()) {
                return;
            }

            archiveDates2.add(archiveDates.get(0));

            if (1 < archiveDates.size()) { // XXX: Workaround, remove the duplicated archive dates
                for (int i = 1; i < archiveDates.size(); i++) {
                    final JSONObject archiveDate = archiveDates.get(i);

                    final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
                    final String dateString = DateFormatUtils.format(time, "yyyy/MM");

                    final JSONObject last = archiveDates2.get(archiveDates2.size() - 1);
                    final String lastDateString = DateFormatUtils.format(last.getLong(ArchiveDate.ARCHIVE_TIME), "yyyy/MM");

                    if (!dateString.equals(lastDateString)) {
                        archiveDates2.add(archiveDate);
                    } else {
                        LOGGER.log(Level.DEBUG, "Found a duplicated archive date [{}]", dateString);
                    }
                }
            }

            final String localeString = preference.getString(Option.ID_C_LOCALE_STRING);
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
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills archive dates failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills post articles recently.
     *
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillRecentArticles(final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill Recent Articles");

        try {
            final int recentArticleDisplayCnt = preference.getInt(Option.ID_C_RECENT_ARTICLE_DISPLAY_CNT);
            final List<JSONObject> recentArticles = articleRepository.getRecentArticles(recentArticleDisplayCnt);
            dataModel.put(Common.RECENT_ARTICLES, recentArticles);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills recent articles failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills favicon URL. 可配置 favicon 图标路径 https://github.com/b3log/solo/issues/12706
     *
     * @param dataModel  the specified data model
     * @param preference the specified preference
     */
    public void fillFaviconURL(final Map<String, Object> dataModel, final JSONObject preference) {
        if (null == preference) {
            dataModel.put(Common.FAVICON_URL, Option.DefaultPreference.DEFAULT_FAVICON_URL);
        } else {
            dataModel.put(Common.FAVICON_URL, preference.optString(Option.ID_C_FAVICON_URL));
        }
    }

    /**
     * Fills usite. 展示站点连接 https://github.com/b3log/solo/issues/12719
     *
     * @param dataModel the specified data model
     */
    public void fillUsite(final Map<String, Object> dataModel) {
        try {
            final JSONObject usiteOpt = optionQueryService.getOptionById(Option.ID_C_USITE);
            if (null == usiteOpt) {
                return;
            }

            dataModel.put(Option.ID_C_USITE, new JSONObject(usiteOpt.optString(Option.OPTION_VALUE)));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills usite failed", e);
        }
    }

    /**
     * Fills common parts (header, side and footer).
     *
     * @param context    the specified HTTP request context
     * @param dataModel  the specified data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillCommon(final RequestContext context, final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        fillSide(context, dataModel, preference);
        fillBlogHeader(context, dataModel, preference);
        fillBlogFooter(context, dataModel, preference);

        // 支持配置自定义模板变量 https://github.com/b3log/solo/issues/12535
        final Map<String, String> customVars = new HashMap<>();
        final String customVarsStr = preference.optString(Option.ID_C_CUSTOM_VARS);
        final String[] customVarsArray = customVarsStr.split("\\|");
        for (final String customVarPair : customVarsArray) {
            if (StringUtils.isNotBlank(customVarsStr)) {
                final String customVarKey = StringUtils.substringBefore(customVarPair, "=");
                final String customVarVal = StringUtils.substringAfter(customVarPair, "=");
                if (StringUtils.isNotBlank(customVarKey) && StringUtils.isNotBlank(customVarVal)) {
                    customVars.put(customVarKey, customVarVal);
                }
            }
        }
        dataModel.put("customVars", customVars);

        dataModel.put(Common.LUTE_AVAILABLE, Markdowns.LUTE_AVAILABLE);
        String hljsTheme = preference.optString(Option.ID_C_HLJS_THEME);
        if (StringUtils.isBlank(hljsTheme)) {
            hljsTheme = Option.DefaultPreference.DEFAULT_HLJS_THEME;
        }
        dataModel.put(Option.ID_C_HLJS_THEME, hljsTheme);

        String showCodeBlockLn = preference.optString(Option.ID_C_SHOW_CODE_BLOCK_LN);
        if (StringUtils.isBlank(showCodeBlockLn)) {
            showCodeBlockLn = Option.DefaultPreference.DEFAULT_SHOW_CODE_BLOCK_LN;
        }
        dataModel.put(Option.ID_C_SHOW_CODE_BLOCK_LN, showCodeBlockLn);

        String speech = preference.optString(Option.ID_C_SPEECH);
        if (StringUtils.isBlank(speech)) {
            speech = "true";
        }
        dataModel.put(Option.ID_C_SPEECH, Boolean.parseBoolean(speech));

        dataModel.put("staticSite", Solos.GEN_STATIC_SITE);
        if (!Solos.GEN_STATIC_SITE) {
            dataModel.put("pagingSep", "?p=");
        } else {
            dataModel.put("pagingSep", "/p/");
        }
    }

    /**
     * Fills footer.ftl.
     *
     * @param context    the specified HTTP request context
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    private void fillBlogFooter(final RequestContext context, final Map<String, Object> dataModel, final JSONObject preference)
            throws ServiceException {
        Stopwatchs.start("Fill Footer");
        try {
            LOGGER.debug("Filling footer....");
            final String blogTitle = preference.getString(Option.ID_C_BLOG_TITLE);
            dataModel.put(Option.ID_C_BLOG_TITLE, blogTitle);
            dataModel.put("blogHost", Latkes.getServePath());
            dataModel.put(Common.VERSION, Server.VERSION);
            dataModel.put(Common.STATIC_RESOURCE_VERSION, Latkes.getStaticResourceVersion());
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            String footerContent = "";
            final JSONObject opt = optionQueryService.getOptionById(Option.ID_C_FOOTER_CONTENT);
            if (null != opt) {
                footerContent = opt.optString(Option.OPTION_VALUE);
            }
            dataModel.put(Option.ID_C_FOOTER_CONTENT, footerContent);
            dataModel.put(Keys.Server.STATIC_SERVER, Latkes.getStaticServer());
            dataModel.put(Keys.Server.SERVER, Latkes.getServer());
            dataModel.put(Common.IS_INDEX, "/".equals(context.requestURI()));
            dataModel.put(User.USER_NAME, "");
            final JSONObject currentUser = Solos.getCurrentUser(context);
            if (null != currentUser) {
                final String userAvatar = currentUser.optString(UserExt.USER_AVATAR);
                dataModel.put(Common.GRAVATAR, userAvatar);
                dataModel.put(User.USER_NAME, currentUser.optString(User.USER_NAME));
            }

            // Activates plugins
            final ViewLoadEventData data = new ViewLoadEventData();
            data.setViewName("footer.ftl");
            data.setDataModel(dataModel);
            eventManager.fireEventSynchronously(new Event<>(Keys.FREEMARKER_ACTION, data));
            if (StringUtils.isBlank((String) dataModel.get(Plugin.PLUGINS))) {
                // There is no plugin for this template, fill ${plugins} with blank.
                dataModel.put(Plugin.PLUGINS, "");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills blog footer failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills header.ftl.
     *
     * @param context    the specified HTTP request context
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    private void fillBlogHeader(final RequestContext context, final Map<String, Object> dataModel, final JSONObject preference)
            throws ServiceException {
        Stopwatchs.start("Fill Header");
        try {
            LOGGER.debug("Filling header....");
            final String topBarHTML = getTopBarHTML(context);
            dataModel.put(Common.LOGIN_URL, userQueryService.getLoginURL(Common.ADMIN_INDEX_URI));
            dataModel.put(Common.LOGOUT_URL, userQueryService.getLogoutURL());
            dataModel.put(Common.ONLINE_VISITOR_CNT, StatisticQueryService.getOnlineVisitorCount());
            dataModel.put(Common.TOP_BAR, topBarHTML);
            dataModel.put(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT, preference.getInt(Option.ID_C_ARTICLE_LIST_DISPLAY_COUNT));
            dataModel.put(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE, preference.getInt(Option.ID_C_ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
            dataModel.put(Option.ID_C_LOCALE_STRING, preference.getString(Option.ID_C_LOCALE_STRING));
            dataModel.put(Option.ID_C_BLOG_TITLE, preference.getString(Option.ID_C_BLOG_TITLE));
            dataModel.put(Option.ID_C_BLOG_SUBTITLE, preference.getString(Option.ID_C_BLOG_SUBTITLE));
            dataModel.put(Option.ID_C_HTML_HEAD, preference.getString(Option.ID_C_HTML_HEAD));
            String metaKeywords = preference.getString(Option.ID_C_META_KEYWORDS);
            if (StringUtils.isBlank(metaKeywords)) {
                metaKeywords = "";
            }
            dataModel.put(Option.ID_C_META_KEYWORDS, metaKeywords);
            String metaDescription = preference.getString(Option.ID_C_META_DESCRIPTION);
            if (StringUtils.isBlank(metaDescription)) {
                metaDescription = "";
            }
            dataModel.put(Option.ID_C_META_DESCRIPTION, metaDescription);
            dataModel.put(Common.YEAR, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            dataModel.put(Common.IS_LOGGED_IN, null != Solos.getCurrentUser(context));
            final String noticeBoard = preference.getString(Option.ID_C_NOTICE_BOARD);
            dataModel.put(Option.ID_C_NOTICE_BOARD, noticeBoard);
            // 皮肤不显示访客用户 https://github.com/b3log/solo/issues/12752
            final Query query = new Query().setPageCount(1).setFilter(new PropertyFilter(User.USER_ROLE, FilterOperator.NOT_EQUAL, Role.VISITOR_ROLE));
            final List<JSONObject> userList = userRepository.getList(query);
            dataModel.put(User.USERS, userList);
            final JSONObject admin = userRepository.getAdmin();
            dataModel.put(Common.ADMIN_USER, admin);
            final String skinDirName = (String) context.attr(Keys.TEMPLATE_DIR_NAME);
            dataModel.put(Option.ID_C_SKIN_DIR_NAME, skinDirName);
            Keys.fillRuntime(dataModel);
            fillPageNavigations(dataModel);
            fillStatistic(dataModel);
            fillMostUsedTags(dataModel, preference);
            fillArchiveDates(dataModel, preference);
            fillMostUsedCategories(dataModel, preference);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills blog header failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills side.ftl.
     *
     * @param context    the specified HTTP request context
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    private void fillSide(final RequestContext context, final Map<String, Object> dataModel, final JSONObject preference)
            throws ServiceException {
        Stopwatchs.start("Fill Side");
        try {
            LOGGER.debug("Filling side....");

            Template template = Skins.getSkinTemplate(context, "side.ftl");
            if (null == template) {
                LOGGER.debug("The skin dose not contain [side.ftl] template");

                template = Skins.getSkinTemplate(context, "index.ftl");
                if (null == template) {
                    LOGGER.debug("The skin dose not contain [index.ftl] template");
                    return;
                }
            }

            if (Templates.hasExpression(template, "<#list recentArticles as article>")) {
                fillRecentArticles(dataModel, preference);
            }

            if (Templates.hasExpression(template, "<#list links as link>")) {
                fillLinks(dataModel);
            }
        } catch (final ServiceException e) {
            LOGGER.log(Level.ERROR, "Fills side failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills the specified template.
     *
     * @param context    the specified HTTP request context
     * @param template   the specified template
     * @param dataModel  data model
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void fillUserTemplate(final RequestContext context, final Template template,
                                 final Map<String, Object> dataModel, final JSONObject preference) throws ServiceException {
        Stopwatchs.start("Fill User Template[name=" + template.getName() + "]");
        try {
            LOGGER.log(Level.DEBUG, "Filling user template[name{}]", template.getName());

            if (Templates.hasExpression(template, "<#list links as link>")) {
                fillLinks(dataModel);
            }

            if (Templates.hasExpression(template, "<#list tags as tag>")) {
                fillTags(dataModel);
            }

            if (Templates.hasExpression(template, "<#list categories as category>")) {
                fillCategories(dataModel);
            }

            if (Templates.hasExpression(template, "<#include \"side.ftl\"/>")) {
                fillSide(context, dataModel, preference);
            }

            final String noticeBoard = preference.getString(Option.ID_C_NOTICE_BOARD);

            dataModel.put(Option.ID_C_NOTICE_BOARD, noticeBoard);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fills user template failed", e);

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
            LOGGER.debug("Filling page navigations....");
            final List<JSONObject> pages = pageRepository.getPages();
            dataModel.put(Common.PAGE_NAVIGATIONS, pages);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Fills page navigations failed", e);
            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Fills statistic.
     *
     * @param dataModel the specified data model
     */
    private void fillStatistic(final Map<String, Object> dataModel) {
        Stopwatchs.start("Fill Statistic");
        try {
            LOGGER.debug("Filling statistic....");
            final JSONObject statistic = statisticQueryService.getStatistic();
            dataModel.put(Option.CATEGORY_C_STATISTIC, statistic);
        } finally {
            Stopwatchs.end();
        }
    }

    /**
     * Sets some extra properties into the specified article with the specified preference, performs content and abstract editor processing.
     * <p>
     * Article ext properties:
     * <pre>
     * {
     *     ....,
     *     "authorName": "",
     *     "authorId": "",
     *     "authorThumbnailURL": "",
     *     "hasUpdated": boolean
     * }
     * </pre>
     * </p>
     *
     * @param context    the specified HTTP request context
     * @param article    the specified article
     * @param preference the specified preference
     * @throws ServiceException service exception
     * @see #setArticlesExProperties(RequestContext, List, JSONObject)
     */
    private void setArticleExProperties(final RequestContext context, final JSONObject article, final JSONObject preference) throws ServiceException {
        try {
            final JSONObject author = articleQueryService.getAuthor(article);
            final String authorName = author.getString(User.USER_NAME);
            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);
            article.put(Common.AUTHOR_ID, authorId);
            article.put(Article.ARTICLE_T_CREATE_DATE, new Date(article.optLong(Article.ARTICLE_CREATED)));
            article.put(Article.ARTICLE_T_UPDATE_DATE, new Date(article.optLong(Article.ARTICLE_UPDATED)));

            final String userAvatar = author.optString(UserExt.USER_AVATAR);
            article.put(Common.AUTHOR_THUMBNAIL_URL, userAvatar);

            if (preference.getBoolean(Option.ID_C_ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleQueryService.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            if (Solos.needViewPwd(context, article)) {
                final String content = langPropsService.get("articleContentPwd");
                article.put(ARTICLE_CONTENT, content);
            }

            processArticleAbstract(preference, article);

            articleQueryService.markdown(article);

            fillCategory(article);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Sets article extra properties failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Fills category for the specified article.
     *
     * @param article the specified article
     */
    public void fillCategory(final JSONObject article) {
        final String tagsStr = article.optString(Article.ARTICLE_TAGS_REF);
        final String[] tags = tagsStr.split(",");
        JSONObject category = null;
        for (final String tagTitle : tags) {
            final JSONObject c = getCategoryOfTag(tagTitle);
            if (null != c) {
                category = c;
                break;
            }
        }
        article.put(Category.CATEGORY, category);
    }

    /**
     * Gets a category for a tag specified by the given tag title.
     *
     * @param tagTitle the given tag title
     * @return category, returns {@code null} if not found
     */
    private JSONObject getCategoryOfTag(final String tagTitle) {
        try {
            final JSONObject tag = tagRepository.getByTitle(tagTitle);
            if (null == tag) {
                return null;
            }

            final String tagId = tag.optString(Keys.OBJECT_ID);
            final Query query = new Query().setFilter(new PropertyFilter(Tag.TAG + "_" + Keys.OBJECT_ID, FilterOperator.EQUAL, tagId)).
                    setPage(1, 1).setPageCount(1);
            final JSONObject tagCategory = categoryTagRepository.getFirst(query);
            if (null == tagCategory) {
                return null;
            }

            final String categoryId = tagCategory.optString(Category.CATEGORY + "_" + Keys.OBJECT_ID);

            return categoryRepository.get(categoryId);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets category of tag [" + tagTitle + "] failed", e);

            return null;
        }
    }

    /**
     * Sets some extra properties into the specified article with the specified preference.
     * <p>
     * The batch version of method {@linkplain #setArticleExProperties(RequestContext, JSONObject, JSONObject)}.
     * </p>
     * <p>
     * Article ext properties:
     * <pre>
     * {
     *     ....,
     *     "authorName": "",
     *     "authorId": "",
     *     "hasUpdated": boolean
     * }
     * </pre>
     * </p>
     *
     * @param context    the specified HTTP request context
     * @param articles   the specified articles
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    public void setArticlesExProperties(final RequestContext context, final List<JSONObject> articles, final JSONObject preference)
            throws ServiceException {
        for (final JSONObject article : articles) {
            setArticleExProperties(context, article, preference);
        }
    }

    /**
     * Processes the abstract of the specified article with the specified preference.
     * <ul>
     * <li>If the abstract is {@code null}, sets it with ""</li>
     * <li>If user configured preference "titleOnly", sets the abstract with ""</li>
     * <li>If user configured preference "titleAndContent", sets the abstract with the content of the article</li>
     * </ul>
     *
     * @param preference the specified preference
     * @param article    the specified article
     */
    private void processArticleAbstract(final JSONObject preference, final JSONObject article) {
        final String articleAbstract = article.optString(Article.ARTICLE_ABSTRACT);
        if (StringUtils.isBlank(articleAbstract)) {
            article.put(Article.ARTICLE_ABSTRACT, "");
        }
        final String articleAbstractText = article.optString(Article.ARTICLE_ABSTRACT_TEXT);
        if (StringUtils.isBlank(articleAbstractText)) {
            // 发布文章时会自动提取摘要文本，其中如果文章加密且没有写摘要，则自动提取文本会返回空字符串 Article#getAbstractText()
            // 所以当且仅当文章加密且没有摘要的情况下 articleAbstractText 会为空
            final LangPropsService langPropsService = BeanManager.getInstance().getReference(LangPropsService.class);
            article.put(Article.ARTICLE_ABSTRACT_TEXT, langPropsService.get("articleContentPwd"));
        }

        final String articleListStyle = preference.optString(Option.ID_C_ARTICLE_LIST_STYLE);
        if ("titleOnly".equals(articleListStyle)) {
            article.put(Article.ARTICLE_ABSTRACT, "");
        } else if ("titleAndContent".equals(articleListStyle)) {
            article.put(Article.ARTICLE_ABSTRACT, article.optString(Article.ARTICLE_CONTENT));
        }
    }

    /**
     * Generates top bar HTML.
     *
     * @param context the specified request context
     * @return top bar HTML
     * @throws ServiceException service exception
     */
    public String getTopBarHTML(final RequestContext context) throws ServiceException {
        if (Solos.GEN_STATIC_SITE) {
            return "";
        }

        Stopwatchs.start("Gens Top Bar HTML");

        try {
            final Template topBarTemplate = Skins.getTemplate("common-template/top-bar.ftl");
            final StringWriter stringWriter = new StringWriter();
            final Map<String, Object> topBarModel = new HashMap<>();
            final JSONObject currentUser = Solos.getCurrentUser(context);

            Keys.fillServer(topBarModel);
            topBarModel.put(Common.IS_LOGGED_IN, false);
            topBarModel.put(Common.IS_MOBILE_REQUEST, Solos.isMobile(context.getRequest()));
            topBarModel.put("mobileLabel", langPropsService.get("mobileLabel"));
            topBarModel.put("onlineVisitor1Label", langPropsService.get("onlineVisitor1Label"));
            topBarModel.put(Common.ONLINE_VISITOR_CNT, StatisticQueryService.getOnlineVisitorCount());
            if (null == currentUser) {
                topBarModel.put(Common.LOGIN_URL, userQueryService.getLoginURL(Common.ADMIN_INDEX_URI));
                topBarModel.put("startToUseLabel", langPropsService.get("startToUseLabel"));
                topBarTemplate.process(topBarModel, stringWriter);

                return stringWriter.toString();
            }

            topBarModel.put(Common.IS_LOGGED_IN, true);
            topBarModel.put(Common.LOGOUT_URL, userQueryService.getLogoutURL());
            topBarModel.put(Common.IS_ADMIN, Role.ADMIN_ROLE.equals(currentUser.getString(User.USER_ROLE)));
            topBarModel.put(Common.IS_VISITOR, Role.VISITOR_ROLE.equals(currentUser.getString(User.USER_ROLE)));
            topBarModel.put("adminLabel", langPropsService.get("adminLabel"));
            topBarModel.put("logoutLabel", langPropsService.get("logoutLabel"));
            final String userName = currentUser.getString(User.USER_NAME);
            topBarModel.put(User.USER_NAME, userName);
            topBarTemplate.process(topBarModel, stringWriter);

            return stringWriter.toString();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gens top bar HTML failed", e);

            throw new ServiceException(e);
        } finally {
            Stopwatchs.end();
        }
    }
}
