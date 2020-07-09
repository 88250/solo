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
<div class="article">
    <div class="article-header">
        <h2>
            <a rel="bookmark" class="no-underline" href="${servePath}${article.articlePermalink}">
                ${article.articleTitle}
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
            </a>
        </h2>
    </div>
    <div class="left article-info">
        <div class="article-date">
            ${article.articleUpdateDate?string("yyyy-MM-dd")}<br>${article.articleUpdateDate?string("HH:mm:ss")}
        </div>
        <div class="article-comment">
            <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                ${commentLabel}(<span data-uvstatcmt="${article.oId}">0</span>)
            </a>
        </div>
    </div>
    <div class="right article-main">
        <#list article.articleTags?split(",") as articleTag>
        <a rel="tag" class="article-tags" href="${servePath}/tags/${articleTag?url('UTF-8')}">
            ${articleTag}</a>
        </#list>
        <div class="clear"></div>
        <div class="article-abstract">
            ${article.articleAbstract}
        </div>
    </div>
    <div class="clear"></div>
</div>
<#if article_has_next>
<div class="line right"></div>
</#if>
<div class="clear"></div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
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
<#else>
&nbsp;
</#if>
