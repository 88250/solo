<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<#include "../../common-template/macro-common_head.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${linkLabel} - ${blogTitle}">
    <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
</@head>
</head>
<body>
<#include "header.ftl">
<div id="pjax" class="main">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="content">
    <main>
        <div class="module">
            <div class="module__content ft__center">
                <i class="icon__home"></i>
                <a href="${servePath}" class="breadcrumb">${blogTitle}</a>
                &nbsp; > &nbsp;
                <i class="icon__link"></i>
            ${linkLabel}
            </div>
        </div>
        <div class="module">
            <div class="module__list">
                <#if 0 != links?size>
                    <ul class="list">
                    <#list links as link>
                        <li>
                            <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}"
                               target="_blank">
                                ${link.linkTitle}
                            </a>
                        </li>
                    </#list>
                    </ul>
                </#if>
            </div>
        </div>
    </main>
    </div>
    <#include "side.ftl">
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
</body>
</html>
