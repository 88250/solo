<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

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
                            ${category.categoryTitle} (${category.categoryPublishedArticleCount})</a>
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
                     src="${link.linkIcon}" width="16" height="16" />
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
