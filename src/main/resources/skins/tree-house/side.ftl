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
<div id="sideNavi">
    <div id="statistic">
        <div>
            ${viewCount1Label}
            <span class='error-msg'>
                <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span>
            </span>
        </div>
        <div>
            ${articleCount1Label}
            <span class='error-msg'>
                ${statistic.statisticPublishedBlogArticleCount}
            </span>
        </div>
    </div>
    <#if "" != noticeBoard>
    <div class="block notice">
        <h3>${noticeBoardLabel}</h3>
        <div>${noticeBoard}</div>
    </div>
    <div class="line"></div>
    </#if>

    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
         <div class="block">
             <h3>${tocLabel}</h3>
             <#include "../../common-template/toc.ftl"/>
         </div>
        <div class="line"></div>
    </#if>

    <#if 0 != mostUsedCategories?size>
        <div class="block">
            <h3 class="most-category">${categoryLabel}</h3>
            <ul>
                <#list mostUsedCategories as category>
                    <li>
                        <a href="${servePath}/category/${category.categoryURI}">
                            ${category.categoryTitle} (${category.categoryTagCnt})</a>
                    </li>
                </#list>
            </ul>
            <div class='clear'></div>
        </div>
        <div class="line"></div>
    </#if>

    <#if 0 != mostUsedTags?size>
    <div class="block">
        <h3 class="most-tag">${tagsLabel}</h3>
        <ul>
            <#list mostUsedTags as tag>
            <li>
                <a rel="tag" title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                    ${tag.tagTitle}(${tag.tagPublishedRefCount})
                </a>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    </#if>
    <#if 0 != links?size>
    <div class="block popTags">
        <h3 class="links">${linkLabel}</h3>
        <ul id="sideLink">
            <#list links as link>
            <li>
                <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                    ${link.linkTitle}
                </a>
                <img onclick="window.location='${link.linkAddress}'"
                     alt="${link.linkTitle}"
                     src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" width="16" height="16" />
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    </#if>
    <#if 0 != archiveDates?size>
    <div class="block">
        <h3 class="fn__clear">
            ${archiveLabel}
            <a class="fn__right" href="${servePath}/archives.html">More</a>
        </h3>
        <ul>
            <#list archiveDates as archiveDate>
            <#if archiveDate_index < 10>
            <li>
                <#if "en" == localeString?substring(0, 2)>
                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                <#else>
                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                </#if>
            </li>
            </#if>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    </#if>
    <#if 1 != users?size>
    <div class="block">
        <h3>${authorLabel}</h3>
        <ul>
            <#list users as user>
            <li>
                <a class="star-icon" href="${servePath}/authors/${user.oId}">
                    ${user.userName}
                </a>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    </#if>
</div>
