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

import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.UserExt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link UserRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.3, Feb 8, 2019
 */
@Test(suiteName = "repository")
public final class UserRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Tests.
     *
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final UserRepository userRepository = getUserRepository();

        final JSONObject another = new JSONObject();
        another.put(User.USER_NAME, "test1");
        another.put(User.USER_URL, "https://b3log.org");
        another.put(User.USER_ROLE, Role.DEFAULT_ROLE);
        another.put(UserExt.USER_AVATAR, "");
        another.put(UserExt.USER_GITHUB_ID, "");
        another.put(UserExt.USER_B3_KEY, "");

        Transaction transaction = userRepository.beginTransaction();
        userRepository.add(another);
        transaction.commit();

        Assert.assertNotNull(userRepository.getAdmin());

        final JSONObject result = userRepository.get(new Query().setFilter(
                new PropertyFilter(User.USER_NAME, FilterOperator.EQUAL, "test1")));

        final JSONArray users = result.getJSONArray(Keys.RESULTS);
        Assert.assertEquals(users.length(), 1);
        Assert.assertEquals(users.getJSONObject(0).getString(User.USER_NAME), "test1");

        final JSONObject notFound = userRepository.getByUserName("not.found");
        Assert.assertNull(notFound);

        final JSONObject found = userRepository.getByUserName("test1");
        Assert.assertNotNull(found);
        Assert.assertEquals(found.getString(User.USER_NAME), "test1");
    }
}
