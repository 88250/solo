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
    <body class="nav-closed">
        <div class="nav">
            <#include "side.ftl">
        </div>
        <div class="site-wrapper">
            <#include "header.ftl">
            <main id="content">
                <#if 0 != recentComments?size>
                <ul class="comments fn-wrap">
                    <#list recentComments as comment>
                    <#if comment_index < 6>
                    <li id="${comment.oId}" class="fn-clear">
                        <div class="fn-left avatar-warp">
                            <img class="avatar-48" title="${comment.commentName}"
                                 alt="${comment.commentName}" src="${comment.commentThumbnailURL}">
                        </div>
                        <div class="fn-left" style="width: 90%">
                            <div class="fn-clear post-meta">
                                <span class="fn-left">
                                    <#if "http://" == comment.commentURL>
                                    <span>${comment.commentName}</span>
                                    <#else>
                                    <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                    </#if>
                                    <time>${comment.commentDate?string("yy-MM-dd HH")}</time>
                                </span>
                                <a class="fn-right" href="${servePath}${comment.commentSharpURL}">${viewLabel}»</a>
                            </div>
                            <div class="comment-content post-content vditor-reset">
                                ${comment.commentContent}
                            </div>
                        </div>
                    </li>
                    </#if>
                    </#list>
                </ul>
                </#if>
            </main>

            <#if 0 != mostCommentArticles?size || 0 != mostViewCountArticles?size>
            <aside class="read-next dynamic">
                <#if 0 != mostCommentArticles?size>
                <div class="read-next-story" style="background-image: url('${staticServePath}/skins/${skinDirName}/images/next.jpg')">
                    <div>
                        ${mostCommentArticlesLabel}
                        <ul>
                            <#list mostCommentArticles as article>
                            <li>
                                <a href="${servePath}${article.articlePermalink}" title="${article.articleTitle}" rel="nofollow">
                                    ${article.articleTitle}
                                </a>
                                <span data-ico="&#xe14e;">
                                    ${article.articleCommentCount}
                                </span>
                            </li>
                            </#list>
                        </ul>
                    </div>
                </div>
                </#if>
                <#if 0 != mostViewCountArticles?size>
                <section class="read-next-story prev" style="background-image: url('${staticServePath}/skins/${skinDirName}/images/preview.jpg')">
                    <div>
                        ${mostViewCountArticlesLabel}
                        <ul>
                            <#list mostViewCountArticles as article>
                            <li>
                                <a href="${servePath}${article.articlePermalink}" title="${article.articleTitle}" rel="nofollow">
                                    ${article.articleTitle}
                                </a>
                                <span data-ico="&#xe185;">
                                    <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>
                                </span>
                            </li>
                            </#list>
                        </ul>
                    </div>
                </section>
                </#if>
            </aside>
            </#if>
            <#include "footer.ftl">
        </div>
    </body>
</html>
