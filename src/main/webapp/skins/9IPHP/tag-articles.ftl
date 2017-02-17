<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${tag.tagTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${tag.tagTitle}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        <#include "header.ftl">
        <div class="wrapper">
            <div class="main-wrap">
                <main class="other">
                    <div class="title">
                        <h2><i class="icon-tags"></i>
                            &nbsp;${tag.tagTitle}
                            <small>${tagLabel}</small>
                    </div>
                    <ul class="list">
                    <#list articles as article>
                        <li>
                            <a class="post-title" href="${servePath}${article.articlePermalink}">
                                <span>${article.articleTitle}</span>
                                <#if article.articlePutTop>
                                    <sup>
                                        ${topArticleLabel}
                                    </sup>
                                </#if>
                                <#if article.hasUpdated>
                                    <sup>
                                        ${updatedLabel}
                                    </sup>
                                </#if>
                                <time class="fn-right">
                                   <i class="icon-date"></i> ${article.articleCreateDate?string("yyyy-MM-dd")}
                                </time>
                            </a>
                        </li>
                    </#list>
                    </ul>

                    <#if 0 != paginationPageCount>
                        <div class="fn-clear">
                            <nav class="pagination fn-right">
                                <#if 1 != paginationPageNums?first>
                                    <a href="${servePath}${path}/${paginationPreviousPageNum}" class="page-number">&laquo;</a>
                                    <a class="page-number" href="${servePath}${path}/1">1</a> <span class="page-number">...</span>
                                </#if>
                                <#list paginationPageNums as paginationPageNum>
                                    <#if paginationPageNum == paginationCurrentPageNum>
                                        <span class="page-number current">${paginationPageNum}</span>
                                        <#else>
                                            <a class="page-number" href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
                                    </#if>
                                </#list>
                                <#if paginationPageNums?last != paginationPageCount> <span class="page-number">...</span>
                                    <a href="${servePath}${path}/${paginationPageCount}" class="page-number">${paginationPageCount}</a>
                                    <a href="${servePath}${path}/${paginationNextPageNum}" class="page-number">&raquo;</a>
                                </#if>
                            </nav>
                        </div>
                    </#if>
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
