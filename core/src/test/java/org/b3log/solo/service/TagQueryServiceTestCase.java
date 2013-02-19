/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.solo.service;

import java.util.List;

import org.b3log.latke.model.User;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Tag;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link TagQueryService} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 11, 2012
 */
@Test(suiteName = "service")
public class TagQueryServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     * 
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = getInitService();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = UserQueryService.getInstance();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Get Tags.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getTags() throws Exception {
        final TagQueryService tagQueryService = getTagQueryService();

        final List<JSONObject> tags = tagQueryService.getTags();
        Assert.assertNotNull(tags);
        Assert.assertEquals(tags.size(), 1);
        Assert.assertEquals(tags.get(0).getString(Tag.TAG_TITLE), "B3log");
    }

    /**
     * Get Tag By Title.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void getTagByTitle() throws Exception {
        final TagQueryService tagQueryService = getTagQueryService();
        
        final JSONObject result = tagQueryService.getTagByTitle("B3log");
        Assert.assertNotNull(result);
        
        final JSONObject tag = result.getJSONObject(Tag.TAG);
        Assert.assertNotNull(tag);
        Assert.assertEquals(tag.getString(Tag.TAG_TITLE), "B3log");
        
    }
}
