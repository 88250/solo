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
        ${topBarReplacement}
        <#include "nav.ftl">
        <div class="wrapper">
            <div class="content">
                <#include "header.ftl">
                <div class="roundtop"></div>
                <div class="body">
                    <div class="left main">
                        <div class="article">
                            <h2 class="article-title">
                                <a class="no-underline" href="${servePath}${article.articlePermalink}">
                                    ${article.articleTitle}
                                </a>
                                <#if article.articlePutTop>
                                <sup class="red">
                                    ${topArticleLabel}
                                </sup>
                                </#if>
                            </h2>
                            <div class="posttime-blue">
                                <div class="posttime-MY">
                                    ${article.articleUpdateDate?string("yyyy-MM")}
                                </div>
                                <div class="posttime-D">
                                    ${article.articleUpdateDate?string("dd")}
                                </div>
                            </div>
                            <div class="article-abstract">
                                <div class="note">
                                    <div class="corner"></div>
                                    <div class="substance vditor-reset">
                                        ${article.articleContent}
                                        <#if "" != article.articleSign.signHTML?trim>
                                        <div class="marginTop12">
                                            ${article.articleSign.signHTML}
                                        </div>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                            <div class="margin25">
                                <a rel="nofollow" href="${servePath}${article.articlePermalink}" class="left">
                                    <span class="left article-browserIcon" title="${viewLabel}"></span>
                                    <span class="count"><span data-uvstaturl="${servePath}${article.articlePermalink}">0</span></span>
                                </a>
                                <div class="left">
                                    <span class="tagsIcon" title="${tagLabel}"></span>
                                    <#list article.articleTags?split(",") as articleTag>
                                    <span class="count">
                                        <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                                            ${articleTag}</a><#if articleTag_has_next>,</#if>
                                    </span>
                                    </#list>
                                </div>
                                <a rel="nofollow" href="${servePath}${article.articlePermalink}#b3logsolocomments" class="left">
                                    <span class="left articles-commentIcon" title="${commentLabel}"></span>
                                    <span class="count" data-uvstatcmt="${article.oId}">0</span>
                                </a>
                                <div class="right">
                                    <a rel="nofollow" href="#b3logsolocomments" class="right">
                                        ${replyLabel}
                                    </a>
                                </div>
                                <div class="clear"></div>
                            </div>

                            <div class="margin25">
                                <#if nextArticlePermalink??>
                                <div class="right">
                                    <a href="${servePath}${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                </div>
                                <div class="clear"></div>
                                </#if>
                                <#if previousArticlePermalink??>
                                <div class="right">
                                    <a href="${servePath}${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                </div>
                                </#if>
                                <div class="clear"></div>
                            </div>
                            <div id="relevantArticles" class="article-relative"></div>
                            <div id="randomArticles" class="article-relative"></div>
                            <div id="externalRelevantArticles" class="article-relative"></div>
                        </div>
                            <div id="gitalk-container" class="comments"
                                 style="padding-top: 15px"></div>
                            <div id="b3logsolocomments"></div>
                            <div id="vcomment"
                                 class="comments"
                                 style="padding-top: 15px"
                                 data-name="${article.authorName}" data-postId="${article.oId}"></div>
                    </div>
                    <div class="right">
                        <#include "side.ftl">
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="roundbottom"></div>
            </div>
        </div>
        <div class="footer">
            <div class="footer-icon"><#include "statistic.ftl"></div>
            <#include "footer.ftl">
        </div>
        <@comment_script oId=article.oId>
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
