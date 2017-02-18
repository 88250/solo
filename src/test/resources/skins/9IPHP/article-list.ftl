<div>
    <#list articles as article>
    <article class="post">
        <header>
            <h1>
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
            </h1>

            <div class="meta">
                <span class="tooltipped tooltipped-n" aria-label="${createDateLabel}">
                    <i class="icon-date"></i>
                    <time>
                        ${article.articleCreateDate?string("yyyy-MM-dd")}
                    </time>
                </span>
                &nbsp; | &nbsp;
                <span class="tooltipped tooltipped-n" aria-label="${commentCountLabel}">
                    <i class="icon-comments"></i>
                    <a href="${servePath}${article.articlePermalink}#comments">
                        ${article.articleCommentCount} ${commentLabel}</a>
                </span>
                &nbsp; | &nbsp;
                <span class="tooltipped tooltipped-n" aria-label="${viewCountLabel}">
                    <i class="icon-views"></i>
                    ${article.articleViewCount} ${viewLabel}
                </span>
            </div>
        </header>
        <div class="content-reset">
            ${article.articleAbstract}
        </div>
        <footer class="fn-clear tags">
            <#list article.articleTags?split(",") as articleTag>
                <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a>
            </#list>
            <a href="${servePath}${article.articlePermalink}#more" rel="contents" class="fn-right">
                ${readLabel} &raquo;
            </a>
        </footer>
    </article>
    </#list>


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
</div>