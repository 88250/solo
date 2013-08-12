<div class="article-list fn-clear">
    <#list articles as article>
    <div>
        <div class="article-abstract">
            <div class="fn-clear">
                <div class="article-date" data-ico="&#xe200;">
                    <#if article.hasUpdated>
                    ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                    <#else>
                    ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
                    </#if>  
                </div>
                <div class="fn-right">
                    <a rel="nofollow" data-ico="&#xe14e;" href="${servePath}${article.articlePermalink}#comments">
                        ${article.articleCommentCount}
                    </a>
                    <a rel="nofollow" data-ico="&#xe185;" href="${servePath}${article.articlePermalink}">
                        ${article.articleViewCount}
                    </a>
                    <a rel="nofollow" data-ico="&#x0060;" href="${servePath}/authors/${article.authorId}">
                        ${article.authorName}
                    </a>
                </div>
            </div>

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
            <div class="article-body">
                ${article.articleAbstract}
            </div>
            <div data-ico="&#x003b;" title="${tagLabel}" class="article-tags">
                <#list article.articleTags?split(",") as articleTag>
                <a  rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>, </#if>
                </#list>
            </div>
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

