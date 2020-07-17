<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<#include "../../common-template/macro-common_head.ftl">
<#include "../../common-template/macro-comment_script.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${article.articleTitle} - ${blogTitle}" description="${article.articleAbstract?html}">
        <link rel="stylesheet"
              href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        <#if previousArticlePermalink??>
            <link rel="prev" title="${previousArticleTitle}" href="${servePath}${previousArticlePermalink}">
        </#if>
        <#if nextArticlePermalink??>
            <link rel="next" title="${nextArticleTitle}" href="${servePath}${nextArticlePermalink}">
        </#if>
    </@head>
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
        </h2>

        <div class="ft__gray fn__clear">
            <time>
            ${article.articleUpdateDate?string("yyyy-MM-dd")}
            </time>
            &nbsp;
            <span class="mobile__none">
            <#list article.articleTags?split(",") as articleTag>
                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}" class="ft__red">
                    ${articleTag}</a><#if articleTag_has_next>, </#if>
            </#list>
            </span>
            <div class="fn__right">
                <a class="ft__red" href="${servePath}${article.articlePermalink}#b3logsolocomments"><span data-uvstatcmt="${article.oId}">0</span> ${commentLabel}</a>
                •
                <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}
            </div>
        </div>

        <div class="vditor-reset article__content">
            ${article.articleContent}
            <#if "" != article.articleSign.signHTML?trim>
            <div>
                ${article.articleSign.signHTML}
            </div>
            </#if>
        </div>

        <#if previousArticlePermalink?? || nextArticlePermalink??>
        <div class="article__near article__near--point fn__flex">
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
        <div id="gitalk-container"></div>
        <div id="b3logsolocomments"></div>
        <div id="vcomment" data-name="${article.authorName}" data-postId="${article.oId}"></div>
    <#if 0 != relevantArticlesDisplayCount>
        <div id="relevantArticles" class="article__near"></div>
    </#if>
    <#if 0 != randomArticlesDisplayCount>
        <div id="randomArticles" class="article__near"></div>
    </#if>
    <#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
        <div id="externalRelevantArticles" class="article__near"></div>
    </#if>
    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <#include "../../common-template/toc.ftl"/>
    </#if>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<@comment_script oId=article.oId>
page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
<#if 0 != externalRelevantArticlesDisplayCount>
    page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
</#if>
<#if 0 != randomArticlesDisplayCount>
    page.loadRandomArticles();
</#if>
<#if 0 != relevantArticlesDisplayCount>
    page.loadRelevantArticles('${article.oId}', '<h4>${relevantArticles1Label}</h4>');
</#if>
</@comment_script>
<#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
