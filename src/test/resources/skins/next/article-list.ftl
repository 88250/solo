<section class="posts-expand">
    <#list articles as article>
    <article class="post-item">
        <header>
            <h1>
                <a class="post-title-link"  rel="bookmark" href="${servePath}${article.articlePermalink}">
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
            </h1>

            <div class="post-meta">
                <span>
                    ${postTimeLabel}
                    <time>
                        ${article.articleCreateDate?string("yyyy-MM-dd")}
                    </time>
                </span>
                <span>
                    &nbsp; | &nbsp;
                    <a href="${servePath}${article.articlePermalink}#comments">
                        ${article.articleCommentCount} ${cmtLabel}</a>
                </span>
                &nbsp; | &nbsp;${viewsLabel} ${article.articleViewCount}°C
            </div>
        </header>
        <div class="article-body">
            ${article.articleAbstract}
        </div>
        <div class="post-more-link">
            <a href="${servePath}${article.articlePermalink}#more" rel="contents">
                ${readLabel} &raquo;
            </a>
        </div>
    </article>
    </#list>
</section>

<#if 0 != paginationPageCount>
<nav class="pagination">
    <#if 1 != paginationPageNums?first>
    <a href="${servePath}${path}/${paginationPreviousPageNum}" class="extend next"><<</a>
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
    <a href="${servePath}${path}/${paginationNextPageNum}" class="extend next">>></a>
    </#if>
</nav>
</#if>