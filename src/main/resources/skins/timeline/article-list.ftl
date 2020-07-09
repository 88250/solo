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
<div class="wrapper">
    <div class="articles container">
        <div class="vertical"></div>
        <#list articles as article>
        <article>
            <div class="module">
                <div class="dot"></div>
                <div class="arrow"></div>
                <time class="article-time">
                    <span>
                        ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                    </span>
                </time>
                <h3 class="article-title">
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
                </h3>
                <div class="vditor-reset">
                    ${article.articleAbstract}
                </div>
                <span class="ico-tags ico" title="${tagLabel}">
                    <#list article.articleTags?split(",") as articleTag><a rel="category tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
                </span>
                <span class="ico-author ico" title="${authorLabel}">
                    <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
                </span>
                <span class="ico-comment ico" title="${commentLabel}">
                    <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                        <span data-uvstatcmt="${article.oId}">0</span>
                    </a>
                </span>
                <span class="ico-view ico" title="${viewLabel}">
                    <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                        <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>
                    </a>
                </span>
            </div>
        </article>
        </#list>
        <#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount>
        <div class="article-more" onclick="timeline.getNextPage(this)" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
        </#if>
    </div>
</div>
