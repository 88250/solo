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
                                        <p>   
                                            ${comment.commentContent}
                                        </p>
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
                                            ${article.articleViewCount}
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
                                 src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" /></a>
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
        <script>
            $(".comments > li > div > p").each(function () {
                this.innerHTML = Util.replaceEmString($(this).html());
            });
        </script>
    </body>
</html>
