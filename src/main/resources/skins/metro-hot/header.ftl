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
<div class="fn-clear header">
    <h1 class="fn-left">
        <a class="title" href="javascript: void(0)">
            ${blogTitle}
            <span data-ico="&#xe0f3;"></span>
        </a>
    </h1>
    <ul class="navigation">
        <li>
            <a rel="nofollow" href="${servePath}/">${indexLabel}</a>
        </li>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"><#if page.pageIcon != ''><img
                        class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}</a>
            </li>
        </#list>
        <#if !staticSite>
            <li>
                <a href="${servePath}/dynamic.html">${dynamicLabel}</a>
            </li>
        </#if>
        <li>
            <a href="${servePath}/categories.html">${categoryLabel}</a>
        </li>
        <li>
            <a href="${servePath}/tags.html">${allTagsLabel}</a>
        </li>
        <li>
            <a href="${servePath}/archives.html">${archiveLabel}</a>
        </li>
        <li class="last">
            <a href="${servePath}/links.html">${linkLabel}</a>
        </li>
    </ul>
    <#if !staticSite>
        <div class="fn-right top-info">
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main" title="${adminLabel}" data-ico="&#x0070;"></a>
                <hr>
                <a href="${logoutURL}" title="${logoutLabel}" data-ico="&#xe040;"></a>
            <#else>
                <a href="${servePath}/start" title="${startToUseLabel}" data-ico="&#xe03f;"></a>
            </#if>
        </div>
    </#if>
</div>
