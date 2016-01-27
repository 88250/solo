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
        <#include "side.ftl">
        <main>
            <article class="post article-body">
                <section class="abstract">
                    ${page.pageContent}
                </section>
                <footer class="fn-clear share">
                    <div class="fn-right">
                        <span class="icon icon-t-weibo" data-type="tencent"></span>
                        <span class="icon icon-weibo" data-type="weibo"></span>
                        <span class="icon icon-twitter" data-type="twitter"></span>
                        <span class="icon icon-gplus" data-type="google"></span>
                    </div>
                </footer>
            </article>
            <@comments commentList=pageComments article=page></@comments>

            <#include "footer.ftl">

            <@comment_script oId=page.oId></@comment_script>
        </main>
    </body>
</html>
