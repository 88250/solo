<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${authorName} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${authorName}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        <#include "side.ftl">
        <main>
            <h2 class="classify-name">
                ${author1Label}${authorName}
            </h2>
            <#include "article-list.ftl">
            <#include "footer.ftl">
        </main>
    </body>
</html>
