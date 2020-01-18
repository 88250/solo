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
<section class="posts-expand">
    <#list articles as article>
        <article class="post-item">
            <header>
                <h2>
                    <a class="post-title-link" rel="bookmark" href="${servePath}${article.articlePermalink}">
                        ${article.articleTitle}
                    </a>
                    <#if article.articlePutTop>
                        <sup>
                            ${topArticleLabel}
                        </sup>
                    </#if>
                    <#if article.hasUpdated>
                        <sup>
                            <a class="post__sup" href="${servePath}${article.articlePermalink}">
                                ${updatedLabel}
                            </a>
                        </sup>
                    </#if>
                </h2>

                <div class="post-meta">
                <span>
                    <#if article.articleCreateDate?datetime != article.articleUpdateDate?datetime>
                        ${updateTimeLabel}
                    <#else>
                        ${postTimeLabel}
                    </#if>
                    <time>
                        ${article.articleUpdateDate?string("yyyy-MM-dd")}
                    </time>
                </span>
                <#if commentable>
                <span>
                    &nbsp; | &nbsp;
                    <a href="${servePath}${article.articlePermalink}#b3logsolocomments">
                       <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span> ${cmtLabel}</a>
                </span>
                </#if>
                    &nbsp; | &nbsp;${viewsLabel} <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>°C
                </div>
            </header>
            <div class="vditor-reset">
                ${article.articleAbstract}
            </div>
            <div class="post-more-link">
                <a href="${servePath}${article.articlePermalink}#more" rel="contents">
                    ${readLabel} &raquo;
                </a>
            </div>
        </article>
    </#list>
</section>

<#if 0 != paginationPageCount>
    <nav class="pagination">
        <#if 1 != paginationPageNums?first>
            <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" class="extend next"><<</a>
            <a class="page-number" href="${servePath}${path}">1</a> ...
        </#if>
        <#list paginationPageNums as paginationPageNum>
            <#if paginationPageNum == paginationCurrentPageNum>
                <span class="page-number current">${paginationPageNum}</span>
            <#else>
                <a class="page-number" href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
            </#if>
        </#list>
        <#if paginationPageNums?last != paginationPageCount> ...
            <a href="${servePath}${path}${pagingSep}${paginationPageCount}" class="page-number">${paginationPageCount}</a>
            <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" class="extend next">>></a>
        </#if>
    </nav>
</#if>
