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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.util.Statics;
import org.json.JSONObject;

/**
 * Page management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Vanessa</a>
 * @version 1.1.2.1, Jul 8, 2020
 * @since 0.4.0
 */
@Service
public class PageMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PageMgmtService.class);

    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;

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
     *                          "pageOrder": int,
     *                          "pagePermalink": "",
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
            permalink = StringUtils.replace(permalink, " ", "-");
            newPage.put(Page.PAGE_PERMALINK, permalink);
            page.put(Page.PAGE_ICON, page.optString(Page.PAGE_ICON));

            pageRepository.update(pageId, newPage);
            transaction.commit();

            Statics.clear();

            LOGGER.log(Level.DEBUG, "Updated a page[id={}]", pageId);
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
            transaction.commit();

            Statics.clear();
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
     *                          "pageOpenTarget": "",
     *                          "pagePermalink": "",
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
            permalink = StringUtils.replace(permalink, " ", "-");
            page.put(Page.PAGE_PERMALINK, permalink);
            page.put(Page.PAGE_ICON, page.optString(Page.PAGE_ICON));
            final String ret = pageRepository.add(page);
            transaction.commit();

            Statics.clear();

            return ret;
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

                LOGGER.log(Level.WARN, "Cant not find the target page of source page[order={}]", srcPageOrder);
                return;
            }

            // Swaps
            srcPage.put(Page.PAGE_ORDER, targetPage.getInt(Page.PAGE_ORDER));
            targetPage.put(Page.PAGE_ORDER, srcPageOrder);
            pageRepository.update(srcPage.getString(Keys.OBJECT_ID), srcPage, Page.PAGE_ORDER);
            pageRepository.update(targetPage.getString(Keys.OBJECT_ID), targetPage, Page.PAGE_ORDER);
            transaction.commit();

            Statics.clear();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Changes page's order failed", e);
            throw new ServiceException(e);
        }
    }
}
