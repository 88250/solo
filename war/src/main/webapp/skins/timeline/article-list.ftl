<div class="content articles">
    <div class="vertical"></div>
    <#list articles as article>
    <article<#if !article_has_next> class="last"</#if>>
        <div>
            <div class="dot"></div>
            <div class="arrow"></div>
            <time>
                <span>
                    ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
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
            </h2>
            <p>
                ${article.articleAbstract}
            </p>
            <span class="ico-tags" title="${tagLabel}">
                <#list article.articleTags?split(",") as articleTag><a rel="category tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
            </span>
            <span class="ico-author" title="${authorLabel}">
                <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
            </span>
            <span class="ico-comment" title="${commentLabel}">
                <#if article.articleCommentCount == 0>
                <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                    ${noCommentLabel}
                </a>
                <#else>
                <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                    ${article.articleCommentCount}
                </a>
                </#if>
            </span>
            <span class="ico-view" title="${viewLabel}">
                <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                    ${article.articleViewCount}
                </a>
            </span>
        </div>
    </article>
    </#list>
</div>
<#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount>
<div class="article-next ft-gray" onclick="getNextPage()" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
</#if>