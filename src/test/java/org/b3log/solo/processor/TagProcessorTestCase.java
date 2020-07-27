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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockRequest;
import org.b3log.solo.MockResponse;
import org.b3log.solo.model.Option;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link TagProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.1, May 29, 2018
 * @since 1.7.0
 */
@Test(suiteName = "processor")
public class TagProcessorTestCase extends AbstractTestCase {

    /**
     * showTagArticles.
     */
    public void showTagArticles() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/tags/Solo");
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>Solo 标签 - Solo 的个人博客</title>"));
    }
}
