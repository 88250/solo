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
        <div class="container one-column  page-post-detail">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">
                        <div id="posts" class="posts-expand">
                            <article class="post post-type-normal">
                                <div class="post-body">
                                    ${page.pageContent}
                                </div>
                            </article>
                        </div>


                        <@comments commentList=pageComments article=page></@comments>

                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
            <@comment_script oId=page.oId></@comment_script>
        </div>
    </body>
</html>
