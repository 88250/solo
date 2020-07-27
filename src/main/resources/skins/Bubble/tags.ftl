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
    <@header type='index'></@header>
    <div class="wrapper">
        <h2 class="other__title"><a href="${servePath}" class="ft__a">${blogTitle}</a> - ${allTagsLabel}</h2>
        <div class="ft__center">
            ${tags?size} ${tagLabel}
        </div>
        <div class="articles">
            <div class="fn__clear">
                <#list tags as tag>
                    <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="other__item fn__left"
                       href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle} - <b>${tag.tagPublishedRefCount}</b> ${countLabel}
                    </a>
                </#list>
            </div>
            <br><br>
        </div>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</div>
<#include "footer.ftl">
</body>
</html>
