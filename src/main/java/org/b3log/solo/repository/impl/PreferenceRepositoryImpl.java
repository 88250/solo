/*
 * Copyright (c) 2010-2015, b3log.org
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
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONObject;

/**
 * Preference repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Nov 23, 2015
 * @since 0.3.1
 * @deprecated this class will be removed in 1.3.0, see issue
 * <a href="https://github.com/b3log/solo/issues/12042">#12042</a>
 * for more details
 */
@Repository
public class PreferenceRepositoryImpl extends AbstractRepository implements PreferenceRepository {

    /**
     * Public constructor.
     */
    public PreferenceRepositoryImpl() {
        super(Option.CATEGORY_C_PREFERENCE);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Bypasses {@linkplain org.b3log.latke.repository.Repositories validation} against the repository structure, adds
     * the specified json object as preference directly.
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
     * Bypasses {@linkplain org.b3log.latke.repository.Repositories validation} against the repository structure, adds
     * the specified json object as preference directly.
     * </p>
     */
    @Override
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        getUnderlyingRepository().update(id, jsonObject);
    }
}
