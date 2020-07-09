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
    <article>
        <header class="fn__flex">
            <h2 class="fn__flex-1">
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
            <time><span class="icon-date"></span> ${article.articleUpdateDate?string("yyyy-MM-dd")}</time>
        </header>
        <#if article.articleAbstractText == ''>
            <a class="abstract" href="${servePath}${article.articlePermalink}">
               <img src="${article.articleImg1URL}" alt="${blogTitle}"/>
            </a>
        <#else>
            <a class="abstract vditor-reset" href="${servePath}${article.articlePermalink}">
                ${article.articleAbstractText}
            </a>
        </#if>
        <footer class="article__footer fn__flex">
            <span class="icon-tag fn__flex-center"></span>
            <span>&nbsp;&nbsp;&nbsp;</span>
            <div class="tags fn__flex-1 fn__flex-center">
                <#list article.articleTags?split(",") as articleTag>
                    <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                        ${articleTag}</a>
                </#list>
            </div>
            <span>&nbsp;&nbsp;&nbsp;</span>
                <a href="${servePath}${article.articlePermalink}#b3logsolocomments"
                   class="vditor-tooltipped__n vditor-tooltipped link fn__flex-center"
                   aria-label="${commentLabel}">
                    <span data-uvstatcmt="${article.oId}">0</span>
                    <span class="icon-chat"></span>
                </a>
            <a class="vditor-tooltipped__n vditor-tooltipped link fn__flex-center"
               href="${servePath}${article.articlePermalink}"
               aria-label="${viewLabel}">
                <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>
                <span class="icon-views"></span>
            </a>
            <a rel="nofollow" href="${servePath}/authors/${article.authorId}" class="fn__flex-center">
                <img class="avatar" title="${article.authorName}" alt="${article.authorName}"
                     src="${article.authorThumbnailURL}"/>
            </a>
        </footer>
    </article>
</#list>

<#if 0 != paginationPageCount>
    <nav class="pagination">
        <#if 1 != paginationPageNums?first>
            <a href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}" class="extend">${previousPageLabel}</a>
            <a class="page-num" href="${servePath}${path}">1</a> ...
        </#if>
        <#list paginationPageNums as paginationPageNum>
            <#if paginationPageNum == paginationCurrentPageNum>
                <span class="current page-num">${paginationPageNum}</span>
            <#else>
                <a class="page-num" href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
            </#if>
        </#list>
        <#if paginationPageNums?last != paginationPageCount> ...
            <a href="${servePath}${path}${pagingSep}${paginationPageCount}" class="page-num">${paginationPageCount}</a>
            <a href="${servePath}${path}${pagingSep}${paginationNextPageNum}" class="extend">${nextPagePabel}</a>
        </#if>
    </nav>
</#if>
