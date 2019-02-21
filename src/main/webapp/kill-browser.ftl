<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2019, b3log.org & hacpai.com

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
<#include "macro-common-page.ftl">

<@commonPage "Kill IE!">
<div class="kill">
    ${killBrowserLabel}
    <div class="kill__btns">
        <button onclick="closeIframe();">${closeLabel}</button> &nbsp;
        <button onclick="closeIframeForever();">${closeForeverLabel}</button>
    </div>
    <img src='${staticServePath}/images/kill-browser.png' title='Kill IE6' class="kill__img" alt='Kill IE6'/>
</div>
<script>
    var closeIframe = function () {
        window.parent.$('iframe').prev().remove()
        window.parent.$('iframe').remove()
    }

    var closeIframeForever = function () {
        window.parent.Cookie.createCookie('showKill', true, 365)
        closeIframe()
    }
</script>
</@commonPage>