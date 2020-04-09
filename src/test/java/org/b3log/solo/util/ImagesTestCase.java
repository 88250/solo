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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link org.b3log.solo.util.Images} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 1, 2020
 * @since 2.7.0
 */
public final class ImagesTestCase {

    /**
     * Test method for {@linkplain Images#randImage()}.
     */
    @Test
    public void randImage() {
        final String url = Images.randImage();
        Assert.assertEquals(url.length(), (Images.COMMUNITY_FILE_URL + "/bing/20171104.jpg").length());
    }

    /**
     * Test method for {@linkplain Images#randomImages(int)}.
     */
    @Test
    public void randImages() {
        final List<String> urls = Images.randomImages(10);
        Assert.assertEquals(urls.size(), 10);
    }
}
