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
<#include "macro-common_page.ftl">

<@commonPage "Kill IE!">
<div class="kill">
    ${killBrowserLabel}
    <div class="kill__btns">
        <button onclick="window.parent.$('.killIEIframe').remove()">${closeLabel}</button> &nbsp;
    </div>
    <img src='${staticServePath}/images/kill-browser.png' title='Kill IE6' class="kill__img" alt='Kill IE6'/>
</div>
</@commonPage>
