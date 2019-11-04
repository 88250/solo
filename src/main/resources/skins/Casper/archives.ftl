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
<body class="fn__flex-column">
<div id="pjax" class="fn__flex-1">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
<#include "macro-header.ftl">
    <@header type='other'></@header>
    <div class="wrapper other">
        <h2 class="other__title"><a href="${servePath}" class="ft__a">${blogTitle}</a> - ${archiveLabel}</h2>
        <div class="other__meta">
        ${archiveDates?size} ${cntMonthLabel}
        ${statistic.statisticPublishedBlogArticleCount} ${cntArticleLabel}
        </div>
        <div class="other__content">
            <#if 0 != archiveDates?size>
            <#list archiveDates as archiveDate>
                <div class="other__item other__item--archive">
                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                    <#if "en" == localeString?substring(0, 2)>
                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}
                    <#else>
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                    </#if>
                    </a>
                    <span>
                        ${archiveDate.archiveDatePublishedArticleCount}
                        ${cntArticleLabel}
                    </span>
                </div>
            </#list>
            </#if>
        </div>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
</body>
</html>
