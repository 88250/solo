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
    <div id="tabOthersPanel_email">
        <table class="form" width="98%" cellpadding="0" cellspacing="9px">
            <tbody>
                <tr>
                    <th width="90px" valign="top">
                        <label for="replayEmailTemplateTitle">${emailSubject1Label}</label>
                    </th>
                    <td>
                        <input id="replayEmailTemplateTitle" type="text" />
                    </td>
                    <td rowspan="2" valign="top" width="260px">
                        <div class="marginLeft12">
                           ${replayEmailExplanationLabel}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th valign="top">
                        <label for="replayEmailTemplateBody">${emailContent1Label}</label>
                    </th>
                    <td>
                        <textarea rows="9" id="replayEmailTemplateBody"></textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="right">
                        <button onclick="admin.others.update()">${updateLabel}</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div id="tabOthersPanel_other" class="none">
        <button class="margin12" onclick="admin.others.removeUnusedTags();">${removeUnusedTagsLabel}</button>
        <#if isMySQL>
        <button class="margin12" onclick="admin.others.exportSQL();">${exportSQLLabel}</button>
        </#if>
    </div>
</div>
${plugins}
