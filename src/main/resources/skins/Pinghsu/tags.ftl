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
<body class="body--gray" class="fn__flex-1">
<#include "header.ftl">
<main id="pjax">
    <#if pjax><!---- pjax {#pjax} start ----></#if>
    <div class="wrapper--min wrapper">
        <div class="page__title">
            <span class="ft__red">#</span>
            ${sumLabel} ${tags?size} ${tagLabel}
        </div>

        <div class="page__content page__tags fn__clear">
         <#list tags as tag>
             <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="tag tag--${tag_index % 10}"
                href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                 <span class="name">${tag.tagTitle}</span>
                 (<b>${tag.tagPublishedRefCount}</b>)
             </a>
         </#list>
        </div>
    </div>
    <#if pjax><!---- pjax {#pjax} end ----></#if>
</main>
<#include "footer.ftl">
</body>
</html>
