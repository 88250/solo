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
package org.b3log.solo.processor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.SoloServletListener;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * KanBanNiang processor. https://github.com/b3log/solo/issues/12472
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 5, 2018
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
     * @param context  the specified http request context
     * @param request  the specified http servlet request
     * @param response the specified http servlet response
     */
    @RequestProcessing(value = "/plugins/kanbanniang/assert/model", method = HTTPRequestMethod.GET)
    public void randomModel(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        try {
            final int i = RandomUtils.nextInt(MODEL_NAMES.length);
            final String name = MODEL_NAMES[i];
            final InputStream modelResource = SoloServletListener.getServletContext().getResourceAsStream("/plugins/kanbanniang/assert/model.json");
            String s = IOUtils.toString(modelResource, "UTF-8");
            s = s.replace("${name}", name);
            final JSONObject model = new JSONObject(s);
            renderer.setJSONObject(model);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Returns a random KanBanNiang model failed", e);
        }
    }

    /**
     * Model names.
     */
    private static final String[] MODEL_NAMES = {
            "Animal Costume.png",
            "Animal Costume Racoon.png",
            "Bunny Girl Costume.png",
            "Bunny Girl Costume Red.png",
            "Cake Costume Choco.png",
            "Cake Costume Cream.png",
            "default-costume.png",
            "Dress Costume.png",
            "Dress Costume Brown.png",
            "Elementary School Costume.png",
            "Elementary School Costume Navy.png",
            "Frill Bikini Costume Green.png",
            "Furisode Costume.png",
            "Halloween Costume.png",
            "Hanbok Costume.png",
            "Kids Costume.png",
            "Kids Costume Navy.png",
            "Maid Costume.png",
            "Maid Costume Red.png",
            "New2015 Costume.png",
            "New2015 Costume Pajamas.png",
            "Nightsky Costume.png",
            "pajamas-costume.png",
            "Pajamas Costume Pink.png",
            "Qipao Costume Pink.png",
            "Qipao Costume Red.png",
            "Sailor Costume.png",
            "Sailor Costume Black.png",
            "Sakura Costume.png",
            "Sakura Costume Navy.png",
            "Santa Costume.png",
            "Santa Costume Green.png",
            "Sarori Costume.png",
            "school-costume.png",
            "School Costume Red.png",
            "Star Witch Costume.png",
            "Star Witch Costume Brown.png",
            "Succubus Costume Black.png",
            "Succubus Costume Red.png",
            "Sukumizu Costume.png",
            "Sukumizu Costume White.png",
            "Summer Dress Costume Blue.png",
            "Summer Dress Costume White.png",
            "Tirami1 Costume.png",
            "Turtleneck Costume.png",
            "Turtleneck Costume Red.png",
            "Winter Coat Costume Pink.png",
            "Winter Coat Costume White.png",
            "Winter Costume.png",
            "Winter Costume White.png",
            "Witch Costume.png",
            "Witch Costume White.png"
    };
}
