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
                                ${category.categoryTitle}(${category.categoryPublishedArticleCount})</a>
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
                         src="${link.linkIcon}" width="16" height="16" />
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
