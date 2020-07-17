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
                        <a rel="nofollow" data-ico="&#xe14e;" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                            <span data-uvstatcmt="${article.oId}">0</span>
                        </a>
                        <a rel="nofollow" data-ico="&#xe185;" href="${servePath}${article.articlePermalink}">
                            <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>
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
                        <div id="gitalk-container"></div>
                        <div id="b3logsolocomments"></div>
                        <div id="vcomment" data-name="${article.authorName}" data-postId="${article.oId}"></div>
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=true />
            </div>
        </div>
        <#include "footer.ftl">
        <@comment_script oId=article.oId>
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
