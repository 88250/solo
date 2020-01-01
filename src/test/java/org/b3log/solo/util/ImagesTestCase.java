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
