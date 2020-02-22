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
<div class="header">
    <div class="wrapper">
        <div class="left">
            <h1>
                <a class="title" href="${servePath}">
                    ${blogTitle}
                </a>
            </h1>
            <span class="sub-title">${blogSubtitle}</span>
        </div>
        <#if !staticSite>
            <form class="right" action="${servePath}/search">
                <input id="search" type="text" name="keyword"/>
                <input type="submit" value="" class="none"/>
            </form>
        </#if>
        <div class="clear"></div>
    </div>
</div>
<div class="nav">
    <div class="wrapper">
        <ul>
            <li>
                <a rel="nofollow" href="${servePath}/">${indexLabel}</a>
            </li>
            <#list pageNavigations as page>
                <li>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"><#if page.pageIcon != ''><img
                            class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}</a>
                </li>
            </#list>
            <li>
                <a href="${servePath}/tags.html">${allTagsLabel}</a>
            </li>
            <li>
                <a rel="alternate" href="${servePath}/rss.xml">RSS<img src="${staticServePath}/images/feed.png"
                                                                       alt="RSS"/></a>
            </li>
        </ul>
        <div class="right">
            <span class="translate-ico" onclick="goTranslate()"></span>
            <div class="right">
                ${viewCount1Label}
                <span class="tip">
                    <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span>
                </span>
                &nbsp;&nbsp;
                ${articleCount1Label}
                <span class="tip">
                    ${statistic.statisticPublishedBlogArticleCount}
                </span>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>
