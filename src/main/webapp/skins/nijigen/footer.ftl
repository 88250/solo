<#--

    Solo - A beautiful, simple, stable, fast Java blogging system.
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
<footer class="footer fn-clear">
    &copy; ${year}
    ${footerContent}
    <a href="${servePath}">${blogTitle}</a>  &nbsp;   • &nbsp;
    <a href="https://solo.b3log.org" target="_blank">Solo</a> ${version}  <br/>

    Powered by <a href="https://b3log.org" target="_blank">B3log</a> 开源 &nbsp;
    <span class="ft-warn">&heartsuit;</span>
    Theme <a rel="friend" href="https://github.com/b3log/solo-skins" target="_blank">9IPHP</a> by <a href="https://github.com/9IPHP/9IPHP" target="_blank">9IPHP</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>
</footer>
<div class="icon-up" onclick="Util.goTop()"></div>


<div class="waifu">
    <div class="waifu-tips"></div>
    <canvas id="live2d" width="280" height="250" class="live2d"></canvas>
    <div class="waifu-tool">
        <span class="fui-home">home</span>
        <span class="fui-eye">eye</span>
        <span class="fui-chat">chat</span>
        <span class="fui-user">user</span>
        <span class="fui-photo">photo</span>
        <span class="fui-info-circle">circle</span>
        <span class="fui-cross">cross</span>
    </div>
</div>

<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/skins/${skinDirName}/js/common${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script async src="${staticServePath}/skins/${skinDirName}/kanban/waifu-tips.js"></script>
<script src="${staticServePath}/skins/${skinDirName}/kanban/live2d.js"></script>
<script type="text/javascript">
    var latkeConfig = {
        "servePath": "${servePath}",
        "staticServePath": "${staticServePath}",
        "isLoggedIn": "${isLoggedIn?string}",
        "userName": "${userName}"
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

    Util.parseMarkdown('content-reset');
</script>
${plugins}
