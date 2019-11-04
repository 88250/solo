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
<@head title="${archiveLabel} - ${blogTitle}">
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
                <i class="icon__inbox"></i>
            ${statistic.statisticPublishedBlogArticleCount} ${archiveLabel}${articleLabel}
            </div>
        </div>
        <div class="module">
            <div class="module__list">
                <#if 0 != archiveDates?size>
                    <ul>
                    <#list archiveDates as archiveDate>
                        <li>
                            <#if "en" == localeString?substring(0, 2)>
                            <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                ${archiveDate.monthName} ${archiveDate.archiveDateYear}
                                (${archiveDate.archiveDatePublishedArticleCount})
                            </a>
                            <#else>
                            <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                                (${archiveDate.archiveDatePublishedArticleCount})
                            </a>
                            </#if>
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
