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
    <body class="nav-closed">
        <div class="nav">
            <#include "side.ftl">
        </div>
        <div class="site-wrapper">
            <#include "header.ftl">
            <main class="fn-wrap">
                <article class="post">
                    <header>
                        <h1 class="post-title">
                            <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                                ${article.articleTitle}
                            </a>
                            <#if article.hasUpdated>
                            <sup class="post-tip">
                                ${updatedLabel}
                            </sup>
                            </#if>
                            <#if article.articlePutTop>
                            <sup class="post-tip">
                                ${topArticleLabel}
                            </sup>
                            </#if>
                        </h1>
                        <section class="post-meta">
                            <#list article.articleTags?split(",") as articleTag>
                            <span>
                                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                    ${articleTag}</a><#if articleTag_has_next>,</#if>
                            </span>
                            </#list>
                            <time>${article.articleCreateDate?string("yyyy-MM-dd")}</time>
                        </section>
                    </header>
                    <section class="post-content">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div class="marginTop12">
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </section>
                    <footer>
                        <figure class="post-author">
                            <a href="${servePath}/authors/${article.authorId}"
                               title="${article.authorName}" alt="${article.authorName}"
                               style="background-image: url('${article.authorThumbnailURL}')">
                                <span class="fn-none">${article.authorName}</span>
                            </a>
                        </figure>
                        <div class="share fn-right">
                            <span class="icon icon-tencent" data-type="tencent"></span>
                            <span class="icon icon-weibo" data-type="weibo"></span>
                            <span class="icon icon-twitter" data-type="twitter"></span>
                            <span class="icon icon-google" data-type="google"></span>
                        </div>
                    </footer>
                </article>
                <@comments commentList=articleComments article=article></@comments>
            </main>
            <#if nextArticlePermalink??>
            <a href="${servePath}${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a><br/>
            </#if>
            <#if previousArticlePermalink??>
            <a href="${servePath}${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
            </#if>  
            <#include "footer.ftl">

            <@comment_script oId=article.oId>
            page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
            <#if 0 != randomArticlesDisplayCount>
            page.loadRandomArticles();
            </#if>
            <#if 0 != relevantArticlesDisplayCount>
            page.loadRelevantArticles('${article.oId}', '<h4>${relevantArticles1Label}</h4>');
            </#if>
            <#if 0 != externalRelevantArticlesDisplayCount>
            page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
            </#if>
            </@comment_script>    
        </div>
    </body>
</html>
