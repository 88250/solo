<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${blogTitle}">
    <meta name="keywords" content="${metaKeywords}" />
    <meta name="description"
          content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>" />
</@head>
</head>
<body>
${topBarReplacement}
<div class="wrapper">
<#include "header.ftl" />
    <div class="articles-header fn-clear">
        <h2>${blogSubtitle}</h2>
    <#if 0 != paginationPageCount>
        <div class="pagination">
            <#if 1 != paginationPageNums?first>
                <a id="previousPage" href="${servePath}${path}/${paginationPreviousPageNum}"
                   title="${previousPageLabel}"><</a>
            </#if>
            <#list paginationPageNums as paginationPageNum>
                <#if paginationPageNum == paginationCurrentPageNum>
                    <span>${paginationPageNum}</span>
                <#else>
                    <a href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
                </#if>
            </#list>
            <#if paginationPageNums?last != paginationPageCount>
                <a id="nextPage" href="${servePath}${path}/${paginationNextPageNum}" title="${nextPagePabel}">></a>
            </#if>
        </div>
    </#if>
    </div>
    <div class="fn-clear">
        <div class="main">
        <#include "article-list.ftl"/>
        <#include "copyright.ftl"/>
        </div>
    <#include "side.ftl" />
    </div>
</div>
<#include "footer.ftl"/>
</body>
</html>
