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
<#include "../common-template/macro-common_page.ftl">

<@commonPage "${articleViewPwdLabel}">
<h2>
${articleTitle}
</h2>
<br><br><br>
<form class="form" method="POST" action="${servePath}/console/article-pwd">
    <label for="pwdTyped">访问密码</label>
    <input type="password" id="pwdTyped" name="pwdTyped" />
    <input type="hidden" name="articleId" value="${articleId}" />
    <div style="text-align: fn__right">
         <#if msg??>
            <span class="error">${msg}</span>
         </#if>
        <button id="confirm" type="submit">${confirmLabel}</button>
    </div>
</form>
<br><br><br>
</@commonPage>