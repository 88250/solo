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
<body>
<#include "header.ftl">
<div id="pjax">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
<#include "nav.ftl">
<div class="main">
<#if noticeBoard??>
    <div class="board">
    ${noticeBoard}
    </div>
</#if>
    <div class="wrapper content">
        <div class="module__title">
            <span>
                ${tags?size}
                    <span class="ft-green ft-12">${tagLabel}</span>
            </span>
        </div>
        <div id="tags">
        <#list tags as tag>
            <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="tag"
               href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
            ${tag.tagTitle}
                <span class="ft-green ft-12">${tag.tagPublishedRefCount} ${countLabel}</span>
            </a>
        </#list>
        </div>
    </div>
<#include "bottom.ftl">
</div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
 <#if pjax><!---- pjax {#pjax} start ----></#if>
<script>
    Skin.initTags()
</script>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</body>
</html>
