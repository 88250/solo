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

<@commonPage "500 Internal Server Error!">
<h2>500 Internal Server Error!</h2>
<img class="img-error" src="${staticServePath}/images/500.png" title="500" alt="500 Internal Server Error!"/>
<div class="a-error">
    Please <a href="https://github.com/88250/solo/issues/new">report</a> it or return to <a href="${servePath}">Index</a>.
</div>
</@commonPage>
