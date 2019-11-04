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
        ${topBarReplacement}
        <#include "nav.ftl">
        <div class="wrapper">
            <div class="content">
                <#include "header.ftl">
                <div class="roundtop"></div>
                <div class="body">
                    <div class="left main">
                        <#if 0 != archiveDates?size>
                            <#assign curYear = year?number>
                            <div class="kind-title">${year} ${yearLabel}</div>
                            <#list archiveDates as archiveDate>
                                <#if curYear != archiveDate.archiveDateYear?number>
                                <div class="kind-title">${archiveDate.archiveDateYear} ${yearLabel}</div>
                                </#if>
                                <div class="kind-panel">
                                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                        ${archiveDate.archiveDateMonth} ${monthLabel}
                                        - ${archiveDate.archiveDatePublishedArticleCount} ${countLabel}</a>
                                </div>
                                <#assign curYear = archiveDate.archiveDateYear?number>
                            </#list>
                        </#if>
                    </div>
                    <div class="right">
                        <#include "side.ftl">
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="roundbottom"></div>
            </div>
        </div>
        <div class="footer">
            <div class="footer-icon"><#include "statistic.ftl"></div>
            <#include "footer.ftl">
        </div>
    </body>
</html>
