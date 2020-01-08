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
<div class="top">
    <div id="navigation">
        <a rel="nofollow" href="${servePath}" class="home">${homeLabel}</a>
        <a href="${servePath}/tags.html" class="about">${allTagsLabel}</a>
        <#list pageNavigations as page>
            <a href="${page.pagePermalink}" class="Guestbook" target="${page.pageOpenTarget}"
               class="${page.pageTitle}">${page.pageTitle}</a>
        </#list>
        <a rel="alternate" href="${servePath}/rss.xml" class="classifiche">RSS</a>
        <#if !staticSite>
            <a href="${servePath}/search?keyword=">Search</a>
        </#if>
    </div>
    <div class="thinks"></div>
</div>