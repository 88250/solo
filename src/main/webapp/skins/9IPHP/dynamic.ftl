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
        <div class="wrapper">
            <div class="main-wrap">
                <main class="post">
                    <#if 0 != recentComments?size>
                        <ul class="comments" id="comments">
                            <#list recentComments as comment>
                                <li id="${comment.oId}">
                                    <div>
                                        <div class="avatar tooltipped tooltipped-n" aria-label="${comment.commentName}"
                                             style="background-image: url(${comment.commentThumbnailURL})"></div>
                                        <main>
                                            <div class="fn-clear">
                                                <#if "http://" == comment.commentURL>
                                                    ${comment.commentName}
                                                    <#else>
                                                        <a class="user-name" href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                                </#if>
                                                <time class="ft-gray">${comment.commentDate?string("yyyy-MM-dd HH:mm")}</time>
                                                <a class="reply-btn" href="${servePath}${comment.commentSharpURL}">${viewLabel}»</a>
                                            </div>
                                            <div class="content-reset">
                                                ${comment.commentContent}
                                            </div>
                                        </main>
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </#if>
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">

        <script>
            var $commentContents = $(".comments .content-reset");
            for (var i = 0; i < $commentContents.length; i++) {
                var str = $commentContents[i].innerHTML;
                $commentContents[i].innerHTML = Util.replaceEmString(str);
            }
        </script>
    </body>
</html>
