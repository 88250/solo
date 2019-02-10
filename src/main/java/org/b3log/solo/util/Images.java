/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
package org.b3log.solo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Image utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Feb 10, 2019
 * @since 2.7.0
 */
public final class Images {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Images.class);

    /**
     * Qiniu image processing.
     *
     * @param html the specified content HTML
     * @return processed content
     */
    public static String qiniuImgProcessing(final String html) {
        String ret = html;
        final String qiniuDomain = "https://img.hacpai.com";
        final String[] imgSrcs = StringUtils.substringsBetween(html, "<img src=\"", "\"");
        if (null == imgSrcs) {
            return ret;
        }

        for (final String imgSrc : imgSrcs) {
            if (!StringUtils.startsWith(imgSrc, qiniuDomain) || StringUtils.contains(imgSrc, ".gif")
                    || StringUtils.containsIgnoreCase(imgSrc, "?imageView2")) {
                continue;
            }

            ret = StringUtils.replace(ret, imgSrc, imgSrc + "?imageView2/2/w/768/format/webp/interlace/1");
        }

        return ret;
    }

    /**
     * Returns image URL of Qiniu image processing style with the specified width and height.
     *
     * @param imageURL the specified image URL
     * @param width    the specified width
     * @param height   the specified height
     * @return image URL
     */
    public static final String imageSize(final String imageURL, final int width, final int height) {
        if (StringUtils.containsIgnoreCase(imageURL, "imageView") || !StringUtils.containsIgnoreCase(imageURL, "img.hacpai.com")) {
            return imageURL;
        }

        return imageURL + "?imageView2/1/w/" + width + "/h/" + height + "/interlace/1/q/100";
    }

    /**
     * Gets an image URL randomly. Sees https://github.com/b3log/bing for more details.
     *
     * @return an image URL
     */
    public static String randImage() {
        try {
            final long min = DateUtils.parseDate("20171104", new String[]{"yyyyMMdd"}).getTime();
            final long max = System.currentTimeMillis();
            final long delta = max - min;
            final long time = ThreadLocalRandom.current().nextLong(0, delta) + min;

            return "https://img.hacpai.com/bing/" + DateFormatUtils.format(time, "yyyyMMdd") + ".jpg";
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates random image URL failed", e);

            return "https://img.hacpai.com/bing/20171104.jpg";
        }
    }

    /**
     * Gets image URLs randomly.
     *
     * @param n the specified size
     * @return image URLs
     */
    public static List<String> randomImages(final int n) {
        final List<String> ret = new ArrayList<>();

        int i = 0;
        while (true) {
            if (i >= n * 5) {
                break;
            }

            final String url = randImage();
            if (!ret.contains(url)) {
                ret.add(url);
            }

            if (ret.size() >= n) {
                return ret;
            }

            i++;
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private Images() {
    }
}
