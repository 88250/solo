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
        <div class="container one-column page-home">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">
                        <#if 0 != recentComments?size>
                        <ul class="comments" id="comments">
                            <#list recentComments as comment>
                            <#if comment_index < 6>
                            <li class="fn-clear">
                                <div class="fn-left avatar-warp">
                                    <img class="avatar-48" title="${comment.commentName}" src="${comment.commentThumbnailURL}">
                                </div>
                                <div class="fn-left" style="width: 90%">
                                    <div class="fn-clear post-meta">
                                        <span class="fn-left">
                                            <#if "http://" == comment.commentURL>
                                             <span>${comment.commentName}</span>
                                            <#else>
                                            <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                            </#if>
                                            <time>${comment.commentDate?string("yyyy-MM-dd HH:mm")}</time> 
                                        </span>
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
                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
        </div>

        <script>
            var $commentContents = $(".comments .comment-content");
            for (var i = 0; i < $commentContents.length; i++) {
                var str = $commentContents[i].innerHTML;
                $commentContents[i].innerHTML = Util.replaceEmString(str);
            }
        </script>
    </body>
</html>
