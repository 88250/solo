<#list articles as article>
<article class="post">
    <header>
        <h2 class="post-title">
            <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                ${article.articleTitle}
            </a>
            <#if article.hasUpdated>
            <sup class="post-tip">
                ${updatedLabel}
            </sup>
            </#if>
            <#if article.articlePutTop>
            <sup class="post-tip">
                ${topArticleLabel}
            </sup>
            </#if>
        </h2>
    </header>
    <section class="post-excerpt">
        <p>${article.articleAbstract}</p>
    </section>
    <footer>

    </footer>

    <div class="article-header">
        <div class="article-title">
            <div class="article-tags">
                ${tags1Label}
                <#list article.articleTags?split(",") as articleTag>
                <span>
                    <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                        ${articleTag}</a><#if articleTag_has_next>,</#if>
                </span>
                </#list>&nbsp;&nbsp;&nbsp;
                ${author1Label}<a rel="nofollow" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="article-footer">
        <div class="right">
            <span class="article-create-date left">
                &nbsp;${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp
            </span>

        </div>
    </div>
</article>
</#list>

<#if 0 != paginationPageCount>
<nav class="pagination" role="navigation">
    <#if paginationCurrentPageNum != 1>
    <a class="newer-posts" href="${servePath}${path}/${paginationPreviousPageNum}">← ${previousPageLabel}</a>
    </#if>
    <span class="page-number">${pageLabel} ${paginationCurrentPageNum} of ${paginationPageCount}</span>
    <#if paginationPageCount != paginationCurrentPageNum>
    <a class="newer-posts" href="${servePath}${path}/${paginationNextPageNum}">${nextPagePabel} →</a>
    </#if>
</nav>
</#if>