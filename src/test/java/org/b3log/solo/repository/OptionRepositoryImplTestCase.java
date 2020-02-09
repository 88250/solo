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
package org.b3log.solo.repository;

import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Option;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link OptionRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 29, 2019
 * @since 0.6.0
 */
@Test(suiteName = "repository")
public final class OptionRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Tests.
     *
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final OptionRepository optionRepository = getOptionRepository();

        Assert.assertTrue(0 < optionRepository.count());
        Assert.assertNotNull(optionRepository.get(Option.ID_C_BLOG_TITLE));
    }
}
