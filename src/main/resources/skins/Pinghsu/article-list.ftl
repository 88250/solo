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
