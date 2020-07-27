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
         <@head title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} ${archiveLabel} - ${blogTitle}">
             <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
         </@head>
    </head>
    <body>
        <div class="wrapper">
            <div id="header">
                <#include "header.ftl" />
                <div class="sub-nav fn-clear">
                    <h2>
                        ${archive1Label}
                        <#if "en" == localeString?substring(0, 2)>
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount})
                        <#else>
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
                        </#if>
                    </h2>
                    <#if 0 != paginationPageCount>
                    <div class="pagination">
                        <#if 1 != paginationPageNums?first>
                        <a id="previousPage" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}"
                           title="${previousPageLabel}"><</a>
                        </#if>
                        <#list paginationPageNums as paginationPageNum>
                        <#if paginationPageNum == paginationCurrentPageNum>
                        <span>${paginationPageNum}</span>
                        <#else>
                        <a href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
                        </#if>
                        </#list>
                        <#if paginationPageNums?last != paginationPageCount>
                        <a id="nextPage" href="${servePath}${path}${pagingSep}${paginationNextPageNum}" title="${nextPagePabel}">></a>
                        </#if>
                    </div>
                    </#if>
                </div>
            </div>
            <div class="fn-clear">
                <div class="main">
                    <#include "article-list.ftl"/>
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=false />
            </div>
        </div>
        <span id="goTop" onclick="Util.goTop()" data-ico="&#xe042;" class="side-tile"></span>
        <#include "footer.ftl"/>
    </body>
</html>
