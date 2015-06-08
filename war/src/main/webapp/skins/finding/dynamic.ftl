<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${dynamicLabel}"/>
        <meta name="description" content="${metaDescription},${dynamicLabel}"/>
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
            <#if 0 != archiveDates?size>
            <ul>
                <li>
                    <h4>${archiveLabel}</h4>
                </li>
                <li>
                    <ul id="archiveSide">
                        <#list archiveDates as archiveDate>
                        <li data-year="${archiveDate.archiveDateYear}">
                            <#if "en" == localeString?substring(0, 2)>
                            <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                               title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                                ${archiveDate.monthName} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount})
                            <#else>
                            <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                               title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                                ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount})
                            </#if>
                        </li>
                        </#list>
                    </ul>
                </li>
            </ul>
            </#if>
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
                                <div class="article-body">${comment.commentContent}</div>
                            </div>
                        </div>
                        </#if>
                        </#list>
                    </div>
                    </#if>
                </div>
                <div class="side">
                    <div>
                        <form target="_blank" method="get" action="http://www.google.com/search">
                            <input placeholder="Search" id="search" type="text" name="q" /><span data-ico="&#x0067;"></span>
                            <input type="submit" name="btnG" value="" class="fn-none" />
                            <input type="hidden" name="oe" value="UTF-8" />
                            <input type="hidden" name="ie" value="UTF-8" />
                            <input type="hidden" name="newwindow" value="0" />
                            <input type="hidden" name="sitesearch" value="${serverHost}" />
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

                        <a rel="alternate" href="${servePath}/blog-articles-feed.do" class="user side-tile">
                            <span data-ico="&#xe135;"></span>
                            <div class="text">
                                ${atomLabel}
                            </div>
                        </a>

                        <div class="online-count side-tile">
                            <span data-ico="&#xe037;"></span>
                            <div class="text">
                                ${viewCount1Label}
                                ${statistic.statisticBlogViewCount}<br/>
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

            <div class="fn-clear">
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
                                    ${article.articleViewCount}
                                </span>
                            </li>
                            </#list>
                        </ul>
                    </div>
                </div>
                </#if>
            </div>

            <div class="fn-clear">
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
        <script>
            $("#comments .article-body").each(function () {
                this.innerHTML = Util.replaceEmString($(this).html());
            });

            if ($(".side").height() < $(".main").height()) {
                $(".main").height($(".side").height() - 5).css({
                    "overflow": "auto",
                    "margin-top": "5px"
                });

                $("#comments").css("margin-top", "0");
            }
        </script> 
    </body>
</html>
