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
<dl>
    <#list articles as article>
    <dd class="article">
        <div class="date">
            <div class="month">${article.articleUpdateDate?string("MM")}</div>
            <div class="day">${article.articleUpdateDate?string("dd")}</div>
        </div>
        <div class="left">
            <h2>
                <a rel="bookmark" href="${servePath}${article.articlePermalink}" title="${tags1Label}${article.articleTags}">
                    ${article.articleTitle}
                </a>
                <#if article.articlePutTop>
                    <sup>
                        ${topArticleLabel}
                    </sup>
                </#if>
                <#if article.hasUpdated>
                    <sup>
                        <a href="${servePath}${article.articlePermalink}">
                            ${updatedLabel}
                        </a>
                    </sup>
                </#if>
            </h2>
            <div class="article-date">
                ${article.articleUpdateDate?string("yyyy HH:mm:ss")}
                by
                <a rel="nofollow" class="underline" title="${article.authorName}" href="${servePath}/authors/${article.authorId}">
                    ${article.authorName}</a>
                |
                <a rel="nofollow" class="underline" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                    <span data-uvstatcmt="${article.oId}">0</span> ${commentLabel}
                </a>
            </div>
        </div>
        <div class="clear"></div>
        <div class="article-abstract vditor-reset">
            ${article.articleAbstract}
            <div class="clear"></div>
            <a class="right underline" href="${servePath}${article.articlePermalink}">
                ${readmore2Label}...
            </a>
            <span class="clear"></span>
        </div>
    </dd>
    </#list>
</dl>
<#if 0 != paginationPageCount>
<div class="pagination right">
    <#if 1 != paginationPageNums?first>
    <a href="${servePath}${path}" title="${firstPageLabel}"><<</a>
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
    <a title="${lastPageLabel}" href="${servePath}${path}${pagingSep}${paginationPageCount}">>></a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
<div class="clear"></div>
</#if>
