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
<div class="side fn__flex-column">
    <div class="overlay">
        <a onclick="$('.side .toc').show()" href="javascript:void(0)" class="toc-btn">${tocLabel}</a>
    </div>
    <#include "../../common-template/macro-user_site.ftl"/>
    <div class="user__sites">
        <@userSite dir=""/>
    </div>
    <header class="content fn__flex-1">
        <a href="${servePath}">
            <img class="avatar" src="${adminUser.userAvatar}" title="${userName}" alt="${userName}"/>
        </a>
        <hgroup>
            <h1>
                <a href="${servePath}">${blogTitle}</a>
            </h1>
        </hgroup>
        <p class="subtitle">
            ${blogSubtitle}
        </p>
        <nav>
            <ul>
                <#list pageNavigations as page>
                    <li>
                        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"><#if page.pageIcon != ''><img
                                class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}</a>
                    </li>
                </#list>
                <li>
                    <a href="${servePath}/tags.html">${allTagsLabel}</a>
                    &nbsp; &nbsp;
                    <a href="${servePath}/archives.html">${archiveLabel}</a>
                </li>
                <#if !staticSite>
                    <li>
                        <a href="${servePath}/links.html">${linkLabel}</a>
                        &nbsp; &nbsp;
                        <a href="${servePath}/search?keyword=">
                            Search
                        </a>
                    </li>
                </#if>
            </ul>
        </nav>
    </header>
    <footer>
        <#if noticeBoard??>
            <div class="vditor-reset">${noticeBoard}</div>
        </#if>
        <#if !staticSite>
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main" title="${adminLabel}" class="icon-setting"></a>
                &nbsp; &nbsp;
                <a title="${logoutLabel}" class="icon-logout" href="${logoutURL}"></a>
            <#else>
                <a href="${servePath}/start" title="${startToUseLabel}" class="icon-login"></a>
            </#if> &nbsp; &nbsp;
        </#if>
        <a rel="alternate" href="${servePath}/rss.xml" title="${subscribeLabel}" class="icon-rss"></a>
    </footer>
    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <div class="toc">
            <a onclick="$('.side .toc').hide();" href="javascript:void(0)" class="close">X</a>
            <#include "../../common-template/toc.ftl"/>
        </div>
    </#if>
</div>
