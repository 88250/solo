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
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="wrapper">
            <div class="container">
                <div class="module">
                    <article class="article">
                        <time class="article-time">
                            <span>
                                <#if article.hasUpdated>
                                ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                                <#else>
                                ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
                                </#if>
                            </span>
                        </time>
                        <h2 class="article-title">
                            <a href="${servePath}${article.articlePermalink}">
                                ${article.articleTitle}
                            </a>
                            <#if article.hasUpdated>
                            <sup>
                                ${updatedLabel}
                            </sup>
                            </#if>
                            <#if article.articlePutTop>
                            <sup>
                                ${topArticleLabel}
                            </sup>
                            </#if>
                        </h2>
                        <p>
                            ${article.articleContent}
                        </p>
                        <#if "" != article.articleSign.signHTML?trim>
                        <p>
                            ${article.articleSign.signHTML}
                        </p>
                        </#if>
                        <span class="ico-tags ico" title="${tagLabel}">
                            <#list article.articleTags?split(",") as articleTag><a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
                        </span>
                        <span class="ico-author ico" title="${authorLabel}">
                            <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
                        </span>
                        <span class="ico-comment ico" title="${commentLabel}">

                            <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                                <#if article.articleCommentCount == 0>
                                ${noCommentLabel}
                                <#else>
                                ${article.articleCommentCount}
                                </#if>
                            </a>
                        </span>
                        <span class="ico-view ico" title="${viewLabel}">
                            <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                                ${article.articleViewCount}
                            </a>
                        </span>
                    </article>
                    <div class="fn-clear" style="margin-top: 30px;">
                        <#if nextArticlePermalink??>
                        <div class="left">
                            <a href="${servePath}${nextArticlePermalink}">
                                <span class="ico-pre">«</span>
                                ${nextArticleTitle}
                            </a>
                        </div>
                        </#if>                            
                        <#if previousArticlePermalink??>
                        <div class="right">
                            <a href="${servePath}${previousArticlePermalink}">
                                ${previousArticleTitle}
                                <span class="ico-next">»</span>
                            </a> 
                        </div>
                        </#if>
                    </div>
                    <@comments commentList=articleComments article=article></@comments>
                </div>
            </div>
        </div>
        <#include "footer.ftl">
        <@comment_script oId=article.oId></@comment_script>    
    </body>
</html>
