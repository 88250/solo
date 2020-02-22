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
<#include "../../common-template/macro-user_site.ftl">
<aside>
    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <#include "../../common-template/toc.ftl"/>
    <#else>
    <section>
        <#if noticeBoard??>
        <div class="ad vditor-reset">
            ${noticeBoard}
        </div>
        </#if>

        <#if 0 != mostUsedCategories?size>
        <div class="module">
            <header><h2>${categoryLabel}</h2></header>
            <main>
                <#list mostUsedCategories as category>
                    <a href="${servePath}/category/${category.categoryURI}"
                       aria-label="${category.categoryTagCnt} ${cntLabel}${tagsLabel}"
                       class="tag vditor-tooltipped vditor-tooltipped__n">
                        ${category.categoryTitle}</a>
                </#list>
            </main>
        </div>
        </#if>

        <#if 0 != mostUsedTags?size>
        <div class="module">
            <header><h2>${tagsLabel}</h2></header>
            <main>
                <#list mostUsedTags as tag>
                    <a rel="tag"
                       href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}"
                       class="tag vditor-tooltipped vditor-tooltipped__n"
                       aria-label="${tag.tagPublishedRefCount} ${countLabel}${articleLabel}">
                        ${tag.tagTitle}</a>
                </#list>
            </main>
        </div>
        </#if>

        <div class="module meta">
            <header>
                <h2 class="ft__center">
                    <@userSite dir="nw"></@userSite>
                </h2>
            </header>
            <main class="fn__clear">
                <img src="${adminUser.userAvatar}" alt="${adminUser.userName}" aria-label="${adminUser.userName}"/>
                <div class="fn-right">
                    <a href="${servePath}/archives.html">
                        ${statistic.statisticPublishedBlogArticleCount}
                        <span class="ft-gray">${articleLabel}</span></a><br/>
                    <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span> <span class="ft-gray">${viewLabel}</span>
                    <#if !staticSite><br/>${onlineVisitorCnt} <span class="ft-gray">${onlineVisitorLabel}</span></#if>
                </div>
            </main>
        </div>

        <#if 0 != links?size>
            <div class="module">
                <header><h2>${linkLabel}</h2></header>
                <main>
                    <#list links as link>
                        <a rel="friend"
                           target="_blank"
                           href="${link.linkAddress}"
                           class="tag vditor-tooltipped vditor-tooltipped__n"
                           aria-label="${link.linkDescription}">
                            ${link.linkTitle}</a>
                    </#list>
                </main>
            </div>
        </#if>

        <#if 0 != archiveDates?size>
            <div class="module">
                <header><h2>${archiveLabel}</h2></header>
                <main class="list">
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
                        <#if archiveDates?size &gt; 10>
                            <li>
                                <a href="${servePath}/archives.html">...</a>
                            </li>
                        </#if>
                    </ul>
                </main>
            </div>
        </#if>

    </section>
    </#if>
</aside>
