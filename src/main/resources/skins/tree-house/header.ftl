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
<div class="header-navi right">
    <ul>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">
                    <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                </a>&nbsp;&nbsp;
            </li>
        </#list>
        <li>
            <a href="${servePath}/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
        </li>
        <li>
            <a rel="alternate" href="${servePath}/rss.xml">
                <img class="page-icon" src="${staticServePath}/images/feed.png" alt="RSS"/>RSS</a>
            </a> &nbsp;&nbsp;
        </li>
        <#if !staticSite>
            <li>
                <a href="${servePath}/search?keyword=">Search</a>
            </li>
        </#if>
    </ul>
</div>
<div class="header-title">
    <h1>
        <a href="${servePath}" id="logoTitle">
            ${blogTitle}
        </a>
    </h1>
    <div>${blogSubtitle}</div>
    <#include "../../common-template/macro-user_site.ftl"/>
    <div>
        <br>
        <@userSite dir="ne"/>
    </div>
</div>
