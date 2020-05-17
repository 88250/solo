/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Image utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.1, May 15, 2020
 * @since 2.7.0
 */
public final class Images {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Images.class);

    /**
     * Community file service URL.
     */
    public static String COMMUNITY_FILE_URL = "https://b3logfile.com";

    /**
     * Checks whether the specified URL has uploaded.
     *
     * @param url the specified URL
     * @return {@code true} if it has uploaded, returns {@code false} otherwise
     */
    public static boolean uploaded(final String url) {
        return StringUtils.startsWith(url, COMMUNITY_FILE_URL) || StringUtils.startsWith(url, "https://img.hacpai.com");
    }

    /**
     * Qiniu image processing.
     *
     * @param doc the specified doc
     */
    public static void qiniuImgProcessing(final Document doc) {
        final Elements imgs = doc.select("img");
        if (imgs.isEmpty()) {
            return;
        }

        for (final Element img : imgs) {
            String imgSrc = img.attr("src");
            if (!uploaded(imgSrc) ||
                    StringUtils.contains(imgSrc, ".gif") || StringUtils.containsIgnoreCase(imgSrc, "imageView") ||
                    StringUtils.containsIgnoreCase(imgSrc, "data:")) {
                continue;
            }

            imgSrc += "?imageView2/2/w/1280/format/jpg/interlace/1/q/100";
            img.attr("src", imgSrc);
        }
    }

    /**
     * Returns image URL of Qiniu image processing style with the specified width and height.
     *
     * @param imageURL the specified image URL
     * @param width    the specified width
     * @param height   the specified height
     * @return image URL
     */
    public static String imageSize(final String imageURL, final int width, final int height) {
        if (StringUtils.containsIgnoreCase(imageURL, "imageView")) {
            return imageURL;
        }

        return imageURL + "?imageView2/1/w/" + width + "/h/" + height + "/interlace/1/q/100";
    }

    /**
     * Gets an image URL randomly. Sees https://github.com/88250/bing for more details.
     *
     * @return an image URL
     */
    public static String randImage() {
        try {
            final long min = DateUtils.parseDate("20171104", new String[]{"yyyyMMdd"}).getTime();
            final long max = System.currentTimeMillis();
            final long delta = max - min;
            final long time = ThreadLocalRandom.current().nextLong(0, delta) + min;
            return COMMUNITY_FILE_URL + "/bing/" + DateFormatUtils.format(time, "yyyyMMdd") + ".jpg";
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Generates random image URL failed", e);
            return COMMUNITY_FILE_URL + "/bing/20171104.jpg";
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
        while (i < n * 5) {
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
