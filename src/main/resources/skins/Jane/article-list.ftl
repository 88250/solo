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
    <article class="article__item">
        <h2 class="article__title">
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
        <div class="ft__gray fn__clear">
            <time>
                ${article.articleUpdateDate?string("yyyy-MM-dd")}
            </time>
            &nbsp;
            <span class="mobile__none">
            <#list article.articleTags?split(",") as articleTag>
                <a rel="tag" class="ft__red" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>, </#if>
            </#list>
            </span>
            <div class="fn__right">
                <a class="ft__red" href="${servePath}${article.articlePermalink}#b3logsolocomments"><span data-uvstatcmt="${article.oId}">0</span> ${commentLabel}</a>
                •
                <a class="ft__red" href="${servePath}${article.articlePermalink}"><span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}</a>
            </div>
        </div>
        <div class="vditor-reset article__content">
            ${article.articleAbstract}
        </div>
        <a class="article__more" href="${servePath}${article.articlePermalink}">More...</a>
    </article>
</#list>


<#if 0 != paginationPageCount>
    <nav class="fn__flex pagination">
        <#if 1 != paginationPageNums?first>
            <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" class="pagination__item fn__flex-center">&laquo; Prev</a>
        </#if>

        <div class="fn__flex-1 ft__center">
        <#if 1 != paginationPageNums?first>
            <a class="pagination__item" href="${servePath}${path}">1</a>
            <span class="pagination__item">...</span>
        </#if>
        <#list paginationPageNums as paginationPageNum>
            <#if paginationPageNum == paginationCurrentPageNum>
            <span class="pagination__item pagination__item--current">${paginationPageNum}</span>
            <#else>
            <a class="pagination__item"
               href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
            </#if>
        </#list>
        <#if paginationPageNums?last != paginationPageCount>
            <span class="pagination__item">...</span>
            <a href="${servePath}${path}${pagingSep}${paginationPageCount}"
               class="pagination__item">${paginationPageCount}</a>
        </#if>
        </div>

        <#if paginationPageNums?last != paginationPageCount>
            <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" class="pagination__item fn__flex-center">Next &raquo;</a>
        </#if>
    </nav>
</#if>
