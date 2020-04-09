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

<@commonPage "403 Forbidden!">
<h2>403 Forbidden!</h2>
<img class="img-error" src="${staticServePath}/images/403.png" alt="403" title="403 Forbidden!" />
<div class="a-error">
    ${msg!}
    Return to <a href="${servePath}">Index</a>.
</div>
</@commonPage>
