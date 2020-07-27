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

        <div class="container">
            <div class="row">
                <div class="col-sm-2"></div>
                <div class="col-sm-8 site article-list">
                    <div class="article">
                        <div class="row article">
                            <h2 class="row article-title">
                                <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                                    ${article.articleTitle}
                                </a>
                            </h2>

                            <div class="row article-tags">
                                <#list article.articleTags?split(",") as articleTag>
                                <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                    ${articleTag}</a><#if articleTag_has_next>, </#if>
                                </#list>
                            </div>

                            <div class="row article-date">
                                <#setting locale="en_US">
                                ${article.articleUpdateDate?string("MMMM d, yyyy")}
                                <#setting locale=localeString>
                            </div>

                            <div class="row article-content code-highlight">
                                <div class="col-sm-12 vditor-reset vditor-reset--article" id="abstract${article.oId}">
                                    ${article.articleContent}
                                    <#if "" != article.articleSign.signHTML?trim>
                                    <p>
                                        ${article.articleSign.signHTML}
                                    </p>
                                    </#if>
                                </div>
                            </div>
                        </div>
                            <div id="gitalk-container"></div>
                            <div id="b3logsolocomments"></div>
                            <div id="vcomment" data-name="${article.authorName}" data-postId="${article.oId}"></div>
                    </div>
                    <#if 0 != relevantArticlesDisplayCount>
                    <div id="relevantArticles"></div>
                    </#if>
                    <#if 0 != randomArticlesDisplayCount>
                    <div id="randomArticles"></div>
                    </#if>
                    <#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
                    <div id="externalRelevantArticles"></div>
                    </#if>
                    <div class="col-sm-2"></div>
                </div>
            </div>
        </div>

        <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
            <#include "../../common-template/toc.ftl"/>
        </#if>

        <#include "footer.ftl">
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
    </body>
</html>
