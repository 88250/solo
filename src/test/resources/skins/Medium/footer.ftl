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
<div class="wrapper">
    <footer class="footer">
        <div class="fn-clear">
        ${blogSubtitle}
            <div class="fn-right">
            ${blogTitle} &copy; ${year} ${footerContent}
            </div>
        </div>
        <div class="fn-clear">
        ${statistic.statisticPublishedBlogArticleCount} ${articleLabel} &nbsp;
        ${statistic.statisticPublishedBlogCommentCount} ${commentLabel} &nbsp;
        ${statistic.statisticBlogViewCount} ${viewLabel} &nbsp;
        ${onlineVisitorCnt} ${onlineVisitorLabel}
            <div class="fn-right">
                Powered by <a href="https://b3log.org" target="_blank">B3log 开源</a> •
                <a href="https://hacpai.com/tag/Solo" target="_blank">Solo</a> •
                Theme <a rel="friend" href="https://github.com/b3log/solo-skins" target="_blank">Medium</a>
            </div>
        </div>
    </footer>
</div>

<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}"
        charset="utf-8"></script>
<script type="text/javascript"
        src="${staticServePath}/skins/${skinDirName}/js/common${miniPostfix}.js?${staticResourceVersion}"
        charset="utf-8"></script>
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
