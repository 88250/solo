<div>
    <#list articles as article>
    <div class="article">
        <div class="article-header">
            <div class="article-date">
                <#if article.hasUpdated>
                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                <#else>
                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                </#if>
            </div>
            <div class="article-title">
                <h2>
                    <a rel="bookmark" class="no-underline" href="${servePath}${article.articlePermalink}">
                        ${article.articleTitle}
                    </a>
                    <#if article.hasUpdated>
                    <sup class="red">
                        ${updatedLabel}
                    </sup>
                    </#if>
                    <#if article.articlePutTop>
                    <sup class="red">
                        ${topArticleLabel}
                    </sup>
                    </#if>
                </h2>
                <div class="article-tags">
                    ${tags1Label}
                    <#list article.articleTags?split(",") as articleTag>
                    <span>
                        <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                            ${articleTag}</a><#if articleTag_has_next>,</#if>
                    </span>
                    </#list>&nbsp;&nbsp;&nbsp;
                    <#-- 注释掉填充用户名部分
                    ${author1Label}<a rel="nofollow" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
                    -->
                </div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="article-body">
            <div class="article-abstract">
                ${article.articleAbstract}
            </div>
        </div>
        <div class="article-footer">
            <div class="right">
                <span class="article-create-date left">
                    &nbsp;${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp
                </span>
                <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments" class="left">
                    <span class="left commentIcon" title="${commentLabel}"></span>
                    ${article.articleCommentCount}
                </a>
                <span class="left">&nbsp;&nbsp;</span>
                <a rel="nofollow" href="${servePath}${article.articlePermalink}" class="left">
                    <span class="left browserIcon" title="${viewLabel}"></span>
                    ${article.articleViewCount}
                </a>
            </div>
            <div class="clear"></div>
        </div>
    </div>
    </#list>
    <#if 0 != paginationPageCount>
    <div class="pagination">
        <#if 1 != paginationPageNums?first>
        <a href="${servePath}${path}/1">${firstPageLabel}</a>
        <a id="previousPage" href="${servePath}${path}/${paginationPreviousPageNum}">${previousPageLabel}</a>
        </#if>
        <#list paginationPageNums as paginationPageNum>
        <#if paginationPageNum == paginationCurrentPageNum>
        <a href="${servePath}${path}/${paginationPageNum}" class="selected">${paginationPageNum}</a>
        <#else>
        <a href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
        </#if>
        </#list>
        <#if paginationPageNums?last != paginationPageCount>
        <a id="nextPage" href="${servePath}${path}/${paginationNextPageNum}">${nextPagePabel}</a>
        <a href="${servePath}${path}/${paginationPageCount}">${lastPageLabel}</a>
        </#if>
        &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
    </div>
    </#if>
</div>