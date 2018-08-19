<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${blogTitle}">
    <meta name="keywords" content="${metaKeywords},${archiveLabel}"/>
    <meta name="description" content="${metaDescription},${archiveLabel}"/>
</@head>
</head>
<body>
<#include "header.ftl">
<#include "nav.ftl">
<div class="main">
<#if noticeBoard??>
    <div class="board">
    ${noticeBoard}
    </div>
</#if>
    <div class="wrapper content">
        <div class="module__title">
            <span>
                ${links?size}
                    <span class="ft-green ft-12">${linkLabel}</span>
            </span>
        </div>
    <#if 0 != links?size>
        <#list links as link>
            <div class="page__item">
                <h3>
                    <a rel="friend" class="ft-gray" href="${link.linkAddress}" target="_blank">
                    ${link.linkTitle}
                        <span class="ft-12 ft-green">${link.linkDescription}</span>
                    </a>
                </h3>

            </div>
        </#list>
    </#if>
    </div>
<#include "bottom.ftl">
</div>
<#include "footer.ftl">
</body>
</html>
