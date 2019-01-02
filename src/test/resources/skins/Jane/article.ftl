<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2019, b3log.org & hacpai.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${article.articleTitle} - ${blogTitle}">
    <meta name="keywords" content="${article.articleTags}"/>
    <meta name="description" content="${article.articleAbstract?html}"/>
    </@head>
    <#if previousArticlePermalink??>
        <link rel="prev" title="${previousArticleTitle}" href="${servePath}${previousArticlePermalink}">
    </#if>
    <#if nextArticlePermalink??>
        <link rel="next" title="${nextArticleTitle}" href="${servePath}${nextArticlePermalink}">
    </#if>
    <!-- Open Graph -->
    <meta property="og:locale" content="zh_CN"/>
    <meta property="og:type" content="article"/>
    <meta property="og:title" content="${article.articleTitle}"/>
    <meta property="og:description" content="${article.articleAbstract?html}"/>
    <meta property="og:image" content="${article.authorThumbnailURL}"/>
    <meta property="og:url" content="${servePath}${article.articlePermalink}"/>
    <meta property="og:site_name" content="Solo"/>
    <!-- Twitter Card -->
    <meta name="twitter:card" content="summary"/>
    <meta name="twitter:description" content="${article.articleAbstract?html}"/>
    <meta name="twitter:title" content="${article.articleTitle}"/>
    <meta name="twitter:image" content="${article.authorThumbnailURL}"/>
    <meta name="twitter:url" content="${servePath}${article.articlePermalink}"/>
    <meta name="twitter:site" content="@DL88250"/>
    <meta name="twitter:creator" content="@DL88250"/>
</head>
<body>
<#include "header.ftl">
<div id="pjax" class="wrapper">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="article__item">
        <h2 class="article__title">
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

        <div class="ft__gray fn__clear">
            <time>
            ${article.articleCreateDate?string("yyyy-MM-dd")}
            </time>
            &nbsp;
            <span class="mobile__none">
            <#list article.articleTags?split(",") as articleTag>
                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}" class="ft__red">
                    ${articleTag}</a><#if articleTag_has_next>, </#if>
            </#list>
            </span>
            <div class="fn__right">
                <a class="ft__red" href="${servePath}${article.articlePermalink}#comments"><#if article.articleCommentCount gt 0>${article.articleCommentCount} </#if>${commentLabel}</a>
                •
                ${article.articleViewCount} ${viewLabel}
            </div>
        </div>

        <div class="content-reset article__content">
            ${article.articleContent}
            <#if "" != article.articleSign.signHTML?trim>
            <div>
                ${article.articleSign.signHTML}
            </div>
            </#if>
        </div>

        <#if previousArticlePermalink?? || nextArticlePermalink??>
        <div class="article__near fn__flex">
            <#if nextArticlePermalink??>
                <a href="${servePath}${nextArticlePermalink}" rel="next"
                   class="fn__flex-1 first">
                    <strong>NEWER</strong>
                    ${nextArticleLabel}
                </a>
            <#else>
                <a class="fn__flex-1 first">&nbsp;</a>
            </#if>
            <#if previousArticlePermalink??>
                <a href="${servePath}${previousArticlePermalink}" rel="prev" class="fn__flex-1">
                    <strong>OLDER</strong>
                    ${previousArticleTitle}
                </a>
            <#else>
                <a class="fn__flex-1">&nbsp;</a>
            </#if>
        </div>
        </#if>
    </div>

    <@comments commentList=articleComments article=article></@comments>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<@comment_script oId=article.oId>
page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
</@comment_script>
<#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
