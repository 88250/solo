<section id="posts" class="posts-expand">
    <#list articles as article>
    <article class="post post-type-normal">
        <header class="post-header">
            <h1 class="post-title">
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
                <span class="post-time">
                    发表于
                    <time>
                         ${article.articleCreateDate?string("yyyy-MM-dd")}
                    </time>
                </span>
                <span class="post-comments-count">
                    &nbsp; | &nbsp;
                    <a href="${servePath}${article.articlePermalink}/#comments">
                        ${article.articleCommentCount}条评论</a>
                </span>
                &nbsp; | &nbsp;热度 ${article.articleViewCount}°C
            </div>
        </header>
        <div class="post-body">
            ${article.articleAbstract}
            <div class="post-more-link text-center">
                <a class="btn" href="${servePath}${article.articlePermalink}/#more" rel="contents">
                    阅读全文 &raquo;
                </a>
            </div>
        </div>
        <footer class="post-footer">
            <div class="post-eof"></div>
        </footer>
    </article>
    </#list>
</section>

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