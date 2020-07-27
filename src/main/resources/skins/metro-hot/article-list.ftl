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
<div class="article-list fn-clear">
    <#list articles as article>
    <div>
        <img src="${article.articleImg1URL}" alt="${article.articleTitle}"/>
        <div class="article-abstract article-image">
            <div class="fn-clear">
                <div class="article-date" data-ico="&#xe200;">
                    ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                </div>
                <div class="fn-right">
                    <a rel="nofollow" data-ico="&#xe14e;" data-uvstatcmt="${article.oId}" href="${servePath}${article.articlePermalink}#b3logsolocomments">0</a>
                    <a rel="nofollow" data-ico="&#xe185;" href="${servePath}${article.articlePermalink}">
                        <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>
                    </a>
                    <a rel="nofollow" data-ico="&#x0060;" href="${servePath}/authors/${article.authorId}">
                        ${article.authorName}
                    </a>
                </div>
            </div>

            <h2 class="article-title">
                <#if article.articlePutTop>
                    <span>
                    [${topArticleLabel}]
                    </span>
                </#if>
                <#if article.hasUpdated>
                    <span>
                        [${updatedLabel}]
                    </span>
                </#if>
                <a rel="bookmark" title="${article.articleTitle}" href="${servePath}${article.articlePermalink}">
                    ${article.articleTitle}
                </a>
            </h2>
            <div class="vditor-reset">
                ${article.articleAbstractText}
            </div>
            <div data-ico="&#x003b;" title="${tagLabel}" class="article-tags">
                <#list article.articleTags?split(",") as articleTag>
                <a  rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>, </#if>
                </#list>
            </div>
        </div>
    </div>
    </#list>
</div>

<#if 0 != paginationPageCount>
<div class="pagination">
    <#if 1 != paginationPageNums?first>
    <a id="previousPage" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}"
       title="${previousPageLabel}"><</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <#if paginationPageNum == paginationCurrentPageNum>
    <span>${paginationPageNum}</span>
    <#else>
    <a href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
    </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
    <a id="nextPage" href="${servePath}${path}${pagingSep}${paginationNextPageNum}" title="${nextPagePabel}">></a>
    </#if>
    </#if>
</div>

