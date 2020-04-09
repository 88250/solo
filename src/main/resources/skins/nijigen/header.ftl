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
<div class="bg"></div>
<div class="bg bg--1"></div>
<div class="bg bg--2"></div>
<div class="bg bg--3"></div>
<div class="bg bg--4"></div>
<div class="bg bg--5"></div>
<header class="header">
    <div class="header__wrap">
        <a href="${servePath}" rel="start" class="header__logo">
            <i class="icon__home"></i> ${blogTitle}
        </a>

        <nav class="mobile__hidden header__nav">
            <#list pageNavigations as page>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                    <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                </a>
            </#list>
            <a href="${servePath}/tags.html" rel="section">
                <i class="icon__tags"></i> ${allTagsLabel}
            </a>
            <a href="${servePath}/archives.html">
                <i class="icon__inbox"></i> ${archiveLabel}
            </a>
            <a rel="archive" href="${servePath}/links.html">
                <i class="icon__link"></i> ${linkLabel}
            </a>
            <a rel="alternate" href="${servePath}/rss.xml" rel="section">
                <i class="icon__rss"></i> RSS
            </a>
        </nav>

        <#if !staticSite>
            <div class="header__login">
                <#if isLoggedIn>
                    <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                        <i class="icon__setting"></i> ${adminLabel}
                    </a>
                    <a href="${logoutURL}">
                        <i class="icon__logout"></i> ${logoutLabel}
                    </a>
                <#else>
                    <a href="${servePath}/start">
                        <i class="icon__login"></i> ${startToUseLabel}
                    </a>
                </#if>
            </div>
        </#if>
    </div>
</header>
<div class="header__m fn__none">
    <i class="icon__list fn__none" onclick="$(this).next().slideToggle()"></i>
    <main class="module__list fn__none"></main>
    <i class="icon__more" onclick="$(this).next().slideToggle()"></i>
    <main class="module__list">
        <ul>
            <#if !staticSite>
                <#if isLoggedIn>
                    <li>
                        <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                            <i class="icon__setting"></i> ${adminLabel}
                        </a>
                    </li>
                    <li>
                        <a href="${logoutURL}">
                            <i class="icon__logout"></i> ${logoutLabel}
                        </a>
                    </li>
                <#else>
                    <li>
                        <a href="${servePath}/start">
                            <i class="icon__login"></i> ${startToUseLabel}
                        </a>
                    </li>
                </#if>
            </#if>
            <#list pageNavigations as page>
                <li>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                        <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                    </a>
                </li>
            </#list>
            <li>
                <a href="${servePath}/tags.html" rel="section">
                    <i class="icon__tags"></i> ${allTagsLabel}
                </a>
            </li>
            <li>
                <a href="${servePath}/archives.html">
                    <i class="icon__inbox"></i> ${archiveLabel}
                </a>
            </li>
            <li>
                <a rel="archive" href="${servePath}/links.html">
                    <i class="icon__link"></i> ${linkLabel}
                </a>
            </li>
            <li>
                <a rel="alternate" href="${servePath}/rss.xml" rel="section">
                    <i class="icon__rss"></i> RSS
                </a>
            </li>
        </ul>
    </main>
</div>
