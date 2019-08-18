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
<div id="tabPreference" class="sub-tabs fn__clear">
    <ul>
        <li>
            <div id="tabPreference_config">
                <a class="tab-current" href="#tools/preference/config">${configSettingsLabel}</a>
            </div>
        </li>
        <li>
            <div id="tabPreference_signs">
                <a href="#tools/preference/signs">${signLabel}</a>
            </div>
        </li>
        <li>
            <div id="tabPreference_setting">
                <a href="#tools/preference/setting">${paramSettingsLabel}</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabPreferencePanel" class="sub-tabs-main">
    <div id="tabPreferencePanel_config" class="form">
        <div class="fn__clear">
            <a style="line-height: 32px" href="https://hacpai.com/settings" target="_blank">前往配置 GitHub，Twitter 等站点链接</a>
            <button onclick="admin.preference.update()" class="fn__right">${updateLabel}</button>
        </div>
        <div class="fn__clear"></div>
        <label for="blogTitle">${blogTitle1Label}</label>
        <input id="blogTitle" type="text"/>
        <label for="blogSubtitle">${blogSubtitle1Label}</label>
        <input id="blogSubtitle" type="text"/>
        <label for="blogHost">${blogHost1Label}</label>
        <input id="blogHost" type="text" value="${serverHost}" readonly="true"/>
        <label for="metaKeywords">${metaKeywords1Label}</label>
        <input id="metaKeywords" type="text"/>
        <label for="metaDescription">${metaDescription1Label}</label>
        <input id="metaDescription" type="text"/>
        <label for="htmlHead">${htmlhead1Label}</label>
        <textarea rows="6" id="htmlHead"></textarea>
        <label for="noticeBoard">${noticeBoard1Label}</label>
        <textarea rows="6" id="noticeBoard"></textarea>
        <label for="footerContent">${footerContent1Label}</label>
        <textarea rows="2" id="footerContent"></textarea><br><br>
        <div class="fn__clear">
            <a style="line-height: 32px" href="https://hacpai.com/settings" target="_blank">前往配置 GitHub，Twitter 等站点链接</a>
            <button onclick="admin.preference.update()" class="fn__right">${updateLabel}</button>
        </div>
    </div>
    <div id="tabPreferencePanel_setting" class="fn__none form">
        <button class="fn__right" onclick="admin.preference.update()">${updateLabel}</button>
        <div class="fn__clear"></div>
        <label for="localeString">${localeString1Label}</label>
        <select id="localeString">
            <option value="zh_CN">简体中文</option>
            <option value="en_US">Englisth(US)</option>
        </select>
        <label for="timeZoneId">${timeZoneId1Label}</label>
        <select id="timeZoneId">
        ${timeZoneIdOptions}
        </select>
        <label for="articleListDisplay">${articleListDisplay1Label}</label>
        <select id="articleListDisplay">
            <option value="titleOnly">${titleOnlyLabel}</option>
            <option value="titleAndAbstract">${titleAndAbstractLabel}</option>
            <option value="titleAndContent">${titleAndContentLabel}</option>
        </select>
        <label for="hljsTheme">
            <a href="https://highlightjs.org/static/demo/" target="_blank">${previewLabel}</a>${hljsThemeLabel}
        </label>
        <select id="hljsTheme">
            <option value="default">Default</option>
            <option value="a11y-dark">A 11 Y Dark</option>
            <option value="a11y-light">A 11 Y Light</option>
            <option value="agate">Agate</option>
            <option value="an-old-hope">An Old Hope</option>
            <option value="androidstudio">Androidstudio</option>
            <option value="arduino-light">Arduino Light</option>
            <option value="arta">Arta</option>
            <option value="ascetic">Ascetic</option>
            <option value="atelier-cave-dark">Atelier Cave Dark</option>
            <option value="atelier-cave-light">Atelier Cave Light</option>
            <option value="atelier-dune-dark">Atelier Dune Dark</option>
            <option value="atelier-dune-light">Atelier Dune Light</option>
            <option value="atelier-estuary-dark">Atelier Estuary Dark</option>
            <option value="atelier-estuary-light">Atelier Estuary Light</option>
            <option value="atelier-forest-dark">Atelier Forest Dark</option>
            <option value="atelier-forest-light">Atelier Forest Light</option>
            <option value="atelier-heath-dark">Atelier Heath Dark</option>
            <option value="atelier-heath-light">Atelier Heath Light</option>
            <option value="atelier-lakeside-dark">Atelier Lakeside Dark</option>
            <option value="atelier-lakeside-light">Atelier Lakeside Light</option>
            <option value="atelier-plateau-dark">Atelier Plateau Dark</option>
            <option value="atelier-plateau-light">Atelier Plateau Light</option>
            <option value="atelier-savanna-dark">Atelier Savanna Dark</option>
            <option value="atelier-savanna-light">Atelier Savanna Light</option>
            <option value="atelier-seaside-dark">Atelier Seaside Dark</option>
            <option value="atelier-seaside-light">Atelier Seaside Light</option>
            <option value="atelier-sulphurpool-dark">Atelier Sulphurpool Dark</option>
            <option value="atelier-sulphurpool-light">Atelier Sulphurpool Light</option>
            <option value="atom-one-dark-reasonable">Atom One Dark Reasonable</option>
            <option value="atom-one-dark">Atom One Dark</option>
            <option value="atom-one-light">Atom One Light</option>
            <option value="brown-paper">Brown Paper</option>
            <option value="codepen-embed">Codepen Embed</option>
            <option value="color-brewer">Color Brewer</option>
            <option value="darcula">Darcula</option>
            <option value="dark">Dark</option>
            <option value="darkula">Darkula</option>
            <option value="docco">Docco</option>
            <option value="dracula">Dracula</option>
            <option value="far">Far</option>
            <option value="foundation">Foundation</option>
            <option value="github-gist">Github Gist</option>
            <option value="github">Github</option>
            <option value="gml">Gml</option>
            <option value="googlecode">Googlecode</option>
            <option value="grayscale">Grayscale</option>
            <option value="gruvbox-dark">Gruvbox Dark</option>
            <option value="gruvbox-light">Gruvbox Light</option>
            <option value="hopscotch">Hopscotch</option>
            <option value="hybrid">Hybrid</option>
            <option value="idea">Idea</option>
            <option value="ir-black">Ir Black</option>
            <option value="isbl-editor-dark">Isbl Editor Dark</option>
            <option value="isbl-editor-light">Isbl Editor Light</option>
            <option value="kimbie.dark">Kimbie Dark</option>
            <option value="kimbie.light">Kimbie Light</option>
            <option value="lightfair">Lightfair</option>
            <option value="magula">Magula</option>
            <option value="mono-blue">Mono Blue</option>
            <option value="monokai-sublime">Monokai Sublime</option>
            <option value="monokai">Monokai</option>
            <option value="nord">Nord</option>
            <option value="obsidian">Obsidian</option>
            <option value="ocean">Ocean</option>
            <option value="paraiso-dark">Paraiso Dark</option>
            <option value="paraiso-light">Paraiso Light</option>
            <option value="pojoaque">Pojoaque</option>
            <option value="purebasic">Purebasic</option>
            <option value="qtcreator_dark">Qtcreator Dark</option>
            <option value="qtcreator_light">Qtcreator Light</option>
            <option value="railscasts">Railscasts</option>
            <option value="rainbow">Rainbow</option>
            <option value="routeros">Routeros</option>
            <option value="school-book">School Book</option>
            <option value="shades-of-purple">Shades Of Purple</option>
            <option value="solarized-dark">Solarized Dark</option>
            <option value="solarized-light">Solarized Light</option>
            <option value="sunburst">Sunburst</option>
            <option value="tomorrow-night-blue">Tomorrow Night Blue</option>
            <option value="tomorrow-night-bright">Tomorrow Night Bright</option>
            <option value="tomorrow-night-eighties">Tomorrow Night Eighties</option>
            <option value="tomorrow-night">Tomorrow Night</option>
            <option value="tomorrow">Tomorrow</option>
            <option value="vs">Vs</option>
            <option value="vs2015">Vs 2015</option>
            <option value="xcode">Xcode</option>
            <option value="xt256">Xt 256</option>
            <option value="zenburn">Zenburn</option>
        </select>
        <label for="mostUsedTagDisplayCount">${indexTagDisplayCnt1Label}</label>
        <input id="mostUsedTagDisplayCount" class="normalInput" type="text"/>
        <label for="recentCommentDisplayCount">${indexRecentCommentDisplayCnt1Label}</label>
        <input id="recentCommentDisplayCount" class="normalInput" type="text"/>
        <label for="mostCommentArticleDisplayCount">${indexMostCommentArticleDisplayCnt1Label}</label>
        <input id="mostCommentArticleDisplayCount" class="normalInput" type="text"/>
        <label for="mostViewArticleDisplayCount">${indexMostViewArticleDisplayCnt1Label}</label>
        <input id="mostViewArticleDisplayCount" class="normalInput" type="text"/>
        <label for="articleListDisplayCount">${pageSize1Label}</label>
        <input id="articleListDisplayCount" class="normalInput" type="text"/>
        <label for="articleListPaginationWindowSize">${windowSize1Label}</label>
        <input id="articleListPaginationWindowSize" class="normalInput" type="text"/>
        <label for="randomArticlesDisplayCount">${randomArticlesDisplayCnt1Label}</label>
        <input id="randomArticlesDisplayCount" class="normalInput" type="text"/>
        <label for="relevantArticlesDisplayCount">${relevantArticlesDisplayCnt1Label}</label>
        <input id="relevantArticlesDisplayCount" class="normalInput" type="text"/>
        <label for="externalRelevantArticlesDisplayCount">${externalRelevantArticlesDisplayCnt1Label}</label>
        <input id="externalRelevantArticlesDisplayCount" class="normalInput" type="text"/>
        <label for="feedOutputMode">${feedOutputModel1Label}</label>
        <select id="feedOutputMode">
            <option value="abstract">${abstractLabel}</option>
            <option value="fullContent">${fullContentLabel}</option>
        </select>
        <label for="feedOutputCnt">${feedOutputCnt1Label}</label>
        <input id="feedOutputCnt" class="normalInput" type="text"/>
        <label for="faviconURL">Favicon</label>
        <input id="faviconURL" class="normalInput" type="text"/>
        <label for="customVars">${customVars1Label}</label>
        <input id="customVars" class="normalInput" type="text"/>
        <label>
            <div class="fn__flex-inline">
                ${enableArticleUpdateHint1Label}
                <input id="enableArticleUpdateHint" type="checkbox" class="normalInput"/>
            </div>
        </label>
        <label>
            <div class="fn__flex-inline">
                ${allowVisitDraftViaPermalink1Label}
                <input id="allowVisitDraftViaPermalink" type="checkbox" class="normalInput"/>
            </div>
        </label>
        <label>
            <div class="fn__flex-inline">
                ${allowComment1Label}
                <input id="commentable" type="checkbox" class="normalInput"/>
            </div>
        </label>
        <label>
            <div class="fn__flex-inline">
                ${syncGitHubLabel}
                <input id="syncGitHub" type="checkbox" class="normalInput"/>
            </div>
        </label>
        <label>
            <div class="fn__flex-inline">
                ${pullGitHubLabel}
                <input id="pullGitHub" type="checkbox" class="normalInput"/>
            </div>
        </label>
        <button class="fn__right" onclick="admin.preference.update()">${updateLabel}</button>
        <div class="fn__clear"></div>
    </div>
    <div id="tabPreferencePanel_signs" class="fn__none form">
        <button onclick="admin.preference.update()" class="fn__right">${updateLabel}</button>
        <div class="fn__clear"></div>
        <button id="preferenceSignButton1">${signLabel}1</button>
        <textarea rows="8" id="preferenceSign1"></textarea>
        <button id="preferenceSignButton2">${signLabel}2</button>
        <textarea rows="8" id="preferenceSign2"></textarea>
        <button id="preferenceSignButton3">${signLabel}3</button>
        <textarea rows="8" id="preferenceSign3"></textarea><br><br>
        <button onclick="admin.preference.update()" class="fn__right">${updateLabel}</button>
        <div class="fn__clear"></div>
    </div>
</div>
${plugins}
