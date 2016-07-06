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
        <div class="container one-column">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">

                        <div id="posts" class="posts-collapse">
                            <div class="collection-title">
                                <h2>
                                    ${tag.tagTitle}
                                    <small>${tagLabel}</small>
                                </h2>
                            </div>
                            <#list articles as article>
                            <article class="post post-type-normal">
                                <header class="post-header">
                                    <h1 class="post-title">
                                        <a class="post-title-link" href="${servePath}${article.articlePermalink}">
                                            <span itemprop="name">${article.articleTitle}</span>
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
                                        </a>
                                    </h1>
                                    <div class="post-meta">
                                        <time class="post-time">
                                            ${article.articleCreateDate?string("MM-dd")}
                                        </time>
                                    </div>
                                </header>
                            </article>
                            </#list>

                            <#if 0 != paginationPageCount>
                            <nav class="pagination">
                                <#if 1 != paginationPageNums?first>
                                <a href="${servePath}${path}/${paginationPreviousPageNum}" class="extend"><i class="fa fa-angle-left"></i></a>
                                <a class="page-number" href="${servePath}${path}/1">1</a> ...
                                </#if>
                                <#list paginationPageNums as paginationPageNum>
                                <#if paginationPageNum == paginationCurrentPageNum>
                                <span class="page-number current">${paginationPageNum}</span>
                                <#else>
                                <a class="page-number" href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
                                </#if>
                                </#list>
                                <#if paginationPageNums?last != paginationPageCount> ...
                                <a href="${servePath}${path}/${paginationPageCount}" class="page-number">${paginationPageCount}</a>
                                <a href="${servePath}${path}/${paginationNextPageNum}" class="extend next"><i class="fa fa-angle-right"></i></a>
                                </#if>
                            </nav>
                            </#if>
                        </div>
                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
        </div>
    </body>
</html>
