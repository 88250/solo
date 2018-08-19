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
<div class="module-panel">
    <div class="module-header">
        <h2>${aboutLabel}</h2>
    </div>
    <div class="module-body padding12 fn__flex">
        <div class="about-logo">
            <a href="https://b3log.org" target="_blank">
                <img width="128" src="${staticServePath}/images/logo.png" alt="Solo" title="Solo" />
            </a>
        </div>
        <div class="left content-reset about__panel" style="margin-left: 20px;">
            <div id="aboutLatest" class="about-margin left">
                ${checkingVersionLabel}
            </div>

            <iframe src="https://ghbtns.com/github-btn.html?user=b3log&repo=solo&type=star&count=true&size=large"
                    frameborder="0" scrolling="0" width="160px" height="30px" class="about__iframe"
                    style="margin: 21px 0 0 20px;border: 0"
                    class="left"></iframe>
            <div class="clear"></div>

            ${aboutContentLabel}
            <ul class="about-list">
                <li><a target="_blank" href="https://hacpai.com/article/1492881378588">用户指南</a></li>
                <li><a target="_blank" href="https://hacpai.com/article/1493822943172">开发指南</a></li>
                <li><a target="_blank" href="https://hacpai.com/article/1493814851007">皮肤开发指南</a></li>
            </ul>
            <button class="right" onclick="window.open('https://b3log.org/donate.html')">${sponsorLabel}</button>
        </div>
        <span class="clear" /> <br/>
    </div>
</div>
${plugins}
