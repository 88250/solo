<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<div class="article-list">
    <#list articles as article>
    <article class="item <#if article_index &lt; 3>item--active</#if>">
        <time class="tooltipped tooltipped__n item__date"
              aria-label="${article.articleCreateDate?string("yyyy")}${yearLabel}">
            ${article.articleCreateDate?string("MM")}${monthLabel}
            <span class="item__day">${article.articleCreateDate?string("dd")}</span>
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
            ${article.articleCreateDate?string("yyy-MM-DD")}
        </div>


        <div class="ft__center">
            <span class="tag">
                <i class="icon__tags"></i>
                ${article.articleTags}
            </span>
            <a class="tag" href="${servePath}${article.articlePermalink}#comments">
                <i class="icon__comments"></i> ${article.articleCommentCount} ${commentLabel}
            </a>
            <span class="tag">
                <i class="icon__views"></i>
                ${article.articleViewCount} ${viewLabel}
            </span>
        </div>

        <div class="content-reset">
            ${article.articleAbstract}
        </div>
    </article>
    </#list>


    <#if 0 != paginationPageCount>
        <div class="fn__clear">
            <nav class="pagination fn__right">
                <#if 1 != paginationPageNums?first>
                    <a href="${servePath}${path}/${paginationPreviousPageNum}" class="pagination__item">&laquo;</a>
                    <a class="pagination__item" href="${servePath}${path}/1">1</a>
                    <span class="pagination__item pagination__item--text">...</span>
                </#if>
                <#list paginationPageNums as paginationPageNum>
                    <#if paginationPageNum == paginationCurrentPageNum>
                    <span class="pagination__item pagination__item--current">${paginationPageNum}</span>
                    <#else>
                    <a class="pagination__item" href="${servePath}${path}/${paginationPageNum}">${paginationPageNum}</a>
                    </#if>
                </#list>
                <#if paginationPageNums?last != paginationPageCount>
                    <span class="pagination__item pagination__item--text">...</span>
                    <a href="${servePath}${path}/${paginationPageCount}" class="pagination__item">${paginationPageCount}</a>
                    <a href="${servePath}${path}/${paginationNextPageNum}" class="pagination__item">&raquo;</a>
                </#if>
            </nav>
        </div>
    </#if>
</div>