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

<@commonPage "500 Internal Server Error!">
<h2>500 Internal Server Error!</h2>
<img class="img-error" src="${staticServePath}/images/500.png" title="500: internal error" alt="500: internal error" />
<div class="a-error">
    Please
    <a href="https://github.com/b3log/solo/issues/new">report</a>.
    Return to <a href="${servePath}">Index</a> or <a href="https://hacpai.com">HacPai</a>.
</div>
</@commonPage>