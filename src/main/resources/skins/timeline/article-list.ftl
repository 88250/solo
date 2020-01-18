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
                        <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span>
                    </a>
                </span>
                <span class="ico-view ico" title="${viewLabel}">
                    <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                        <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>
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
