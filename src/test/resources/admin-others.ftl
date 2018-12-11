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
<div id="tabOthers" class="sub-tabs">
    <ul>
        <li>
            <div id="tabOthers_email">
                <a class="tab-current" href="#tools/others/email">${replayEmailTemplateLabel}</a>
            </div>
        </li>
        <li>
            <div id="tabOthers_other">
                <a href="#tools/others/other">${othersLabel}</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabOthersPanel" class="sub-tabs-main">
    <div id="tabOthersPanel_email" class="form form__no-table">
        <label for="replayEmailTemplateTitle">${emailSubject1Label}</label>
        <input id="replayEmailTemplateTitle" type="text"/>
        <label for="replayEmailTemplateBody">${emailContent1Label}</label>
        <textarea rows="9" id="replayEmailTemplateBody"></textarea><br><br>
        <button onclick="admin.others.update()" class="right">${updateLabel}</button>
        <div class="clear"></div>
        <div class="content-reset">
        ${replayEmailExplanationLabel}
        </div>
    </div>
    <div id="tabOthersPanel_other" class="none">
        <button class="margin12" onclick="admin.others.removeUnusedTags();">${removeUnusedTagsLabel}</button>
        <#if supportExport>
        <button class="margin12" onclick="admin.others.exportSQL();">${exportSQLLabel}</button>
        </#if>
        <button class="margin12" onclick="admin.others.exportJSON();">${exportJSONLabel}</button>
        <button class="margin12" onclick="admin.others.exportHexo();">${exportHexoLabel}</button>
    </div>
</div>
${plugins}
