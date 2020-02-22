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
<aside>
    <#if !staticSite>
        <nav>
            <h4 class="h4">Search</h4>
            <form action="${servePath}/search">
                <input style="width: 194px" id="search" type="text" name="keyword"/>
                <input type="submit" value="" class="none"/>
            </form>
        </nav>
    </#if>
    <nav>
        <h4 class="h4">Navigation</h4>
        <ul>
            <li>
                <a rel="nofollow" class="home" href="${servePath}">${indexLabel}</a>
            </li>
            <#list pageNavigations as page>
                <li>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">
                        <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                    </a>
                </li>
            </#list>
            <li>
                <a href="${servePath}/tags.html">${allTagsLabel}</a>
            </li>
            <li>
                <a rel="alternate" href="${servePath}/rss.xml">
                    RSS
                    <img src="${staticServePath}/images/feed.png" alt="RSS"/>
                </a>
            </li>
            <li>
                <a class="lastNavi" href="javascript:void(0);"></a>
            </li>
        </ul>
    </nav>
    <#if "" != noticeBoard>
        <h4 class="h4">${noticeBoardLabel}</h4>
        <div id="c">
            <p>
                ${noticeBoard}
            </p>
        </div>
    </#if>

    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <#include "../../common-template/toc.ftl"/>
    <#else>
        <#if 0 != mostUsedCategories?size>
            <h4 class="h4">${categoryLabel}</h4>
            <ul>
                <#list mostUsedCategories as category>
                    <li>
                        <a href="${servePath}/category/${category.categoryURI}"
                           title="${category.categoryTitle} (${category.categoryTagCnt})">
                            ${category.categoryTitle}</a>(${category.categoryTagCnt})
                    </li>
                </#list>
            </ul>
        </#if>

        <#if 0 != mostUsedTags?size>
            <h4 class="h4">${tagsLabel}</h4>
            <ul class="navi-tags">
                <#list mostUsedTags as tag>
                    <li>
                        <a rel="tag" title="${tag.tagTitle}(${tag.tagPublishedRefCount})"
                           href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                            <sup>[${tag.tagPublishedRefCount}]</sup>
                            ${tag.tagTitle}</a>
                    </li>
                </#list>
            </ul>
        </#if>
        <#if 0 != links?size>
            <h4 class="h4">${linkLabel}</h4>
            <ul id="sideLink" class="navi-tags">
                <#list links as link>
                    <li>
                        <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                            <img alt="${link.linkTitle}"
                                 src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>"
                                 width="16" height="16"/></a>
                        <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                            ${link.linkTitle}
                        </a>
                    </li>
                </#list>
            </ul>
        </#if>
        <#if 0 != archiveDates?size>
            <h4 class="h4"><a href="${servePath}/archives.html">${archiveLabel}</a></h4>
            <ul>
                <#list archiveDates as archiveDate>
                    <#if archiveDate_index < 10>
                        <li>
                            <#if "en" == localeString?substring(0, 2)>
                                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                                   title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                                    ${archiveDate.monthName} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount})
                            <#else>
                                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount})
                            </#if>
                        </li>
                    </#if>
                </#list>
            </ul>
        </#if>
    </#if>
</aside>
