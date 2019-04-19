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
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Page management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Vanessa</a>
 * @version 1.1.0.18, Apr 19, 2019
 * @since 0.4.0
 */
@Service
public class PageMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageMgmtService.class);

    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;

    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Permalink query service.
     */
    @Inject
    private PermalinkQueryService permalinkQueryService;

    /**
     * Statistic management service.
     */
    @Inject
    private StatisticMgmtService statisticMgmtService;

    /**
     * Statistic query service.
     */
    @Inject
    private StatisticQueryService statisticQueryService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Option management service.
     */
    @Inject
    private OptionMgmtService optionMgmtService;

    /**
     * Updates a page by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "page": {
     *                          "oId": "",
     *                          "pageTitle": "",
     *                          "pageContent": "",
     *                          "pageOrder": int,
     *                          "pageCommentCount": int,
     *                          "pagePermalink": "",
     *                          "pageCommentable": boolean,
     *                          "pageOpenTarget": "",
     *                          "pageIcon": "" // optional
     *                          }
     *                          }, see {@link Page} for more details
     * @throws ServiceException service exception
     */
    public void updatePage(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = pageRepository.beginTransaction();
        try {
            final JSONObject page = requestJSONObject.getJSONObject(Page.PAGE);
            final String pageId = page.getString(Keys.OBJECT_ID);
            final JSONObject oldPage = pageRepository.get(pageId);
            final JSONObject newPage = new JSONObject(page, JSONObject.getNames(page));
            newPage.put(Page.PAGE_ORDER, oldPage.getInt(Page.PAGE_ORDER));
            String permalink = page.optString(Page.PAGE_PERMALINK).trim();

            final String oldPermalink = oldPage.getString(Page.PAGE_PERMALINK);
            if (!oldPermalink.equals(permalink)) {
                if (StringUtils.isBlank(permalink)) {
                    permalink = Latkes.getServePath();
                }

                if (!permalink.startsWith("/")) {
                    permalink = "/" + permalink;
                }

                if (PermalinkQueryService.invalidPagePermalinkFormat(permalink)) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    throw new ServiceException(langPropsService.get("invalidPermalinkFormatLabel"));
                }

                if (!oldPermalink.equals(permalink) && permalinkQueryService.exist(permalink)) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    throw new ServiceException(langPropsService.get("duplicatedPermalinkLabel"));
                }
            }

            newPage.put(Page.PAGE_PERMALINK, permalink.replaceAll(" ", "-"));

            if (!oldPage.getString(Page.PAGE_PERMALINK).equals(permalink)) { // The permalink has been updated
                // Updates related comments' links
                processCommentsForPageUpdate(newPage);
            }

            page.put(Page.PAGE_ICON, page.optString(Page.PAGE_ICON));

            pageRepository.update(pageId, newPage);
            transaction.commit();

            LOGGER.log(Level.DEBUG, "Updated a page[id={0}]", pageId);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }
    }

    /**
     * Removes a page specified by the given page id.
     *
     * @param pageId the given page id
     * @throws ServiceException service exception
     */
    public void removePage(final String pageId) throws ServiceException {
        final Transaction transaction = pageRepository.beginTransaction();
        try {
            pageRepository.remove(pageId);
            commentRepository.removeComments(pageId);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Removes a page[id=" + pageId + "] failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Adds a page with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "page": {
     *                          "pageTitle": "",
     *                          "pageContent": "",
     *                          "pageOpenTarget": "",
     *                          "pageCommentable": boolean,
     *                          "pagePermalink": "", // optional
     *                          "pageIcon": "" // optional
     *                          }
     *                          }, see {@link Page} for more details
     * @return generated page id
     * @throws ServiceException if permalink format checks failed or persists failed
     */
    public String addPage(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = pageRepository.beginTransaction();

        try {
            final JSONObject page = requestJSONObject.getJSONObject(Page.PAGE);
            final int maxOrder = pageRepository.getMaxOrder();
            page.put(Page.PAGE_ORDER, maxOrder + 1);

            String permalink = page.optString(Page.PAGE_PERMALINK);
            if (StringUtils.isBlank(permalink)) {
                permalink = Latkes.getServePath();
            }

            if (!permalink.startsWith("/")) {
                permalink = "/" + permalink;
            }

            if (PermalinkQueryService.invalidPagePermalinkFormat(permalink)) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new ServiceException(langPropsService.get("invalidPermalinkFormatLabel"));
            }

            if (permalinkQueryService.exist(permalink)) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                throw new ServiceException(langPropsService.get("duplicatedPermalinkLabel"));
            }

            page.put(Page.PAGE_PERMALINK, permalink.replaceAll(" ", "-"));

            page.put(Page.PAGE_ICON, page.optString(Page.PAGE_ICON));
            final String ret = pageRepository.add(page);
            transaction.commit();

            return ret;
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }
    }

    /**
     * Changes the order of a page specified by the given page id with the specified direction.
     *
     * @param pageId    the given page id
     * @param direction the specified direction, "up"/"down"
     * @throws ServiceException service exception
     */
    public void changeOrder(final String pageId, final String direction) throws ServiceException {
        final Transaction transaction = pageRepository.beginTransaction();
        try {
            final JSONObject srcPage = pageRepository.get(pageId);
            final int srcPageOrder = srcPage.getInt(Page.PAGE_ORDER);

            JSONObject targetPage;
            if ("up".equals(direction)) {
                targetPage = pageRepository.getUpper(pageId);
            } else { // Down
                targetPage = pageRepository.getUnder(pageId);
            }

            if (null == targetPage) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                LOGGER.log(Level.WARN, "Cant not find the target page of source page[order={0}]", srcPageOrder);
                return;
            }

            // Swaps
            srcPage.put(Page.PAGE_ORDER, targetPage.getInt(Page.PAGE_ORDER));
            targetPage.put(Page.PAGE_ORDER, srcPageOrder);
            pageRepository.update(srcPage.getString(Keys.OBJECT_ID), srcPage);
            pageRepository.update(targetPage.getString(Keys.OBJECT_ID), targetPage);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Changes page's order failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Processes comments for page update.
     *
     * @param page the specified page to update
     * @throws Exception exception
     */
    public void processCommentsForPageUpdate(final JSONObject page) throws Exception {
        final String pageId = page.getString(Keys.OBJECT_ID);

        final List<JSONObject> comments = commentRepository.getComments(pageId, 1, Integer.MAX_VALUE);
        for (final JSONObject comment : comments) {
            final String commentId = comment.getString(Keys.OBJECT_ID);
            final String sharpURL = Comment.getCommentSharpURLForPage(page, commentId);
            comment.put(Comment.COMMENT_SHARP_URL, sharpURL);
            if (StringUtils.isBlank(comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID))) {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            }
            if (StringUtils.isBlank(comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_NAME))) {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            }

            commentRepository.update(commentId, comment);
        }
    }
}
