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
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

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

        final List<JSONObject> users = (List<JSONObject>) result.opt(Keys.RESULTS);
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.get(0).getString(User.USER_NAME), "test1");

        final JSONObject notFound = userRepository.getByUserName("not.found");
        Assert.assertNull(notFound);

        final JSONObject found = userRepository.getByUserName("test1");
        Assert.assertNotNull(found);
        Assert.assertEquals(found.getString(User.USER_NAME), "test1");
    }
}
