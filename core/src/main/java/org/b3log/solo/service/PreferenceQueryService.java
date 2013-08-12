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


import javax.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONObject;


/**
 * Preference query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 31, 2011
 * @since 0.4.0
 */
@Service
public class PreferenceQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PreferenceQueryService.class.getName());

    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;

    /**
     * Gets the reply notification template.
     * 
     * @return reply notification template, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getReplyNotificationTemplate() throws ServiceException {
        try {
            return preferenceRepository.get(Preference.REPLY_NOTIFICATION_TEMPLATE);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Updates reply notification template failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the user preference.
     * 
     * <p>
     *   <b>Note</b>: Invoking the method will not load skin.
     * </p>
     *
     * @return user preference, returns {@code null} if not found
     * @throws ServiceException if repository exception
     */
    public JSONObject getPreference() throws ServiceException {
        try {
            final JSONObject ret = preferenceRepository.get(Preference.PREFERENCE);

            if (null == ret) {
                LOGGER.log(Level.WARN, "Can not load preference from datastore");
                return null;
            }

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sets the preference repository with the specified preference repository.
     * 
     * @param preferenceRepository the specified preference repository
     */
    public void setPreferenceRepository(final PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }
}
