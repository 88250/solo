<#include "macro-head.ftl">
<#include "macro-side.ftl">
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
        <div class="wrapper">
            <#include "header.ftl" />
            <div class="sub-nav fn-clear">
                <h2>
                    <a id="tag" rel="alternate" href="${servePath}/tag-articles-feed.do?oId=${tag.oId}">
                        ${tag1Label}
                        ${tag.tagTitle}
                        (${tag.tagPublishedRefCount})
                    </a>
                </h2>
            </div>
            <div class="fn-clear">
                <div class="main">
                    <#include "article-list.ftl"/>
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=false />
            </div>
        </div>
        <span id="goTop" onclick="Util.goTop()" data-ico="&#xe042;" class="side-tile"></span>
        <#include "footer.ftl"/>
    </body>
</html>
