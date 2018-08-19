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
<!-- Here we're establishing whether the page was loaded via Ajax or not, for dynamic purposes. If it's ajax, we're not bringing in footer.php -->
<div id="footer">
    <center>
        <div id="wptouch-switch-link">
            <script type="text/javascript">function switch_delayer() { location.reload();}</script>${mobileLabel} <a id="switch-link" onclick="wptouch_switch_confirmation('normal');" href="javascript:void(0)"></a>		</div>
    </center>
    <p><span style="color: gray;">&copy; ${year}</span> - <a href="${servePath}">${blogTitle}</a>${footerContent}</p>
    <p>Powered by <a href="https://b3log.org" target="_blank">B3log 开源</a> • <a href="https://b3log.org/services/#solo" target="_blank">Solo</a> ${version},
        Theme by <a rel="friend" href="http://dx.b3log.org" target="_blank">dx</a> &lt
        <a rel="friend" href="http://www.bravenewcode.com/products/wptouch-pro">WPtouch</a>.</p>
</div>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    var latkeConfig = {
        "servePath": "${servePath}",
        "staticServePath": "${staticServePath}"
    };
    
    var Label = {
        "skinDirName": "${skinDirName}",
        "em00Label": "${em00Label}",
        "em01Label": "${em01Label}",
        "em02Label": "${em02Label}",
        "em03Label": "${em03Label}",
        "em04Label": "${em04Label}",
        "em05Label": "${em05Label}",
        "em06Label": "${em06Label}",
        "em07Label": "${em07Label}",
        "em08Label": "${em08Label}",
        "em09Label": "${em09Label}",
        "em10Label": "${em10Label}",
        "em11Label": "${em11Label}",
        "em12Label": "${em12Label}",
        "em13Label": "${em13Label}",
        "em14Label": "${em14Label}"
    };
    
    $(document).ready(function () {
        Util.init();
    
        var toggleArchive = function (it) {
            var $it = $(it);
            $it.next().slideToggle(260, function () {
                var h4Obj = $it.find("h4");
                if (this.style.display === "none") {
                    h4Obj.html("${archiveLabel} +");
                } else {
                    h4Obj.html("${archiveLabel} -");
                }
            });
        }
    });
    
</script>
${plugins}
