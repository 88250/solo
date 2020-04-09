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
package org.b3log.solo.util;

import io.netty.handler.codec.http.*;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.Response;
import org.b3log.latke.util.URLs;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mock utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Feb 28, 2020
 * @since 3.9.0
 */
public final class Mocks {

    private Mocks() {
    }

    public static String mockRequest(final String uri, final String scheme, final String host) {
        final Mocks.MockRequest request = Mocks.mockRequest0(uri, scheme, host);
        if (StringUtils.contains(uri, "?")) {
            final Map<String, String> params = new LinkedHashMap<>();
            final String query = StringUtils.substringAfter(uri, "?");
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                params.put(URLs.decode(pair.substring(0, idx)), URLs.decode(pair.substring(idx + 1)));
            }
            request.setParams(params);
        }

        final Mocks.MockResponse response = Mocks.mockResponse();
        Mocks.mockDispatcher(request, response);

        return response.getString();
    }

    private static void mockDispatcher(final Request request, final Response response) {
        new MockDispatcher().handle(request, response);
    }

    private static MockRequest mockRequest0(final String uri, final String scheme, final String host) {
        final FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
        Latkes.setScheme(scheme);
        if (StringUtils.contains(host, ":")) {
            Latkes.setHost(host.split(":")[0]);
            Latkes.setPort(host.split(":")[1]);
        } else {
            Latkes.setHost(host);
            Latkes.setPort("");
        }

        return new MockRequest(req);
    }

    private static MockResponse mockResponse() {
        final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        final MockResponse response = new MockResponse(res);

        return response;
    }

    private static class MockDispatcher {
        public void handle(final Request req, final Response resp) {
            Dispatcher.handle(req, resp);
        }
    }

    private static class MockRequest extends Request {
        public MockRequest(final FullHttpRequest req) {
            super(null, req);
        }

        @Override
        public String getRemoteAddr() {
            return "localhost mock";
        }
    }

    private static class MockResponse extends Response {
        public MockResponse(final HttpResponse res) {
            super(null, res);
        }
    }
}
