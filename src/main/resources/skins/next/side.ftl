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
<div class="sidebar-toggle">
    <span class="sidebar-toggle-line sidebar-toggle-line-first"></span>
    <span class="sidebar-toggle-line sidebar-toggle-line-middle"></span>
    <span class="sidebar-toggle-line sidebar-toggle-line-last"></span>
</div>

<aside class="sidebar">
    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <#include "../../common-template/toc.ftl"/>
    <#else>
        <section>
            <img class="site-author-image" src="${adminUser.userAvatar}" alt="${userName}"title="${userName}"/>
            <p class="site-author-name">${userName}</p>
            <#if "" != noticeBoard>
                <p class="site-description motion-element">${blogSubtitle}</p>
            </#if>
            <nav>
                <div class="site-state-item">
                    <a href="${servePath}/archives.html">
                        <span class="site-state-item-count">${statistic.statisticPublishedBlogArticleCount}</span>
                        <span class="site-state-item-name">${articleLabel}</span>
                    </a>
                </div>

                <div class="site-state-item site-state-categories">
                    <span class="site-state-item-count"><span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span></span>
                    <span class="site-state-item-name">${viewLabel}</span>
                </div>
            </nav>

            <div class="feed-link">
                <a href="${servePath}/rss.xml" rel="alternate">
                    RSS
                </a>
                <a href="${servePath}/tags.html" rel="section">
                    ${allTagsLabel}
                </a>
                <a href="${servePath}/archives.html">
                    ${archiveLabel}
                </a>
            </div>

            <#if !staticSite>
                <div class="links-of-author">
                    <#if isLoggedIn>
                        <span class="links-of-author-item">
                <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                    <i class="icon-setting"></i> ${adminLabel}
                </a>
            </span>

                        <span class="links-of-author-item">
                <a href="${logoutURL}">
                    <i class="icon-logout"></i> ${logoutLabel}
                </a>
            </span>
                    <#else>
                        <span class="links-of-author-item">
                <a href="${servePath}/start">
                    ${startToUseLabel}
                </a>
            </span>
                    </#if>
                </div>
            </#if>

            <#if noticeBoard??>
                <div class="links-of-author">
                    ${noticeBoard}
                </div>
            </#if>

            <#if 0 != links?size>
                <div class="links-of-author">
                    <p class="site-author-name">Links</p>
                    <#list links as link>
                        <span class="links-of-author-item">
                <a rel="friend" href="${link.linkAddress}"
                   title="${link.linkDescription}" target="_blank">
                    ${link.linkTitle}
                </a>
            </span>
                    </#list>
                </div>
            </#if>
        </section>
    </#if>
</aside>
