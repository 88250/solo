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
<#include "macro-common-page.ftl">

<@commonPage "${articleViewPwdLabel}">
<h2>
${articleTitle}
</h2>
    <#if msg??>
    <div>${msg}</div>
    </#if>
<form class="form" method="POST" action="${servePath}/console/article-pwd">
    <label for="pwdTyped">访问密码：</label>
    <input type="password" id="pwdTyped" name="pwdTyped" />
    <input type="hidden" name="articleId" value="${articleId}" />
    <button id="confirm" type="submit">${confirmLabel}</button>
</form>
</@commonPage>