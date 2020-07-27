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
<#macro header type>
    <header class="header header--${type}">
        <div class="wrapper header__title">
            <h1 class="header__h1 fn__flex-inline">
                <img src="${faviconURL}" alt="${blogTitle}">
                <a href="${servePath}" rel="start" class="header__title">${blogTitle}</a>
            </h1>
            <h2 class="header__h2">${blogSubtitle}</h2>
        </div>
        <nav class="wrapper header__nav fn__clear">
            <a href="${servePath}" rel="start">
                <#if type == 'article'>
                    ${blogTitle}
                <#else>
                    ${indexLabel}
                </#if>
            </a>

            <#list pageNavigations as page>
                <a class="fn__flex-inline" href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                    <#if page.pageIcon != ''><img src="${page.pageIcon}" alt="${page.pageTitle}"></#if> ${page.pageTitle}
                </a>
            </#list>

            <#if !staticSite>
                <a href="${servePath}/search">
                    ${searchLabel}
                </a>
            </#if>

            <div class="fn__right">
                <#include "../../common-template/macro-user_site.ftl">
                <@userSite dir=""></@userSite>
                <a rel="alternate" href="${servePath}/rss.xml">
                    RSS
                </a>

                <#if !staticSite>
                    <#if isLoggedIn>
                        <a href="${servePath}/admin-index.do#main">
                            ${adminLabel}
                        </a>
                        <a href="${logoutURL}">
                            ${logoutLabel}
                        </a>
                    <#else>
                        <a rel="alternate" href="${servePath}/start">
                            ${startToUseLabel}
                        </a>
                    </#if>
                </#if>
            </div>
        </nav>
    </header>
</#macro>
