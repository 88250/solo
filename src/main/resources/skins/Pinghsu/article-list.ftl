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
<main class="article-list fn__clear">
    <#list articles as article>
        <article class="item">
            <div class="item__container">
                <div class="item__cover" style="background-image: url(${article.articleImg1URL})"></div>
                <div rel="bookmark" class="item__abstract">
                    <a pjax-title="${article.articleTitle}" href="${servePath}${article.articlePermalink}">
                        ${article.articleAbstractText}
                    </a>
                </div>
                <div class="item__slant"></div>
                <div class="item__slant item__slant--white"></div>
                <div class="item__main">
                    <span class="item__sup">
                        <#if article.articlePutTop>
                            <sup class="ft__red">
                            ${topArticleLabel}
                            </sup>
                        </#if>
                        <#if article.hasUpdated>
                            <sup class="ft__red">
                                ${updatedLabel}
                            </sup>
                        </#if>
                    </span>
                    <h2 class="item__title">
                        <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                            ${article.articleTitle}
                        </a>
                    </h2>
                    <#list article.articleTags?split(",") as articleTag>
                        <#if articleTag_index == 0>
                            <div class="ico ico--${article_index % 10}"></div>
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
                </div>
            </div>
        </article>
    </#list>
</main>
<#if 0 != paginationPageCount>
    <nav class="pagination">
        <#if 1 != paginationPageNums?first>
            <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" class="pagination__item">←</a>
            <a class="pagination__item" href="${servePath}${path}">1</a>
            <span class="pagination__item pagination__item--text">...</span>
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
            <span class="pagination__item pagination__item--text">...</span>
            <a href="${servePath}${path}${pagingSep}${paginationPageCount}"
               class="pagination__item">${paginationPageCount}</a>
            <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" class="pagination__item">→</a>
        </#if>
    </nav>
</#if>
