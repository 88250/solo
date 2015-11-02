<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${blogTitle}">
        <meta name="keywords" content="${article.articleTags}" />
        <meta name="description" content="${article.articleAbstract?html}" />
        </@head>
    </head>
    <body>
        <#include "side.ftl">
        <main>
            <article class="post article-body">
                <header>
                    <h2>
                        <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                            ${article.articleTitle}
                        </a>
                        <#if article.articlePutTop>
                        <sup>
                            ${topArticleLabel}
                        </sup>
                        </#if>
                        <#if article.hasUpdated>
                        <sup>
                            ${updatedLabel}
                        </sup>
                        </#if>
                    </h2>
                    <time><span class="icon-date"></span> ${article.articleCreateDate?string("yyyy-MM-dd")}</time>

                    <section class="tags">
                        <span class="icon-tag"></span>  &nbsp;
                        <#list article.articleTags?split(",") as articleTag>
                        <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                            ${articleTag}</a>
                        </#list>

                        <a rel="nofollow" href="${servePath}/authors/${article.authorId}">
                            <img class="avatar" title="${article.authorName}" alt="${article.authorName}" src="${article.authorThumbnailURL}"/>
                        </a>
                    </section>
                </header>
                <section class="abstract">
                    ${article.articleContent}
                    <#if "" != article.articleSign.signHTML?trim>
                    <div>
                        ${article.articleSign.signHTML}
                    </div>
                    </#if>

                    <#if nextArticlePermalink?? || previousArticlePermalink??>
                    <aside class="fn-clear">
                        <#if previousArticlePermalink??>
                        <a class="fn-left" rel="prev" href="${servePath}${previousArticlePermalink}">
                            <strong>&lt;</strong> ${previousArticleTitle}
                        </a>
                        </#if>
                        <#if nextArticlePermalink??>
                        <a class="fn-right" rel="next" href="${servePath}${nextArticlePermalink}">
                            ${nextArticleTitle} <strong>&gt;</strong>
                        </a>
                        </#if>
                    </aside>
                    </#if>
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

            <@comments commentList=articleComments article=article></@comments>

            <#include "footer.ftl">

            <@comment_script oId=article.oId>
            page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
            </@comment_script>    
        </main>
    </body>
</html>
