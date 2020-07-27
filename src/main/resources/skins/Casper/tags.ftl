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
                 <span>${category.categoryPublishedArticleCount} ${articleLabel}</span>
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
