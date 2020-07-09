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
<div class="article-list">
    <#list articles as article>
    <article class="item <#if article_index &lt; 3>item--active</#if>">
        <time class="vditor-tooltipped vditor-tooltipped__n item__date"
              aria-label="${article.articleUpdateDate?string("yyyy")}${yearLabel}">
            ${article.articleUpdateDate?string("MM")}${monthLabel}
            <span class="item__day">${article.articleUpdateDate?string("dd")}</span>
        </time>

        <h2 class="item__title">
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
                    ${updatedLabel}
                </sup>
            </#if>
        </h2>

        <div class="item__date--m fn__none">
            <i class="icon__date"></i>
            ${article.articleUpdateDate?string("yyyy-MM-dd")}
        </div>


        <div class="ft__center">
            <span class="tag">
                <i class="icon__tags"></i>
                <#list article.articleTags?split(",") as articleTag>
                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>,</#if>
                </#list>
            </span>
            <a class="tag" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                <i class="icon__comments"></i> <span data-uvstatcmt="${article.oId}">0</span> ${commentLabel}
            </a>
            <span class="tag">
                <i class="icon__views"></i>
                <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}
            </span>
        </div>

        <div class="vditor-reset">
            ${article.articleAbstract}
        </div>
    </article>
    </#list>


    <#if 0 != paginationPageCount>
        <div class="fn__clear">
            <nav class="pagination fn__right">
                <#if 1 != paginationPageNums?first>
                    <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" class="pagination__item">&laquo;</a>
                    <a class="pagination__item" href="${servePath}${path}">1</a>
                    <span class="pagination__item pagination__item--text">...</span>
                </#if>
                <#list paginationPageNums as paginationPageNum>
                    <#if paginationPageNum == paginationCurrentPageNum>
                    <span class="pagination__item pagination__item--current">${paginationPageNum}</span>
                    <#else>
                    <a class="pagination__item" href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
                    </#if>
                </#list>
                <#if paginationPageNums?last != paginationPageCount>
                    <span class="pagination__item pagination__item--text">...</span>
                    <a href="${servePath}${path}${pagingSep}${paginationPageCount}" class="pagination__item">${paginationPageCount}</a>
                    <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" class="pagination__item">&raquo;</a>
                </#if>
            </nav>
        </div>
    </#if>
</div>
