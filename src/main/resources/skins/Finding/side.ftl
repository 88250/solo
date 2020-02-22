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
<#if !staticSite>
    <div class="fn-clear">
    <span class="fn-right">
        <#if isLoggedIn>
            <a href="${servePath}/admin-index.do#main" title="${adminLabel}" class="icon-setting"></a>
        &nbsp;
        <a title="${logoutLabel}" class="icon-logout" href="${logoutURL}"></a>
        <#else>
            <a href="${servePath}/start" title="${startToUseLabel}" class="icon-login"></a>
        </#if>
    </span>
    </div>
</#if>
<#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
    <#include "../../common-template/toc.ftl"/>
<#else>
    <ul>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"><#if page.pageIcon != ''><img
                        class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}</a>
            </li>
        </#list>
        <li>
            <a href="${servePath}/categories.html">${categoryLabel}</a>
        </li>
        <li>
            <a href="${servePath}/tags.html">${allTagsLabel}</a>
        </li>
        <li>
            <a href="${servePath}/archives.html">${archiveLabel}</a>
        </li>
        <li>
            <a href="${servePath}/links.html">${linkLabel}</a>
        </li>
        <li>
            <a rel="alternate" href="${servePath}/rss.xml">${subscribeLabel}</a>
        </li>
        <#if !staticSite>
            <li>
                <a href="${servePath}/search?keyword=">Search</a>
            </li>
        </#if>
    </ul>
</#if>
<div class="count">
    <div class="fn-clear">
        <#include "../../common-template/macro-user_site.ftl"/>
        <@userSite dir=""/>
    </div>
    <span>
        ${viewCount1Label}
        <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span>
    </span> &nbsp; &nbsp;
    <span>
        ${articleCount1Label}
        ${statistic.statisticPublishedBlogArticleCount}
    </span><br/>
    <#if !staticSite>
    <span>
        ${onlineVisitor1Label} ${onlineVisitorCnt}
    </span>
    </#if>
</div>
