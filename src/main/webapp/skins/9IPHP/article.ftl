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
        <div class="wrapper">
            <div class="main-wrap">
                <main>
                    <article class="post">
                        <header>
                            <h1>
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
                            </h1>
                            <div class="meta">
                                <span class="tooltipped tooltipped-n" aria-label="${createDateLabel}">
                                    <i class="icon-date"></i>
                                    <time>
                                        ${article.articleCreateDate?string("yyyy-MM-dd")}
                                    </time>
                                </span>
                                                &nbsp; | &nbsp;
                                                <span class="tooltipped tooltipped-n" aria-label="${commentCountLabel}">
                                    <i class="icon-comments"></i>
                                    <a href="${servePath}${article.articlePermalink}#comments">
                                        ${article.articleCommentCount} ${commentLabel}</a>
                                </span>
                                                &nbsp; | &nbsp;
                                                <span class="tooltipped tooltipped-n" aria-label="${viewCountLabel}">
                                    <i class="icon-views"></i>
                                    ${article.articleViewCount} ${viewLabel}
                                </span>
                            </div>
                        </header>

                        <div class="content-reset">
                            ${article.articleContent}
                            <#if "" != article.articleSign.signHTML?trim>
                                <div>
                                    ${article.articleSign.signHTML}
                                </div>
                            </#if>
                        </div>

                        <footer>
                            <#list article.articleTags?split(",") as articleTag>
                                <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                    ${articleTag}</a>
                            </#list>

                            <div class="article-cr">
                                转载请注明来源：
                                <a href="http://9iphp.com/web/laravel/laravel-5-acl-define.html">使用Laravel5.1自带权限控制系统 ACL</a> -
                                <a href="http://9iphp.com" title="" data-original-title="Specs' Blog-就爱PHP">Specs' Blog-就爱PHP</a>
                            </div>

                            <div class="post-nav fn-clear">
                                <#if previousArticlePermalink??>
                                    <div class="fn-left">
                                        <a href="${servePath}${previousArticlePermalink}" rel="prev"
                                           aria-label="${previousArticleTitle}">
                                            ${previousArticleLabel}
                                        </a>
                                    </div>
                                </#if>
                                <#if nextArticlePermalink??>
                                    <div class="fn-right">
                                        <a href="${servePath}${nextArticlePermalink}" rel="next"
                                           aria-label="${nextArticleTitle}">
                                            ${nextArticleLabel}
                                        </a>
                                    </div>
                                </#if>
                            </div>
                        </footer>
                        <@comments commentList=articleComments article=article></@comments>
                        <div id="externalRelevantArticles"></div>
                    </article>
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">
        <@comment_script oId=article.oId>
        page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
        <#if 0 != externalRelevantArticlesDisplayCount>
        page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
        </#if>
        </@comment_script>    
    </body>
</html>
