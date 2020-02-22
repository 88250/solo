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
