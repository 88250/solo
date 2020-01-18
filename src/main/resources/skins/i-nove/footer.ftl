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
<div class="left copyright">
    <span style="color: gray;">&copy; ${year}</span> <a href="${servePath}">${blogTitle}</a> ${footerContent}<br/>
    Powered by <a href="https://solo.b3log.org" target="_blank">Solo</a>&nbsp;&nbsp;
    Theme ${skinDirName} by <a rel="friend" href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
</div>
<div class="right goTop">
    <span onclick="Util.goTop();">${goTopLabel}</span>
</div>
<#include "../../common-template/label.ftl">
<script type="text/javascript" src="${staticServePath}/js/common.min.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    $(document).ready(function () {
        Util.setTopBar()

        // set selected navi
        $("#header-navi li").each(function (i) {
            if (i < $("#header-navi li").length - 1) {
                var $it = $(this),
                locationURL = window.location.pathname + window.location.search;
                if (i === 0 && (locationURL === "/")) {
                    $it.addClass("selected");
                    return;
                }
                if (locationURL.indexOf($it.find("a").attr("href")) > -1 && i !== 0) {
                    $it.addClass("selected");
                }
            }
        });
    });
</script>
${plugins}
