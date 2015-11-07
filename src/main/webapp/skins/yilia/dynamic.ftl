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
        <#include "side.ftl">
        <main class="dynamic">
            <#if 0 != recentComments?size>
            <ul class="comments">
                <#list recentComments as comment>
                <#if comment_index < 6>
                <li>
                    <img class="avatar" title="${comment.commentName}"
                         alt="${comment.commentName}" src="${comment.commentThumbnailURL}">
                    <div class="content">
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
                        <div class="comment-content">
                            ${comment.commentContent}
                        </div>
                    </div>
                </li>
                </#if>
                </#list>
            </ul>
            </#if>


            <#if 0 != mostCommentArticles?size || 0 != mostViewCountArticles?size>

            <#if 0 != mostCommentArticles?size>
            <article>
                <header>
                    <h2>
                        ${mostCommentArticlesLabel}
                    </h2>
                </header>
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
            </article>
            </#if>
            <#if 0 != mostViewCountArticles?size>
            <article>
                <header>
                    <h2>
                        ${mostViewCountArticlesLabel}
                    </h2>
                </header>
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
            </article>
            </#if>
            </#if>

            <#include "footer.ftl">
        </main>

        <script>
            var $commentContents = $(".comments .comment-content");
            for (var i = 0; i < $commentContents.length; i++) {
                var str = $commentContents[i].innerHTML;
                $commentContents[i].innerHTML = Util.replaceEmString(str);
            }
        </script>
    </body>
</html>
