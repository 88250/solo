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
<#if 1 != users?size>
    <div class="header-user">
        <div class="content">
            <div class="moon-current-icon"></div>
            <#list users as user>
                <a class="star-icon" href="${servePath}/authors/${user.oId}">
                    ${user.userName}
                </a>
            </#list>
            <div class="clear"></div>
        </div>
    </div>
</#if>
<div class="header-navi">
    <div class="header-navi-main content">
        <div class="left">
            <a rel="nofollow" href="${servePath}" class="header-title">
                ${blogTitle}
            </a>
            <span class="sub-title">${blogSubtitle}</span>
        </div>
        <div class="right">
            <ul class="tabs">
                <li class="tab">
                    <a href="${servePath}">${homeLabel}</a>
                </li>
                <li class="tab">
                    <a href="${servePath}/tags.html">${allTagsLabel}</a>
                </li>
                <#if 0 != pageNavigations?size>
                    <li class="tab" id="header-pages">
                        <a href="${servePath}">
                        <span class="left">
                            ${pageLabel}
                        </span>
                            <span class="arrow-dowm-icon"></span>
                            <span class="clear"></span>
                        </a>
                        <ul class="sub-tabs none">
                            <#list pageNavigations as page>
                                <li class="sub-tab">
                                    <a href="${page.pagePermalink}"
                                       target="${page.pageOpenTarget}"><#if page.pageIcon != ''>
									   <img class="page-icon" alt="${page.pageTitle}" src="${page.pageIcon}"></#if>${page.pageTitle}
                                    </a>
                                </li>
                            </#list>
                        </ul>
                    </li>
                </#if>
                <li class="tab">
                    <a rel="alternate" href="${servePath}/rss.xml">
                        <span class="left">RSS</span>
                        <span class="atom-icon"></span>
                        <span class="clear"></span>
                    </a>
                </li>
                <#if !staticSite>
                    <li class="tab">
                        <a href="${servePath}/search?keyword=">Search</a>
                    </li>
                </#if>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
</div>
