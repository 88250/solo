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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URI;
import java.util.*;

/**
 * Import service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.8, Jun 16, 2020
 * @since 2.2.0
 */
@Service
public class ImportService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ImportService.class);

    /**
     * Default tag.
     */
    private static final String DEFAULT_TAG = "Note";

    /**
     * Article management service.
     */
    @Inject
    private ArticleMgmtService articleMgmtService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Imports markdown files as articles. See <a href="https://ld246.com/article/1498490209748">Solo 支持 Hexo/Jekyll 数据导入</a> for more details.
     */
    public void importMarkdowns() {
        new Thread(() -> {
            try {
                final URI uri = Skins.class.getResource("/markdowns").toURI();
                if ("jar".equals(uri.getScheme())) {
                    LOGGER.info("Ignored import markdowns when running in jar");
                    return;
                }
            } catch (final Exception e) {
                return;
            }

            final File markdownsPath = Latkes.getFile("/markdowns");
            importMarkdownDir(markdownsPath);
        }).start();
    }

    /**
     * Imports markdown files under the specified markdown files dir.
     *
     * @param markdownsDir the specified markdown files dir
     * @return <pre>
     * {
     *     "failCount": int,
     *     "succCnt": int
     * }
     * </pre>
     */
    public JSONObject importMarkdownDir(final File markdownsDir) {
        LOGGER.debug("Import directory [" + markdownsDir.getPath() + "]");

        final JSONObject admin = userQueryService.getAdmin();
        if (null == admin) { // Not init yet
            return null;
        }

        final String adminId = admin.optString(Keys.OBJECT_ID);

        int succCnt = 0, failCnt = 0;
        final Set<String> failSet = new TreeSet<>();
        final Collection<File> mds = FileUtils.listFiles(markdownsDir, new String[]{"md"}, true);
        if (mds.isEmpty()) {
            return null;
        }

        for (final File md : mds) {
            final String fileName = md.getName();
            if (StringUtils.equalsIgnoreCase(fileName, "README.md")) {
                continue;
            }

            try {
                final String fileContent = FileUtils.readFileToString(md, "UTF-8");
                final JSONObject article = parseArticle(fileName, fileContent);
                article.put(Article.ARTICLE_AUTHOR_ID, adminId);

                final JSONObject request = new JSONObject();
                request.put(Article.ARTICLE, article);

                final String id = articleMgmtService.addArticle(request);
                FileUtils.moveFile(md, new File(md.getPath() + "." + id));
                LOGGER.info("Imported article [" + article.optString(Article.ARTICLE_TITLE) + "]");
                succCnt++;
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Import file [" + fileName + "] failed", e);

                failCnt++;
                failSet.add(fileName);
            }
        }

        if (0 == succCnt && 0 == failCnt) {
            return null;
        }

        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("[").append(succCnt).append("] imported, [").append(failCnt).append("] failed");
        if (failCnt > 0) {
            logBuilder.append(": ").append(Strings.LINE_SEPARATOR);

            for (final String fail : failSet) {
                logBuilder.append("    ").append(fail).append(Strings.LINE_SEPARATOR);
            }
        } else {
            logBuilder.append(" :p");
        }
        LOGGER.info(logBuilder.toString());
        return new JSONObject().put("failCount", failCnt).put("succCount", succCnt);
    }

    private JSONObject parseArticle(final String fileName, String fileContent) {
        fileContent = StringUtils.trim(fileContent);
        String frontMatter = StringUtils.substringBefore(fileContent, "---");
        if (StringUtils.isBlank(frontMatter)) {
            fileContent = StringUtils.substringAfter(fileContent, "---");
            frontMatter = StringUtils.substringBefore(fileContent, "---");
        }

        final JSONObject ret = new JSONObject();
        final Yaml yaml = new Yaml();
        Map elems;

        try {
            elems = (Map) yaml.load(frontMatter);
        } catch (final Exception e) {
            // treat it as plain markdown
            ret.put(Article.ARTICLE_TITLE, StringUtils.substringBeforeLast(fileName, "."));
            ret.put(Article.ARTICLE_CONTENT, fileContent);
            ret.put(Article.ARTICLE_ABSTRACT, Article.getAbstractText(fileContent));
            ret.put(Article.ARTICLE_TAGS_REF, DEFAULT_TAG);
            ret.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
            ret.put(Article.ARTICLE_VIEW_PWD, "");
            return ret;
        }

        String title = (String) elems.get("title");
        if (StringUtils.isBlank(title)) {
            title = StringUtils.substringBeforeLast(fileName, ".");
        }
        ret.put(Article.ARTICLE_TITLE, title);

        String content = StringUtils.substringAfter(fileContent, frontMatter);
        if (StringUtils.startsWith(content, "---")) {
            content = StringUtils.substringAfter(content, "---");
            content = StringUtils.trim(content);
        }
        ret.put(Article.ARTICLE_CONTENT, content);

        final String abs = parseAbstract(elems, content);
        ret.put(Article.ARTICLE_ABSTRACT, abs);

        Date date = parseDate(elems);
        ret.put(Article.ARTICLE_CREATED, date.getTime());

        // 文章 id 必须使用存档时间戳，否则生成的存档时间会是当前时间：导入 Markdown 文件存档时间问题 https://github.com/88250/solo/issues/112
        // 另外，如果原文中存在重复时间，则需要增加随机数避免 id 重复：自动生成的文章链接重复问题优化 https://github.com/88250/solo/issues/147
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, RandomUtils.nextInt(256));
        date = calendar.getTime();
        ret.put(Keys.OBJECT_ID, String.valueOf(date.getTime()));

        final String permalink = (String) elems.get("permalink");
        if (StringUtils.isNotBlank(permalink)) {
            ret.put(Article.ARTICLE_PERMALINK, permalink);
        }

        final List<String> tags = parseTags(elems);
        final StringBuilder tagBuilder = new StringBuilder();
        for (final String tag : tags) {
            tagBuilder.append(tag).append(",");
        }
        tagBuilder.deleteCharAt(tagBuilder.length() - 1);
        ret.put(Article.ARTICLE_TAGS_REF, tagBuilder.toString());
        ret.put(Article.ARTICLE_STATUS, Article.ARTICLE_STATUS_C_PUBLISHED);
        ret.put(Article.ARTICLE_VIEW_PWD, "");
        return ret;
    }

    private String parseAbstract(final Map map, final String content) {
        String ret = (String) map.get("description");
        if (null == ret) {
            ret = (String) map.get("summary");
        }
        if (null == ret) {
            ret = (String) map.get("abstract");
        }
        if (StringUtils.isNotBlank(ret)) {
            return ret;
        }
        return Article.getAbstractText(content);
    }

    private Date parseDate(final Map map) {
        Object date = map.get("date");
        if (null == date) {
            return new Date();
        }

        if (date instanceof String) {
            try {
                return DateUtils.parseDate((String) date, new String[]{
                        "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm:ss",
                        "dd-MM-yyyy HH:mm:ss", "yyyyMMdd HH:mm:ss",
                        "yyyy/MM/dd HH:mm", "yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm",
                        "dd-MM-yyyy HH:mm", "yyyyMMdd HH:mm"});
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Parse date [" + date + "] failed", e);

                throw new RuntimeException(e);
            }
        } else if (date instanceof Date) {
            return (Date) date;
        }
        return new Date();
    }

    private List<String> parseTags(final Map map) {
        final List<String> ret = new ArrayList<>();

        Object tags = map.get("tags");
        if (null == tags) {
            tags = map.get("category");
        }
        if (null == tags) {
            tags = map.get("categories");
        }
        if (null == tags) {
            tags = map.get("keyword");
        }
        if (null == tags) {
            tags = map.get("keywords");
        }
        if (null == tags) {
            ret.add(DEFAULT_TAG);
            return ret;
        }

        if (tags instanceof String) {
            final String[] tagArr = ((String) tags).split(" ");
            tags = Arrays.asList(tagArr);
        }
        final TreeSet tagSet = new TreeSet();
        for (final String tag : (List<String>) tags) {
            if (StringUtils.isBlank(tag)) {
                tagSet.add(DEFAULT_TAG);
            } else {
                tagSet.add(tag);
            }
        }
        ret.addAll(tagSet);
        return ret;
    }
}
