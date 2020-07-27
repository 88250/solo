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
    <div class="marginBottom40">
        <div class="article-header">
            <div class="article-date">
                ${article.articleUpdateDate?string("yyyy-MM-dd")}
            </div>
            <div class="arrow-right"></div>
            <div class="clear"></div>
            <ul>
                <li>
                <span class="left">
                    by&nbsp;
                </span>
                    <a rel="nofollow" class="left" title="${article.authorName}"
                       href="${servePath}/authors/${article.authorId}">
                        ${article.authorName}
                    </a>
                    <span class="clear"></span>
                </li>
                <li>
                    <a rel="nofollow" href="${servePath}${article.articlePermalink}" title="${viewLabel}">
                        ${viewLabel} (<span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>)
                    </a>
                </li>
                <li>
                    <a rel="nofollow" title="${commentLabel}" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                        ${commentLabel} (<span data-uvstatcmt="${article.oId}">0</span>)
                    </a>
                </li>
            </ul>
        </div>
        <div class="article-main">
            <h2 class="title">
                <a rel="bookmark" class="no-underline" href="${servePath}${article.articlePermalink}">
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
            <div class="vditor-reset">
                ${article.articleAbstract}
            </div>
            <div class="read-more">
                <a href="${servePath}${article.articlePermalink}">
                    <span class="left">${readmore2Label}</span>
                    <span class="read-more-icon"></span>
                    <span class="clear"></span>
                </a>
                <div class="clear"></div>
            </div>
        </div>
        <div class="article-footer">
            <h3>${tagsLabel}</h3>
            <ul>
                <#list article.articleTags?split(",") as articleTag>
                    <li>
                        <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                            ${articleTag}
                        </a>
                    </li>
                </#list>
                <li>
                    <a href="${servePath}${article.articlePermalink}">
                        <#if  article.articleCreateDate?datetime != article.articleUpdateDate?datetime>
                            ${updateDateLabel}
                        <#else>
                            ${createDateLabel}
                        </#if>:${article.articleUpdateDate?string("yyyy-MM-dd HH:mm")}
                    </a>
                </li>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
</#list>
<#if 0 != paginationPageCount>
    <div class="pagination">
        <#if 1 != paginationPageNums?first>
            <a href="${servePath}${path}">${firstPageLabel}</a>
            <a id="previousPage" href="${servePath}${path}${pagingSep}${paginationPreviousPageNum}">${previousPageLabel}</a>
        </#if>
        <#list paginationPageNums as paginationPageNum>
            <#if paginationPageNum == paginationCurrentPageNum>
                <a href="${servePath}${path}${pagingSep}${paginationPageNum}" class="selected">${paginationPageNum}</a>
            <#else>
                <a href="${servePath}${path}${pagingSep}${paginationPageNum}">${paginationPageNum}</a>
            </#if>
        </#list>
        <#if paginationPageNums?last != paginationPageCount>
            <a id="nextPage" href="${servePath}${path}${pagingSep}${paginationNextPageNum}">${nextPagePabel}</a>
            <a href="${servePath}${path}${pagingSep}${paginationPageCount}">${lastPageLabel}</a>
        </#if>
        &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
    </div>
</#if>
