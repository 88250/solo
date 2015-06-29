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
    <body class="nav-closed">
        <div class="nav">
            <#include "side.ftl">
        </div>
        <div class="site-wrapper">
            <#include "header.ftl">
            <main>
                <article class="post">
                    <section class="post-content article-body">
                        ${page.pageContent}
                    </section>
                    <footer>
                        <div class="share fn-right">
                            <span class="icon icon-tencent" data-type="tencent"></span>
                            <span class="icon icon-weibo" data-type="weibo"></span>
                            <span class="icon icon-twitter" data-type="twitter"></span>
                            <span class="icon icon-google" data-type="google"></span>
                        </div>
                    </footer>
                </article>
                <@comments commentList=pageComments article=page></@comments>
            </main>

            <#include "footer.ftl">

            <@comment_script oId=page.oId></@comment_script>
        </div>
    </body>
</html>
