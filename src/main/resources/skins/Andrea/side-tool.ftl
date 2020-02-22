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
<div class="side-tool">
    <ul>
        <li>
            <ul>
                <#list pageNavigations as page>
                <li>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" title="${page.pageTitle}">
                        <#if page.pageIcon != ''><img class="page-icon" src="${page.pageIcon}" alt="${page.pageTitle}"></#if>${page.pageTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </li>
        <li id="changeBG">
            <a title="grey" id="greyBG" class="selected"></a>
            <a title="brown" id="brownBG"></a>
            <a title="blue" id="blueBG"></a>
        </li>
        <li>
            <span id="goTop" onclick="Util.goTop();" title="${goTopLabel}"></span>
        </li>
    </ul>
</div>
