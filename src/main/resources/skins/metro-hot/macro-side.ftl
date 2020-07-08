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
<#macro side isArticle>
    <div class="side">
        <div class="fn-clear">
            <#if !isArticle>
                <#if !staticSite>
                    <form action="${servePath}/search">
                        <input placeholder="Search" id="search" type="text" name="keyword"/><span
                                onclick="$(this).parent().submit()" data-ico="&#x0067;"></span>
                        <input type="submit" value="" class="fn-none"/>
                    </form>
                </#if>

                <#if "" != noticeBoard>
                    <div class="notice-board side-tile">
                        <div class="title">
                            ${noticeBoard}
                        </div>
                    </div>
                </#if>

                <div class="online-count side-tile">
                    <span data-ico="&#xe037;"></span>
                    <div class="text">
                        ${viewCount1Label}
                        <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span><br/>
                        ${articleCount1Label}
                        ${statistic.statisticPublishedBlogArticleCount}<br/>
                        <#if !staticSite>
                        ${onlineVisitor1Label}${onlineVisitorCnt}
                        </#if>
                    </div>
                </div>
            </#if>

            <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
                <div class="vditor-reset" style="margin-top: 20px">
                    <#include "../../common-template/toc.ftl"/>
                </div>
            </#if>

            <a rel="alternate" href="${servePath}/rss.xml" class="atom side-tile">
                <span data-ico="&#xe135;"></span>
                <div class="title">
                    RSS
                </div>
            </a>

            <a href="javascript: MetroHot.goTranslate();" class="translate side-tile">
                <span data-ico="&#x0038;"></span>
                <div class="title">
                    ${translateLabel}
                </div>
            </a>

            <#if isArticle>
                <div class="side-tile article-relative">
                    <#include "../../common-template/share.ftl">
                </div>

                <#if relevantArticlesDisplayCount??>
                    <#if nextArticlePermalink??>
                        <a class="next side-tile" title="${nextArticleTitle}"
                           href="${servePath}${nextArticlePermalink}">
                            <span data-ico="&#xe107;"></span>
                            <div class="title">
                                ${nextArticleLabel}
                            </div>
                        </a>
                    <#else>
                        <div class="next-disabled side-tile">
                            <span data-ico="&#xe107;"></span>
                            <div class="title">
                                ${recentArticlesLabel}
                            </div>
                        </div>
                    </#if>

                    <#if previousArticlePermalink??>
                        <a class="prev side-tile" title="${previousArticleTitle}" rel="prev"
                           href="${servePath}${previousArticlePermalink}">
                            <span data-ico="&#xe106;"></span>
                            <div class="title">
                                ${previousArticleLabel}
                            </div>
                        </a>
                    <#else>
                        <div class="prev-disabled side-tile">
                            <span data-ico="&#xe106;"></span>
                            <div class="title">
                                ${lastArticleLabel}
                            </div>
                        </div>
                    </#if>

                    <#if 0 != relevantArticlesDisplayCount>
                        <div id="relevantArticles" class="side-tile article-relative">
                            <span data-ico="&#xe020;"></span>
                            <div class="title">
                                ${relevantArticlesLabel}
                            </div>
                            <div class="text">
                            </div>
                        </div>
                    </#if>

                    <#if 0 != randomArticlesDisplayCount>
                        <div id="randomArticles" class="side-tile article-relative">
                            <span data-ico="&#xe024;"></span>
                            <div class="title">
                                ${randomArticlesLabel}
                            </div>
                            <div class="text">
                            </div>
                        </div>
                    </#if>

                    <#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
                        <div id="externalRelevantArticles" class="side-tile article-relative">
                            <span data-ico="&#xe021;"></span>
                            <div class="title">
                                ${externalRelevantArticlesLabel}
                            </div>
                            <div class="text">
                            </div>
                        </div>
                    </#if>
                </#if>
            </#if>

            <div class="user side-tile">
            <span>
                <img src="${faviconURL}" alt="${blogTitle}"/>
            </span>
                <div class="text fn-clear">
                    <#include "../../common-template/macro-user_site.ftl"/>
                    <@userSite dir="n"/>
                </div>
            </div>

            <#if !staticSite>
                <#if isLoggedIn>
                    <a href="${servePath}/admin-index.do#main" class="settings side-tile">
                        <span data-ico="&#x0070;"></span>
                        <div class="title">
                            ${adminLabel}
                        </div>
                    </a>
                    <a class="logout side-tile" href="${logoutURL}">
                        <span data-ico="&#xe040;"></span>
                        <div class="title">
                            ${logoutLabel}
                        </div>
                    </a>
                <#else>
                    <a href="${servePath}/start" class="register side-tile">
                        <span data-ico="&#xe040;"></span>
                        <div class="title">
                            ${startToUseLabel}
                        </div>
                    </a>
                </#if>
            </#if>
        </div>
    </div>
</#macro>
