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
<div id="pjax">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="main post__main">
        <#if noticeBoard??>
        <div class="board">
            ${noticeBoard}
        </div>
        </#if>
        <div class="wrapper content">
            <article class="post">
                <header>
                    <h2 class="post__title">
                    ${article.articleTitle}
                    <#if article.articlePutTop>
                        <sup>
                            ${topArticleLabel}
                        </sup>
                    </#if>
                    </h2>
                </header>
                <section class="vditor-reset">
                ${article.articleContent}
            <#if "" != article.articleSign.signHTML?trim>
                <div>
                    ${article.articleSign.signHTML}
                </div>
            </#if>
                </section>
                <footer data-oid="${article.oId}"
                        class="post__tags"
                        data-tag="<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>">
            <#list article.articleTags?split(",") as articleTag>
                <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a>
            </#list>
                </footer>
                <div class="post__share fn-clear">
                    <time class="ft-gray">
                    ${article.articleUpdateDate?string("yyyy-MM-dd")} •
                    </time>
                    <a class="post__view" href="${servePath}${article.articlePermalink}">
                        <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}</a>
                    <div class="fn-right">
                    <span class="vditor-tooltipped vditor-tooltipped__n post__share-icon ft-green"
                          onclick="$('#comment').focus()"
                          aria-label="${commentLabel}">
                        <svg>
                            <use xlink:href="#icon-comment"></use>
                        </svg>
                    <span data-uvstatcmt="${article.oId}">0</span>  &nbsp; &nbsp;
                    </span>

                        <span id="articleShare">
                        <span class="post__share-icon" data-type="wechat">
                            <svg><use xlink:href="#icon-wechat"></use></svg>
                        </span> &nbsp; &nbsp;
                        <span class="post__share-icon" data-type="weibo">
                            <svg><use xlink:href="#icon-weibo"></use></svg>
                        </span> &nbsp; &nbsp;
                        <span class="post__share-icon" data-type="twitter">
                            <svg><use xlink:href="#icon-twitter"></use></svg>
                        </span> &nbsp; &nbsp;
                        <span class="post__share-icon" data-type="qqz">
                            <svg><use xlink:href="#icon-qqz"></use></svg>
                        </span>
                        <span class="article__code"
                              data-title="${article.articleTitle}"
                              data-blogtitle="${blogTitle}"
                              data-url="${servePath}${article.articlePermalink}"
                              data-avatar="${article.authorThumbnailURL}"></span>
                    </span>
                    </div>
                </div>
            </article>
        </div>
        <div class="article__bottom">
            <div class="wrapper">
                <div class="fn-flex footer__tag">
                    <div class="fn-flex-1" id="externalRelevantArticles"></div>
                    <div class="fn-flex-1" id="relevantArticles"></div>
                    <div class="fn-flex-1" id="randomArticles"></div>
                </div>
                    <div id="gitalk-container" class="article__comment"></div>
                    <div id="b3logsolocomments"></div>
                    <div id="vcomment" class="article__comment" data-name="${article.authorName}" data-postId="${article.oId}"></div>
            </div>
        </div>

        <div class="article__toolbar">
            <div class="wrapper">
                <a class="post__view" href="${servePath}${article.articlePermalink}">
                    <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span> ${viewLabel}
                </a>
                <div class="fn-right">
                <span class="vditor-tooltipped vditor-tooltipped__n post__share-icon ft-green"
                      onclick="$('#comment').focus()"
                      aria-label="${commentLabel}">
                    <svg>
                        <use xlink:href="#icon-comment"></use>
                    </svg>
                <span data-uvstatcmt="${article.oId}">0</span>  &nbsp; &nbsp;
                </span>
                    <span id="articleBottomShare">
                    <span class="post__share-icon" data-type="wechat">
                        <svg><use xlink:href="#icon-wechat"></use></svg>
                    </span> &nbsp; &nbsp;
                    <span class="post__share-icon" data-type="weibo">
                        <svg><use xlink:href="#icon-weibo"></use></svg>
                    </span> &nbsp; &nbsp;
                    <span class="post__share-icon" data-type="twitter">
                        <svg><use xlink:href="#icon-twitter"></use></svg>
                    </span> &nbsp; &nbsp;
                    <span class="post__share-icon" data-type="qqz">
                        <svg><use xlink:href="#icon-qqz"></use></svg>
                    </span>
                    <span class="article__code"
                          data-title="${article.articleTitle}"
                          data-blogtitle="${blogTitle}"
                          data-url="${servePath}${article.articlePermalink}"
                          data-avatar="${article.authorThumbnailURL}"></span>
                </span>

                <#if nextArticlePermalink??>
                    <a href="${servePath}${nextArticlePermalink}" rel="next" class="article__next">
                        <span class="ft-12 ft-gray">${nextArticleLabel}</span> <br>
                        ${nextArticleTitle}
                    </a>
                </#if>
                </div>
            </div>
        </div>

        <div class="post__side">
        <span class="vditor-tooltipped vditor-tooltipped__e post__share-icon ft-green"
              onclick="$('#comment').focus()"
              aria-label="${commentLabel}">
            <span class="ft-gray" data-uvstatcmt="${article.oId}">0</span>
            <svg>
                <use xlink:href="#icon-comment"></use>
            </svg>
        </span>
            <div id="articleSideShare">
            <span class="post__share-icon" data-type="wechat">
                <svg><use xlink:href="#icon-wechat"></use></svg>
            </span> &nbsp; &nbsp;
                <span class="post__share-icon" data-type="weibo">
                <svg><use xlink:href="#icon-weibo"></use></svg>
            </span> &nbsp; &nbsp;
                <span class="post__share-icon" data-type="twitter">
                <svg><use xlink:href="#icon-twitter"></use></svg>
            </span> &nbsp; &nbsp;
                <span class="post__share-icon" data-type="qqz">
                <svg><use xlink:href="#icon-qqz"></use></svg>
            </span>
                <span class="article__code"
                      data-title="${article.articleTitle}"
                      data-blogtitle="${blogTitle}"
                      data-url="${servePath}${article.articlePermalink}"
                      data-avatar="${article.authorThumbnailURL}"></span>
            </div>
        </div>
        <div class="main">
        <#include "bottom.ftl">
        </div>
        <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
            <#include "../../common-template/toc.ftl"/>
        </#if>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<@comment_script oId=article.oId>
    Skin.initArticle()
    Skin.initComment = function (articleOId, articleTags) {
        page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
    <#if 0 != randomArticlesDisplayCount>
        page.loadRandomArticles("<div class='module__title'><span>${randomArticlesLabel}</span></div>");
    </#if>
    <#if 0 != externalRelevantArticlesDisplayCount>
        page.loadExternalRelevantArticles(articleTags, "<div class='module__title'><span>${externalRelevantArticlesLabel}</span></div>");
    </#if>
    <#if 0 != relevantArticlesDisplayCount>
        page.loadRelevantArticles(articleOId, '<div class="module__title"><span>${relevantArticlesLabel}</span></div>');
    </#if>
    }
    Skin.initComment('${article.oId}', "<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>")
</@comment_script>
<#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
