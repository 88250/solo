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
package org.b3log.solo.processor;

import org.apache.commons.lang.StringUtils;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.MockHttpServletRequest;
import org.b3log.solo.MockHttpServletResponse;
import org.b3log.solo.processor.api.MetaWeblogAPI;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * {@link MetaWeblogAPI} test case.
 *
 * @author yugt
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Oct 19, 2018
 * @since 1.7.0
 */
@Test(suiteName = "api")
public class MetaWeblogAPITestCase extends AbstractTestCase {

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
     * 手动构造rpc请求
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void metaWeblog() throws Exception {
        final MockHttpServletRequest request = mockRequest();
        request.setRequestURI("/apis/metaweblog");
        request.setMethod("POST");
        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>")
                .append("<methodCall>")
                .append("<methodName>metaWeblog.newPost</methodName>")
                .append("<params>")
                .append("<param>")
                .append("<value><int>11</int></value>")
                .append("</param>")
                .append("<param>")
                .append("<value><string>test@gmail.com</string></value>")
                .append("</param>")
                .append("<param>")
                .append("<value><string>pass</string></value>")
                .append("</param>")
                .append("<param>")
                .append("<value>")
                .append("<struct>")
                .append("<member>")
                .append("<name>dateCreated</name>")
                .append("<value><dateTime.iso8601>20040503T17:30:08</dateTime.iso8601></value>")
                .append("</member>")
                .append("<member>")
                .append("<name>title</name>")
                .append("<value><string>title</string></value>")
                .append("</member>")
                .append("<member>")
                .append("<name>description</name>")
                .append("<value><string>description</string></value>")
                .append("</member>")
                .append("<member>")
                .append("<name>categories</name>")
                .append("<value>")
                .append("<array>")
                .append("<data>")
                .append("<value>")
                .append("<string>Solo</string>")
                .append("</value>")
                .append("</data>")
                .append("</array>")
                .append("</value>")
                .append("</member>")
                .append("</struct>")
                .append("</value>")
                .append("</param>")
                .append("<param>")
                .append("<value><boolean>1</boolean></value>")
                .append("</param>")
                .append("</params>")
                .append("</methodCall>");
        final BufferedReader reader = new BufferedReader(new StringReader(sb.toString()));
        request.setReader(reader);

        final MockHttpServletResponse response = mockResponse();
        mockDispatcherServletService(request, response);

        final String content = response.body();
        // System.out.println("xxxxxcontent:" + content);
        Assert.assertTrue(StringUtils.startsWith(content, "<?xml version=\"1.0\""));
    }

    /**
     * 使用XmlRpcClient发送rpc请求
     *
     * @throws Exception exception
     */
//    @Test(dependsOnMethods = "init")
//    public void metaWeblog2() throws Exception {
//    	final MetaWeblogAPI metaWeblogAPI = getMetaWeblogAPI();
//        metaWeblogAPI.metaWeblog(null,null,null);
//        
//    	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();  
//        config.setServerURL(new URL("http://localhost:8080/solo/apis/metaweblog"));
//        XmlRpcClient client = new XmlRpcClient();  
//        client.setConfig(config);  
//        Vector<Object> params= new Vector<Object>();
//        params.add(1, 12);
//        params.add(2, "gangtaoyu@gmail.com");
//        params.add(3, "sky");
//        params.add(4, new Struct());
//        params.add(5, "publish");
//        Integer result=(Integer)client.execute("metaWeblog.newPost",params);  
//
//        System.out.println(result);  
//        
//        
//    }

    class Struct {
        String title = "title";
        String link = "link";
        String description = "description";
        String author = "author";
        String[] category = {"category1", "category2"};
        String comments = "comments";
        String enclosure = "enclosure";
        String guid = "guid";
        String pubDate = "pubDate";
        String source = "source";
    }

}
