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
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

/**
 * Preference query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.4, Sep 17, 2018
 * @since 0.4.0
 */
@Service
public class PreferenceQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceQueryService.class);

    /**
     * Option repository.
     */
    @Inject
    private OptionRepository optionRepository;

    /**
     * Optiona query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Gets the reply notification template.
     *
     * @return reply notification template, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getReplyNotificationTemplate() throws ServiceException {
        try {
            final JSONObject ret = new JSONObject();
            final JSONObject preference = getPreference();

            ret.put("subject", preference.optString(Option.ID_C_REPLY_NOTI_TPL_SUBJECT));
            ret.put("body", preference.optString(Option.ID_C_REPLY_NOTI_TPL_BODY));

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Updates reply notification template failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the user preference.
     *
     * @return user preference, returns {@code null} if not found
     * @throws ServiceException if repository exception
     */
    public JSONObject getPreference() throws ServiceException {
        try {
            final JSONObject checkInit = optionRepository.get(Option.ID_C_ADMIN_EMAIL);
            if (null == checkInit) {
                return null;
            }

            return optionQueryService.getOptions(Option.CATEGORY_C_PREFERENCE);
        } catch (final RepositoryException e) {
            return null;
        }
    }
}
