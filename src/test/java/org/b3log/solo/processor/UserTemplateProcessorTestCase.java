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
 * {@link UserTemplateProcessor} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jul 9, 2020
 * @since 2.9.8
 */
@Test(suiteName = "processor")
public class UserTemplateProcessorTestCase extends AbstractTestCase {

    /**
     * showCategoryArticles.
     */
    public void showCategoryArticles() {
        final MockRequest request = mockRequest();
        request.setRequestURI("/links.html");
        request.setAttribute(Keys.TEMPLATE_DIR_NAME, Option.DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        final MockResponse response = mockResponse();
        mockDispatcher(request, response);

        final String content = response.getString();
        Assert.assertTrue(StringUtils.contains(content, "<title>友链 - Solo 的个人博客</title>"));
    }
}
