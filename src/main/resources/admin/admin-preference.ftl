<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

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
        <li>
            <div id="tabPreference_markdown">
                <a href="#tools/preference/markdown">Markdown</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabPreferencePanel" class="sub-tabs-main">
    <div id="tabPreferencePanel_config" class="form">
        <div class="fn__clear">
            <a style="line-height: 32px" href="https://ld246.com/settings" target="_blank">${configSiteLabel}</a>
            <button onclick="admin.preference.update()" class="fn__right">${updateLabel}</button>
        </div>
        <div class="fn__clear"></div>
        <label for="blogTitle">${blogTitle1Label}</label>
        <input id="blogTitle" type="text"/>
        <label for="blogSubtitle">${blogSubtitle1Label}</label>
        <input id="blogSubtitle" type="text"/>
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
            <a style="line-height: 32px" href="https://ld246.com/settings" target="_blank">${configSiteLabel}</a>
            <button onclick="admin.preference.update()" class="fn__right">${updateLabel}</button>
        </div>
    </div>
    <div id="tabPreferencePanel_setting" class="fn__none form">
        <button class="fn__right" onclick="admin.preference.update()">${updateLabel}</button>
        <div class="fn__clear"></div>
        <div class="fn__flex">
            <div class="fn__flex-1">
                <label>
                    ${localeString1Label}
                    <select id="localeString">
                        <option value="zh_CN">简体中文</option>
                        <option value="en_US">Englisth(US)</option>
                    </select>
                </label>
                <label>
                    ${timeZoneId1Label}
                    <select id="timeZoneId">
                        ${timeZoneIdOptions}
                    </select>
                </label>
                <label>
                    ${articleListDisplay1Label}
                    <select id="articleListDisplay">
                        <option value="titleOnly">${titleOnlyLabel}</option>
                        <option value="titleAndAbstract">${titleAndAbstractLabel}</option>
                        <option value="titleAndContent">${titleAndContentLabel}</option>
                    </select>
                </label>
                <label>
                    <a href="https://xyproto.github.io/splash/docs/longer/all.html"
                       target="_blank">${previewLabel}</a>${hljsThemeLabel}
                    <select id="hljsTheme">
                        <option value="abap">abap</option>
                        <option value="algol">algol</option>
                        <option value="algol_nu">algol_nu</option>
                        <option value="arduino">arduino</option>
                        <option value="autumn">autumn</option>
                        <option value="borland">borland</option>
                        <option value="bw">bw</option>
                        <option value="colorful">colorful</option>
                        <option value="dracula">dracula</option>
                        <option value="emacs">emacs</option>
                        <option value="friendly">friendly</option>
                        <option value="fruity">fruity</option>
                        <option value="github">github</option>
                        <option value="igor">igor</option>
                        <option value="lovelace">lovelace</option>
                        <option value="manni">manni</option>
                        <option value="monokai">monokai</option>
                        <option value="monokailight">monokailight</option>
                        <option value="murphy">murphy</option>
                        <option value="native">native</option>
                        <option value="paraiso-dark">paraiso-dark</option>
                        <option value="paraiso-light">paraiso-light</option>
                        <option value="pastie">pastie</option>
                        <option value="perldoc">perldoc</option>
                        <option value="pygments">pygments</option>
                        <option value="rainbow_dash">rainbow_dash</option>
                        <option value="rrt">rrt</option>
                        <option value="solarized-dark">solarized-dark</option>
                        <option value="solarized-dark256">solarized-dark256</option>
                        <option value="solarized-light">solarized-light</option>
                        <option value="swapoff">swapoff</option>
                        <option value="tango">tango</option>
                        <option value="trac">trac</option>
                        <option value="vim">vim</option>
                        <option value="vs">vs</option>
                        <option value="xcode">xcode</option>
                    </select>
                </label>
                <label>
                    ${feedOutputModel1Label}
                    <select id="feedOutputMode">
                        <option value="abstract">${abstractLabel}</option>
                        <option value="fullContent">${fullContentLabel}</option>
                    </select>
                </label>
                <label>
                    Favicon
                    <input id="faviconURL" class="normalInput" type="text"/>
                </label>
                <label>
                    ${customVars1Label}
                    <input id="customVars" class="normalInput" type="text"/>
                </label>
                <label>
                    ${githubPATLabel}
                    <input id="githubPAT" class="normalInput" type="text"/>
                </label>
                <label class="checkbox">
                    <input id="enableArticleUpdateHint" type="checkbox" class="normalInput"/>
                    <span>&nbsp;${enableArticleUpdateHint1Label}</span>
                </label>
                <label class="checkbox">
                    <input id="allowVisitDraftViaPermalink" type="checkbox" class="normalInput"/>
                    <span>&nbsp;${allowVisitDraftViaPermalink1Label}</span>
                </label>
                <label class="checkbox">
                    <input id="syncGitHub" type="checkbox" class="normalInput"/>
                    <span>&nbsp;${syncGitHubLabel}</span>
                </label>
                <label class="checkbox">
                    <input id="pullGitHub" type="checkbox" class="normalInput"/>
                    <span>&nbsp;${pullGitHubLabel}</span>
                </label>
            </div>
            <div class="fn__margin12"></div>
            <div class="fn__flex-1">
                <label>
                    ${indexTagDisplayCnt1Label}
                    <input id="mostUsedTagDisplayCount" class="normalInput" type="text"/>
                </label>
                <label>
                    ${randomArticlesDisplayCnt1Label}
                    <input id="randomArticlesDisplayCount" class="normalInput" type="text"/>
                </label>
                <label>
                    ${relevantArticlesDisplayCnt1Label}
                    <input id="relevantArticlesDisplayCount" class="normalInput" type="text"/>
                </label>
                <label>
                    ${externalRelevantArticlesDisplayCnt1Label}
                    <input id="externalRelevantArticlesDisplayCount" class="normalInput" type="text"/>
                </label>
                <label>
                    ${pageSize1Label}
                    <input id="articleListDisplayCount" class="normalInput" type="text"/>
                </label>
                <label>
                    ${windowSize1Label}
                    <input id="articleListPaginationWindowSize" class="normalInput" type="text"/>
                </label>
                <label>
                    ${feedOutputCnt1Label}
                    <input id="feedOutputCnt" class="normalInput" type="text"/>
                </label>
            </div>
        </div>
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
    <div id="tabPreferencePanel_markdown" class="fn__none form">
        <button class="fn__right" onclick="admin.preference.update()">${updateLabel}</button>
        <div class="fn__clear"></div>
        ${editorModeLabel}
        <span class="fn__flex">
            <label class="fn__flex-1 checkbox">
                <input name="editorMode" type="radio" value="wysiwyg" checked>
                <span>&nbsp;${editorModeWYSIWYGLabel}</span>
            </label>
            <label class="fn__flex-1 checkbox">
                <input name="editorMode" type="radio" value="ir">
                <span>&nbsp;${editorModeIRLabel}</span>
            </label>
            <label class="fn__flex-1 checkbox">
                <input name="editorMode" type="radio" value="sv">
                <span>&nbsp;${editorModeSVLabel}</span>
            </label>
        </span>
        <label class="checkbox">
            <input id="showCodeBlockLn" type="checkbox" class="normalInput"/>
            <span>&nbsp;${showCodeBlockLnLabel}</span>
        </label>
        <label class="checkbox">
            <input id="speech" type="checkbox" class="normalInput"/>
            <span>&nbsp;${speechSelectedLabel}</span>
        </label>
        <#if !luteAvailable>
            <br><br>
            ${luteHTTPLabel}
        </#if>
        <label class="checkbox">
            <input id="paragraphBeginningSpace" type="checkbox" class="normalInput"/>
            <span>&nbsp;${paragraphBeginningSpaceLabel}</span>
        </label>
        <label class="checkbox">
            <input id="footnotes" type="checkbox" class="normalInput"/>
            <span>&nbsp;${supportFootnotesLabel}</span>
        </label>
        <label class="checkbox">
            <input id="showToC" type="checkbox" class="normalInput"/>
            <span>&nbsp;${supportToCLabel}</span>
        </label>
        <label class="checkbox">
            <input id="autoSpace" type="checkbox" class="normalInput"/>
            <span>&nbsp;${autoSpaceLabel}</span>
        </label>
        <label class="checkbox">
            <input id="fixTermTypo" type="checkbox" class="normalInput"/>
            <span>&nbsp;${fixTermTypoLabel}</span>
        </label>
        <label class="checkbox">
            <input id="chinesePunct" type="checkbox" class="normalInput"/>
            <span>&nbsp;${chinesePunctLabel}</span>
        </label>
        <label class="checkbox">
            <input id="inlineMathAllowDigitAfterOpenMarker" type="checkbox" class="normalInput"/>
            <span>&nbsp;${inlineMathAllowDigitAfterOpenMarkerLabel}</span>
        </label>
        <button class="fn__right" onclick="admin.preference.update()">${updateLabel}</button>
        <div class="fn__clear"></div>
    </div>
</div>
${plugins}
