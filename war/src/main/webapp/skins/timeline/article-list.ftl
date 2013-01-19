<div class="content articles">
    <div class="vertical"></div>
    <#list articles as article>
    <article<#if !article_has_next> class="last"</#if>>
        <div>
            <div class="dot"></div>
            <div class="arrow"></div>
            <time>
                <span>
                    <#if article.hasUpdated>
                    ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                    <#else>
                    ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
                    </#if>
                </span>
            </time>
            <h2>
                <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                    ${article.articleTitle}
                </a>
                <#if article.hasUpdated>
                <sup>
                    ${updatedLabel}
                </sup>
                </#if>
                <#if article.articlePutTop>
                <sup>
                    ${topArticleLabel}
                </sup>
                </#if>
                <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                    ${article.articleViewCount}&nbsp;&nbsp;${viewLabel}
                </a>
            </h2>
            <p>
                ${article.articleAbstract}
            </p>
            <span class="ico-author">
                <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
            </span>
            <span class="ico-tags">
                <#list article.articleTags?split(",") as articleTag><a rel="category tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
            </span>
            <span class="ico-comment">
                <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                    ${article.articleCommentCount}
                </a>
            </span>
        </div>
    </article>
    </#list>
</div>
<#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount>
<div class="article-next ft-gray" onclick="getNextPage()" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
</#if>