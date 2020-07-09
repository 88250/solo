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
<div class="articles">
<#list articles as article>
    <article class="item<#if article_index % 6 ==0> item--large</#if>">
        <a href="${servePath}${article.articlePermalink}" class="item__cover"
           style="background-image: url(${article.articleImg1URL})"> ${article.articleTitle}
        </a>
        <div class="item__main">
            <#list article.articleTags?split(",") as articleTag>
                <#if articleTag_index == 0>
                    <#if article.category??>
                    <a class="item__tag"
                       href="${servePath}/category/${article.category.categoryURI}">${article.category.categoryTitle}</a>
                    <#else>
                    <a rel="tag" class="item__tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                        ${articleTag}
                    </a>
                    </#if>
                </#if>
            </#list>
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
                        <a href="${servePath}${article.articlePermalink}">
                            ${updatedLabel}
                        </a>
                    </sup>
                </#if>
            </h2>
            <a class="item__abstract" pjax-title="${article.articleTitle}"
               href="${servePath}${article.articlePermalink}">
                <#if article.articleAbstractText?length gt 80>
                    ${article.articleAbstractText[0..80]}
                <#else>
                    ${article.articleAbstractText}
                </#if>

            </a>
            <div class="fn__clear">
                <a href="${servePath}/authors/${article.authorId}"
                   aria-label="${article.authorName}"
                   class="vditor-tooltipped vditor-tooltipped__n item__avatar">
                    <img src="${article.authorThumbnailURL}" alt="${article.authorId}"/>
                </a>
                <a class="item__meta fn__none" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                    <span data-uvstatcmt="${article.oId}">0</span> ${commentLabel}
                </a>
                <a class="item__meta" href="${servePath}${article.articlePermalink}">
                    <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}
                </a>
            </div>
        </div>
    </article>
</#list>
</div>

<#if 0 != paginationPageCount>
<nav class="pagination">
    <#if 1 != paginationPageNums?first>
        <a pjax-title="${blogTitle}" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}"
           aria-label="${previousPageLabel}"
           class="pagination__item vditor-tooltipped__n vditor-tooltipped">&laquo;</a>
        <a pjax-title="${blogTitle}" class="pagination__item" href="${servePath}${path}">1</a>
        <span class="pagination__item pagination__item--omit">...</span>
    </#if>
    <#list paginationPageNums as paginationPageNum>
        <#if paginationPageNum == paginationCurrentPageNum>
            <span class="pagination__item pagination__item--active">${paginationPageNum}</span>
        <#else>
            <a pjax-title="${blogTitle}" class="pagination__item" href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
        </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
        <span class="pagination__item pagination__item--omit">...</span>
        <a pjax-title="${blogTitle}" href="${servePath}${path}${pagingSep}${paginationPageCount}" class="pagination__item">${paginationPageCount}</a>
        <a pjax-title="${blogTitle}" href="${servePath}${path}${pagingSep}${paginationNextPageNum}" aria-label="${nextPagePabel}"
           class="pagination__item vditor-tooltipped__n vditor-tooltipped">&raquo;</a>
    </#if>
</nav>
</#if>
