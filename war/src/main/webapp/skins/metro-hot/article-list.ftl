<div class="article-list fn-clear">
<#list articles as article>
    <div>
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
    </div>
</#list>
</div>

<#if 0 != paginationPageCount>
<div class="pagination">
    <#if 1 != paginationPageNums?first>
        <a id="previousPage" href="${servePath}${path}/${paginationPreviousPageNum}"
           title="${previousPageLabel}"><</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
        <#if paginationPageNum == paginationCurrentPageNum>
            <span>${paginationPageNum}</span>
        <#else>
            <a href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
        </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
        <a id="nextPage" href="${servePath}${path}/${paginationNextPageNum}" title="${nextPagePabel}">></a>
    </#if>
</#if>
</div>

