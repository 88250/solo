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
<div>
    <#list articles as article>
    <article class="post">
        <header>
            <h2>
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
                        <a href="${servePath}${article.articlePermalink}">
                            ${updatedLabel}
                        </a>
                    </sup>
                </#if>
            </h2>

            <div class="meta">
                <span class="vditor-tooltipped vditor-tooltipped__n"
                      aria-label="<#if article.articleCreateDate?datetime != article.articleUpdateDate?datetime>${updateDateLabel}<#else>${createDateLabel}</#if>">
                    <i class="icon-date"></i>
                    <time>
                        ${article.articleUpdateDate?string("yyyy-MM-dd")}
                    </time>
                </span>
                <#if commentable>
                &nbsp; | &nbsp;
                <span class="vditor-tooltipped vditor-tooltipped__n" aria-label="${commentCountLabel}">
                    <i class="icon-comments"></i>
                    <a href="${servePath}${article.articlePermalink}#b3logsolocomments">
                        <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span> ${commentLabel}</a>
                </span>
                </#if>
                &nbsp; | &nbsp;
                <span class="vditor-tooltipped vditor-tooltipped__n" aria-label="${viewCountLabel}">
                    <i class="icon-views"></i>
                    <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span> ${viewLabel}
                </span>
            </div>
        </header>
        <div class="vditor-reset">
            ${article.articleAbstract}
        </div>
        <footer class="fn-clear tags">
            <#list article.articleTags?split(",") as articleTag>
                <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a>
            </#list>
            <a href="${servePath}${article.articlePermalink}#more" rel="contents" class="fn-right">
                ${readLabel} &raquo;
            </a>
        </footer>
    </article>
    </#list>


    <#if 0 != paginationPageCount>
        <div class="fn-clear">
            <nav class="pagination fn-right">
                <#if 1 != paginationPageNums?first>
                <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" class="page-number">&laquo;</a>
                    <a class="page-number" href="${servePath}${path}">1</a> <span class="page-number">...</span>
                </#if>
                <#list paginationPageNums as paginationPageNum>
                <#if paginationPageNum == paginationCurrentPageNum>
                <span class="page-number current">${paginationPageNum}</span>
                <#else>
                <a class="page-number" href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
                </#if>
                </#list>
                <#if paginationPageNums?last != paginationPageCount> <span class="page-number">...</span>
                <a href="${servePath}${path}${pagingSep}${paginationPageCount}" class="page-number">${paginationPageCount}</a>
                <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" class="page-number">&raquo;</a>
                </#if>
            </nav>
        </div>
    </#if>
</div>
