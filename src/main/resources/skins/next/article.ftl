<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

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
<#include "../../common-template/macro-common_head.ftl">
<#include "macro-comments.ftl">
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
<main class="main">
    <div class="wrapper">
        <div class="content">
            <article class="posts-expand">
                <header class="post-header">
                    <h2 class="post-title">
                        ${article.articleTitle}
                        <#if article.articlePutTop>
                            <sup>
                                ${topArticleLabel}
                            </sup>
                        </#if>
                    </h2>
                    <div class="post-meta">
                            <span class="post-time">
                                <#if article.articleCreateDate?datetime != article.articleUpdateDate?datetime>
                                    ${updateTimeLabel}
                                <#else>
                                    ${postTimeLabel}
                                </#if>
                                <time>
                                    ${article.articleUpdateDate?string("yyyy-MM-dd")}
                                </time>
                            </span>
                        <#if commentable>
                        <span class="post-comments-count">
                                &nbsp; | &nbsp;
                                <a href="${servePath}${article.articlePermalink}#b3logsolocomments">
                                    <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span> ${cmtLabel}</a>
                        </span>
                        </#if>
                        &nbsp; | &nbsp; ${viewsLabel}
                        <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>°C
                    </div>
                </header>

                <div class="post-body post-body--article vditor-reset">
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
                                <a href="${servePath}${previousArticlePermalink}" rel="prev"
                                   title="${previousArticleTitle}">
                                    ${previousArticleTitle} >
                                </a>
                            </div>
                        </#if>
                        <#if nextArticlePermalink??>
                            <div class="post-nav-next post-nav-item fn-left">
                                <a href="${servePath}${nextArticlePermalink}" rel="next" title="${nextArticleTitle}">
                                    < ${nextArticleTitle}
                                </a>
                            </div>
                        </#if>
                    </div>
                </footer>
            </article>
        </div>
        <#if commentable>
            <div id="b3logsolocomments"></div>
            <div id="vcomment" data-name="${article.authorName}" data-postId="${article.oId}"></div>
            <#if !staticSite>
            <div id="soloComments" style="display: none;">
                <@comments commentList=articleComments article=article></@comments>
            </div>
            </#if>
        </#if>
        <#if 0 != relevantArticlesDisplayCount>
            <div id="relevantArticles"></div>
        </#if>
        <#if 0 != randomArticlesDisplayCount>
            <div id="randomArticles"></div>
        </#if>
        <#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
            <div id="externalRelevantArticles"></div>
        </#if>
        <#include "side.ftl">
    </div>
</main>
<#include "footer.ftl">
<@comment_script oId=article.oId commentable=article.commentable>
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
    NexT.initArticle()
</@comment_script>
</body>
</html>
