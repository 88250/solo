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
<aside class="side">
    <#if article?? && article.articleToC?? && article.articleToC?size &gt; 0>
        <div class="module">
            <div class="module__list">
                <#include "../../common-template/toc.ftl"/>
            </div>
        </div>
    </#if>
    <#if noticeBoard??>
        <section class="vditor-reset module">
            <main class="module__content">
                ${noticeBoard}
            </main>
        </section>
    </#if>

    <#if !staticSite>
        <section class="module">
            <header class="module__header">
                <form class="form" action="${servePath}/search">
                    <input placeholder="${searchLabel}" class="form__input" type="text" name="keyword"/>
                    <button class="side__btn" type="submit"><i class="icon__search"></i></button>
                </form>
            </header>
        </section>
    </#if>

    <div class="module item">
        <header class="module__header ft__center">
            ${adminUser.userName}
        </header>
        <main class="module__content ft__center">
            <img class="side__avatar" src="${adminUser.userAvatar}" alt="${adminUser.userName}"/>
            ${blogSubtitle} <br> <br>
            <#include "../../common-template/macro-user_site.ftl"/>
            <@userSite dir=""/>
        </main>
    </div>

    <#if 0 != mostUsedCategories?size>
        <div class="module item">
            <header class="module__header">
                ${categoryLabel}
            </header>
            <main class="module__content fn__clear module__content--three">
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
        <div class="module item">
            <header class="module__header">${tagsLabel}</header>
            <main class="module__content--three module__content fn__clear">
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

    <div class="module item">
        <div class="module__header">
            <div class="fn__flex">
                <a href="${servePath}/archives.html" class="fn__flex-1 ft__center">
                    ${statistic.statisticPublishedBlogArticleCount}
                    ${articleLabel}
                </a>
            </div>
            <br/>
            <div class="fn__flex">
                <div class="fn__flex-1 ft__center">
                    <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span> <span class="ft-gray">${viewLabel}</span>
                </div>
                <#if !staticSite>
                <div class="fn__flex-1 ft__center">
                    ${onlineVisitorCnt} <span class="ft-gray">${onlineVisitorLabel}</span>
                </div>
                </#if>
            </div>
        </div>
    </div>
</aside>
