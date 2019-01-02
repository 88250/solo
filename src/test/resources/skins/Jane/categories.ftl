<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2019, b3log.org & hacpai.com

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
<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${categoryLabel} - ${blogTitle}">
    <meta name="keywords" content="${metaKeywords},${categoryLabel}"/>
    <meta name="description"
          content="<#list categories as category>${category.categoryTitle}<#if category_has_next>,</#if></#list>"/>
</@head>
</head>
<body>
<#include "header.ftl">
<div id="pjax" class="wrapper">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="article__item">
        <h2 class="article__title">
            <i class="icon__category"></i> ${categoryLabel}${articleLabel}
        </h2>
        <div class="ft__gray">
        ${categories?size} ${cntLabel}${categoryLabel}
        </div>
        <div class="tags fn__clear">
            <#list categories as category>
                <a href="${servePath}/category/${category.categoryURI}"
                   class="ft__red">
                    ${category.categoryTitle}
                    <span class="ft__gray">(${category.categoryTagCnt} ${tagsLabel})</span>
                </a>
            </#list>
        </div>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
    <#include "footer.ftl">
</body>
</html>
