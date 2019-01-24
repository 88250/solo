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

<@commonPage "${loginLabel}">
<h2>
${loginLabel}
</h2>
<div id="github">
    <div class="github__icon"
        onclick="window.location.href = '${servePath}/oauth/github/redirect';$('#github').addClass('github--loading')">
        <img src="${staticServePath}/images/github-init.gif"/>
    </div>
    <button class="hover" onclick="window.location.href = '${servePath}/oauth/github/redirect';$('#github').addClass('github--loading')">${useGitHubAccountLoginLabel}</button>
    <br>
</div>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript">
    $('.wrap').css('padding', ($(window).height() - 450) / 2 + 'px 0')
</script>
</@commonPage>