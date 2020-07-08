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
${topBarReplacement}
<div class="header">

    <div class="container">
        <div class="col-sm-2"></div>
        <nav>
            <a class="nav-item" href="${servePath}">${indexLabel}</a>

            <#list pageNavigations as page>
                <a class="nav-item" href="${page.pagePermalink}"
                   target="${page.pageOpenTarget}"><#if page.pageIcon != ''>
				   <img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                </a>
            </#list>

            <a class="nav-item" href="${servePath}/links.html">${linkLabel}</a>
            <a class="nav-item" href="${servePath}/tags.html">${tagLabel}</a>
            <a class="nav-item" href="${servePath}/archives.html">${archiveLabel}</a>
            <#if !staticSite>
                <a href="${servePath}/search?keyword=">Search</a>
            </#if>
        </nav>
        <div class="col-sm-2"></div>
    </div>
</div>
