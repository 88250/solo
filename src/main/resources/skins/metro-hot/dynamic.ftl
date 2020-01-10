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
        <div class="wrapper">
            <div id="header">
                <#include "header.ftl" />
                <div class="article-header">
                    <h2>${blogSubtitle}</h2>
                </div>
            </div>

            <div class="fn-clear" id="dynamic">
                <div class="main">
                    <#if 0 != recentComments?size>
                    <div id="comments">
                        <#list recentComments as comment>
                        <#if comment_index < 6>
                        <div id="${comment.oId}" class="fn-clear">
                            <img title="${comment.commentName}"
                                 alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                            <div class="comment-main">
                                <div class="fn-clear comment-info">
                                    <#if "http://" == comment.commentURL>
                                    <span>${comment.commentName}</span>
                                    <#else>
                                    <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                    </#if>

                                    <a class="fn-right" rel="bookmark" data-ico="&#xe185;" href="${servePath}${comment.commentSharpURL}">
                                        ${viewLabel}
                                    </a>
                                    <div class="fn-right" data-ico="&#xe200;">
                                        ${comment.commentDate?string("yy-MM-dd HH:mm")}&nbsp; &nbsp;
                                    </div>
                                </div>
                                <div class="vditor-reset">${comment.commentContent}</div>
                            </div>
                        </div>
                        </#if>
                        </#list>
                    </div>
                    </#if>
                </div>
                <div class="side">
                    <div>
                        <form action="${servePath}/search">
                            <input placeholder="Search" id="search" type="text" name="keyword" /><span onclick="$(this).parent().submit()" data-ico="&#x0067;"></span>
                            <input type="submit" value="" class="fn-none" />
                        </form>

                        <#if "" != noticeBoard>
                        <div class="notice-board side-tile">
                            <span data-ico="&#xe1e9;"></span>
                            <div class="title">
                                ${noticeBoard}
                            </div>
                            <div class="text">
                                ${noticeBoardLabel}
                            </div>
                        </div>
                        </#if>

                        <a rel="alternate" href="${servePath}/rss.xml" class="user side-tile">
                            <span data-ico="&#xe135;"></span>
                            <div class="text">
                                RSS
                            </div>
                        </a>

                        <div class="online-count side-tile">
                            <span data-ico="&#xe037;"></span>
                            <div class="text">
                                ${viewCount1Label}
                                <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span><br/>
                                ${articleCount1Label}
                                ${statistic.statisticPublishedBlogArticleCount}<br/>
                                ${commentCount1Label}
                                ${statistic.statisticPublishedBlogCommentCount}<br/>
                            </div>
                        </div>

                        <#include "copyright.ftl">
                    </div>
                </div>
            </div>

            <div class="fn__flex">
                <#if 0 != mostCommentArticles?size>
                <div class="side-tile most-comment fn-clear">
                    <div class="fn-left">
                        <span data-ico="&#xe14e;"></span>
                        <div class="title">
                            ${mostCommentArticlesLabel}
                        </div>
                    </div>
                    <div class="text fn-right">
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
                <div class="side-tile most-view fn-clear">
                    <div class="fn-left">
                        <span data-ico="&#xe185;"></span>
                        <div class="title">
                            ${mostViewCountArticlesLabel}
                        </div>
                    </div>
                    <div class="text fn-right">
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
                </div>
                </#if>
            </div>

            <div class="fn__flex">
                <#if 0 != links?size>
                <div class="side-tile links-tile fn-clear">
                    <div class="fn-left">
                        <span data-ico="&#xe14a;"></span>
                        <div class="title">
                            ${linkLabel}
                        </div>
                    </div>
                    <div class="text fn-right">
                        <#list links as link>
                        <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                            ${link.linkTitle}
                        </a>
                        </#list>
                    </div>
                </div>
                </#if>

                <#if 0 != mostUsedTags?size>
                <div class="side-tile tags-tile fn-clear">
                    <div class="fn-left">
                        <span data-ico="&#x003b;"></span>
                        <div class="title">
                            ${popTagsLabel}
                        </div>
                    </div>
                    <div class="text fn-right">
                        <#list mostUsedTags as tag>
                        <a rel="tag" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}"
                           title="${tag.tagTitle}(${tag.tagPublishedRefCount})">
                            ${tag.tagTitle}
                        </a>
                        </#list>
                    </div>
                </div>
                </#if>
            </div>
        </div>
        <span id="goTop" onclick="Util.goTop()" data-ico="&#xe042;" class="side-tile"></span>
        <#include "footer.ftl"/>
    </body>
</html>
