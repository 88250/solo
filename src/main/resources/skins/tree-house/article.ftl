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
        <div class="wrapper">
            <div class="bg-bottom">
                ${topBarReplacement}
                <div class="content">
                    <div class="header">
                        <#include "header.ftl">
                    </div>
                    <div class="body">
                        <div class="left main">
                            <div class="article">
                                <div class="article-header">
                                    <h2 class="marginBottom12">
                                        <a class="no-underline" href="${servePath}${article.articlePermalink}">
                                            ${article.articleTitle}
                                            <#if article.articlePutTop>
                                            <sup>
                                                ${topArticleLabel}
                                            </sup>
                                            </#if>
                                            <span>
                                                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                            </span>
                                        </a>
                                    </h2>
                                    <div class="marginLeft12">
                                        <#list article.articleTags?split(",") as articleTag>
                                        <a rel="tag" class="article-tags" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                            ${articleTag}</a>
                                        </#list>
                                        <div class="clear"></div>
                                    </div>
                                </div>
                                <div class="vditor-reset">
                                    ${article.articleContent}
                                    <#if "" != article.articleSign.signHTML?trim>
                                    <div class="marginTop12">
                                        ${article.articleSign.signHTML}
                                    </div>
                                    </#if>
                                </div>
                                <div class="article-details-footer">
                                    <div class="left">
                                        <#if nextArticlePermalink??>
                                        <a href="${servePath}${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                        <br/>
                                        </#if>
                                        <#if previousArticlePermalink??>
                                        <a href="${servePath}${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                        </#if>
                                    </div>
                                    <div class="right">
                                        <#if commentable>
                                        <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments" class="left">
                                            &nbsp;<span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span> ${commentLabel}&nbsp;&nbsp;
                                        </a>
                                        </#if>
                                        <a rel="nofollow" href="${servePath}${article.articlePermalink}" class="left">
                                            &nbsp;&nbsp;<span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span> ${viewLabel}&nbsp;&nbsp;
                                        </a>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                                <div id="relevantArticles" class="article-relative"></div>
                                <div id="randomArticles" class="article-relative"></div>
                                <div id="externalRelevantArticles" class="article-relative"></div>
                            </div>
                            <div class="line right"></div>
                            <#if commentable>
                                <div id="b3logsolocomments"></div>
                                <div id="vcomment"
                                     style="margin: 88px 100px 0 99px;background-color: #effdff;border-radius: 10px;padding: 10px;"
                                     data-name="${article.authorName}" data-postId="${article.oId}"></div>
                                <#if !staticSite>
                                    <div id="soloComments" style="display: none;">
                                        <@comments commentList=articleComments article=article></@comments>
                                    </div>
                                </#if>
                            </#if>
                        </div>
                        <div class="left side">
                            <#include "side.ftl">
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="footer">
                    <#include "footer.ftl">
                </div>
            </div>
        </div>
        <@comment_script oId=article.oId commentable=article.commentable>
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
    </body>
</html>
