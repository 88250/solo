<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<#list articles as article>
<h2 class="h2">
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
            <a class="sup" href="${servePath}${article.articlePermalink}">
                ${updatedLabel}
            </a>
        </sup>
    </#if>
</h2>
<div class="vditor-reset">${article.articleAbstract}</div>
<section class="meta">
    <p>
        ${author1Label}<a rel="nofollow" href="${servePath}/authors/${article.authorId}">${article.authorName}</a> |
        <#if  article.articleCreateDate?datetime != article.articleUpdateDate?datetime>
        ${updateDateLabel}:
        <#else>
        ${createDateLabel}:
        </#if> ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm")} |
        ${viewCount1Label}
        <a rel="nofollow" href="${servePath}${article.articlePermalink}">
            <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>
        </a> | ${commentCount1Label}
        <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments">
            <span class="left articles-commentIcon" title="${commentLabel}"></span>
            <span data-uvstatcmt="${article.oId}">0</span>
        </a>
    </p>
    <p>
        ${tags1Label}
        <#list article.articleTags?split(",") as articleTag>
        <span>
            <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
	            ${articleTag}
            </a><#if articleTag_has_next>,</#if>
        </span>
        </#list>
    </p>
</section>
</#list>
<#if 0 != paginationPageCount>
<div>
    <#if 1 != paginationPageNums?first>
    <a href="${servePath}${path}">${firstPageLabel}</a>
    <a id="previousPage" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <#if paginationPageNum == paginationCurrentPageNum>
    <a href="${servePath}${path}${pagingSep}${paginationPageNum}" class="selected">${paginationPageNum}</a>
    <#else>
    <a href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
    </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
    <a id="nextPage" href="${servePath}${path}${pagingSep}${paginationNextPageNum}">${nextPagePabel}</a>
    <a href="${servePath}${path}${pagingSep}${paginationPageCount}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>
