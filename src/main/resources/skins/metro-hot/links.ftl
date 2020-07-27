<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<#include "../../common-template/macro-common_head.ftl">
<#include "macro-side.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${linkLabel} - ${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
    </head>
    <body>
        <div class="wrapper">
            <div id="header">
                <#include "header.ftl" />
                <div class="sub-nav fn-clear">
                    <h2>${linkLabel}</h2>
                </div>
            </div>
            <div class="fn-clear">
                <div class="main">
                    <#if 0 != links?size>
                    <ul class="archives fn-clear">
                        <#list links as link>
                        <li>
                            <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                                <img alt="${link.linkTitle}" src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" width="16" height="16" />
                                ${link.linkTitle}
                            </a>
                        </li>
                        </#list>
                    </ul>
                    </#if>
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=false />
            </div>
        </div>
        <span id="goTop" onclick="Util.goTop()" data-ico="&#xe042;" class="side-tile"></span>
        <#include "footer.ftl"/>
    </body>
</html>
