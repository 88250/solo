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

import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.repository.PluginRepository;
import org.testng.annotations.Test;

/**
 * {@link PluginRepositoryImpl} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 31, 2011
 */
@Test(suiteName = "repository")
public class PluginRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Tests.
     * 
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final PluginRepository pluginRepository = getPluginRepository();
    }
}
