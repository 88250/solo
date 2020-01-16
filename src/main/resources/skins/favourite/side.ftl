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
<div id="sideNavi" class="side-navi">
    <div class="rings"></div>
    <div class="null"></div>
    <#if "" != noticeBoard>
    <div class="item">
        <div class="antefatto">
            <h4>${noticeBoardLabel}</h4>
        </div>
        <div class="marginLeft12 marginTop12">
            ${noticeBoard}
            <#include "../../common-template/macro-user_site.ftl"/>
            <div class="marginTop12">
            <@userSite dir="nw"></@userSite>
            </div>
        </div>
    </div>
    <div class="line"></div>
    </#if>

    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <div class="item navi-comments">
            <div class="ads">
                <h4>${tocLabel}</h4>
            </div>
            <#include "../../common-template/toc.ftl"/>
        </div>
    <#else>
        <#if 0 != mostUsedCategories?size>
            <div class="item">
                <div class="categorie">
                    <h4>${categoryLabel}</h4>
                </div>
                <ul>
                    <#list mostUsedCategories as category>
                        <li>
                            <a href="${servePath}/category/${category.categoryURI}"
                               title="${category.categoryTitle} (${category.categoryTagCnt})">
                                ${category.categoryTitle}</a>
                        </li>
                    </#list>
                </ul>
            </div>
            <div class="line"></div>
        </#if>

        <#if 0 != mostUsedTags?size>
        <div class="item">
            <div class="tags">
                <h4>${tagsLabel}</h4>
            </div>
            <ul>
                <#list mostUsedTags as tag>
                <li>
                    <a rel="alternate" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}" class="no-underline">
                    </a>
                    <a rel="tag" title="${tag.tagTitle}" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})
                    </a>
                </li>
                </#list>
            </ul>
        </div>
        <div class="line"></div>
        </#if>


        <#if 0 != links?size>
        <div class="item">
            <div class="blog">
                <h4>${linkLabel}</h4>
            </div>
            <ul id="sideLink" class="navi-tags">
                <#list links as link>
                <li>
                     <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        <img alt="${link.linkTitle}"
                             src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" width="16" height="16" /></a>
                    <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
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
            <div class="archivio">
                <h4><a href="${servePath}/archives.html">${archiveLabel}</a></h4>
            </div>
            <ul id="save">
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
        <div class="line"></div>
        </#if>
        <#if 1 != users?size>
        <div class="item">
            <div class="side-author">
                <h4>${authorLabel}</h4>
            </div>
            <ul id="sideAuthor">
                <#list users as user>
                <li>
                    <a href="${servePath}/authors/${user.oId}" title="${user.userName}">
                        ${user.userName}
                    </a>
                </li>
                </#list>
            </ul>
        </div>
        </#if>
    </#if>
    <div class="rings" style="bottom: 0px;"></div>
</div>
