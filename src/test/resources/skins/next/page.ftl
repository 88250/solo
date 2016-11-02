<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${page.pageTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${page.pageTitle}" />
        <meta name="description" content="${metaDescription}" />
        </@head>
    </head>
    <body>
        <#include "header.ftl">
        <main class="main wrapper">
            <div class="content">
                <article class="post-body">
                    ${page.pageContent}
                </article>
                <@comments commentList=pageComments article=page></@comments>
            </div>
            <#include "side.ftl">
        </main>
        <#include "footer.ftl">
        <@comment_script oId=page.oId></@comment_script>
    </body>
</html>
