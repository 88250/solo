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
<nav id="headerNav" class="header__nav">
    <div class="wrapper">
        <a href="${servePath}">
            <svg>
                <use xlink:href="#icon-home"></use>
            </svg>
            ${indexLabel}
        </a>
        <a href="${servePath}/tags.html" rel="section">
            <svg>
                <use xlink:href="#icon-tag"></use>
            </svg> ${allTagsLabel}
        </a>
        <a href="${servePath}/archives.html">
            <svg>
                <use xlink:href="#icon-bookmark"></use>
            </svg> ${archiveLabel}
        </a>

        <#list pageNavigations as page>
            <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                <#if page.pageIcon != ''><img src="${page.pageIcon}" alt="${page.pageTitle} "></#if> ${page.pageTitle}
            </a>
        </#list>

        <a rel="archive" href="${servePath}/links.html">
            <svg>
                <use xlink:href="#icon-link"></use>
            </svg> ${linkLabel}
        </a>

        <a rel="alternate" href="${servePath}/rss.xml" rel="section">
            <svg>
                <use xlink:href="#icon-feed"></use>
            </svg>
            RSS
        </a>
        <#if !staticSite>
            <#if isLoggedIn>
                <a href="${servePath}/admin-index.do#main">
                    <svg>
                        <use xlink:href="#icon-setting"></use>
                    </svg> ${adminLabel}
                </a>
                <a href="${logoutURL}">
                    <svg>
                        <use xlink:href="#icon-out"></use>
                    </svg>
                    ${logoutLabel}
                </a>
            <#else>
                <a rel="alternate" href="${servePath}/start" rel="section">
                    <svg>
                        <use xlink:href="#icon-enter"></use>
                    </svg>
                    ${startToUseLabel}
                </a>
            </#if>
        </#if>
    </div>
</nav>
