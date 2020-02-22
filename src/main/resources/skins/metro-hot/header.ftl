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
<div class="fn-clear header">
    <h1 class="fn-left">
        <a class="title" href="javascript: void(0)">
            ${blogTitle}
            <span data-ico="&#xe0f3;"></span>
        </a>
    </h1>
    <ul class="navigation">
        <li>
            <a rel="nofollow" href="${servePath}/">${indexLabel}</a>
        </li>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"><#if page.pageIcon != ''><img
                        class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}</a>
            </li>
        </#list>
        <#if commentable && !staticSite>
            <li>
                <a href="${servePath}/dynamic.html">${dynamicLabel}</a>
            </li>
        </#if>
        <li>
            <a href="${servePath}/categories.html">${categoryLabel}</a>
        </li>
        <li>
            <a href="${servePath}/tags.html">${allTagsLabel}</a>
        </li>
        <li>
            <a href="${servePath}/archives.html">${archiveLabel}</a>
        </li>
        <li class="last">
            <a href="${servePath}/links.html">${linkLabel}</a>
        </li>
    </ul>
    <#if !staticSite>
        <div class="fn-right top-info">
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main" title="${adminLabel}" data-ico="&#x0070;"></a>
                <hr>
                <a href="${logoutURL}" title="${logoutLabel}" data-ico="&#xe040;"></a>
            <#else>
                <a href="${servePath}/start" title="${startToUseLabel}" data-ico="&#xe03f;"></a>
            </#if>
        </div>
    </#if>
</div>
