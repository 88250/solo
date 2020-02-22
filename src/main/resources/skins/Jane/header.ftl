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
    <div class="fn__flex-1">
        <a href="${servePath}" rel="start" class="vditor-tooltipped__w vditor-tooltipped" aria-label="${blogTitle}">
            <i class="icon__home"></i>
        </a>

        <#list pageNavigations as page>
            <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"
               class="vditor-tooltipped__w vditor-tooltipped" rel="section" aria-label="${page.pageTitle}">
                <#if page.pageIcon != ''><img src="${page.pageIcon}" alt="${page.pageTitle}"><#else>
                    <i class="icon__page"></i>
                </#if>
            </a>
        </#list>

        <a href="${servePath}/categories.html" rel="section" aria-label="${categoryLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__category"></i>
        </a>
        <a href="${servePath}/tags.html" rel="section" aria-label="${allTagsLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__tags"></i>
        </a>
        <a href="${servePath}/archives.html" aria-label="${archiveLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__inbox"></i>
        </a>
        <a rel="archive" href="${servePath}/links.html" aria-label="${linkLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__link"></i>
        </a>
        <#if !staticSite>
            <a href="${servePath}/search" class="vditor-tooltipped__w vditor-tooltipped" aria-label="${searchLabel}">
                <i class="icon__search"></i>
            </a>
        </#if>
        <a rel="alternate" href="${servePath}/rss.xml" rel="section" aria-label="RSS"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__rss"></i>
        </a>
    </div>

    <#if !staticSite>
        <div>
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main"
                   aria-label="${adminLabel}" class="vditor-tooltipped vditor-tooltipped__w">
                    <i class="icon__setting"></i>
                </a>
                <a href="${logoutURL}"
                   aria-label="${logoutLabel}" class="vditor-tooltipped vditor-tooltipped__w">
                    <i class="icon__logout"></i>
                </a>
            <#else>
                <a href="${servePath}/start"
                   aria-label="${startToUseLabel}" class="vditor-tooltipped vditor-tooltipped__w">
                    <i class="icon__login"></i>
                </a>
            </#if>
            <span onclick="Util.goTop()"
                  aria-label="${putTopLabel}" class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__up"></i>
        </span>
        </div>
    </#if>
</header>
