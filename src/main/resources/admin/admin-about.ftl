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
        <div class="about__panel">
            <div id="aboutLatest" class="about-margin fn__left">
                ${checkingVersionLabel}
            </div>

            <iframe src="https://ghbtns.com/github-btn.html?user=88250&repo=solo&type=star&count=true&size=large"
                    frameborder="0" scrolling="0" width="160px" height="30px" class="about__iframe"
                    style="margin: 21px 0 0 20px;border: 0"
                    class="fn__left"></iframe>
            <div class="fn__clear"></div>

            ${aboutContentLabel}
            <br/>
            <ul class="about-list">
                <li><a target="_blank" href="https://hacpai.com/article/1492881378588">用户指南</a></li>
                <li><a target="_blank" href="https://hacpai.com/article/1537690756242">Solo 从设计到实现</a></li>
                <li><a target="_blank" href="https://hacpai.com/article/1493814851007">皮肤开发指南</a></li>
            </ul>
        </div>
        <span class="fn__clear" /> <br/><br/>
    </div>
</div>

<div class="module-panel">
    <div class="module-header">
        <h2>❤️ 欢迎成为我们的赞助者</h2>
    </div>
    <div class="module-body padding12">
        <a href="https://b3log.org">B3log 开源组织</a>旗下包含
        <a href="https://sym.b3log.org/">Symphony</a>、
        <a href="https://solo.b3log.org/">Solo</a>、
        <a href="https://github.com/88250/pipe">Pipe</a>、
        <a href="https://github.com/88250/wide">Wide</a>、
        <a href="https://github.com/88250/latke">Latke</a>、
        <a href="https://github.com/vanessa219/vditor">Vditor</a>、
        <a href="https://github.com/88250/gulu">Gulu</a>&nbsp;等一系列开源项目。随着项目规模的增长，我们需要有相应的资金支持才能持续项目的维护和开发。
        <br/> <br/>
        如果你觉得 Solo 还算好用，可通过支付宝对我们进行赞助，谢谢 🙏
        <br/> <br/>
        <div class="ft__center">
            <button class="fn__flex-inline" onclick="window.open('https://hacpai.com/sponsor')">
                <svg viewBox="0 0 32 32" width="100%" height="100%"
                     className={classes.svg}>
                    <path
                            d="M32 21.906v-15.753c0-3.396-2.757-6.152-6.155-6.152h-19.692c-3.396 0-6.152 2.756-6.152 6.152v19.694c0 3.396 2.754 6.152 6.152 6.152h19.694c3.027 0 5.545-2.189 6.058-5.066-1.632-0.707-8.703-3.76-12.388-5.519-2.804 3.397-5.74 5.434-10.166 5.434s-7.38-2.726-7.025-6.062c0.234-2.19 1.736-5.771 8.26-5.157 3.438 0.323 5.012 0.965 7.815 1.89 0.726-1.329 1.329-2.794 1.785-4.35h-12.433v-1.233h6.151v-2.212h-7.503v-1.357h7.504v-3.195c0 0 0.068-0.499 0.62-0.499h3.077v3.692h7.999v1.357h-7.999v2.212h6.526c-0.6 2.442-1.51 4.686-2.651 6.645 1.895 0.686 10.523 3.324 10.523 3.324v0 0 0zM8.859 24.736c-4.677 0-5.417-2.953-5.168-4.187 0.246-1.227 1.6-2.831 4.201-2.831 2.987 0 5.664 0.767 8.876 2.328-2.256 2.94-5.029 4.69-7.908 4.69v0 0z"></path>
                </svg>
                &nbsp;
                使用支付宝进行赞助
            </button>
        </div>
        <br/>
        <ul id="adminAboutSponsors" style="list-style: none"></ul>
    </div>
</div>
${plugins}
