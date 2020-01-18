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
    <h2>
        <span class="left">
        <a rel="bookmark" class="article-title" href="${servePath}${article.articlePermalink}">
            ${article.articleTitle}
        </a>
        <#if article.articlePutTop>
            <sup class="tip">
                ${topArticleLabel}
            </sup>
        </#if>
        <#if article.hasUpdated>
            <sup>
                <a class="tip tip__sup" href="${servePath}${article.articlePermalink}">
                ${updatedLabel}
                </a>
            </sup>
        </#if>
        </span>
        <span class="expand-ico" onclick="getArticle(this, '${article.oId}');"></span>
        <span class="clear"></span>
    </h2>
    <div class="left article-element">
        <span class="date-ico" title="${dateLabel}">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            <#else>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            </#if>
        </span>
        <span class="user-ico" title="${authorLabel}">
            <a rel="nofollow" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
        </span>
    </div>
    <div class="right article-element">
        <#if commentable>
        <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments">
            <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span>&nbsp;&nbsp;${commentLabel}
        </a>&nbsp;&nbsp;
        </#if>
        <a rel="nofollow" href="${servePath}${article.articlePermalink}">
            <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>&nbsp;&nbsp;${viewLabel}
        </a>
    </div>
    <div class="clear"></div>
    <div class="vditor-reset">
        <div id="abstract${article.oId}">
            ${article.articleAbstract}
        </div>
        <div id="content${article.oId}" class="none"></div>
    </div>
    <div class="article-element">
        <span class="tag-ico" title="${tagsLabel}">
            <#list article.articleTags?split(",") as articleTag>
            <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
        </span>
    </div>
</div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if 1 != paginationPageNums?first>
    <a href="${servePath}${path}" title="${firstPageLabel}"><<</a>
    <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" title="${previousPageLabel}"><</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <#if paginationPageNum == paginationCurrentPageNum>
    <a href="${servePath}${path}${pagingSep}${paginationPageNum}" class="current">${paginationPageNum}</a>
    <#else>
    <a href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
    </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
    <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" title="${nextPagePabel}">></a>
    <a href="${servePath}${path}${pagingSep}${paginationPageCount}" title="${lastPageLabel}">>></a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>
