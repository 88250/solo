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
         <@head title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} ${archiveLabel} - ${blogTitle}">
             <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
         </@head>
    </head>
    <body>
        <#include "header.ftl">
        <main class="main">
            <div class="wrapper">
            <div class="content page-archive">
                <section class="posts-collapse">
                    <span class="archive-move-on"></span>
                    <span class="archive-page-counter">
                      ${ohLabel}..! 
                        <#if "en" == localeString?substring(0, 2)>
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} 
                        <#else>
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                        </#if>
                        ${sumLabel} ${archiveDate.archiveDatePublishedArticleCount} ${fightLabel}
                    </span>
                </section>
                <#include "article-list.ftl">
            </div>
            <#include "side.ftl">
            </div>
        </main>
        <#include "footer.ftl">
    </body>
</html>
