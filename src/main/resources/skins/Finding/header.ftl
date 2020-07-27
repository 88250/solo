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
<header class="main-header"<#if !isIndex> style='height:30vh;'</#if>>
    <div class="fn-vertical">
        <div class="main-header-content fn-wrap">
            <h2 class="page-title">
                <a href="${servePath}">${blogTitle}</a>
            <#if "" != noticeBoard>
                <small class="page-description"> &nbsp; ${blogSubtitle}</small>
            </#if>
            </h2>
            <h2 class="page-description">
            <#if "" != noticeBoard>
                ${noticeBoard}
                <#else>
            ${blogSubtitle}
            </#if>
            </h2>
        </div>
    </div>
<#if isIndex><a class="scroll-down icon-arrow-left" href="#content" data-offset="-45"></a></#if>
</header>
