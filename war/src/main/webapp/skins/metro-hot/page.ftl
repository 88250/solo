<#include "macro-head.ftl">
<#include "macro-side.ftl">
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
        ${topBarReplacement}
        <div class="wrapper">
            <div id="header">
                <#include "header.ftl">
                <div class="article-header">
                    <h2>${blogSubtitle}</h2>
                    
                    <div class="article-info">
                        <a rel="nofollow" data-ico="&#xe14e;" href="${servePath}${page.pagePermalink}#comments">
                            ${page.pageCommentCount}
                        </a>
                       
                    </div>
                </div>
            </div>
            <div class="fn-clear">
                <div class="main">
                    <div class="article-body">
                        ${page.pageContent}
                    </div>
                    <@comments commentList=pageComments article=page></@comments>
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=true />
            </div>
        </div>
        <#include "footer.ftl">
        <@comment_script oId=page.oId></@comment_script>
    </body>
</html>
