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
<div class="header-navi right">
    <ul>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">
                    <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                </a>&nbsp;&nbsp;
            </li>
        </#list>
        <li>
            <a href="${servePath}/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
        </li>
        <li>
            <a rel="alternate" href="${servePath}/rss.xml">
                <img class="page-icon" src="${staticServePath}/images/feed.png" alt="RSS"/>RSS</a>
            </a> &nbsp;&nbsp;
        </li>
        <#if !staticSite>
            <li>
                <a href="${servePath}/search?keyword=">Search</a>
            </li>
        </#if>
    </ul>
</div>
<div class="header-title">
    <h1>
        <a href="${servePath}" id="logoTitle">
            ${blogTitle}
        </a>
    </h1>
    <div>${blogSubtitle}</div>
    <#include "../../common-template/macro-user_site.ftl"/>
    <div>
        <br>
        <@userSite dir="ne"/>
    </div>
</div>
