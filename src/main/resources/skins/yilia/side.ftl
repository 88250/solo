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
