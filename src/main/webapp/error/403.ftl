<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<#include "../macro-common-page.ftl">

<@commonPage "403 Forbidden!">
<h2>403 Forbidden!</h2>
<img class="img-error" src="${staticServePath}/images/403.png" alt="403: forbidden" title="403: forbidden" />
<div class="a-error">
    Please
    <a href="${loginURL}">Login</a>.
    Return to <a href="${servePath}">Index</a> or <a href="https://hacpai.com">HacPai</a>.
</div>
</@commonPage>
