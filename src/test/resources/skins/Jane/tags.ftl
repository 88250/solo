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
<@head title="${allTagsLabel} - ${blogTitle}">
    <meta name="keywords" content="${metaKeywords},${allTagsLabel}"/>
    <meta name="description" content="<#list tags as tag>${tag.tagTitle}<#if tag_has_next>,</#if></#list>"/>
</@head>
</head>
<body>
<#include "header.ftl">
<div id="pjax" class="wrapper">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="article__item">
        <h2 class="article__title">
            <i class="icon__tags"></i> ${allTagsLabel}
        </h2>
        <div class="ft__gray">
        ${sumLabel} ${tags?size} ${tagLabel}
        </div>
        <div class="tags fn__clear">
            <#list tags as tag>
                <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="ft__red"
                   href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                    <span class="name">${tag.tagTitle}</span>
                    <span class="ft__gray">(${tag.tagPublishedRefCount})</span>
                </a>
            </#list>
        </div>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
    </div>
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<script type="text/javascript" src="${staticServePath}/skins/${skinDirName}/js/isotope.pkgd.min.js"
        charset="utf-8"></script>
<script>
    $('.tags').isotope({
        transitionDuration: '1.5s',
        itemSelector: '.ft__red',
        layoutMode: 'fitRows',
        getSortData: {
            name: '.name'
        }
    })
    $('.tags').isotope({
        sortBy: 'name',
    })
</script>
<#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
