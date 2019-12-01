<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
