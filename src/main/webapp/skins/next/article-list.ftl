<#list articles as article>
<article>
    <header>
        <h2>
            <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                ${article.articleTitle}
            </a>
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
        </h2>

        <time><span class="icon-date"></span> ${article.articleCreateDate?string("yyyy-MM-dd")}</time>
    </header>
    <section class="abstract">
        ${article.articleAbstract}
    </section>
    <footer class="tags">
        <span class="icon-tag"></span>  &nbsp;
        <#list article.articleTags?split(",") as articleTag>
        <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
            ${articleTag}</a>
        </#list>

        <a rel="nofollow" href="${servePath}/authors/${article.authorId}">
            <img class="avatar" title="${article.authorName}" alt="${article.authorName}" src="${article.authorThumbnailURL}"/>
        </a>
    </footer>
</article>
</#list>

<#if 0 != paginationPageCount>
    <nav class="pagination">
        <#if 1 != paginationPageNums?first>
        <a href="${servePath}${path}/${paginationPreviousPageNum}" class="extend">${previousPageLabel}</a>
        <a class="page-num" href="${servePath}${path}/1">1</a> ...
        </#if>
        <#list paginationPageNums as paginationPageNum>
        <#if paginationPageNum == paginationCurrentPageNum>
        <span class="current page-num">${paginationPageNum}</span>
        <#else>
        <a class="page-num" href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
        </#if>
        </#list>
        <#if paginationPageNums?last != paginationPageCount> ...
        <a href="${servePath}${path}/${paginationPageCount}" class="page-num">${paginationPageCount}</a>
        <a href="${servePath}${path}/${paginationNextPageNum}" class="extend">${nextPagePabel}</a>
        </#if>
    </nav>
    </#if>