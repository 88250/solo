<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<#list articles as article>
<div class="article">
    <h2 class="article-title">
        <a rel="bookmark" class="no-underline" href="${servePath}${article.articlePermalink}">
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
    <div class="margin5">
        <div class="article-date left">
            <a rel="nofollow" class="left" title="${article.authorName}" href="${servePath}/authors/${article.authorId}">
                <span class="authorIcon"></span>
                ${article.authorName}
            </a>
            <span class="dateIcon"></span>
            <span class="left">
                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            </span>
        </div>
        <#if commentable>
        <div class="right">
            <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments" class="left">
                <span class="left articles-commentIcon" title="${commentLabel}"></span>
                <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span>
            </a>
        </div>
        </#if>
        <div class="clear"></div>
    </div>
    <div class="article-abstract vditor-reset">
        ${article.articleAbstract}
        <div class="clear"></div>
    </div>
    <div class="article-footer">
        <a rel="nofollow" href="${servePath}${article.articlePermalink}" class="left">
            <span class="left article-browserIcon" title="${viewLabel}"></span>
            <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>
        </a>
        <div class="left">
            <span class="tagsIcon" title="${tagLabel}"></span>
            <#list article.articleTags?split(",") as articleTag>
            <span>
                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>,</#if>
            </span>
            </#list>
        </div>
        <div class="clear"></div>
    </div>
</div>
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
</#if>
