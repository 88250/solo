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
<#include "macro-side.ftl">
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
            <div id="header">
                <#include "header.ftl">
                <div class="article-header">
                    <span class="article-date" data-ico="&#xe200;">
                        ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                    </span>

                    <h2>
                        <#if article.articlePutTop>
                        <span>
                            [${topArticleLabel}]
                        </span>
                        </#if>
                        ${article.articleTitle}
                    </h2>
                    <div data-ico="&#x003b;" title="${tagLabel}">
                        <#list article.articleTags?split(",") as articleTag>
                        <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if>
                        </#list>
                    </div>
                    <div class="article-info">
                        <#if commentable>
                        <a rel="nofollow" data-ico="&#xe14e;" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                            <span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span>
                        </a>
                        </#if>
                        <a rel="nofollow" data-ico="&#xe185;" href="${servePath}${article.articlePermalink}">
                            <span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>
                        </a>
                        <a rel="nofollow" data-ico="&#x0060;" href="${servePath}/authors/${article.authorId}">
                            ${article.authorName}
                        </a>
                    </div>
                </div>
            </div>
            <div class="fn-clear">
                <div class="main">
                    <div class="vditor-reset">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        ${article.articleSign.signHTML}
                        </#if>
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
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=true />
            </div>
        </div>
        <#include "footer.ftl">
        <@comment_script oId=article.oId commentable=article.commentable>
        MetroHot.tips = {
            externalRelevantArticlesDisplayCount: "${externalRelevantArticlesDisplayCount}",
        blogHost: "${blogHost}"
        }
        <#if 0 != randomArticlesDisplayCount>
        MetroHot.loadRandomArticles();
        </#if>
        <#if 0 != relevantArticlesDisplayCount>
        MetroHot.loadRelevantArticles('${article.oId}', '<h4>${relevantArticles1Label}</h4>');
        </#if>
        <#if 0 != externalRelevantArticlesDisplayCount>
        MetroHot.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
        </#if>
            page.share()
        </@comment_script>
    </body>
</html>
