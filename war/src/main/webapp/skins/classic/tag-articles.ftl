<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${tag.tagTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${tag.tagTitle}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <div class="content">
            <div class="header">
                <#include "header.ftl">
            </div>
            <div class="body">
                <div class="left main">
                    <div>
                        <h2 class="marginLeft12 marginBottom12">${tag1Label}
                            <a rel="alternate" href="${servePath}/tag-articles-feed.do?oId=${tag.oId}"><span id="tagArticlesTag">
                                ${tag.tagTitle}
                            </span>(${tag.tagPublishedRefCount})</a>
                        </h2>
                    </div>
                    <#include "article-list.ftl">
                </div>
                <div class="right side">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "footer.ftl">
            </div>
        </div>
    </body>
</html>
