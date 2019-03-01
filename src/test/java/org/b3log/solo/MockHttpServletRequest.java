/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
package org.b3log.solo;

import org.b3log.latke.Latkes;
import org.b3log.latke.servlet.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.security.Principal;
import java.util.*;

/**
 * Mock HTTP servlet request.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 1, 2019
 */
public class MockHttpServletRequest implements HttpServletRequest {

    /**
     * Header.
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * Request URI.
     */
    private String requestURI = "/";

    /**
     * Context path.
     */
    private String contextPath = "";

    /**
     * Attributes.
     */
    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private Cookie[] cookies;

    public void setCookies(final Cookie[] cookies) {
        this.cookies = cookies;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public long getDateHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets header with the specified name and value.
     *
     * @param name  the specified name
     * @param value the specified value
     */
    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(final String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration getHeaders(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Enumeration getHeaderNames() {
        return new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public Object nextElement() {
                return null;
            }
        };
    }

    @Override
    public int getIntHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private String method = HttpMethod.GET.toString();

    public void setMethod(final String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isUserInRole(final String role) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Sets request URI with the specified request URI.
     *
     * @param requestURI the specified request URI
     */
    public void setRequestURI(final String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(Latkes.getServePath() + requestURI);
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpSession getSession(final boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCharacterEncoding() {
        return "mock character encoding";
    }

    @Override
    public void setCharacterEncoding(final String env) {
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getContentType() {
        return "mock content type";
    }

    @Override
    public ServletInputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Map<String, String> param = new HashMap<>();

    public void putParameter(final String name, final String value) {
        param.put(name, value);
    }

    @Override
    public String getParameter(final String name) {
        return param.get(name);
    }

    @Override
    public Enumeration getParameterNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getParameterValues(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map getParameterMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private BufferedReader reader;

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public BufferedReader getReader() {
        return reader;
    }


    private String remoteAddr;

    public void setRemoteAddr(final String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return "mock remote host";
    }

    @Override
    public void setAttribute(final String name, final Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Enumeration getLocales() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRealPath(final String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "mock local name";
    }

    @Override
    public String getLocalAddr() {
        return "mock local addr";
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean authenticate(final HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void login(final String username, final String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Part> getParts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Part getPart(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
