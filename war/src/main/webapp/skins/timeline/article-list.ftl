<div class="wrapper">
    <div class="articles container">
        <div class="vertical"></div>
        <#list articles as article>
        <article>
            <div class="module">
                <div class="dot"></div>
                <div class="arrow"></div>
                <time class="article-time">
                    <span>
                        ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
                    </span>
                </time>
                <h2 class="article-title">
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
                <span class="ico-tags ico" title="${tagLabel}">
                    <#list article.articleTags?split(",") as articleTag><a rel="category tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
                </span>
                <span class="ico-author ico" title="${authorLabel}">
                    <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
                </span>
                <span class="ico-comment ico" title="${commentLabel}">
                    <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                        <#if article.articleCommentCount == 0>
                        ${noCommentLabel}
                        <#else>
                        ${article.articleCommentCount}
                        </#if>
                    </a>
                </span>
                <span class="ico-view ico" title="${viewLabel}">
                    <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                        ${article.articleViewCount}
                    </a>
                </span>
            </div>
        </article>
        </#list>
        <#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount>
        <div class="article-more" onclick="timeline.getNextPage(this)" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
        </#if>
    </div>
</div>