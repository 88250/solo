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
<#include "../../common-template/macro-common_head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${dynamicLabel} - ${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="wrapper">
            <div class="container">
                <div class="fn-clear">
                    <div class="dynamic-l">
                        <#if "" != noticeBoard>
                        <div class="module">
                            ${noticeBoard}
                        </div>
                        </#if>
                        <#if 0 != recentComments?size>
                        <div class="module">
                            <h3 class="title">${recentCommentsLabel}</h3>
                            <ul class="comments list">
                                <#list recentComments as comment>
                                <li>
                                    <img
                                        alt='${comment.commentName}'
                                        src='${comment.commentThumbnailURL}'/>
                                    <div>
                                        <span class="author">
                                            <#if "http://" == comment.commentURL>
                                            ${comment.commentName}
                                            <#else>
                                            <a target="_blank" href="${comment.commentURL}">${comment.commentName}</a>
                                            </#if>
                                        </span>
                                        <small><b>${comment.commentDate?string("yyyy-MM-dd HH:mm")}</b></small>
                                        <span class="ico ico-view right">
                                            <a rel="nofollow" href="${servePath}${comment.commentSharpURL}">
                                                ${viewLabel}
                                            </a>
                                        </span>
                                        <div class="vditor-reset">
                                            ${comment.commentContent}
                                        </div>
                                    </div>
                                </li>
                                </#list>
                            </ul>
                        </div>
                        </#if>
                    </div>
                    <div class="dynamic-r">
                        <#if 0 != mostCommentArticles?size>
                        <div class="module">
                            <h3 class="title">${mostCommentArticlesLabel}</h3>
                            <ul class="list">
                                <#list mostCommentArticles as article>
                                <li class="fn-clear">
                                    <a class="left" rel="nofollow" title="${article.articleTitle}"
                                       href="${servePath}${article.articlePermalink}">
                                        ${article.articleTitle}
                                    </a>
                                    <span class="ico ico-comment right" title="${commentLabel}">
                                        <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                                            <#if article.articleCommentCount == 0>
                                            ${noCommentLabel}
                                            <#else>
                                            ${article.articleCommentCount}
                                            </#if>
                                        </a>
                                    </span>
                                </li>
                                </#list>
                            </ul>
                        </div>
                        </#if>
                        <#if 0 != mostViewCountArticles?size>
                        <div class="module">
                            <h3 class="title">${mostViewCountArticlesLabel}</h3>
                            <ul class="list">
                                <#list mostViewCountArticles as article>
                                <li class="fn-clear">
                                    <a rel="nofollow" class="left" title="${article.articleTitle}" href="${servePath}${article.articlePermalink}">
                                        ${article.articleTitle}
                                    </a>
                                    <span class="ico ico-view right" title="${viewLabel}">
                                        <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                                            <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>
                                        </a>
                                    </span>
                                </li>
                                </#list>
                            </ul>
                        </div>
                        </#if>
                        <#if 0 != mostUsedTags?size>
                        <div class="module tags">
                            <h3 class="title">${popTagsLabel}</h3>
                            <#list mostUsedTags as tag>
                            <a rel="tag" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}"
                               title="${tag.tagTitle}(${tag.tagPublishedRefCount})">
                                ${tag.tagTitle}
                            </a>&nbsp; &nbsp;
                            </#list>
                            </ul>
                        </div>
                        </#if>
                    </div>
                </div>
                <#if 0 != links?size>
                <div class="module links">
                    <h3 class="title">${linkLabel}</h3>
                    <#list links as link>
                    <span>
                        <a rel="friend" href="${link.linkAddress}" alt="${link.linkTitle}" target="_blank">
                            <img alt="${link.linkTitle}"
                                 src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" width="16" height="16" /></a>
                        <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                            ${link.linkTitle}
                        </a>
                    </span> &nbsp; &nbsp;
                    </#list>
                </div>
                </#if>
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
