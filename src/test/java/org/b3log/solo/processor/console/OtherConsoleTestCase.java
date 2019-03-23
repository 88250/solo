package org.b3log.solo.processor.console;

import org.apache.commons.lang.StringUtils;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link OtherConsole} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 23, 2019
 * @since 3.4.0
 */
@Test(suiteName = "processor")
public class OtherConsoleTestCase extends AbstractTestCase {

    /**
     * Init.
     *
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        super.init();
    }

    /**
     * removeUnusedArchives.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void removeUnusedArchives() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/console/archive/unused");
        request.setMethod("DELETE");

        mockAdminLogin(request);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        Assert.assertTrue(StringUtils.contains(content, "sc\":true"));
    }
}
