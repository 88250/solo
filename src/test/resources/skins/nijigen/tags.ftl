<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<div class="main">
    <div id="pjax" class="content">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <main>
        <div class="module">
            <div class="module__content ft__center">
                <i class="icon__home"></i>
                <a href="${servePath}" class="breadcrumb">${blogTitle}</a>
                &nbsp; > &nbsp;
                <i class="icon__tags"></i> ${sumLabel} ${tags?size} ${tagLabel}
            </div>
        </div>

        <div class="module">
            <div class="module__content fn__clear tags">
                 <#list tags as tag>
                 <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="tag"
                    href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                     <span class="name">${tag.tagTitle}</span>
                     (<b>${tag.tagPublishedRefCount}</b>)
                 </a>
                 </#list>
            </div>
        </div>
    </main>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
    </div>
    <#include "side.ftl">
</div>
<#include "footer.ftl">
<#if pjax><!---- pjax {#pjax} start ----></#if>
<script type="text/javascript" src="${staticServePath}/skins/${skinDirName}/js/isotope.pkgd.min.js"
        charset="utf-8"></script>
<script>
    $('.tags').isotope({
        transitionDuration: '1.5s',
        itemSelector: '.tag',
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
