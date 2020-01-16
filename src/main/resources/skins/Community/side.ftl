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
<div class="footer-secondary">
    <div class="content">

        <h4>
            ${siteLinkLabel}
        </h4>
        <div class="arrow-right"></div>
        <div class="notice">
            <#include "../../common-template/macro-user_site.ftl">
            <@userSite dir="n"/>
        </div>
        <div class="clear"></div>
        <div class="hr"></div>

        <#if "" != noticeBoard>
        <h4>
            ${noticeBoardLabel}
        </h4>
        <div class="arrow-right"></div>
        <div class="notice">
            ${noticeBoard}
        </div>
        <div class="clear"></div>
        <div class="hr"></div>
        </#if>
    </div>
</div>
<div class="footer-widgets">
    <div class="content">
        <#if 0 != mostUsedCategories?size>
            <div class="left footer-block">
                <h4><span class="left">${categoryLabel}</span></h4>
                <span class="clear"></span>
                <ul>
                    <#list mostUsedCategories as category>
                        <li class="mostUsedTags">
                            <a href="${servePath}/category/${category.categoryURI}">
                                ${category.categoryTitle}(${category.categoryTagCnt})</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </#if>

        <#if 0 != mostUsedTags?size>
        <div class="left footer-block">
            <h4><span class="left">${tagsLabel}</span></h4>
            <span class="clear"></span>
            <ul>
                <#list mostUsedTags as tag>
                <li class="mostUsedTags">
                    <a rel="tag" title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})
                    </a>
                </li>
                </#list>
            </ul>
        </div>
        </#if>
        <#if 0 != links?size>
        <div class="left footer-block">
            <h4><span class="left">${linkLabel}</span></h4>
            <span class="clear"></span>
            <ul id="sideLink">
                <#list links as link>
                <li class="mostUsedTags">
                    <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        ${link.linkTitle}
                    </a>
                    <img onclick="window.location='${link.linkAddress}'"
                         alt="${link.linkTitle}"
                         src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" width="16" height="16" />
                </li>
                </#list>
            </ul>
        </div>
        </#if>
        <#if 0 != archiveDates?size>
        <div class="left footer-block" style="margin-right: 0px;">
            <h4><span class="left">${archiveLabel}</span></h4>
            <span class="clear"></span>
            <ul>
                <#list archiveDates as archiveDate>
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
                </#list>
            </ul>
        </div>
        </#if>
        <div class="clear"></div>
    </div>
</div>
