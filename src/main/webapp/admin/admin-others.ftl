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
<div id="tabOthers" class="sub-tabs">
    <ul>
        <li>
            <div id="tabOthers_tag">
                <a href="#tools/others/tag">${clearDataLabel}</a>
            </div>
        </li>
        <li>
            <div id="tabOthers_data">
                <a href="#tools/others/data">${exportDataLabel}</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabOthersPanel" class="sub-tabs-main">
    <div id="tabOthersPanel_tag" class="fn__none">
        <button class="fn__margin12" onclick="admin.others.removeUnusedTags();">${removeUnusedTagsLabel}</button>
        <button class="fn__margin12" onclick="admin.others.removeUnusedArchives();">${removeUnusedArchivesLabel}</button>
    </div>
    <div id="tabOthersPanel_data" class="fn__none">
        <#if supportExport>
        <button class="fn__margin12" onclick="admin.others.exportSQL();">${exportSQLLabel}</button>
        </#if>
        <button class="fn__margin12" onclick="admin.others.exportJSON();">${exportJSONLabel}</button>
        <button class="fn__margin12" onclick="admin.others.exportHexo();">${exportHexoLabel}</button>
    </div>
</div>
${plugins}
