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
<#include "../../common-template/macro-common_head.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${allTagsLabel} - ${blogTitle}">
    <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
</@head>
</head>
<body class="fn__flex-column">
<div id="pjax" class="fn__flex-1">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
<#include "macro-header.ftl">
    <@header type='other'></@header>
    <div class="wrapper other">
        <h2 class="other__title"><a href="${servePath}" class="ft__a">${blogTitle}</a> - ${allTagsLabel}</h2>
        <div class="other__meta">
        ${tags?size} ${tagLabel}
        </div>
        <div class="other__content">
         <#list mostUsedCategories as category>
             <span class="other__item--archive other__item">
                 <a href="${servePath}/category/${category.categoryURI}">
                     ${category.categoryTitle}
                 </a>
                 <span>${category.categoryTagCnt} ${tagLabel}</span>
             </span>
         </#list>
        </div>
        <div class="other__content">
        <#list tags as tag>
            <span class="other__item other__item--archive">
                 <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="tag"
                    href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                     ${tag.tagTitle}
                 </a>
                <span>${tag.tagPublishedRefCount} ${countLabel}</span>
            </span>
        </#list>
        </div>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
</body>
</html>
