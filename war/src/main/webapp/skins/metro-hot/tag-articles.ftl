<#include "macro-head.ftl">
<#include "macro-side.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${tag.tagTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${tag.tagTitle}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <div class="wrapper">
            <div id="header">
                <#include "header.ftl" />
                <div class="sub-nav fn-clear">
                    <h2>
                        <a id="tag" rel="alternate" href="${servePath}/tag-articles-feed.do?oId=${tag.oId}">
                            ${tag1Label}
                            ${tag.tagTitle}
                            (${tag.tagPublishedRefCount})
                        </a>
                    </h2>
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
