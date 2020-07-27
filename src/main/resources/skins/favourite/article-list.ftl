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
<div class="article">
    <h2 class="article-title">
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
    <div class="posttime-blue">
        <div class="posttime-MY">
            ${article.articleUpdateDate?string("yyyy-MM")}
        </div>
        <div class="posttime-D">
            ${article.articleUpdateDate?string("dd")}
        </div>
    </div>
    <div class="article-abstract">
        <div class="note">
            <div class="corner"></div>
            <div class="substance vditor-reset">
                ${article.articleAbstract}
                <div class="clear"></div>
            </div>
        </div>
    </div>
    <div class="margin25">
        <a rel="nofollow" href="${servePath}${article.articlePermalink}" class="left">
            <span class="left article-browserIcon" title="${viewLabel}"></span>
            <span class="count"><span data-uvstaturl="${servePath}${article.articlePermalink}">0</span></span>
        </a>
        <div class="left">
            <span class="tagsIcon" title="${tagLabel}"></span>
            <#list article.articleTags?split(",") as articleTag>
            <span class="count">
                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>,</#if>
            </span>
            </#list>
        </div>
        <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments" class="left">
            <span class="left articles-commentIcon" title="${commentLabel}"></span>
            <span class="count" data-uvstatcmt="${article.oId}">0</span>
        </a>
        <div class="right more">
            <a href="${servePath}${article.articlePermalink}" class="right">
                ${readmoreLabel}
            </a>
        </div>
        <div class="clear"></div>
    </div>
    <div class="footer">
        <div class="clear"></div>
    </div>
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
