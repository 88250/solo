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
package org.b3log.solo.processor.util;


import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.annotation.Service;
import org.b3log.solo.model.Tag;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.TagQueryService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Fill tag articles.
 *
 * @author <a href="mailto:385321165@qq.com">DASHU</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Apr 12, 2017
 * @since 0.6.1
 */
@Service
public class FillTagArticles implements TemplateMethodModelEx {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FillTagArticles.class);
    /**
     * Arg size.
     */
    private static final int ARG_SIZE = 3;
    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;
    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    @Override
    public Object exec(final List arguments) throws TemplateModelException {
        if (arguments.size() != ARG_SIZE) {
            LOGGER.debug("FillTagArticles with wrong arguments!");

            throw new TemplateModelException("Wrong arguments!");
        }

        final String tagTitle = (String) arguments.get(0);
        final int currentPageNum = Integer.parseInt((String) arguments.get(1));
        final int pageSize = Integer.parseInt((String) arguments.get(2));

        try {
            final JSONObject result = tagQueryService.getTagByTitle(tagTitle);
            if (null == result) {
                return new ArrayList<JSONObject>();
            }

            final JSONObject tag = result.getJSONObject(Tag.TAG);
            final String tagId = tag.getString(Keys.OBJECT_ID);

            final List<JSONObject> ret = articleQueryService.getArticlesByTag(tagId, currentPageNum, pageSize);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Fill tag articles failed", e);
        }

        return null;
    }
}
