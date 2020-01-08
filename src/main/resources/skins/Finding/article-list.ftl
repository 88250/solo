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
<#if !isIndex && paginationCurrentPageNum != 1>
<nav class="pagination fn-clear fn-wrap" role="navigation">
    <#if paginationCurrentPageNum != 1>
    <a class="fn-left" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}">← ${previousPageLabel}</a>
    </#if>
    <span>${pageLabel} ${paginationCurrentPageNum} of ${paginationPageCount}</span>
    <#if paginationPageCount != paginationCurrentPageNum>
    <a class="fn-right" href="${servePath}${path}${pagingSep}${paginationNextPageNum}">${nextPagePabel} →</a>
    </#if>
</nav>
</#if>

<#list articles as article>
<article class="post fn-wrap">
    <header>
        <h2 class="post-title">
            <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                ${article.articleTitle}
            </a>
            <#if article.articlePutTop>
                <sup class="post-tip">
                    ${topArticleLabel}
                </sup>
            </#if>
            <#if article.hasUpdated>
                <sup>
                    <a class="post-tip" href="${servePath}${article.articlePermalink}">
                        ${updatedLabel}
                    </a>
                </sup>
            </#if>
        </h2>
    </header>
    <section class="post-excerpt post-content fn-clear">
        <p>${article.articleAbstract}</p>
    </section>
    <footer class="post-meta">
        <img class="avatar" title="${article.authorName}" alt="${article.authorName}" src="${article.authorThumbnailURL}"/>
        <a rel="nofollow" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
        on
        <#list article.articleTags?split(",") as articleTag>
        <span>
            <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>,</#if>
        </span>
        </#list>
        <time>${article.articleUpdateDate?string("yyyy-MM-dd")}</time>
    </footer>
</article>
</#list>

<#if 0 != paginationPageCount>
<nav class="pagination fn-clear fn-wrap" role="navigation">
    <#if paginationCurrentPageNum != 1>
    <a class="fn-left" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}">← ${previousPageLabel}</a>
    </#if>
    <span>${pageLabel} ${paginationCurrentPageNum} of ${paginationPageCount}</span>
    <#if paginationPageCount != paginationCurrentPageNum>
    <a class="fn-right" href="${servePath}${path}${pagingSep}${paginationNextPageNum}">${nextPagePabel} →</a>
    </#if>
</nav>
</#if>