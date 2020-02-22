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
<nav id="headerNav" class="header__nav">
    <div class="wrapper">
        <a href="${servePath}">
            <svg>
                <use xlink:href="#icon-home"></use>
            </svg>
            ${indexLabel}
        </a>
        <a href="${servePath}/tags.html" rel="section">
            <svg>
                <use xlink:href="#icon-tag"></use>
            </svg> ${allTagsLabel}
        </a>
        <a href="${servePath}/archives.html">
            <svg>
                <use xlink:href="#icon-bookmark"></use>
            </svg> ${archiveLabel}
        </a>

        <#list pageNavigations as page>
            <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                <#if page.pageIcon != ''><img src="${page.pageIcon}" alt="${page.pageTitle} "></#if> ${page.pageTitle}
            </a>
        </#list>

        <a rel="archive" href="${servePath}/links.html">
            <svg>
                <use xlink:href="#icon-link"></use>
            </svg> ${linkLabel}
        </a>

        <a rel="alternate" href="${servePath}/rss.xml" rel="section">
            <svg>
                <use xlink:href="#icon-feed"></use>
            </svg>
            RSS
        </a>
        <#if !staticSite>
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main">
                    <svg>
                        <use xlink:href="#icon-setting"></use>
                    </svg> ${adminLabel}
                </a>
                <a href="${logoutURL}">
                    <svg>
                        <use xlink:href="#icon-out"></use>
                    </svg>
                    ${logoutLabel}
                </a>
            <#else>
                <a rel="alternate" href="${servePath}/start" rel="section">
                    <svg>
                        <use xlink:href="#icon-enter"></use>
                    </svg>
                    ${startToUseLabel}
                </a>
            </#if>
        </#if>
    </div>
</nav>
