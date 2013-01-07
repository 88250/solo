<ul>
    <#list articles as article>
    <li class="article<#if !article_has_next> article-last</#if>">
        <div class="article-title">
            <h2>
                <a rel="bookmark" class="ft-gray" href="${servePath}${article.articlePermalink}">
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
            <div class="right">
                <a rel="nofollow" class="ft-gray" href="${servePath}${article.articlePermalink}#comments">
                    ${article.articleCommentCount}&nbsp;&nbsp;${commentLabel}
                </a>&nbsp;&nbsp;
                <a rel="nofollow" class="ft-gray" href="${servePath}${article.articlePermalink}">
                    ${article.articleViewCount}&nbsp;&nbsp;${viewLabel}
                </a>
            </div>
            <div class="clear"></div>
        </div>
        <div class="article-body">
            <div id="abstract${article.oId}">
                ${article.articleAbstract}
            </div>
            <div id="content${article.oId}" class="none"></div>
        </div>
        <div class="right ft-gray">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
            <#else>
            ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
            </#if>
            <a rel="nofollow" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
        </div>
        <div class="left ft-gray">
            ${tag1Label}
            <#list article.articleTags?split(",") as articleTag>
            <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>, </#if>
            </#list>
        </div>
        <div class="clear"></div>
    </li>
    </#list>
</ul>
<#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount>
<div class="article-next ft-gray" onclick="getNextPage()" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
</#if>