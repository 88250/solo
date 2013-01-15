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


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.util.Comments;
import org.b3log.solo.util.Permalinks;
import org.b3log.solo.util.Statistics;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Page management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jun 8, 2012
 * @since 0.4.0
 */
public final class PageMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageMgmtService.class.getName());

    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();

    /**
     * Comment repository.
     */
    private CommentRepository commentRepository = CommentRepositoryImpl.getInstance();

    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Permalink utilities.
     */
    private Permalinks permalinks = Permalinks.getInstance();

    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    /**
     * Updates a page by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "page": {
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageContent": "",
     *         "pageOrder": int,
     *         "pageCommentCount": int,
     *         "pagePermalink": "",
     *         "pageCommentable": boolean,
     *         "pageType": "",
     *         "pageOpenTarget": ""
     *     }
     * }, see {@link Page} for more details
     * </pre>
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
            newPage.put(Page.PAGE_COMMENT_COUNT, oldPage.getInt(Page.PAGE_COMMENT_COUNT));
            String permalink = page.optString(Page.PAGE_PERMALINK).trim();

            final String oldPermalink = oldPage.getString(Page.PAGE_PERMALINK);

            if (!oldPermalink.equals(permalink)) {
                if (Strings.isEmptyOrNull(permalink)) {
                    permalink = "/pages/" + pageId + ".html";
                }

                if (Page.PAGE.equals(page.getString(Page.PAGE_TYPE))) {
                    if (!permalink.startsWith("/")) {
                        permalink = "/" + permalink;
                    }

                    if (Permalinks.invalidPagePermalinkFormat(permalink)) {
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }

                        throw new ServiceException(langPropsService.get("invalidPermalinkFormatLabel"));
                    }

                    if (!oldPermalink.equals(permalink) && permalinks.exist(permalink)) {
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }

                        throw new ServiceException(langPropsService.get("duplicatedPermalinkLabel"));
                    }
                }
            }

            // TODO: SBC case
            newPage.put(Page.PAGE_PERMALINK, permalink.replaceAll(" ", "-"));

            if (!oldPage.getString(Page.PAGE_PERMALINK).equals(permalink)) { // The permalink has been updated
                // Updates related comments' links
                processCommentsForPageUpdate(newPage);
            }

            // Editor type
            final JSONObject preference = preferenceQueryService.getPreference();

            newPage.put(Page.PAGE_EDITOR_TYPE, preference.optString(Preference.EDITOR_TYPE));

            pageRepository.update(pageId, newPage);

            transaction.commit();

            LOGGER.log(Level.FINER, "Updated a page[id={0}]", pageId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
            LOGGER.log(Level.FINER, "Removing a page[id={0}]", pageId);
            removePageComments(pageId);
            pageRepository.remove(pageId);

            transaction.commit();

        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Removes a page[id=" + pageId + "] failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Adds a page with the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "page": {
     *         "pageTitle": "",
     *         "pageContent": "",
     *         "pageOpenTarget": "",
     *         "pageCommentable": boolean,
     *         "pageType": "",
     *         "pagePermalink": "" // optional
     *     }
     * }, see {@link Page} for more details
     * </pre>
     * @return generated page id
     * @throws ServiceException if permalink format checks failed or persists
     * failed
     */
    public String addPage(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = pageRepository.beginTransaction();

        try {
            final JSONObject page = requestJSONObject.getJSONObject(Page.PAGE);

            page.put(Page.PAGE_COMMENT_COUNT, 0);
            final int maxOrder = pageRepository.getMaxOrder();

            page.put(Page.PAGE_ORDER, maxOrder + 1);

            String permalink = page.optString(Page.PAGE_PERMALINK);

            if (Strings.isEmptyOrNull(permalink)) {
                permalink = "/pages/" + Ids.genTimeMillisId() + ".html";
            }

            if (Page.PAGE.equals(page.getString(Page.PAGE_TYPE))) {
                if (!permalink.startsWith("/")) {
                    permalink = "/" + permalink;
                }

                if (Permalinks.invalidPagePermalinkFormat(permalink)) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    throw new ServiceException(langPropsService.get("invalidPermalinkFormatLabel"));
                }

                if (permalinks.exist(permalink)) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    throw new ServiceException(langPropsService.get("duplicatedPermalinkLabel"));
                }
            }

            // TODO: SBC case
            page.put(Page.PAGE_PERMALINK, permalink.replaceAll(" ", "-"));

            // Editor type
            final JSONObject preference = preferenceQueryService.getPreference();

            page.put(Page.PAGE_EDITOR_TYPE, preference.optString(Preference.EDITOR_TYPE));

            final String ret = pageRepository.add(page);

            transaction.commit();

            return ret;
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServiceException(e);
        }
    }

    /**
     * Changes the order of a page specified by the given page id with 
     * the specified direction.
     *
     * @param pageId the given page id
     * @param direction the specified direction, "up"/"down"
     * @throws ServiceException service exception
     */
    public void changeOrder(final String pageId, final String direction) throws ServiceException {

        final Transaction transaction = pageRepository.beginTransaction();

        try {
            final JSONObject srcPage = pageRepository.get(pageId);
            final int srcPageOrder = srcPage.getInt(Page.PAGE_ORDER);

            JSONObject targetPage = null;

            if ("up".equals(direction)) {
                targetPage = pageRepository.getUpper(pageId);
            } else { // Down
                targetPage = pageRepository.getUnder(pageId);
            }

            if (null == targetPage) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                LOGGER.log(Level.WARNING, "Cant not find the target page of source page[order={0}]", srcPageOrder);
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

            LOGGER.log(Level.SEVERE, "Changes page's order failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link PageMgmtService} singleton.
     *
     * @return the singleton
     */
    public static PageMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Removes page comments by the specified page id.
     *
     * <p>
     * Removes related comments, sets page/blog comment statistic count.
     * </p>
     *
     * @param pageId the specified page id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    private void removePageComments(final String pageId) throws JSONException, RepositoryException {
        final int removedCnt = commentRepository.removeComments(pageId);

        int blogCommentCount = statistics.getBlogCommentCount();

        blogCommentCount -= removedCnt;
        statistics.setBlogCommentCount(blogCommentCount);

        int publishedBlogCommentCount = statistics.getPublishedBlogCommentCount();

        publishedBlogCommentCount -= removedCnt;
        statistics.setPublishedBlogCommentCount(publishedBlogCommentCount);
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
            final String sharpURL = Comments.getCommentSharpURLForPage(page, commentId);

            comment.put(Comment.COMMENT_SHARP_URL, sharpURL);

            if (Strings.isEmptyOrNull(comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID))) {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
            }
            if (Strings.isEmptyOrNull(comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_NAME))) {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");
            }

            commentRepository.update(commentId, comment);
        }
    }

    /**
     * Private constructor.
     */
    private PageMgmtService() {}

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 27, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PageMgmtService SINGLETON = new PageMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {}
    }
}
