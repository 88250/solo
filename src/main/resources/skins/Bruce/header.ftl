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
${topBarReplacement}
<div class="header">

    <div class="container">
        <div class="col-sm-2"></div>
        <nav>
            <a class="nav-item" href="${servePath}">${indexLabel}</a>

            <#list pageNavigations as page>
                <a class="nav-item" href="${page.pagePermalink}"
                   target="${page.pageOpenTarget}"><#if page.pageIcon != ''>
				   <img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                </a>
            </#list>

            <a class="nav-item" href="${servePath}/links.html">${friendLinkLabel}</a>
            <a class="nav-item" href="${servePath}/tags.html">${tagLabel}</a>
            <a class="nav-item" href="${servePath}/archives.html">${archiveLabel}</a>
            <#if !staticSite>
                <a href="${servePath}/search?keyword=">Search</a>
            </#if>
        </nav>
        <div class="col-sm-2"></div>
    </div>
</div>
