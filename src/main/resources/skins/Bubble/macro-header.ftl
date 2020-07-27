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
<#macro header type>
    <progress class="fn__progress"></progress>
    <header class="header header--${type}">
        <div class="wrapper header__title">
            <h1 class="header__h1 fn__flex-inline">
                <a href="${servePath}" rel="start">${blogTitle}</a>
            </h1>
            <h2 class="header__desc header__desc--title">${blogSubtitle}</h2>
            <div class="header__desc">
                ${noticeBoard}
            </div>
            <svg class="header__down" id="headerDown" viewBox="0 0 32 32" width="100%" height="100%">
                <path d="M15.992 25.304c-0 0-0 0-0.001 0-0.516 0-0.981-0.216-1.31-0.563l-0.001-0.001-14.187-14.996c-0.306-0.323-0.494-0.76-0.494-1.241 0-0.998 0.809-1.807 1.807-1.807 0.517 0 0.983 0.217 1.313 0.565l0.001 0.001 12.875 13.612 12.886-13.612c0.331-0.348 0.797-0.565 1.314-0.565 0.481 0 0.918 0.187 1.242 0.493l-0.001-0.001c0.723 0.687 0.755 1.832 0.072 2.555l-14.201 14.996c-0.33 0.348-0.795 0.564-1.311 0.564-0.001 0-0.003 0-0.004 0h0z"></path>
            </svg>
        </div>
    </header>
    <div class="side__menu">
        <svg viewBox="0 0 32 32" width="100%" height="100%">
            <path d="M30 18h-28c-1.1 0-2-0.9-2-2s0.9-2 2-2h28c1.1 0 2 0.9 2 2s-0.9 2-2 2zM30 6.25h-28c-1.1 0-2-0.9-2-2s0.9-2 2-2h28c1.1 0 2 0.9 2 2s-0.9 2-2 2zM2 25.75h28c1.1 0 2 0.9 2 2s-0.9 2-2 2h-28c-1.1 0-2-0.9-2-2s0.9-2 2-2z"></path>
        </svg>
        <span>
            &nbsp; menu
        </span>
    </div>
    <div class="side__main">
        <div class="side__bg"></div>
        <div class="side__panel">
            <svg class="side__close ft__a" version="1.1" xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                 viewBox="0 0 20 20">
                <path d="M18.362 19.324c-0.902 0.902-2.363 0.902-3.263 0l-5.098-5.827-5.098 5.825c-0.902 0.902-2.363 0.902-3.263 0-0.902-0.902-0.902-2.363 0-3.263l5.304-6.057-5.306-6.061c-0.902-0.902-0.902-2.361 0-3.263s2.361-0.902 3.263 0l5.1 5.829 5.098-5.829c0.902-0.902 2.361-0.902 3.263 0s0.902 2.363 0 3.263l-5.304 6.061 5.304 6.057c0.902 0.902 0.902 2.363 0 3.265z"></path>
            </svg>

            <div class="side__header">
                <a href="${servePath}" rel="start"><img class="side__logo" alt="${blogTitle}" src="${adminUser.userAvatar}"></a>

                <div class="side__meta">
                    ${statistic.statisticPublishedBlogArticleCount} ${articleLabel} <br>
                    <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span> ${viewLabel}
                    <#if !staticSite><br>${onlineVisitorCnt} ${onlineVisitorLabel}</#if>
                </div>
            </div>

            <div class="side__title">
                <span>ღゝ◡╹)ノ❤️</span>
            </div>
            <ul class="side__nav">
                <li>
                    <a href="${servePath}" rel="start">
                        <#if type == 'article'>
                            ${blogTitle}
                        <#else>
                            ${indexLabel}
                        </#if>
                    </a>
                </li>
                <#list pageNavigations as page>
                    <li>
                        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"><#if page.pageIcon != ''><img
                                class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}</a>
                    </li>
                </#list>
                <li>
                    <a href="${servePath}/categories.html">${categoryLabel}</a>
                </li>
                <li>
                    <a href="${servePath}/tags.html">${allTagsLabel}</a>
                </li>
                <li>
                    <a href="${servePath}/archives.html">${archiveLabel}</a>
                </li>
                <li>
                    <a href="${servePath}/links.html">${linkLabel}</a>
                </li>
                <li>
                    <a rel="alternate" href="${servePath}/rss.xml">RSS</a>
                </li>
                <#if !staticSite>
                    <li>
                        <a href="${servePath}/search?keyword=">Search</a>
                    </li>
                </#if>
                <#if !staticSite>
                    <#if isLoggedIn>
                        <li>
                            <a href="${servePath}/admin-index.do#main">
                                ${adminLabel}
                            </a>
                        </li>
                        <li>
                            <a href="${logoutURL}">
                                ${logoutLabel}
                            </a>
                        </li>
                    <#else>
                        <li>
                            <a rel="alternate" href="${servePath}/start">
                                ${startToUseLabel}
                            </a>
                        </li>
                    </#if>
                </#if>
            </ul>
        </div>
    </div>
</#macro>
