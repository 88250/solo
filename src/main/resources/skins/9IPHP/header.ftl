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
<header>
    <div class="banner">
        <div class="fn-clear wrapper">
            <h1 class="fn-inline">
                <a href="${servePath}" rel="start">
                    ${blogTitle}
                </a>
            </h1>
            <small> &nbsp; ${blogSubtitle}</small>
            <#if !staticSite>
                <div class="fn-right">
                    <#if isLoggedIn>
                        <a class="fn__flex-inline" href="${servePath}/admin-index.do#main" title="${adminLabel}">
                            <i class="icon-setting"></i>&nbsp;${adminLabel}
                        </a>
                        <a class="fn__flex-inline" href="${logoutURL}">
                            <i class="icon-logout"></i>&nbsp;${logoutLabel}
                        </a>
                    <#else>
                        <a class="fn__flex-inline" href="${servePath}/start">
                            <i class="icon-login"></i>&nbsp;${startToUseLabel}
                        </a>
                    </#if>
                </div>
            </#if>
        </div>
    </div>

    <div class="navbar">
        <div class="fn-clear wrapper">
            <nav class="fn-left">
                <a href="${servePath}">
                    <i class="icon-home"></i>
                    ${indexLabel}
                </a>
                <#list pageNavigations as page>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                        <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                    </a>
                </#list>
                <a href="${servePath}/tags.html" rel="section">
                    <i class="icon-tags"></i> ${allTagsLabel}
                </a>
                <a href="${servePath}/archives.html">
                    <i class="icon-inbox"></i> ${archiveLabel}
                </a>
                <a rel="archive" href="${servePath}/links.html">
                    <i class="icon-link"></i> ${linkLabel}
                </a>
                <a rel="alternate" href="${servePath}/rss.xml" rel="section">
                    <i class="icon-rss"></i> RSS
                </a>
            </nav>
            <#if !staticSite>
            <div class="fn-right">
                <form class="form" action="${servePath}/search">
                    <input placeholder="${searchLabel}" id="search" type="text" name="keyword"/>
                    <button type="submit"><i class="icon-search"></i></button>
                </form>
            </div>
            </#if>
        </div>
    </div>
</header>
<div class="responsive fn-none">
    <i class="icon-list"></i>
    <ul class="list">
        <#if !staticSite>
            <#if isLoggedIn>
                <li>
                    <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                        <i class="icon-setting"></i> ${adminLabel}
                    </a>
                </li>
                <li>
                    <a href="${logoutURL}">
                        <i class="icon-logout"></i> ${logoutLabel}
                    </a>
                </li>
            <#else>
                <li>
                    <a href="${servePath}/start">
                        <i class="icon-login"></i> ${startToUseLabel}
                    </a>
                </li>
            </#if>
        </#if>
        <li>
            <a href="${servePath}">
                <i class="icon-home"></i>
                ${indexLabel}
            </a>
        </li>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                    <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                </a>
            </li>
        </#list>
        <li>
            <a href="${servePath}/tags.html" rel="section">
                <i class="icon-tags"></i> ${allTagsLabel}
            </a>
        </li>
        <li>
            <a href="${servePath}/archives.html">
                <i class="icon-inbox"></i> ${archiveLabel}
            </a>
        </li>
        <li>
            <a rel="archive" href="${servePath}/links.html">
                <i class="icon-link"></i> ${linkLabel}
            </a>
        </li>
        <li>
            <a rel="alternate" href="${servePath}/rss.xml" rel="section">
                <i class="icon-rss"></i> RSS
            </a>
        </li>
    </ul>
</div>
