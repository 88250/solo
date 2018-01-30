<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${page.pageTitle} - ${blogTitle}">
    <meta name="keywords" content="${metaKeywords},${page.pageTitle}"/>
    <meta name="description" content="${metaDescription}"/>
</@head>
</head>
<body>
<#include "header.ftl">
<#include "nav.ftl">
<div class="main">
<#if noticeBoard??>
    <div class="board">
    ${noticeBoard}
    </div>
</#if>
    <div class="wrapper content">
        <article class="post">
            <div class="content-reset">
            ${page.pageContent}
            </div>
        <@comments commentList=pageComments article=page></@comments>
        </article>
    </div>
<#include "bottom.ftl">
</div>
<#include "footer.ftl">
<@comment_script oId=page.oId></@comment_script>
</body>
</html>
