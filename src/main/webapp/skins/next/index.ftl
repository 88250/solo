<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <#if metaKeywords??>
        <meta name="keywords" content="${metaKeywords}"/>
        </#if>
        <#if metaDescription??>
        <meta name="description" content="${metaDescription}"/>
        </#if>
        </@head>
    </head>
    <body>
        <div class="container one-column page-home">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">
                        <#include "article-list.ftl">
                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
        </div>
    </body>
</html>
