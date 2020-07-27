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
                <span>
                    &nbsp; | &nbsp;
                    <a href="${servePath}${article.articlePermalink}#b3logsolocomments">
                       <span data-uvstatcmt="${article.oId}">0</span> ${cmtLabel}</a>
                </span>
                    &nbsp; | &nbsp;${viewsLabel} <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>°C
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
