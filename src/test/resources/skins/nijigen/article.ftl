<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<div class="main">
    <div id="pjax" class="content">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <main class="article-list" id="articlePage">
        <div class="item item--active">
            <time class="tooltipped tooltipped__n item__date"
                  aria-label="${article.articleCreateDate?string("yyyy")}${yearLabel}">
            ${article.articleCreateDate?string("MM")}${monthLabel}
                <span class="item__day">${article.articleCreateDate?string("dd")}</span>
            </time>

            <h2 class="item__title">
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

            <div class="item__date--m fn__none">
                <i class="icon__date"></i>
            ${article.articleCreateDate?string("yyy-MM-DD")}
            </div>

            <div class="ft__center">
                <span class="tag">
                    <i class="icon__tags"></i>
                ${article.articleTags}
                </span>
                <a class="tag" href="${servePath}${article.articlePermalink}#comments">
                    <i class="icon__comments"></i> ${article.articleCommentCount} ${commentLabel}
                </a>
                <span class="tag">
                    <i class="icon__views"></i>
                ${article.articleViewCount} ${viewLabel}
                </span>
            </div>

            <div class="content-reset">
            ${article.articleContent}
                <#if "" != article.articleSign.signHTML?trim>
                <div>
                    ${article.articleSign.signHTML}
                </div>
                </#if>
            </div>
        </div>

        <#if previousArticlePermalink?? || nextArticlePermalink??>
        <div class="module">
            <div class="module__content fn__clear">
                <#if previousArticlePermalink??>
                    <a href="${servePath}${previousArticlePermalink}" rel="prev" class="fn__left breadcrumb">
                        ${previousArticleLabel}: ${previousArticleTitle}
                    </a>
                </#if>
                <#if nextArticlePermalink??>
                    <a href="${servePath}${nextArticlePermalink}" rel="next"
                       class="fn__right breadcrumb">
                        ${nextArticleTitle}: ${nextArticleLabel}
                    </a>
                </#if>
            </div>
        </div>
        </#if>

        <@comments commentList=articleComments article=article></@comments>

        <div class="fn__flex">
            <div class="fn__flex-1" id="externalRelevantArticlesWrap">
                <div class="module">
                    <div id="externalRelevantArticles" class="module__list"></div>
                </div>
            </div>
            <div>&nbsp; &nbsp; &nbsp; &nbsp; </div>
            <div class="fn__flex-1" id="randomArticlesWrap">
                <div class="module">
                    <div id="randomArticles" class="module__list"></div>
                </div>
            </div>
            <div>&nbsp; &nbsp; &nbsp; &nbsp; </div>
            <div class="fn__flex-1" id="relevantArticlesWrap">
                <div class="module">
                    <div id="relevantArticles" class="module__list"></div>
                </div>
            </div>
        </div>
    </main>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
    </div>
    <#include "side.ftl">
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<@comment_script oId=article.oId>
page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
    <#if 0 != randomArticlesDisplayCount>
page.loadRandomArticles('<header class="module__header">${randomArticles1Label}</header>');
    </#if>
    <#if 0 != externalRelevantArticlesDisplayCount>
page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>"
    , "<header class='module__header'>${externalRelevantArticlesLabel}</header>");
    </#if>
    <#if 0 != relevantArticlesDisplayCount>
    page.loadRelevantArticles('${article.oId}',
    '<header class="module__header">${relevantArticlesLabel}</header>');
    </#if>
</@comment_script>
<#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
