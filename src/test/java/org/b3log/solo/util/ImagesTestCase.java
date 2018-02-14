/*
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
package org.b3log.solo.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * {@link org.b3log.solo.util.Images} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Feb 14, 2018
 * @since 2.7.0
 */
public final class ImagesTestCase {

    /**
     * Test method for {@linkplain Images#randImage()}.
     */
    @Test
    public void randImage() {
        final String url = Images.randImage();
        Assert.assertEquals(url.length(), "https://img.hacpai.com/bing/20171104.jpg".length());
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
