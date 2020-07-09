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

import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Link;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link LinkRepository} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jul 9, 2020
 */
@Test(suiteName = "repository")
public final class LinkRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Tests.
     *
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final LinkRepository linkRepository = getLinkRepository();

        final int link1Order = 0, link2Order = 1, link3Order = 2;

        JSONObject link1 = new JSONObject();
        link1.put(Link.LINK_TITLE, "link title");
        link1.put(Link.LINK_DESCRIPTION, "link description");
        link1.put(Link.LINK_ADDRESS, "link address");
        link1.put(Link.LINK_ICON, "link icon");
        link1.put(Link.LINK_ORDER, link1Order);

        Transaction transaction = linkRepository.beginTransaction();
        linkRepository.add(link1);
        transaction.commit();

        Assert.assertNull(linkRepository.getByAddress("test"));
        Assert.assertNotNull(linkRepository.getByAddress("link address"));

        Assert.assertNotNull(linkRepository.getByOrder(0));
        Assert.assertNotNull(linkRepository.getByOrder(link1Order));

        final JSONObject link2 = new JSONObject();
        link2.put(Link.LINK_TITLE, "link title");
        link2.put(Link.LINK_DESCRIPTION, "link description");
        link2.put(Link.LINK_ADDRESS, "link address");
        link2.put(Link.LINK_ORDER, link2Order);

        transaction = linkRepository.beginTransaction();
        final String link2Id = linkRepository.add(link2);
        transaction.commit();

        Assert.assertEquals(linkRepository.getMaxOrder(), link2Order);

        JSONObject link3 = new JSONObject();
        link3.put(Link.LINK_TITLE, "link title");
        link3.put(Link.LINK_DESCRIPTION, "link description");
        link3.put(Link.LINK_ADDRESS, "link address");
        link3.put(Link.LINK_ORDER, link3Order);
        transaction = linkRepository.beginTransaction();
        linkRepository.add(link3);

        transaction.commit();

        Assert.assertEquals(linkRepository.count(), 3);

        link1 = linkRepository.getUpper(link2Id);
        Assert.assertNotNull(link1);
        Assert.assertEquals(link1.getInt(Link.LINK_ORDER), link1Order);

        link3 = linkRepository.getUnder(link2Id);
        Assert.assertNotNull(link3);
        Assert.assertEquals(link3.getInt(Link.LINK_ORDER), link3Order);
    }
}
