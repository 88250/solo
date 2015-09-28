<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${tag.tagTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${tag.tagTitle}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body class="nav-closed">
        <div class="nav">
            <#include "side.ftl">
        </div>
        <div class="site-wrapper">
            <#include "header.ftl">
            <main id="content">
                <h2 class="fn-wrap">
                    ${tag1Label}
                    <a rel="alternate" href="${servePath}/tag-articles-feed.do?oId=${tag.oId}">
                        ${tag.tagTitle}
                        (${tag.tagPublishedRefCount})
                    </a>
                </h2>
                <#include "article-list.ftl">
            </main>
            <#include "footer.ftl">
        </div>
    </body>
</html>
