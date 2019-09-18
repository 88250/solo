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

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.io.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.util.Solos;
import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Export service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.1, Sep 18, 2019
 * @since 2.5.0
 */
@Service
public class ExportService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ExportService.class);

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
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Category repository.
     */
    @Inject
    private CategoryRepository categoryRepository;

    /**
     * Category-Tag relation repository.
     */
    @Inject
    private CategoryTagRepository categoryTagRepository;

    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;

    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;

    /**
     * Plugin repository.
     */
    @Inject
    private PluginRepository pluginRepository;

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
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Exports public articles to admin's GitHub repos. 博文定时同步 GitHub 仓库 https://hacpai.com/article/1557238327458
     */
    public void exportGitHubRepo() {
        LOGGER.log(Level.INFO, "Github repo syncing....");
        try {
            final JSONObject preference = optionQueryService.getPreference();
            if (null == preference) {
                return;
            }

            if (!preference.optBoolean(Option.ID_C_SYNC_GITHUB)) {
                return;
            }

            if (Latkes.getServePath().contains("localhost") || Strings.isIPv4(Latkes.getServerHost())) {
                return;
            }

            if (Latkes.RuntimeMode.PRODUCTION != Latkes.getRuntimeMode()) {
                return;
            }

            final JSONObject mds = exportHexoMDs();
            final List<JSONObject> posts = (List<JSONObject>) mds.opt("posts");

            final String tmpDir = System.getProperty("java.io.tmpdir");
            final String date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
            String localFilePath = tmpDir + File.separator + "solo-hexo-" + date;
            final File localFile = new File(localFilePath);

            final File postDir = new File(localFilePath + File.separator + "posts");
            exportHexoMd(posts, postDir.getPath());

            final File zipFile = ZipUtil.zip(localFile);
            byte[] zipData;
            try (final FileInputStream inputStream = new FileInputStream(zipFile)) {
                zipData = IOUtils.toByteArray(inputStream);
            }

            FileUtils.deleteQuietly(localFile);
            FileUtils.deleteQuietly(zipFile);

            final JSONObject user = userRepository.getAdmin();
            final String userName = user.optString(User.USER_NAME);
            final String userB3Key = user.optString(UserExt.USER_B3_KEY);
            final String clientTitle = preference.optString(Option.ID_C_BLOG_TITLE);
            final String clientSubtitle = preference.optString(Option.ID_C_BLOG_SUBTITLE);

            final Set<String> articleIds = new HashSet<>();
            final Filter published = new PropertyFilter(Article.ARTICLE_STATUS, FilterOperator.EQUAL, Article.ARTICLE_STATUS_C_PUBLISHED);

            final StringBuilder bodyBuilder = new StringBuilder("### 最新\n");
            final List<JSONObject> recentArticles = articleRepository.getList(new Query().setFilter(published).select(Keys.OBJECT_ID, Article.ARTICLE_TITLE, Article.ARTICLE_PERMALINK).addSort(Article.ARTICLE_CREATED, SortDirection.DESCENDING).setPage(1, 20));
            for (final JSONObject article : recentArticles) {
                final String title = article.optString(Article.ARTICLE_TITLE);
                final String link = Latkes.getServePath() + article.optString(Article.ARTICLE_PERMALINK);
                bodyBuilder.append("\n* [").append(title).append("](").append(link).append(")");
                articleIds.add(article.optString(Keys.OBJECT_ID));
            }
            bodyBuilder.append("\n\n");

            final StringBuilder mostViewBuilder = new StringBuilder();
            final List<JSONObject> mostViewArticles = articleRepository.getList(new Query().setFilter(published).select(Keys.OBJECT_ID, Article.ARTICLE_TITLE, Article.ARTICLE_PERMALINK).addSort(Article.ARTICLE_VIEW_COUNT, SortDirection.DESCENDING).setPage(1, 40));
            int count = 0;
            for (final JSONObject article : mostViewArticles) {
                final String articleId = article.optString(Keys.OBJECT_ID);
                if (!articleIds.contains(articleId)) {
                    final String title = article.optString(Article.ARTICLE_TITLE);
                    final String link = Latkes.getServePath() + article.optString(Article.ARTICLE_PERMALINK);
                    mostViewBuilder.append("\n* [").append(title).append("](").append(link).append(")");
                    articleIds.add(articleId);
                    count++;
                }
                if (20 <= count) {
                    break;
                }
            }
            if (0 < mostViewBuilder.length()) {
                bodyBuilder.append("### 热门\n").append(mostViewBuilder).append("\n\n");
            }

            final StringBuilder mostCmtBuilder = new StringBuilder();
            final List<JSONObject> mostCmtArticles = articleRepository.getList(new Query().setFilter(published).select(Keys.OBJECT_ID, Article.ARTICLE_TITLE, Article.ARTICLE_PERMALINK).addSort(Article.ARTICLE_COMMENT_COUNT, SortDirection.DESCENDING).setPage(1, 60));
            count = 0;
            for (final JSONObject article : mostCmtArticles) {
                final String articleId = article.optString(Keys.OBJECT_ID);
                if (!articleIds.contains(articleId)) {
                    final String title = article.optString(Article.ARTICLE_TITLE);
                    final String link = Latkes.getServePath() + article.optString(Article.ARTICLE_PERMALINK);
                    mostCmtBuilder.append("\n* [").append(title).append("](").append(link).append(")");
                    articleIds.add(articleId);
                    count++;
                }
                if (20 <= count) {
                    break;
                }
            }
            if (0 < mostCmtBuilder.length()) {
                bodyBuilder.append("### 热议\n").append(mostCmtBuilder);
            }

            final HttpResponse response = HttpRequest.post("https://hacpai.com/github/repos").
                    connectionTimeout(7000).timeout(60000).trustAllCerts(true).header("User-Agent", Solos.USER_AGENT).
                    form("userName", userName,
                            "userB3Key", userB3Key,
                            "clientName", "Solo",
                            "clientVersion", SoloServletListener.VERSION,
                            "clientHost", Latkes.getServePath(),
                            "clientFavicon", preference.optString(Option.ID_C_FAVICON_URL),
                            "clientTitle", clientTitle,
                            "clientSubtitle", clientSubtitle,
                            "clientBody", bodyBuilder.toString(),
                            "file", zipData).send();
            response.close();
            response.charset("UTF-8");
            LOGGER.info("Github repo sync completed: " + response.bodyText());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Exports articles to github repo failed", e);
        } finally {
            LOGGER.log(Level.INFO, "Github repo synced");
        }
    }

    /**
     * Exports the specified articles to the specified dir path.
     *
     * @param articles the specified articles
     * @param dirPath  the specified dir path
     */
    public void exportHexoMd(final List<JSONObject> articles, final String dirPath) {
        articles.forEach(article -> {
            final String filename = Solos.sanitizeFilename(article.optString("title")) + ".md";
            final String text = article.optString("front") + "---" + Strings.LINE_SEPARATOR + article.optString("content");

            try {
                final String date = DateFormatUtils.format(article.optLong("created"), "yyyyMM");
                final String dir = dirPath + File.separator + date + File.separator;
                new File(dir).mkdirs();
                FileUtils.writeStringToFile(new File(dir + filename), text, "UTF-8");
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Write markdown file failed", e);
            }
        });
    }

    /**
     * Exports as Hexo markdown format.
     *
     * @return posts, password posts and drafts, <pre>
     * {
     *     "posts": [
     *         {
     *             "front": "", // yaml front matter,
     *             "title": "",
     *             "content": "",
     *             "created": long
     *         }, ....
     *     ],
     *     "passwords": [], // format is same as post
     *     "drafts": [] // format is same as post
     * }
     * </pre>
     */
    public JSONObject exportHexoMDs() {
        final JSONObject ret = new JSONObject();
        final List<JSONObject> posts = new ArrayList<>();
        ret.put("posts", (Object) posts);
        final List<JSONObject> passwords = new ArrayList<>();
        ret.put("passwords", (Object) passwords);
        final List<JSONObject> drafts = new ArrayList<>();
        ret.put("drafts", (Object) drafts);

        final JSONArray articles = getJSONs(articleRepository);
        for (int i = 0; i < articles.length(); i++) {
            final JSONObject article = articles.optJSONObject(i);
            final Map<String, Object> front = new LinkedHashMap<>();
            final String title = article.optString(Article.ARTICLE_TITLE);
            front.put("title", title);
            final String date = DateFormatUtils.format(article.optLong(Article.ARTICLE_CREATED), "yyyy-MM-dd HH:mm:ss");
            front.put("date", date);
            front.put("updated", DateFormatUtils.format(article.optLong(Article.ARTICLE_UPDATED), "yyyy-MM-dd HH:mm:ss"));
            final List<String> tags = Arrays.stream(article.optString(Article.ARTICLE_TAGS_REF).split(",")).filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
            if (tags.isEmpty()) {
                tags.add("Solo");
            }
            front.put("tags", tags);
            front.put("permalink", article.optString(Article.ARTICLE_PERMALINK));
            final JSONObject one = new JSONObject();
            one.put("front", new Yaml().dump(front));
            one.put("title", title);
            one.put("content", article.optString(Article.ARTICLE_CONTENT));
            one.put("created", article.optLong(Article.ARTICLE_CREATED));

            if (StringUtils.isNotBlank(article.optString(Article.ARTICLE_VIEW_PWD))) {
                passwords.add(one);

                continue;
            } else if (Article.ARTICLE_STATUS_C_PUBLISHED == article.optInt(Article.ARTICLE_STATUS)) {
                posts.add(one);

                continue;
            } else {
                drafts.add(one);
            }
        }

        return ret;
    }

    /**
     * Gets all data as JSON format.
     */
    public JSONObject getJSONs() {
        final JSONObject ret = new JSONObject();
        final JSONArray archiveDates = getJSONs(archiveDateRepository);
        ret.put(ArchiveDate.ARCHIVE_DATES, archiveDates);

        final JSONArray archiveDateArticles = getJSONs(archiveDateArticleRepository);
        ret.put(ArchiveDate.ARCHIVE_DATE + "_" + Article.ARTICLE, archiveDateArticles);

        final JSONArray articles = getJSONs(articleRepository);
        ret.put(Article.ARTICLES, articles);

        final JSONArray categories = getJSONs(categoryRepository);
        ret.put(Category.CATEGORIES, categories);

        final JSONArray categoryTags = getJSONs(categoryTagRepository);
        ret.put(Category.CATEGORY + "_" + Tag.TAG, categoryTags);

        final JSONArray comments = getJSONs(commentRepository);
        ret.put(Comment.COMMENTS, comments);

        final JSONArray links = getJSONs(linkRepository);
        ret.put(Link.LINKS, links);

        final JSONArray options = getJSONs(optionRepository);
        ret.put(Option.OPTIONS, options);

        final JSONArray pages = getJSONs(pageRepository);
        ret.put(Page.PAGES, pages);

        final JSONArray plugins = getJSONs(pluginRepository);
        ret.put(Plugin.PLUGINS, plugins);

        final JSONArray tags = getJSONs(tagRepository);
        ret.put(Tag.TAGS, tags);

        final JSONArray tagArticles = getJSONs(tagArticleRepository);
        ret.put(Tag.TAG + "_" + Article.ARTICLES, tagArticles);

        final JSONArray users = getJSONs(userRepository);
        ret.put(User.USERS, users);

        return ret;
    }

    private JSONArray getJSONs(final Repository repository) {
        try {
            return repository.get(new Query()).optJSONArray(Keys.RESULTS);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets data from repository [" + repository.getName() + "] failed", e);

            return new JSONArray();
        }
    }
}
