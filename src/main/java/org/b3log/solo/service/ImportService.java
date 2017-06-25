package org.b3log.solo.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Import service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jun 25, 2017
 * @since 2.2.0
 */
@Service
public class ImportService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UpgradeService.class);

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

    public void importMarkdowns() {
        final ServletContext servletContext = SoloServletListener.getServletContext();
        final String markdownsPath = servletContext.getRealPath("markdowns");
        LOGGER.debug("Import directory [" + markdownsPath + "]");

        JSONObject admin;
        try {
            admin = userQueryService.getAdmin();
        } catch (final Exception e) {
            return;
        }

        if (null == admin) { // Not init yet
            return;
        }

        final String adminEmail = admin.optString(User.USER_EMAIL);

        final Collection<File> mds = FileUtils.listFiles(new File(markdownsPath), new String[]{"md"}, true);
        for (final File md : mds) {
            final String fileName = md.getName();
            if (StringUtils.equalsIgnoreCase(fileName, "README.md")) {
                continue;
            }

            try {
                final String fileContent = FileUtils.readFileToString(md, "UTF-8");
                final JSONObject article = parseArticle(fileName, fileContent);
                article.put(Article.ARTICLE_AUTHOR_EMAIL, adminEmail);

                final JSONObject request = new JSONObject();
                request.put(Article.ARTICLE, article);

                final String id = articleMgmtService.addArticle(request);
                FileUtils.moveFile(md, new File(md.getPath() + "." + id));
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Import file [" + fileName + "] failed", e);
            }
        }
    }

    private JSONObject parseArticle(final String fileName, final String fileContent) {
        String frontMatter = StringUtils.substringBetween(fileContent, "---", "---");
        if (StringUtils.isBlank(frontMatter)) {
            frontMatter = StringUtils.substringBefore(fileContent, "---");
        }

        final JSONObject ret = new JSONObject();

        if (StringUtils.isBlank(frontMatter)) { // plain markdown
            ret.put(Article.ARTICLE_TITLE, StringUtils.substringBeforeLast(fileName, "."));
            ret.put(Article.ARTICLE_CONTENT, fileContent);
            ret.put(Article.ARTICLE_ABSTRACT, Article.getAbstract(fileContent));
            ret.put(Article.ARTICLE_TAGS_REF, DEFAULT_TAG);
            ret.put(Article.ARTICLE_IS_PUBLISHED, true);
            ret.put(Article.ARTICLE_COMMENTABLE, true);
            ret.put(Article.ARTICLE_VIEW_PWD, "");

            return ret;
        }

        final Yaml yaml = new Yaml();
        final Map elems = (Map) yaml.load(frontMatter);
        String title = (String) elems.get("title");
        if (StringUtils.isBlank(title)) {
            title = StringUtils.substringBeforeLast(fileName, ".");
        }
        ret.put(Article.ARTICLE_TITLE, title);

        final String content = StringUtils.substringAfter(fileContent, frontMatter);
        ret.put(Article.ARTICLE_CONTENT, content);

        
        ret.put(Article.ARTICLE_ABSTRACT, Article.getAbstract(content));

        final String date = (String) elems.get("date");
        if (StringUtils.isNotBlank(date)) {
            try {
                ret.put(Article.ARTICLE_CREATE_DATE, DateUtils.parseDate(date, new String[]{"yyyy/MM/dd HH:mm:ss"}));
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Parse date [" + date + "] failed", e);
            }
        }

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

        ret.put(Article.ARTICLE_IS_PUBLISHED, true);
        ret.put(Article.ARTICLE_COMMENTABLE, true);
        ret.put(Article.ARTICLE_VIEW_PWD, "");

        return ret;
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

        if (tags instanceof List) {
            ret.addAll((List) tags);

            return ret;
        }

        ret.add((String) tags);

        return ret;
    }
}
