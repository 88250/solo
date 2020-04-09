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