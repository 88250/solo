<div class="module-panel">
    <div class="module-header">
        <h2>${aboutLabel}</h2>
    </div>
    <div class="module-body padding12">
        <div class="about-logo">
            <a href="http://b3log.org" target="_blank">
                <img width="128" src="${staticServePath}/images/logo.png" alt="Solo" title="Solo" />
            </a>
        </div>
        <div class="left content-reset" style="width: 73%">
            <div id="aboutLatest" class="about-margin left">
                ${checkingVersionLabel}
            </div>

            <iframe src="https://ghbtns.com/github-btn.html?user=b3log&repo=solo&type=star&count=true&size=large"
                    frameborder="0" scrolling="0" width="160px" height="30px"
                    style="margin: 21px 0 0 20px;border: 0"
                    class="left"></iframe>
            <div class="clear"></div>

            ${aboutContentLabel}
            <ul class="about-list">
                <li><a target="_blank" href="https://hacpai.com/article/1492881378588">用户指南</a></li>
                <li><a target="_blank" href="https://hacpai.com/article/1493822943172">开发指南</a></li>
                <li><a target="_blank" href="https://hacpai.com/article/1493814851007">皮肤开发指南</a></li>
            </ul>
            <button class="right" onclick="window.open('http://b3log.org/donate.html')">${sponsorLabel}</button>
        </div>
        <span class="clear" /> <br/>
    </div>
</div>
${plugins}
