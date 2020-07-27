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
<div class="main" id="pjax">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="content">
        <main id="articlePage">
            <div class="article-list">
                <div class="item item--active">
                    <time class="vditor-tooltipped vditor-tooltipped__n item__date"
                          aria-label="${article.articleUpdateDate?string("yyyy")}${yearLabel}">
                        ${article.articleUpdateDate?string("MM")}${monthLabel}
                        <span class="item__day">${article.articleUpdateDate?string("dd")}</span>
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
                    </h2>

                    <div class="item__date--m fn__none">
                        <i class="icon__date"></i>
                        ${article.articleUpdateDate?string("yyyy-MM-dd")}
                    </div>

                    <div class="ft__center">
                        <span class="tag">
                            <i class="icon__tags"></i>
                            <#list article.articleTags?split(",") as articleTag>
                            <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                ${articleTag}</a><#if articleTag_has_next>,</#if>
                            </#list>
                        </span>
                            <a class="tag" href="${servePath}${article.articlePermalink}#b3logsolocomments">
                                <i class="icon__comments"></i> <span data-uvstatcmt="${article.oId}">0</span> ${commentLabel}
                            </a>
                        <span class="tag">
                            <i class="icon__views"></i>
                        <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}
                        </span>
                    </div>

                    <div class="vditor-reset">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                            <div>
                                ${article.articleSign.signHTML}
                            </div>
                        </#if>
                    </div>
                </div>
            </div>

            <#if previousArticlePermalink?? || nextArticlePermalink??>
                <div class="module mobile__hidden">
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

            <#if previousArticlePermalink??>
                <div class="module mobile__hidden fn__none">
                    <div class="module__content">
                        <a href="${servePath}${previousArticlePermalink}" rel="prev" class="breadcrumb">
                            ${previousArticleLabel}: ${previousArticleTitle}
                        </a>
                    </div>
                </div>
            </#if>

            <#if nextArticlePermalink??>
                <div class="module mobile__hidden fn__none">
                    <div class="module__content">
                        <a href="${servePath}${nextArticlePermalink}" rel="next"
                           class="breadcrumb">
                            ${nextArticleLabel}: ${nextArticleTitle}
                        </a>
                    </div>
                </div>
            </#if>
                <div id="gitalk-container" class="module__content"
                     style="border-radius: 5px;margin-bottom: 30px;box-shadow: 1px 1px 3px 1px rgba(0,0,0,0.2);transition: all .3s;"></div>
                <div id="b3logsolocomments"></div>
                <div id="vcomment" class="module__content"
                     style="border-radius: 5px;margin-bottom: 30px;box-shadow: 1px 1px 3px 1px rgba(0,0,0,0.2);transition: all .3s;"
                     data-name="${article.authorName}" data-postId="${article.oId}"></div>

            <div class="fn__flex article__relevant">
                <div class="fn__flex-1" id="externalRelevantArticlesWrap">
                    <div class="module">
                        <div id="externalRelevantArticles" class="module__list"></div>
                    </div>
                </div>
                <div class="mobile__hidden">&nbsp; &nbsp; &nbsp; &nbsp;</div>
                <div class="fn__flex-1" id="randomArticlesWrap">
                    <div class="module">
                        <div id="randomArticles" class="module__list"></div>
                    </div>
                </div>
                <div class="mobile__hidden">&nbsp; &nbsp; &nbsp; &nbsp;</div>
                <div class="fn__flex-1" id="relevantArticlesWrap">
                    <div class="module">
                        <div id="relevantArticles" class="module__list"></div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    <#include "side.ftl">
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<@comment_script oId=article.oId>
    page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
    <#if 0 != randomArticlesDisplayCount>
    page.loadRandomArticles('<header class="module__header">${randomArticlesLabel}</header>');
    </#if>
    <#if 0 != externalRelevantArticlesDisplayCount>
    page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>" , "<header class='module__header'>${externalRelevantArticlesLabel}</header>");
    </#if>
    <#if 0 != relevantArticlesDisplayCount>
    page.loadRelevantArticles('${article.oId}', '<header class="module__header">${relevantArticlesLabel}</header>');
    </#if>
    Skin.initArticle()
</@comment_script>
<#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
