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
<header class="header">
    <div class="fn__flex-1">
        <a href="${servePath}" rel="start" class="vditor-tooltipped__w vditor-tooltipped" aria-label="${blogTitle}">
            <i class="icon__home"></i>
        </a>

        <#list pageNavigations as page>
            <a href="${page.pagePermalink}" target="${page.pageOpenTarget}"
               class="vditor-tooltipped__w vditor-tooltipped" rel="section" aria-label="${page.pageTitle}">
                <#if page.pageIcon != ''><img src="${page.pageIcon}" alt="${page.pageTitle}"><#else>
                    <i class="icon__page"></i>
                </#if>
            </a>
        </#list>

        <a href="${servePath}/categories.html" rel="section" aria-label="${categoryLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__category"></i>
        </a>
        <a href="${servePath}/tags.html" rel="section" aria-label="${allTagsLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__tags"></i>
        </a>
        <a href="${servePath}/archives.html" aria-label="${archiveLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__inbox"></i>
        </a>
        <a rel="archive" href="${servePath}/links.html" aria-label="${linkLabel}"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__link"></i>
        </a>
        <#if !staticSite>
            <a href="${servePath}/search" class="vditor-tooltipped__w vditor-tooltipped" aria-label="${searchLabel}">
                <i class="icon__search"></i>
            </a>
        </#if>
        <a rel="alternate" href="${servePath}/rss.xml" rel="section" aria-label="RSS"
           class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__rss"></i>
        </a>
    </div>

    <#if !staticSite>
        <div>
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main"
                   aria-label="${adminLabel}" class="vditor-tooltipped vditor-tooltipped__w">
                    <i class="icon__setting"></i>
                </a>
                <a href="${logoutURL}"
                   aria-label="${logoutLabel}" class="vditor-tooltipped vditor-tooltipped__w">
                    <i class="icon__logout"></i>
                </a>
            <#else>
                <a href="${servePath}/start"
                   aria-label="${startToUseLabel}" class="vditor-tooltipped vditor-tooltipped__w">
                    <i class="icon__login"></i>
                </a>
            </#if>
            <span onclick="Util.goTop()"
                  aria-label="${putTopLabel}" class="vditor-tooltipped vditor-tooltipped__w">
            <i class="icon__up"></i>
        </span>
        </div>
    </#if>
</header>
