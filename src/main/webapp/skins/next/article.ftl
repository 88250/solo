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
        <#include "header.ftl">
        <main class="main wrapper">
            <div class="content">
                <article class="posts-expand">
                    <header class="post-header">
                        <h1 class="post-title">
                            ${article.articleTitle}
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
                        </h1>
                        <div class="post-meta">
                            <span class="post-time">
                                ${postTimeLabel}
                                <time>
                                    ${article.articleCreateDate?string("yyyy-MM-dd")}
                                </time>
                            </span>
                            <span class="post-comments-count">
                                &nbsp; | &nbsp;
                                <a href="${servePath}${article.articlePermalink}#comments">
                                    ${article.articleCommentCount} ${cmtLabel}</a>
                            </span>
                            &nbsp; | &nbsp; ${viewsLabel}
                            ${article.articleViewCount}°C
                        </div>
                    </header>

                    <div class="post-body article-body">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div>
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </div>
                    <footer>
                        <div class="post-tags">
                            <#list article.articleTags?split(",") as articleTag>
                            <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                ${articleTag}</a>
                            </#list>
                        </div>
                        <div class="post-nav fn-clear">
                            <#if previousArticlePermalink??>
                            <div class="post-nav-prev post-nav-item fn-right">
                                <a href="${servePath}${previousArticlePermalink}" rel="next" title="${previousArticleTitle}">
                                    ${previousArticleTitle} >
                                </a>
                            </div>
                            </#if>
                            <#if nextArticlePermalink??>
                            <div class="post-nav-next post-nav-item fn-left">
                                <a href="${servePath}${nextArticlePermalink}" rel="prev" title="${nextArticleTitle}">
                                   < ${nextArticleTitle} 
                                </a>
                            </div>
                            </#if>
                        </div>
                    </footer>
                </article>
            </div>
            <@comments commentList=articleComments article=article></@comments>
            <div id="externalRelevantArticles"></div>
            <#include "side.ftl">
        </main>
        <#include "footer.ftl">
        <@comment_script oId=article.oId>
        page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
        <#if 0 != externalRelevantArticlesDisplayCount>
        page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
        </#if>
        </@comment_script>    
    </body>
</html>
