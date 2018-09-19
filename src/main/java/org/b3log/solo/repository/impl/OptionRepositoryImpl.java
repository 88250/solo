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
package org.b3log.solo.repository.impl;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.solo.cache.OptionCache;
import org.b3log.solo.model.Option;
import org.b3log.solo.repository.OptionRepository;
import org.json.JSONObject;

import java.util.List;

/**
 * Option repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 19, 2018
 * @since 0.6.0
 */
@Repository
public class OptionRepositoryImpl extends AbstractRepository implements OptionRepository {

    /**
     * Option cache.
     */
    @Inject
    private OptionCache optionCache;

    /**
     * Public constructor.
     */
    public OptionRepositoryImpl() {
        super(Option.OPTION);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        final JSONObject option = get(id);
        if (null == option) {
            return;
        }

        super.remove(id);
        optionCache.removeOption(id);

        final String category = option.optString(Option.OPTION_CATEGORY);
        optionCache.removeCategory(category);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = optionCache.getOption(id);
        if (null != ret) {
            return ret;
        }

        ret = super.get(id);
        if (null == ret) {
            return null;
        }

        optionCache.putOption(ret);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject option) throws RepositoryException {
        super.update(id, option);

        option.put(Keys.OBJECT_ID, id);
        optionCache.putOption(option);
    }

    @Override
    public JSONObject getOptions(final String category) throws RepositoryException {
        final JSONObject cached = optionCache.getCategory(category);
        if (null != cached) {
            return cached;
        }

        final JSONObject ret = new JSONObject();
        try {
            final List<JSONObject> options = getList(new Query().setFilter(new PropertyFilter(Option.OPTION_CATEGORY, FilterOperator.EQUAL, category)));
            if (0 == options.size()) {
                return null;
            }
            options.stream().forEach(option -> ret.put(option.optString(Keys.OBJECT_ID), option.opt(Option.OPTION_VALUE)));
            optionCache.putCategory(category, ret);

            return ret;
        } catch (final Exception e) {
            throw new RepositoryException(e);
        }
    }
}
