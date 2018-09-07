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
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Strings;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permalink query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Sep 7, 2018
 * @since 0.6.1
 */
@Service
public class PermalinkQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PermalinkQueryService.class);

    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Reserved permalinks.
     */
    public static final String[] RESERVED_LINKS = new String[]{
            "/", "/article", "/tags.html", "/tags", "/page", "/blog-articles-feed.do", "/tag-articles-feed.do", "/blog-articles-rss.do",
            "/tag-articles-rss.do", "/get-random-articles.do", "/captcha.do", "/kill-browser",
            "/add-article-comment.do", "/add-article-from-symphony-comment.do", "/add-page-comment.do", "/get-article-content", "/sitemap.xml",
            "/login", "/logout", "/forgot", "/get-article-content", "/admin-index.do", "/admin-article.do", "/admin-article-list.do",
            "/admin-link-list.do", "/admin-preference.do", "/admin-file-list.do", "/admin-page-list.do", "/admin-others.do",
            "/admin-draft-list.do", "/admin-user-list.do", "/admin-plugin-list.do", "/admin-main.do", "/admin-about.do", "/admin-label",
            "/admin-about.do", "/init", "/register", "/upload"
    };

    /**
     * Checks whether the specified article permalink matches the system generated format pattern ("/articles/yyyy/MM/dd/${articleId}.html").
     *
     * @param permalink the specified permalink
     * @return {@code true} if matches, returns {@code false} otherwise
     */
    public static boolean matchDefaultArticlePermalinkFormat(final String permalink) {
        final Pattern pattern = Pattern.compile("/articles/\\d{4}/\\d{2}/\\d{2}/\\d+\\.html");
        final Matcher matcher = pattern.matcher(permalink);

        return matcher.matches();
    }

    /**
     * Checks whether the specified page permalink matches the system generated format pattern ("/pages/${pageId}.html").
     *
     * @param permalink the specified permalink
     * @return {@code true} if matches, returns {@code false} otherwise
     */
    public static boolean matchDefaultPagePermalinkFormat(final String permalink) {
        final Pattern pattern = Pattern.compile("/pages/\\d+\\.html");
        final Matcher matcher = pattern.matcher(permalink);

        return matcher.matches();
    }

    /**
     * Checks whether the specified permalink is a {@link #invalidArticlePermalinkFormat(java.lang.String) invalid article
     * permalink format} and {@link #invalidPagePermalinkFormat(java.lang.String) invalid page permalink format}.
     *
     * @param permalink the specified permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    public static boolean invalidPermalinkFormat(final String permalink) {
        return invalidArticlePermalinkFormat(permalink) && invalidPagePermalinkFormat(permalink);
    }

    /**
     * Checks whether the specified article permalink is invalid on format.
     *
     * @param permalink the specified article permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    public static boolean invalidArticlePermalinkFormat(final String permalink) {
        if (StringUtils.isBlank(permalink)) {
            return true;
        }

        if (matchDefaultArticlePermalinkFormat(permalink)) {
            return false;
        }

        return invalidUserDefinedPermalinkFormat(permalink);
    }

    /**
     * Checks whether the specified page permalink is invalid on format.
     *
     * @param permalink the specified page permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    public static boolean invalidPagePermalinkFormat(final String permalink) {
        if (StringUtils.isBlank(permalink)) {
            return true;
        }

        if (matchDefaultPagePermalinkFormat(permalink)) {
            return false;
        }

        return invalidUserDefinedPermalinkFormat(permalink);
    }

    /**
     * Checks whether the specified user-defined permalink is invalid on format.
     *
     * @param permalink the specified user-defined permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    private static boolean invalidUserDefinedPermalinkFormat(final String permalink) {
        if (StringUtils.isBlank(permalink)) {
            return true;
        }

        if (isReservedLink(permalink)) {
            return true;
        }

        if (Strings.isNumeric(permalink.substring(1))) {
            // Conflict with pagination
            return true;
        }

        int slashCnt = 0;

        for (int i = 0; i < permalink.length(); i++) {
            if ('/' == permalink.charAt(i)) {
                slashCnt++;
            }

            if (slashCnt > 1) {
                return true;
            }
        }

        return !Strings.isURL(Latkes.getServer() + permalink);
    }

    /**
     * Determines whether the specified request URI is a reserved link.
     * <p>
     * A URI starts with one of {@link PermalinkQueryService#RESERVED_LINKS reserved links}
     * will be treated as reserved link.
     * </p>
     *
     * @param requestURI the specified request URI
     * @return {@code true} if it is a reserved link, returns {@code false} otherwise
     */
    private static boolean isReservedLink(final String requestURI) {
        for (int i = 0; i < RESERVED_LINKS.length; i++) {
            final String reservedLink = RESERVED_LINKS[i];

            if (reservedLink.startsWith(requestURI)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether the specified permalink exists.
     *
     * @param permalink the specified permalink
     * @return {@code true} if exists, returns {@code false} otherwise
     */
    public boolean exist(final String permalink) {
        try {
            return isReservedLink(permalink) || null != articleRepository.getByPermalink(permalink)
                    || null != pageRepository.getByPermalink(permalink) || permalink.endsWith(".ftl");
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Determines whether the permalink[" + permalink + "] exists failed, returns true", e);

            return true;
        }
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
     * Set the page repository with the specified page repository.
     *
     * @param pageRepository the specified page repository
     */
    public void setPageRepository(final PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }
}
