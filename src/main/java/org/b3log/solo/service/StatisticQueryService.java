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

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.json.JSONObject;

/**
 * Statistic query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Sep 19, 2018
 * @since 0.5.0
 */
@Service
public class StatisticQueryService {

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Gets the online visitor count.
     *
     * @return online visitor count
     */
    public static int getOnlineVisitorCount() {
        return StatisticMgmtService.ONLINE_VISITORS.size();
    }

    /**
     * Get blog comment count.
     *
     * @return blog comment count
     * @throws ServiceException service exception
     */
    public int getBlogCommentCount() throws ServiceException {
        final JSONObject opt = optionQueryService.getOptionById(Option.ID_C_STATISTIC_BLOG_COMMENT_COUNT);
        if (null == opt) {
            throw new ServiceException("Not found statistic");
        }

        return opt.optInt(Option.OPTION_VALUE);
    }

    /**
     * Get blog comment(published article) count.
     *
     * @return blog comment count
     * @throws ServiceException service exception
     */
    public int getPublishedBlogCommentCount() throws ServiceException {
        final JSONObject opt = optionQueryService.getOptionById(Option.ID_C_STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
        if (null == opt) {
            throw new ServiceException("Not found statistic");
        }

        return opt.optInt(Option.OPTION_VALUE);
    }

    /**
     * Gets blog statistic published article count.
     *
     * @return published blog article count
     * @throws ServiceException service exception
     */
    public int getPublishedBlogArticleCount() throws ServiceException {
        final JSONObject opt = optionQueryService.getOptionById(Option.ID_C_STATISTIC_PUBLISHED_ARTICLE_COUNT);
        if (null == opt) {
            throw new ServiceException("Not found statistic");
        }

        return opt.optInt(Option.OPTION_VALUE);
    }

    /**
     * Gets blog statistic article count.
     *
     * @return blog article count
     * @throws ServiceException service exception
     */
    public int getBlogArticleCount() throws ServiceException {
        final JSONObject opt = optionQueryService.getOptionById(Option.ID_C_STATISTIC_BLOG_ARTICLE_COUNT);
        if (null == opt) {
            throw new ServiceException("Not found statistic");
        }

        return opt.optInt(Option.OPTION_VALUE);
    }

    /**
     * Gets the statistic.
     *
     * @return statistic, returns {@code null} if not found
     * @throws ServiceException if repository exception
     */
    public JSONObject getStatistic() throws ServiceException {
        return optionQueryService.getOptions(Option.CATEGORY_C_STATISTIC);
    }

    /**
     * Sets the option query service with the specified option query service.
     *
     * @param optionQueryService the specified option query service
     */
    public void setOptionQueryService(final OptionQueryService optionQueryService) {
        this.optionQueryService = optionQueryService;
    }
}
