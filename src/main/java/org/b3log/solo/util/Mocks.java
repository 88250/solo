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
package org.b3log.solo.util;

import io.netty.handler.codec.http.*;
import org.apache.commons.lang.StringUtils;
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
 * @version 1.0.0.0, Jan 7, 2020
 * @since 3.9.0
 */
public final class Mocks {

    private Mocks() {
    }

    public static String mockRequest(final String uri) {
        final Mocks.MockRequest request = Mocks.mockRequest();
        request.setRequestURI(uri);

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

    private static MockRequest mockRequest() {
        final FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/a");
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
