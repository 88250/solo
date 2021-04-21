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
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.util.Statics;
import org.json.JSONObject;

/**
 * Link management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Apr 21, 2021
 * @since 0.4.0
 */
@Service
public class LinkMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(LinkMgmtService.class);

    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;

    /**
     * Removes a link specified by the given link id.
     *
     * @param linkId the given link id
     * @throws ServiceException service exception
     */
    public void removeLink(final String linkId) throws ServiceException {
        final Transaction transaction = linkRepository.beginTransaction();
        try {
            linkRepository.remove(linkId);
            transaction.commit();

            Statics.clear();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Removes a link[id=" + linkId + "] failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Updates a link by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "link": {
     *                          "oId": "",
     *                          "linkTitle": "",
     *                          "linkAddress": "",
     *                          "linkDescription": "",
     *                          "linkIcon": ""
     *                          }
     *                          see {@link Link} for more details
     * @throws ServiceException service exception
     */
    public void updateLink(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final JSONObject link = requestJSONObject.getJSONObject(Link.LINK);
            String icon = StringUtils.trim(link.optString(Link.LINK_ICON));
            if (StringUtils.isBlank(icon)) {
                icon = "https://b3log.org/images/brand/solo-32.png";
            }
            link.put(Link.LINK_ICON, icon);
            final String linkId = link.getString(Keys.OBJECT_ID);
            final JSONObject oldLink = linkRepository.get(linkId);
            link.put(Link.LINK_ORDER, oldLink.getInt(Link.LINK_ORDER));
            linkRepository.update(linkId, link);
            transaction.commit();

            Statics.clear();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, e.getMessage(), e);

            throw new ServiceException(e);
        }
    }

    /**
     * Changes the order of a link specified by the given link id with the
     * specified direction.
     *
     * @param linkId    the given link id
     * @param direction the specified direction, "up"/"down"
     * @throws ServiceException service exception
     */
    public void changeOrder(final String linkId, final String direction) throws ServiceException {
        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final JSONObject srcLink = linkRepository.get(linkId);
            final int srcLinkOrder = srcLink.getInt(Link.LINK_ORDER);

            JSONObject targetLink = null;

            if ("up".equals(direction)) {
                targetLink = linkRepository.getUpper(linkId);
            } else { // Down
                targetLink = linkRepository.getUnder(linkId);
            }

            if (null == targetLink) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                LOGGER.log(Level.WARN, "Cant not find the target link of source link[order={}]", srcLinkOrder);
                return;
            }

            // Swaps
            srcLink.put(Link.LINK_ORDER, targetLink.getInt(Link.LINK_ORDER));
            targetLink.put(Link.LINK_ORDER, srcLinkOrder);

            linkRepository.update(srcLink.getString(Keys.OBJECT_ID), srcLink);
            linkRepository.update(targetLink.getString(Keys.OBJECT_ID), targetLink);
            transaction.commit();

            Statics.clear();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Changes link's order failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Adds a link with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "link": {
     *                          "linkTitle": "",
     *                          "linkAddress": "",
     *                          "linkDescription": "",
     *                          "linkIcon": ""
     *                          }
     *                          }, see {@link Link} for more details
     * @return generated link id
     * @throws ServiceException service exception
     */
    public String addLink(final JSONObject requestJSONObject) throws ServiceException {
        final Transaction transaction = linkRepository.beginTransaction();
        try {
            final JSONObject link = requestJSONObject.getJSONObject(Link.LINK);
            String icon = StringUtils.trim(link.optString(Link.LINK_ICON));
            if (StringUtils.isBlank(icon)) {
                icon = "https://b3log.org/images/brand/solo-32.png";
            }
            link.put(Link.LINK_ICON, icon);
            final int maxOrder = linkRepository.getMaxOrder();
            link.put(Link.LINK_ORDER, maxOrder + 1);
            final String ret = linkRepository.add(link);
            transaction.commit();

            Statics.clear();

            return ret;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.ERROR, "Adds a link failed", e);
            throw new ServiceException(e);
        }
    }
}
