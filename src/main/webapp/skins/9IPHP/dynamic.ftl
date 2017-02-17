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
        <#include "header.ftl">
        <main class="main wrapper">
            <div class="content">
                <#if 0 != recentComments?size>
                <ul class="comments" id="comments">
                    <#list recentComments as comment>
                    <li class="fn-clear">
                        <img class="avatar-48" title="${comment.commentName}" src="${comment.commentThumbnailURL}">
                        <div class="comment-body">
                            <div class="fn-clear comment-meta">
                                <span class="fn-left">
                                    <#if "http://" == comment.commentURL>
                                    <span>${comment.commentName}</span>
                                    <#else>
                                    <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                    </#if>
                                    <time>${comment.commentDate?string("yyyy-MM-dd HH:mm")}</time> 
                                </span>
                                <a class="fn-right" href="${servePath}${comment.commentSharpURL}">${viewLabel}»</a>
                            </div>
                            <div class="comment-content post-body article-body">
                                ${comment.commentContent}
                            </div>
                        </div>
                    </li>
                    </#list>
                </ul>
                </#if>
            </div>
            <#include "side.ftl">
        </main>
        <#include "footer.ftl">

        <script>
            var $commentContents = $(".comments .comment-content");
            for (var i = 0; i < $commentContents.length; i++) {
                var str = $commentContents[i].innerHTML;
                $commentContents[i].innerHTML = Util.replaceEmString(str);
            }
        </script>
    </body>
</html>
