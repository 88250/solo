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

import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link LinkRepositoryImpl} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 29, 2011
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

        final int link1Order = 1, link2Order = 2, link3Order = 3;

        JSONObject link1 = new JSONObject();

        link1.put(Link.LINK_TITLE, "link title");
        link1.put(Link.LINK_DESCRIPTION, "link description");
        link1.put(Link.LINK_ADDRESS, "link address");
        link1.put(Link.LINK_ORDER, link1Order);

        Transaction transaction = linkRepository.beginTransaction();
        linkRepository.add(link1);
        transaction.commit();

        Assert.assertNull(linkRepository.getByAddress("test"));
        Assert.assertNotNull(linkRepository.getByAddress("link address"));

        Assert.assertNull(linkRepository.getByOrder(0));
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

        final int total = 3;
        Assert.assertEquals(linkRepository.count(), total);

        link1 = linkRepository.getUpper(link2Id);
        Assert.assertNotNull(link1);
        Assert.assertEquals(link1.getInt(Link.LINK_ORDER), link1Order);

        link3 = linkRepository.getUnder(link2Id);
        Assert.assertNotNull(link3);
        Assert.assertEquals(link3.getInt(Link.LINK_ORDER), link3Order);
    }
}
