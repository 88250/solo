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
package org.b3log.solo.repository.impl;


import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONObject;


/**
 * Preference repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Feb 28, 2012
 * @since 0.3.1
 */
@Repository
public class PreferenceRepositoryImpl extends AbstractRepository implements PreferenceRepository {

    /**
     * Public constructor.
     */
    public PreferenceRepositoryImpl() {
        super(Preference.PREFERENCE);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Bypasses {@linkplain org.b3log.latke.repository.Repositories validation}
     * against the repository structure, adds the specified json object as 
     * preference directly.
     * </p>
     */
    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        return getUnderlyingRepository().add(jsonObject);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Bypasses {@linkplain org.b3log.latke.repository.Repositories validation}
     * against the repository structure, adds the specified json object as 
     * preference directly.
     * </p>
     */
    @Override
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        getUnderlyingRepository().update(id, jsonObject);
    }
}
