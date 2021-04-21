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
<div id="sideNavi" class="side-navi">
    <div class="item">
        <#if "" != noticeBoard>
        <h4>${noticeBoardLabel}</h4>
        <div class="marginLeft12 marginTop12">
            ${noticeBoard}
        </div>
        </#if>
        <#include "../../common-template/macro-user_site.ftl"/>
        <div class="marginTop12 marginLeft12">
            <@userSite dir="nw"/>
        </div>
    </div>
    <div class="line"></div>
    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
    <div class="item">
        <h4>${tocLabel}</h4>
        <#include "../../common-template/toc.ftl"/>
    </div>
    <#else>

        <#if 0 != mostUsedCategories?size>
            <div class="item">
                <h4>${categoryLabel}</h4>
                <ul class="navi-tags">
                    <#list mostUsedCategories as category>
                        <li>
                            <a href="${servePath}/category/${category.categoryURI}">
                                ${category.categoryTitle}</a> (${category.categoryPublishedArticleCount})
                        </li>
                    </#list>
                </ul>
            </div>
            <div class="line"></div>
        </#if>


        <#if 0 != mostUsedTags?size>
        <div class="item">
            <h4>${tagsLabel}</h4>
            <ul class="navi-tags">
                <#list mostUsedTags as tag>
                <li>
                    <a rel="tag" title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}</a> (${tag.tagPublishedRefCount})
                </li>
                </#list>
            </ul>
        </div>
        <div class="line"></div>
        </#if>
        <#if 0 != links?size>
        <div class="item">
            <h4>${linkLabel}</h4>
            <ul id="sideLink" class="navi-tags">
                <#list links as link>
                <li>
                    <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        <img alt="${link.linkTitle}"
                             src="${link.linkIcon}" width="16" height="16" />
                        ${link.linkTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </div>
        <div class="line"></div>
        </#if>
        <#if 0 != archiveDates?size>
        <div class="item">
            <h4><a href="${servePath}/archives.html">${archiveLabel}</a></h4>
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
        </div>
        </#if>
    </#if>
</div>
