/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
package org.b3log.solo.processor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JsonRenderer;
import org.b3log.solo.SoloServletListener;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * KanBanNiang processor. https://github.com/b3log/solo/issues/12472
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Mar 4, 2019
 * @since 2.9.2
 */
@RequestProcessor
public class KanBanNiangProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(KanBanNiangProcessor.class);

    /**
     * Returns a random model.
     *
     * @param context the specified request context
     */
    @RequestProcessing(value = "/plugins/kanbanniang/assets/model", method = HttpMethod.GET)
    public void randomModel(final RequestContext context) {
        final JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        try {
            final String assets = "/plugins/kanbanniang/assets";
            String model;
            final ServletContext servletContext = SoloServletListener.getServletContext();
            try (final InputStream inputStream = servletContext.getResourceAsStream(assets + "/model-list.json")) {
                final JSONArray models = new JSONArray(IOUtils.toString(inputStream, "UTF-8"));
                final int i = RandomUtils.nextInt(models.length());
                model = models.getString(i);
            }

            try (final InputStream modelResource = servletContext.getResourceAsStream(assets + "/model/" + model + "/index.json")) {
                final JSONObject index = new JSONObject(IOUtils.toString(modelResource, "UTF-8"));
                final JSONArray textures = index.optJSONArray("textures");
                if (textures.isEmpty()) {
                    try (final InputStream texturesRes = servletContext.getResourceAsStream(assets + "/model/" + model + "/textures.json")) {
                        final JSONArray texturesArray = new JSONArray(IOUtils.toString(texturesRes, "UTF-8"));
                        final Object element = texturesArray.opt(RandomUtils.nextInt(texturesArray.length()));
                        if (element instanceof JSONArray) {
                            index.put("textures", element);
                        } else {
                            index.put("textures", new JSONArray().put(element));
                        }
                    }
                }
                renderer.setJSONObject(index);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Returns a random KanBanNiang model failed", e);
        }
    }
}
