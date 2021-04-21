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
<div class="side">
    <div>
        <br>
        <#include "../../common-template/macro-user_site.ftl"/>
        <@userSite dir="nw"/>
        <#if "" != noticeBoard>
        <br><br>
        <div style="text-align: center">${noticeBoard}</div>
        </#if>
    </div>
     <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
     <div>
         <h4>${tocLabel}</h4>
           <#include "../../common-template/toc.ftl"/>
     </div>
     <#else>
         <#if 0 != mostUsedCategories?size>
        <div>
            <h4>${categoryLabel}</h4>
            <ul class="tags">
                <#list mostUsedCategories as category>
                    <li>
                        <sup>[${category.categoryPublishedArticleCount}]</sup>
                        <a class="tag" href="${servePath}/category/${category.categoryURI}">
                            ${category.categoryTitle}</a>
                    </li>
                </#list>
            </ul>
            <div class="clear"></div>
        </div>
         </#if>

         <#if 0 != mostUsedTags?size>
    <div>
        <h4>${tagsLabel}</h4>
        <ul id="tagsSide" class="tags">
            <#list mostUsedTags as tag>
                <li>
                    <a data-count="${tag.tagPublishedRefCount}"
                       href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}"
                       title="${tag.tagTitle}(${tag.tagPublishedRefCount})">
                        <span>${tag.tagTitle}</span>
                    </a>
                </li>
            </#list>
        </ul>
        <div class="clear"></div>
    </div>
         </#if>
         <#if 0 != links?size>
    <div>
        <h4>${linkLabel}</h4>
        <ul>
            <#list links as link>
                <li>
                    <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        <img alt="${link.linkTitle}"
                             src="${link.linkIcon}"
                             width="16" height="16"/></a>
                    <a rel="friend" href="${link.linkAddress}" title="${link.linkTitle}"
                       target="_blank">${link.linkTitle}
                    </a>
                </li>
            </#list>
        </ul>
    </div>
         </#if>
         <#if 0 != archiveDates?size>
    <div>
        <h4>${archiveLabel}</h4>
        <ul id="archiveSide">
            <#list archiveDates as archiveDate>
                <li data-year="${archiveDate.archiveDateYear}">
                <#if "en" == localeString?substring(0, 2)>
                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                       title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount}
                    )
                <#else>
                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount}
                    )
                </#if>
                </li>
            </#list>
        </ul>
    </div>
         </#if>
     </#if>
</div>
