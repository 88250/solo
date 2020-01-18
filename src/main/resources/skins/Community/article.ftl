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
        ${topBarReplacement}
        <div class="header">
            <#include "header.ftl">
        </div>
        <div class="content">
            <div class="marginBottom40">
                <div class="article-header">
                    <div class="article-date">
                        ${article.articleUpdateDate?string("yyyy-MM-dd")}
                    </div>
                    <div class="arrow-right"></div>
                    <div class="clear"></div>
                    <ul>
                        <li>
                            <span class="left">
                                by&nbsp;
                            </span>
                            <a rel="nofollow" class="left" title="${article.authorName}" href="${servePath}/authors/${article.authorId}">
                                ${article.authorName}
                            </a>
                            <span class="clear"></span>
                        </li>
                        <li>
                            <a rel="nofollow" href="${servePath}${article.articlePermalink}" title="${viewLabel}">
                                ${viewLabel} (<span data-uvstaturl="${servePath}${article.articlePermalink}">${article.articleViewCount}</span>)
                            </a>
                        </li>
                        <#if commentable>
                        <li>
                            <a rel="nofollow" title="${commentLabel}" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                                ${commentLabel} (<span data-uvstatcmt="${article.oId}">${article.articleCommentCount}</span>)
                            </a>
                        </li>
                        </#if>
                    </ul>
                    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
                        <br> <br>
                        <div class="article-date">
                            ${tocLabel}
                        </div>
                        <div class="arrow-right"></div>
                        <div class="clear"></div>
                        <#include "../../common-template/toc.ftl"/>
                    </#if>
                </div>
                <div class="article-main article-detail-body">
                    <h2 class="title">
                        <a href="${servePath}${article.articlePermalink}">${article.articleTitle}</a>
                        <#if article.articlePutTop>
                        <sup class="red">
                            ${topArticleLabel}
                        </sup>
                        </#if>
                    </h2>
                    <div class="vditor-reset vditor-reset--article">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div class="marginTop12">
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </div>
                    <div class="tags">
                        <span class="tag-icon" title="${tagsLabel}"></span>
                        ${tags1Label}
                        <#list article.articleTags?split(",") as articleTag>
                        <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                            ${articleTag}</a><#if articleTag_has_next>,</#if>
                        </#list>
                    </div>
                </div>
                <div class="clear"></div>
                <div class="article-detail-footer">
                    <#if nextArticlePermalink??>
                    <a href="${servePath}${nextArticlePermalink}" class="left">${nextArticle1Label} ${nextArticleTitle}</a>
                    </#if>
                    <#if previousArticlePermalink??>
                    <a href="${servePath}${previousArticlePermalink}" class="right">${previousArticle1Label} ${previousArticleTitle}</a>
                    </#if>
                    <div class="clear"></div>
                    <div id="randomArticles" class="left article-relative"></div>
                    <div id="relevantArticles" class="article-relative left" style="width: 48%;"></div>
                    <div class="clear"></div>
                    <div id="externalRelevantArticles" class="article-relative"></div>
                </div>
            </div>
            <#if commentable>
                <div id="b3logsolocomments"></div>
                <div id="vcomment" style="margin-bottom: 40px;border-top: 1px solid #dcdcdc;padding-top: 30px;" data-name="${article.authorName}" data-postId="${article.oId}"></div>
                <#if !staticSite>
                    <div id="soloComments" style="display: none;">
                        <@comments commentList=articleComments article=article></@comments>
                    </div>
                </#if>
            </#if>
        </div>
        <div>
            <#include "side.ftl">
        </div>
        <div class="footer">
            <#include "footer.ftl">
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
