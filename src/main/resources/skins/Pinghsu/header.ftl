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
<header class="header">
    <div class="wrapper">
        <a href="${servePath}" rel="start" class="header__logo">
            <img src="${adminUser.userAvatar}" alt="${blogTitle}"/>
            ${blogTitle}
        </a>

        <nav class="header__nav mobile__none">
            <a href="${servePath}/tags.html" rel="section">
                Tags
            </a>
            <a href="${servePath}/archives.html">
                Archives
            </a>
            <a rel="archive" href="${servePath}/links.html">
                Links
            </a>
            <#if !staticSite>
                <a href="${servePath}/search" class="search">
                    <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 28 28">
                        <path fill="#444"
                              d="M19.385 11.846c0-4.156-3.382-7.538-7.538-7.538s-7.538 3.382-7.538 7.538 3.382 7.538 7.538 7.538 7.538-3.382 7.538-7.538zM28 25.846c0 1.178-0.976 2.154-2.154 2.154-0.572 0-1.127-0.236-1.514-0.639l-5.772-5.755c-1.969 1.363-4.325 2.087-6.714 2.087-6.546 0-11.846-5.3-11.846-11.846s5.3-11.846 11.846-11.846 11.846 5.3 11.846 11.846c0 2.389-0.724 4.745-2.087 6.714l5.772 5.772c0.387 0.387 0.623 0.942 0.623 1.514z"></path>
                    </svg>
                </a>
            </#if>
        </nav>

        <div class="header__bar fn__none" onclick="$(this).next().slideToggle()">
            <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20">
                <path fill="#444" d="M0 3h20v2h-20v-2zM0 9h20v2h-20v-2zM0 15h20v2h-20v-2z"></path>
            </svg>
        </div>
        <main class="header__menu fn__none">
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
                            ${page.pageTitle}
                        </a>
                    </li>
                </#list>
                <li>
                    <a href="${servePath}/tags.html" rel="section">
                        Tags
                    </a>
                </li>
                <li>
                    <a href="${servePath}/archives.html">
                        Archives
                    </a>
                </li>
                <li>
                    <a rel="archive" href="${servePath}/links.html">
                        Links
                    </a>
                </li>
                <#if !staticSite>
                    <li>
                        <a href="${servePath}/search">
                            Search
                        </a>
                    </li>
                </#if>
                <li>
                    <a rel="alternate" href="${servePath}/rss.xml" rel="section">
                        RSS
                    </a>
                </li>
            </ul>
        </main>
    </div>
</header>
