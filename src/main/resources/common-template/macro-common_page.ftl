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
<#include "macro-common_head.ftl"/>
<#macro commonPage title>
<!DOCTYPE html>
<html>
<head>
    <#if !blogTitle??>
    <#assign blogTitle = "Solo">
    </#if>
    <@head title="${title} - ${blogTitle}">
        <link type="text/css" rel="stylesheet"
        href="${staticServePath}/scss/start.css?${staticResourceVersion}" charset="utf-8"/>
        <meta name="robots" content="none"/>
    </@head>
</head>
<body>
<div class="wrap">
    <div class="content-wrap">
        <div class="content">
            <div class="main">
            <#nested>
            </div>
        </div>
    </div>
    <div class="footerWrapper">
        <div class="footer">
            Powered by <a href="https://b3log.org" target="_blank">B3log 开源</a> • <a href="https://b3log.org/solo" target="_blank">Solo</a> ${version}
        </div>
    </div>
</div>
</body>
</html>
</#macro>
