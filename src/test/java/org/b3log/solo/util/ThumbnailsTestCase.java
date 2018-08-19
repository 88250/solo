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
package org.b3log.solo.util;

import org.b3log.latke.Latkes;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;

/**
 * {@link org.b3log.solo.util.Thumbnails} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Mar 11, 2018
 * @since 0.6.1
 */
public final class ThumbnailsTestCase {

    /**
     * Test method for {@linkplain Thumbnails#getGravatarURL(java.lang.String, java.lang.String)}.
     *
     * @throws Exception exception
     */
    @Test
    public void getGravatarURL() throws Exception {
        Latkes.initRuntimeEnv();
        Latkes.setLocale(Locale.SIMPLIFIED_CHINESE);

        final String gravatarURL = Thumbnails.getGravatarURL("test@b3log.org", "128");

        Assert.assertEquals(gravatarURL, Solos.GRAVATAR + "bd7e4673cf7fa4b4777353008c86e093?s=128");
    }
}
